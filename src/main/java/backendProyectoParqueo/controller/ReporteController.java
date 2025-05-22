// src/main/java/backendProyectoParqueo/controller/ReporteController.java
package backendProyectoParqueo.controller;

import backendProyectoParqueo.dto.ApiResponse;
import backendProyectoParqueo.dto.ReporteEstadoCuentaVehiculoDTO;
import backendProyectoParqueo.dto.VehiculoParqueoActivoDTO;
import backendProyectoParqueo.service.ReporteService;
import backendProyectoParqueo.util.ApiResponseUtil;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reportes") // Usar un path base versionado es una buena práctica
public class ReporteController {

    private final ReporteService reporteService;

    @Autowired
    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    /**
     * Obtiene la lista de vehículos (placas) activos asociados a un cliente.
     * El cliente usará esta lista para seleccionar un vehículo y ver su estado de cuenta.
     *
     * @param clienteId El UUID del cliente.
     * @return Lista de VehiculoParqueoActivoDTO.
     */
    @GetMapping("/cliente/{clienteId}/vehiculos-activos")
    public ResponseEntity<ApiResponse<List<VehiculoParqueoActivoDTO>>> getVehiculosActivosPorCliente(
            @PathVariable UUID clienteId) {
        List<VehiculoParqueoActivoDTO> vehiculos = reporteService.getVehiculosActivosParaReporte(clienteId);
        if (vehiculos.isEmpty()) {
            return ApiResponseUtil.success("El cliente no tiene vehículos con parqueo activo.", vehiculos);
        }
        return ApiResponseUtil.success("Vehículos activos del cliente obtenidos exitosamente.", vehiculos);
    }

    /**
     * Obtiene el estado de cuenta detallado para un vehículo específico de un cliente.
     *
     * @param clienteId El UUID del cliente.
     * @param placa     La placa del vehículo.
     * @return ReporteEstadoCuentaVehiculoDTO con los detalles.
     */
    @GetMapping("/cliente/{clienteId}/vehiculo/{placa}/estado-cuenta")
    public ResponseEntity<ApiResponse<ReporteEstadoCuentaVehiculoDTO>> getEstadoCuentaVehiculo(
            @PathVariable UUID clienteId,
            @PathVariable String placa) {
        try {
            ReporteEstadoCuentaVehiculoDTO reporte = reporteService.getEstadoCuentaVehiculo(clienteId, placa);
            return ApiResponseUtil.success("Estado de cuenta del vehículo " + placa + " obtenido exitosamente.", reporte);
        } catch (ObjectNotFoundException e) {
            // Puedes tener un manejador de excepciones global más robusto,
            // pero para este caso específico, esto funciona.
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("error", HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            // Captura genérica para otros posibles errores inesperados
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("error", HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error al generar el reporte: " + e.getMessage(), null));
        }
    }
}