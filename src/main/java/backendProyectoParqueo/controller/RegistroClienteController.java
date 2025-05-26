package backendProyectoParqueo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backendProyectoParqueo.dto.ApiResponse;
import backendProyectoParqueo.dto.RegistroRequestDTO;
import backendProyectoParqueo.service.RegistroClienteService;
import backendProyectoParqueo.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class RegistroClienteController {

    private final RegistroClienteService registroClienteService;

    @PostMapping("/registrar")
    public ResponseEntity<ApiResponse<Void>> registrar(@RequestBody @Valid RegistroRequestDTO request) {
        registroClienteService.registrarCliente(request);
        return ApiResponseUtil.created("Cliente registrado exitosamente.", null);
    }
}
