package br.com.acougue.services;

import br.com.acougue.dto.ClientRequestDTO;
import br.com.acougue.dto.ClientResponseDTO;
import br.com.acougue.dto.ClientSummaryDTO;
import br.com.acougue.entities.Client;
import br.com.acougue.entities.Establishment;
import br.com.acougue.entities.PhoneNumber;
import br.com.acougue.enums.PhoneType;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.mapper.ClientMapper;
import br.com.acougue.repository.ClientRepository;
import br.com.acougue.repository.EstablishmentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final EstablishmentRepository establishmentRepository;
    private final ClientMapper clientMapper;

    public ClientService(ClientRepository clientRepository, EstablishmentRepository establishmentRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.establishmentRepository = establishmentRepository;
        this.clientMapper = clientMapper;
    }

    // NOVO MÉTODO: Listagem leve
    public List<ClientSummaryDTO> listSummaries(Long establishmentId) {
        return clientRepository.findClientSummariesByEstablishmentId(establishmentId);
    }

    @Transactional
    public ClientResponseDTO create(ClientRequestDTO requestDTO) {
        if (requestDTO.getPhoneNumbers() != null && !requestDTO.getPhoneNumbers().isEmpty()) {
            String primaryNumber = requestDTO.getPhoneNumbers().get(0).getNumber().replaceAll("\\D", "");
            if (clientRepository.existsByPhoneNumbersNumber(primaryNumber)) {
                 throw new DataIntegrityViolationException("Um cliente com o número de telefone principal já existe.");
            }
        }
        Client client = clientMapper.toEntity(requestDTO);
        Client savedClient = clientRepository.save(client);
        return clientMapper.toResponseDTO(savedClient);
    }

    @Transactional
    public Map<String, Object> importClientsFromExcel(MultipartFile file, Long establishmentId) throws IOException {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        List<String> errors = new ArrayList<>();

        Establishment establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado com id: " + establishmentId));

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            Map<String, Integer> headerMap = new HashMap<>();
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) throw new IllegalArgumentException("A planilha está vazia ou sem cabeçalho.");

            List<Integer> clientColumnIndexes = new ArrayList<>();
            List<Integer> phoneColumnIndexes = new ArrayList<>();

            for (Cell cell : headerRow) {
                String headerValue = getCellValueAsString(cell).toLowerCase().trim();
                if (headerValue.contains("cliente")) clientColumnIndexes.add(cell.getColumnIndex());
                else if (headerValue.contains("telefone")) phoneColumnIndexes.add(cell.getColumnIndex());
                else if (headerValue.contains("bairro")) headerMap.put("neighborhood", cell.getColumnIndex());
                else if (headerValue.contains("observa")) headerMap.put("observation", cell.getColumnIndex());
                else if (headerValue.contains("endere")) headerMap.put("address_explicit", cell.getColumnIndex());
            }

            if (clientColumnIndexes.isEmpty()) {
                throw new IllegalArgumentException("A planilha deve ter pelo menos uma coluna para 'Cliente' (Nome).");
            }

            int nameIndex = clientColumnIndexes.get(0);
            Integer addressIndex = headerMap.get("address_explicit") != null ? headerMap.get("address_explicit") : (clientColumnIndexes.size() > 1 ? clientColumnIndexes.get(1) : null);
            Integer neighborhoodIndex = headerMap.get("neighborhood");
            Integer observationIndex = headerMap.get("observation");

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                try {
                    String name = getCellValue(row, nameIndex);
                    if (name.isEmpty()) continue;

                    Client newClient = new Client();
                    newClient.setName(name);
                    newClient.setAddress(getCellValue(row, addressIndex));
                    newClient.setAddressNeighborhood(getCellValue(row, neighborhoodIndex));
                    newClient.setObservation(getCellValue(row, observationIndex));
                    newClient.setEstablishment(establishment);

                    boolean hasPhone = false;
                    for (Integer phoneIdx : phoneColumnIndexes) {
                        String phoneRaw = getCellValue(row, phoneIdx);
                        String cleanPhone = phoneRaw.replaceAll("\\D", "");
                        if (!cleanPhone.isEmpty()) {
                            PhoneNumber phone = new PhoneNumber();
                            phone.setType(PhoneType.CELULAR);
                            phone.setNumber(cleanPhone);
                            phone.setPrimary(!hasPhone); // O primeiro encontrado é o principal
                            newClient.addPhoneNumber(phone);
                            hasPhone = true;
                        }
                    }
                    
                    if (!hasPhone) {
                        newClient.setObservation( (newClient.getObservation() + " [IMPORTAÇÃO]: Telefone não fornecido.").trim() );
                    }

                    clientRepository.save(newClient);
                    successCount++;

                } catch (Exception e) {
                    failCount++;
                    errors.add("Linha " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }
        }

        result.put("success", successCount);
        result.put("failed", failCount);
        result.put("errors", errors);
        return result;
    }

    private String getCellValue(Row row, Integer index) {
        if (index == null || row == null) return "";
        return getCellValueAsString(row.getCell(index));
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell);
    }

    public List<ClientResponseDTO> advancedSearch(Long establishmentId, String name, String address, String neighborhood, String productName, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;
        List<Client> clients = clientRepository.findByAdvancedFilters(establishmentId, name, address, neighborhood, productName, startDateTime, endDateTime);
        return clientMapper.toResponseDTOList(clients);
    }

    public ClientResponseDTO findById(Long id) {
        Client client = clientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o ID: " + id));
        return clientMapper.toResponseDTO(client);
    }

    @Transactional
    public ClientResponseDTO update(Long id, ClientRequestDTO requestDTO) {
        Client existingClient = clientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o ID: " + id));
        clientMapper.updateEntityFromDTO(existingClient, requestDTO);
        Client updatedClient = clientRepository.save(existingClient);
        return clientMapper.toResponseDTO(updatedClient);
    }

    @Transactional
    public void delete(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente não encontrado com o ID: " + id);
        }
        clientRepository.deleteById(id);
    }
}
