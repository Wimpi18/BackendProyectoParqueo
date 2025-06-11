package backendProyectoParqueo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backendProyectoParqueo.dto.ApiResponse;
import backendProyectoParqueo.dto.RegistroRequestDTO;
import backendProyectoParqueo.enums.RoleEnum;
import backendProyectoParqueo.service.RegistroClienteService;
import backendProyectoParqueo.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class RegistroClienteController {

    private final RegistroClienteService registroClienteService;

    @PostMapping("/registrar")
    @PreAuthorize("hasRole('" + RoleEnum.Const.ADMINISTRADOR + "')")
    public ResponseEntity<ApiResponse<Void>> registrar(@RequestBody @Valid RegistroRequestDTO request) {
        registroClienteService.registrarCliente(request);
        return ApiResponseUtil.created("Cliente registrado exitosamente.", null);
    }
}
