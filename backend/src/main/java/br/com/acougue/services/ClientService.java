package br.com.acougue.services;

import br.com.acougue.dto.ClientRequestDTO;
import br.com.acougue.dto.ClientResponseDTO;
import br.com.acougue.dto.PhoneNumberDTO;
import br.com.acougue.entities.Client;
import br.com.acougue.enums.PhoneType;
import br.com.acougue.exceptions.ResourceNotFoundException;
import br.com.acougue.mapper.ClientMapper;
import br.com.acougue.repository.ClientRepository;
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
    private final ClientMapper clientMapper;

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
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

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // Mapeamento dinâmico para os nomes de coluna específicos
            Map<String, List<Integer>> headerMap = new HashMap<>();
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) throw new IllegalArgumentException("A planilha está vazia ou sem cabeçalho.");

            for (Cell cell : headerRow) {
                String headerValue = getCellValueAsString(cell).toLowerCase().trim();
                headerMap.computeIfAbsent(headerValue, k -> new ArrayList<>()).add(cell.getColumnIndex());
            }

            // Valida se as colunas obrigatórias foram encontradas
            if (!headerMap.containsKey("clientes") || headerMap.get("clientes").size() < 2 || !headerMap.containsKey("telefones")) {
                throw new IllegalArgumentException("A planilha deve ter duas colunas 'Clientes' (Nome e Endereço) e uma 'Telefones'.");
            }

            // Define os índices com base na convenção
            int nameIndex = headerMap.get("clientes").get(0);
            int addressIndex = headerMap.get("clientes").get(1);
            int phoneIndex = headerMap.get("telefones").get(0);
            Integer neighborhoodIndex = headerMap.containsKey("bairro") ? headerMap.get("bairro").get(0) : null;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                try {
                    String name = getCellValue(row, nameIndex);
                    String address = getCellValue(row, addressIndex);
                    String phoneRaw = getCellValue(row, phoneIndex);
                    String neighborhood = getCellValue(row, neighborhoodIndex);

                    if (name.isEmpty() || phoneRaw.isEmpty()) continue;

                    String cleanPhone = phoneRaw.replaceAll("\\D", "");
                    if (cleanPhone.isEmpty()) throw new IllegalArgumentException("Telefone inválido: " + phoneRaw);

                    if (!clientRepository.existsByPhoneNumbersNumber(cleanPhone)) {
                        ClientRequestDTO dto = new ClientRequestDTO();
                        dto.setName(name);
                        
                        List<PhoneNumberDTO> phones = new ArrayList<>();
                        phones.add(new PhoneNumberDTO(PhoneType.CELULAR, cleanPhone, true));
                        dto.setPhoneNumbers(phones);
                        
                        dto.setAddress(address);
                        dto.setAddressNeighborhood(neighborhood);
                        dto.setObservation(""); // Observação não vem da planilha
                        dto.setEstablishmentId(establishmentId);

                        create(dto);
                        successCount++;
                    }

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
