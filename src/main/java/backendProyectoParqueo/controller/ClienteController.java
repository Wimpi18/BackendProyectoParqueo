package backendProyectoParqueo.controller;

import backendProyectoParqueo.model.Cajero;
import backendProyectoParqueo.model.Cliente;
import backendProyectoParqueo.repository.ClienteRepository;
import backendProyectoParqueo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/getCliente")
    public List<Cliente> getAllCliente() {
        return clienteRepository.findAll();
    }

    @PostMapping
    public Cliente createCliente(@RequestBody Cliente cliente) {
        return clienteRepository.save(cliente);
    }
}
