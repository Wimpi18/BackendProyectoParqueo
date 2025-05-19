package backendProyectoParqueo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import backendProyectoParqueo.model.Cliente;
import backendProyectoParqueo.repository.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Cliente save(@RequestBody Cliente cliente) {
        return clienteRepository.save(cliente);
    }
}
