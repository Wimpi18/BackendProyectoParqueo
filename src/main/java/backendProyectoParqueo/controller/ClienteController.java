package backendProyectoParqueo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backendProyectoParqueo.dto.ApiResponse;
import backendProyectoParqueo.dto.ClienteDTO;
import backendProyectoParqueo.dto.JwtUserPayload;
import backendProyectoParqueo.enums.RoleEnum;
import backendProyectoParqueo.model.Cliente;
import backendProyectoParqueo.resolvers.UserGuard;
import backendProyectoParqueo.service.ClienteService;
import backendProyectoParqueo.util.ApiResponseUtil;

@RestController
@RequestMapping("cliente")
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
    @PreAuthorize("hasRole('" + RoleEnum.Const.CAJERO + "')")
    public ResponseEntity<ApiResponse<List<ClienteDTO>>> getAllClientesNoInactivos(@UserGuard JwtUserPayload user) {
        return ApiResponseUtil.success("Todos los clientes que pueden realizar un pago del parqueo",
                clienteService.findAllClientesNoInactivos(user.getUserId()));
    }

    @PostMapping
    public Cliente createCliente(@RequestBody Cliente cliente) {
        return clienteService.save(cliente);
    }
}
