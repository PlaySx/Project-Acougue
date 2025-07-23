package br.com.acougue.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.acougue.entities.Client;
import br.com.acougue.globalException.ClienteNaoEncontradoException;
import br.com.acougue.services.ClientService;

@RestController
@RequestMapping("/cliente")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PostMapping
    public ResponseEntity<Client> salvar(@RequestBody Client client){
        Client salvo = clientService.salvar(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public List<Client> listarClient(){
        return clientService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> buscarPorId(@PathVariable Long id){
        return clientService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado com id: " + id));
    }

    @PutMapping("/{id}")
    public Client atualizarClient(@PathVariable Long id, @RequestBody Client clientAtualizado) {
        return clientService.atualizar(id, clientAtualizado);
    }

    @DeleteMapping("/{id}")
    public void deletarClient(@PathVariable Long id) {
        clientService.deletar(id);
    }
}
