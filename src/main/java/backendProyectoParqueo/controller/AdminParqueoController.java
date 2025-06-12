package backendProyectoParqueo.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backendProyectoParqueo.dto.ApiResponse;
import backendProyectoParqueo.dto.CambiarEstadoParqueoDTO;
import backendProyectoParqueo.dto.JwtUserPayload;
import backendProyectoParqueo.model.Parqueo;
import backendProyectoParqueo.resolvers.UserGuard;
import backendProyectoParqueo.service.admin.CambioEstadoParqueo;
import jakarta.validation.Valid;

@RestController
@RequestMapping("admin")
public class AdminParqueoController {
  
  private final CambioEstadoParqueo parqueoAdminService;

    @Autowired
    public AdminParqueoController(CambioEstadoParqueo parqueoAdminService) {
        this.parqueoAdminService = parqueoAdminService;
    }

    // Endpoint para BLOQUEAR un parqueo (ID y motivo en el body)
    @PostMapping("/bloquear")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<?>> bloquearParqueoCliente(
            @Valid @RequestBody CambiarEstadoParqueoDTO requestDTO,
            @UserGuard JwtUserPayload adminAutenticado) {
        try {
            UUID adminEjecutorId = adminAutenticado.getUserId();
            Parqueo parqueoActualizado = parqueoAdminService.bloquearCliente(
                    requestDTO.getUsuarioId(), 
                    requestDTO.getMotivo(),
                    adminEjecutorId
            );
            String mensaje = String.format("Cliente ID %d bloqueado exitosamente. Nuevo estado del cliente: %s",
                                           requestDTO.getUsuarioId(),  parqueoActualizado.getEstado());
            return ResponseEntity.ok(ApiResponse.success(mensaje, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al bloquear cliente: " + e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    // Endpoint para INACTIVAR un parqueo
    @PostMapping("/inactivar")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<?>> inactivarParqueoCliente(
            @Valid @RequestBody CambiarEstadoParqueoDTO requestDTO,
            @UserGuard JwtUserPayload adminAutenticado) {
        try {
            UUID adminEjecutorId = adminAutenticado.getUserId();
            Parqueo parqueoActualizado = parqueoAdminService.inactivarCliente(
                    requestDTO.getUsuarioId(), 
                    requestDTO.getMotivo(),
                    adminEjecutorId
            );
            String mensaje = String.format("Cliente ID %d inactivado exitosamente. Nuevo estado del cliente: %s. Espacio liberado: %s",
                                           requestDTO.getUsuarioId(),  parqueoActualizado.getEstado(), parqueoActualizado.getNroEspacio() == null ? "Sí" : "No (o ya estaba libre)");
            return ResponseEntity.ok(ApiResponse.success(mensaje, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al inactivar cliente: " + e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    // Endpoint para ACTIVAR un parqueo
    @PostMapping("/activar")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<?>> activarParqueoCliente(
            @Valid @RequestBody CambiarEstadoParqueoDTO requestDTO,
            @UserGuard JwtUserPayload adminAutenticado) {
        try {
            UUID adminEjecutorId = adminAutenticado.getUserId();
            Parqueo parqueoActualizado = parqueoAdminService.activarCliente(
                    requestDTO.getUsuarioId(), 
                    requestDTO.getMotivo(),
                    adminEjecutorId
            );
             String mensaje = String.format("Cliente ID %d activado exitosamente. Nuevo estado del parqueo: %s",
                                           requestDTO.getUsuarioId(),  parqueoActualizado.getEstado());
            return ResponseEntity.ok(ApiResponse.success(mensaje, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al activar cliente: " + e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

  
}
