package backendProyectoParqueo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backendProyectoParqueo.dto.ApiResponse;
import backendProyectoParqueo.dto.RegistroUsuarioAdminRequestDTO;
import backendProyectoParqueo.service.RegistroAdminService;
import backendProyectoParqueo.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class RegistroAdminController {

    private final RegistroAdminService usuarioAdminService;

    @PostMapping("/registrar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<String>> registrarUsuarioAdmin(
            @Valid @RequestBody RegistroUsuarioAdminRequestDTO request) {

        String username = usuarioAdminService.registrarUsuarioAdmin(request);
        return ApiResponseUtil.created("Usuario registrado correctamente.", username);
    }
}
