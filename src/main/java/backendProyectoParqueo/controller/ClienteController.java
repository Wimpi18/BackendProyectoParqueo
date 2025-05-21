package backendProyectoParqueo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backendProyectoParqueo.dto.ApiResponse;
import backendProyectoParqueo.model.Cliente;
import backendProyectoParqueo.service.ClienteService;
import backendProyectoParqueo.util.ApiResponseUtil;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping()
    public List<Cliente> getAllCliente() {
        return clienteService.findAll();
    }

    @GetMapping("/activos")
    public ResponseEntity<ApiResponse<List<Object>>> getAllClientesNoIanactivos() {
        return ApiResponseUtil.success("Todos los clientes que pueden realizar un pago del parqueo",
                clienteService.findAllClientesNoInactivos());
    }

    @PostMapping
    public Cliente createCliente(@RequestBody Cliente cliente) {
        return clienteService.save(cliente);
    }
}
