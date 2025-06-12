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
import backendProyectoParqueo.dto.AccionConMotivoOpcionalRequestDT;
import backendProyectoParqueo.dto.AccionConMotivoRequestDTO;
import backendProyectoParqueo.dto.JwtUserPayload;
import backendProyectoParqueo.model.Parqueo;
import backendProyectoParqueo.resolvers.UserGuard;
import backendProyectoParqueo.service.admin.GestionEstadoUsuario;
import jakarta.validation.Valid;

@RestController
@RequestMapping("admin")
public class AdminEstadoUsuarioController {
  
  private final GestionEstadoUsuario gestionEstadoService;

    @Autowired
    public AdminEstadoUsuarioController(GestionEstadoUsuario gestionEstadoService) {
        this.gestionEstadoService = gestionEstadoService;
    }

    // Endpoint para BLOQUEAR un usuario
    @PostMapping("/bloquear")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<?>> bloquearParqueoCliente(
            @Valid @RequestBody AccionConMotivoRequestDTO requestDTO,
            @UserGuard JwtUserPayload adminAutenticado) {
        try {
            UUID adminEjecutorId = adminAutenticado.getUserId();
            Parqueo parqueoActualizado = gestionEstadoService.bloquearParqueoDelCliente(
                    requestDTO.getUsuarioId(), 
                    requestDTO.getMotivo(),
                    adminEjecutorId
            );

            System.out.println("DEBUG CONTROLLER: Operación de servicio completada. Parqueo ID: " + parqueoActualizado.getId() + ", Estado: " + parqueoActualizado.getEstado());
            String mensaje = String.format("Cliente ID %s bloqueado exitosamente. Nuevo estado del cliente: %s",
                                           requestDTO.getUsuarioId(),  parqueoActualizado.getEstado());
            return ResponseEntity.ok(ApiResponse.success(mensaje, HttpStatus.OK));
        } catch (RuntimeException e) {
           System.err.println("ERROR EN CONTROLADOR: " + e.getMessage());
            e.printStackTrace(); // Imprime el stack trace completo del error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al bloquear cliente: " + e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    // Endpoint para INACTIVAR un usuario
    @PostMapping("/inactivar")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<?>> inactivarEntidad(
            @Valid @RequestBody AccionConMotivoRequestDTO requestDTO,
            @UserGuard JwtUserPayload adminAutenticado) {
        try {
            UUID adminEjecutorId = adminAutenticado.getUserId();
            Object entidadActualizada = gestionEstadoService.inactivarCuenta(
                    requestDTO.getUsuarioId(), 
                    requestDTO.getMotivo(),
                    adminEjecutorId
            );
            String tipoEntidad = entidadActualizada.getClass().getSimpleName();
            String mensaje = String.format("%s con Usuario ID %s inactivado(a) exitosamente.",
                                           tipoEntidad, requestDTO.getUsuarioId());
            return ResponseEntity.ok(ApiResponse.success(mensaje, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al inactivar usuario: " + e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    // Endpoint para ACTIVAR un usuario
    @PostMapping("/activar")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<?>> activarEntidad(
            @Valid @RequestBody AccionConMotivoOpcionalRequestDT requestDTO,
            @UserGuard JwtUserPayload adminAutenticado) {
        try {
            UUID adminEjecutorId = adminAutenticado.getUserId();
            Object entidadActualizada = gestionEstadoService.activarCuenta(
                    requestDTO.getUsuarioId(), 
                    requestDTO.getMotivo(),
                    adminEjecutorId
            );
            String tipoEntidad = entidadActualizada.getClass().getSimpleName();
            String mensaje = String.format("%s con Usuario ID %s activado(a) exitosamente.",
                                           tipoEntidad, requestDTO.getUsuarioId());
            return ResponseEntity.ok(ApiResponse.success(mensaje, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al activar usuario: " + e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

  
}
