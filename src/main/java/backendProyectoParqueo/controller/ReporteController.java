// src/main/java/backendProyectoParqueo/controller/ReporteController.java
package backendProyectoParqueo.controller;

import backendProyectoParqueo.dto.ApiResponse;
import backendProyectoParqueo.model.Cliente; // Importa el nuevo DTO
import backendProyectoParqueo.dto.ReporteEstadoCuentaVehiculoDTO;
import backendProyectoParqueo.dto.VehiculoDTO;
import backendProyectoParqueo.service.ReporteService;
import backendProyectoParqueo.util.ApiResponseUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import java.util.ArrayList; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody; // Para el DTO
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam; // Ya no lo necesitas para este endpoint
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reporte")
public class ReporteController {

    private final ReporteService reporteService;

    @Autowired
    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    /**
     * Obtiene la lista de TODOS los vehículos (DTOs) asociados a un cliente.
     * El clienteId se pasa en el cuerpo de la solicitud POST.
     * Ejemplo: POST /reporte/cliente/vehiculos
     * Body: { "id": "uuid-aqui" }
     */
    @GetMapping("/cliente/vehiculos") // Cambiado a @PostMapping
    public ResponseEntity<ApiResponse<List<Object>>> getTodosVehiculosPorCliente(
            @RequestBody Cliente requestDTO) { // Recibe el DTO
        try {
            UUID clienteId = requestDTO.getId(); // Obtén el ID del DTO
            if (clienteId == null) {
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>("error", HttpStatus.BAD_REQUEST.value(), "El campo 'id' del cliente es requerido en el body.", null)
                );
            }
            List<Object> vehiculos = reporteService.getTodosVehiculosDTOPorCliente(clienteId);
            if (vehiculos.isEmpty()) {
                return ApiResponseUtil.success("El cliente no tiene vehículos asociados.", vehiculos);
            }
            return ApiResponseUtil.success("Vehículos del cliente obtenidos exitosamente.", vehiculos);
        } catch (Exception e) {
            System.err.println("Error en getTodosVehiculosPorCliente: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("error", HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error al obtener vehículos del cliente: " + e.getMessage(), null));
        }
    }

    // ... (otros endpoints como getVehiculosActivosCliente, getEstadoCuentaVehiculoActivo, getTodosEstadosCuentaPorPlaca
    //      pueden permanecer como GET con @RequestParam o @PathVariable si es apropiado para ellos) ...

    // Por ejemplo, este sigue estando bien como GET:
    @GetMapping("/vehiculo/estados-cuenta") // Cambiado a POST y ruta más genérica
    public ResponseEntity<ApiResponse<List<ReporteEstadoCuentaVehiculoDTO>>> getTodosEstadosCuentaPorPlacaEnBody(
            @RequestBody VehiculoDTO requestDTO) { // Recibe el DTO del body
        try {
            String placa = requestDTO.getPlaca(); // Obtén la placa del DTO

            if (placa == null || placa.trim().isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(new ApiResponse<>("error", HttpStatus.BAD_REQUEST.value(), "El campo 'placa' es requerido en el cuerpo de la solicitud.", null));
            }

            List<ReporteEstadoCuentaVehiculoDTO> reportes = reporteService.getEstadosCuentaPorPlacaVehiculo(placa);
            if (reportes.isEmpty()) {
                return ApiResponseUtil.success("No se encontraron registros de parqueo para la placa " + placa + ".", new ArrayList<>());
            }
            return ApiResponseUtil.success("Estados de cuenta para la placa " + placa + " obtenidos exitosamente.", reportes);
        } catch (EntityNotFoundException e) { // Esta se lanzará si el Vehículo con esa placa no existe en el servicio
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("error", HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
             System.err.println("Error procesando /reporte/vehiculo/estados-cuenta (POST): " + e.getMessage());
             e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("error", HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error al generar los reportes: " + e.getMessage(), null));
        }
    }
}