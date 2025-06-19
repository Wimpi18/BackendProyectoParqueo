package backendProyectoParqueo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import backendProyectoParqueo.dto.ClienteDTO;
import backendProyectoParqueo.exception.BusinessException;
import backendProyectoParqueo.model.Cliente;
import backendProyectoParqueo.repository.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public List<ClienteDTO> findAllClientesNoInactivos(UUID id) {
        return clienteRepository.findAllClientesNoInactivos(id);
    }

    public Cliente save(@RequestBody Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente findById(UUID id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new BusinessException("El ID proporcionado no corresponde a un cliente válido.",
                        "idCliente"));
    }
}
