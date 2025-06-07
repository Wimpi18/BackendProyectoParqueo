package backendProyectoParqueo.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backendProyectoParqueo.dto.RegistroUsuarioAdminRequestDTO;
import backendProyectoParqueo.service.UsuarioAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
public class UsuarioAdminController {

    private final UsuarioAdminService usuarioAdminService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')") // Solo accesible para administradores
    public ResponseEntity<?> registrarUsuarioAdmin(
            @Valid @RequestBody RegistroUsuarioAdminRequestDTO request) {

        String username = usuarioAdminService.registrarUsuarioAdmin(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of("message", "Usuario registrado correctamente",
                        "username", username));
    }
}
