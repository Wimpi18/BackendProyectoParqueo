package backendProyectoParqueo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import backendProyectoParqueo.dto.CambiarEstadoParqueoDTO;
import backendProyectoParqueo.model.Parqueo;
import backendProyectoParqueo.service.admin.CambioEstadoParqueo;
import jakarta.validation.Valid;

@RestController
@RequestMapping("admin/parqueo")
public class AdminParqueoController {
  
  private final CambioEstadoParqueo parqueoAdminService;

    @Autowired
    public AdminParqueoController(CambioEstadoParqueo parqueoAdminService) {
        this.parqueoAdminService = parqueoAdminService;
    }

    // Endpoint para BLOQUEAR un parqueo (ID y motivo en el body)
    @PostMapping("/bloquear")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> bloquearParqueoCliente(
            @Valid @RequestBody CambiarEstadoParqueoDTO requestDTO) {
        try {
            Parqueo parqueoActualizado = parqueoAdminService.bloquearParqueoCliente(
                    requestDTO.getParqueoId(), 
                    requestDTO.getMotivo()
            );
            String mensaje = String.format("Parqueo ID %d bloqueado exitosamente. Cliente asociado ahora está BLOQUEADO. Nuevo estado del parqueo: %s",
                                           requestDTO.getParqueoId(), parqueoActualizado.getEstado());
            return ResponseEntity.ok(mensaje);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al bloquear parqueo: " + e.getMessage());
        }
    }

    // Endpoint para DESBLOQUEAR un parqueo lo cual es equivalente a activarlo
    @PostMapping("/desbloquear")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> desbloquearParqueoCliente(
            @Valid @RequestBody CambiarEstadoParqueoDTO requestDTO) {
        try {
            Parqueo parqueoActualizado = parqueoAdminService.desbloquearParqueoCliente(
                    requestDTO.getParqueoId(),
                    requestDTO.getMotivo()
            );
            String mensaje = String.format("Parqueo ID %d desbloqueado exitosamente. Cliente asociado ahora está ACTIVO. Nuevo estado del parqueo: %s",
                                           requestDTO.getParqueoId(), parqueoActualizado.getEstado());
            return ResponseEntity.ok(mensaje);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al desbloquear parqueo: " + e.getMessage());
        }
    }

    // Endpoint para INACTIVAR un parqueo
    @PostMapping("/inactivar")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> inactivarParqueoCliente(
            @Valid @RequestBody CambiarEstadoParqueoDTO requestDTO) {
        try {
            Parqueo parqueoActualizado = parqueoAdminService.inactivarParqueoCliente(
                    requestDTO.getParqueoId(),
                    requestDTO.getMotivo()
            );
            String mensaje = String.format("Parqueo ID %d inactivado exitosamente. Cliente asociado ahora está INACTIVO. Nuevo estado del parqueo: %s. Espacio liberado: %s",
                                           requestDTO.getParqueoId(), parqueoActualizado.getEstado(), parqueoActualizado.getNroEspacio() == null ? "Sí" : "No (o ya estaba libre)");
            return ResponseEntity.ok(mensaje);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al inactivar parqueo: " + e.getMessage());
        }
    }

    // Endpoint para ACTIVAR un parqueo
    @PostMapping("/activar")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> activarParqueoCliente(
            @Valid @RequestBody CambiarEstadoParqueoDTO requestDTO) {
        try {
            Parqueo parqueoActualizado = parqueoAdminService.activarParqueoCliente(
                    requestDTO.getParqueoId(),
                    requestDTO.getMotivo()
            );
             String mensaje = String.format("Parqueo ID %d activado exitosamente. Cliente asociado ahora está ACTIVO. Nuevo estado del parqueo: %s",
                                           requestDTO.getParqueoId(), parqueoActualizado.getEstado());
            return ResponseEntity.ok(mensaje);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al activar parqueo: " + e.getMessage());
        }
    }

  
}
