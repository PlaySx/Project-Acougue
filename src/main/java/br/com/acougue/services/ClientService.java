package br.com.acougue.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.acougue.entities.Client;
import br.com.acougue.entities.Establishment;
import br.com.acougue.globalException.ClienteNaoEncontradoException;
import br.com.acougue.repository.ClientRepository;
import br.com.acougue.repository.EstablishmentRepository;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    public Client salvar(Client client) {
        if (client.getEstablishment() != null && client.getEstablishment().getId() != null) {
            Establishment est = establishmentRepository.findById(client.getEstablishment().getId())
                    .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado"));
            client.setEstablishment(est);
        }
        Client saved = clientRepository.save(client);
        // Buscar novamente para garantir que vem com todos os dados carregados
        return clientRepository.findById(saved.getId())
                .orElseThrow(() -> new RuntimeException("Erro ao buscar cliente salvo"));
    }

    public List<Client> listarTodos() {
        return clientRepository.findAll();
    }

    public Optional<Client> buscarPorId(Long id) {
        return clientRepository.findById(id);
    }

    public Client atualizar(Long id, Client clientAtualizado) {
        return clientRepository.findById(id)
                .map(client -> {
                    client.setName(clientAtualizado.getName());
                    client.setNumberPhone(clientAtualizado.getNumberPhone());
                    client.setAddress(clientAtualizado.getAddress());
                    client.setAddressNeighborhood(clientAtualizado.getAddressNeighborhood());
                    client.setObservation(clientAtualizado.getObservation());
                    if (clientAtualizado.getEstablishment() != null && clientAtualizado.getEstablishment().getId() != null) {
                        Establishment est = establishmentRepository.findById(clientAtualizado.getEstablishment().getId())
                                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado"));
                        client.setEstablishment(est);
                    }
                    return clientRepository.save(client);
                })
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado com id: " + id));
    }

    public void deletar(Long id) {
        clientRepository.deleteById(id);
    }
}
