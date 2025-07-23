package br.com.acougue.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.acougue.entities.Client;
import br.com.acougue.globalException.ClienteNaoEncontradoException;
import br.com.acougue.repository.ClientRepository;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public Client salvar(Client client) {
        return clientRepository.save(client);
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
                    return clientRepository.save(client);
                })
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado com id: " + id));
    }

    public void deletar(Long id) {
        clientRepository.deleteById(id);
    }
}

