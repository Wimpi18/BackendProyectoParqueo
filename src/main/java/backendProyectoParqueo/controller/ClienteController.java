package backendProyectoParqueo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import backendProyectoParqueo.dto.ApiResponse;
import backendProyectoParqueo.dto.RegistroClienteDTO;
import backendProyectoParqueo.service.ClienteService;
import backendProyectoParqueo.util.ApiResponseUtil;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cliente")
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

}
