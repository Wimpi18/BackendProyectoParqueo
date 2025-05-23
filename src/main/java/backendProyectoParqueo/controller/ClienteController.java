package backendProyectoParqueo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import backendProyectoParqueo.dto.ApiResponse;
import backendProyectoParqueo.dto.RegistroClienteDTO;
import backendProyectoParqueo.model.Cliente;
import backendProyectoParqueo.service.ClienteService;
import backendProyectoParqueo.util.ApiResponseUtil;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping("/registrar")
    public ResponseEntity<ApiResponse<Void>> registrarCliente(@Valid @RequestBody RegistroClienteDTO dto) {
        try {
            clienteService.registrarCliente(dto);
            return ApiResponseUtil.successMessage("Cliente registrado exitosamente.");
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al registrar cliente", ex);
        }
    }

    @GetMapping
    public List<Cliente> getAllCliente() {
        return clienteService.findAll();
    }

    @GetMapping("/activos")
    public ResponseEntity<ApiResponse<List<Object>>> getAllClientesNoInactivos() {
        return ApiResponseUtil.success("Todos los clientes que pueden realizar un pago del parqueo",
                clienteService.findAllClientesNoInactivos());
    }

    @PostMapping
    public Cliente createCliente(@RequestBody Cliente cliente) {
        return clienteService.save(cliente);
    }
}
