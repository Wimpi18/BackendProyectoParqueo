// src/main/java/backendProyectoParqueo/controller/ReporteController.java
package backendProyectoParqueo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // Importa el nuevo DTO

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backendProyectoParqueo.dto.ApiResponse;
import backendProyectoParqueo.dto.JwtUserPayload;
import backendProyectoParqueo.dto.ReporteEstadoCuentaVehiculoDTO;
import backendProyectoParqueo.dto.VehiculoDTO;
import backendProyectoParqueo.resolvers.UserGuard;
import backendProyectoParqueo.service.ReporteService;
import backendProyectoParqueo.util.ApiResponseUtil; 
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/reporte")
public class ReporteController {

    private final ReporteService reporteService;

    @Autowired
    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }


    @PostMapping("/vehiculo/estados-cuenta") 
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<ReporteEstadoCuentaVehiculoDTO>>> getTodosEstadosCuentaPorPlacaEnBody(
            @RequestBody VehiculoDTO requestDTO) {
        try {
            String placa = requestDTO.getPlaca(); 

            if (placa == null || placa.trim().isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(new ApiResponse<>("error", HttpStatus.BAD_REQUEST.value(),
                                "El campo 'placa' es requerido en el cuerpo de la solicitud.", null));
            }

            List<ReporteEstadoCuentaVehiculoDTO> reportes = reporteService.getEstadosCuentaPorPlacaVehiculo(placa);
            if (reportes.isEmpty()) {
                return ApiResponseUtil.success("No se encontraron registros de parqueo para la placa " + placa + ".",
                        new ArrayList<>());
            }
            return ApiResponseUtil.success("Estados de cuenta para la placa " + placa + " obtenidos exitosamente.",
                    reportes);
        } catch (EntityNotFoundException e) { // Esta se lanzará si el Vehículo con esa placa no existe en el servicio
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("error", HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            System.err.println("Error procesando /reporte/vehiculo/estados-cuenta (POST): " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("error", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error al generar los reportes: " + e.getMessage(), null));
        }
    }

    @GetMapping("/cliente-vehiculo/estados-cuenta")
    public ResponseEntity<ApiResponse<List<ReporteEstadoCuentaVehiculoDTO>>> getMisEstadosCuentaVehiculoActual(
            @UserGuard JwtUserPayload usuarioAutenticado) {

            
        try {
            UUID clienteId = usuarioAutenticado.getUserId();
             List<ReporteEstadoCuentaVehiculoDTO> reportes = reporteService.getEstadosCuentaParaVehiculoPrincipalDeCliente(clienteId);

            if (reportes.isEmpty()) {
                return ApiResponseUtil.success(
                    String.format("No se encontraron estados de cuenta para sus vehículos activos/principales."),
                    new ArrayList<>()
                );
            }            
            return ApiResponseUtil.success(
                    String.format("Sus estados de cuenta para el vehículo principal/activo obtenidos exitosamente."),
                    reportes
            );
        } catch (EntityNotFoundException e) { 
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("error", HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (IllegalStateException e) { 
             return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) 
                    .body(new ApiResponse<>("error", HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } 
        catch (Exception e) {
            System.err.println("Error procesando /reporte/mis-estados-cuenta-vehiculo-actual (POST): " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("error", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error al generar sus reportes de estado de cuenta: " + e.getMessage(), null));
        }
    }
}