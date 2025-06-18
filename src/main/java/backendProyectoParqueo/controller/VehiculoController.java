package backendProyectoParqueo.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backendProyectoParqueo.dto.ApiResponse;
import backendProyectoParqueo.dto.ClienteDTO;
import backendProyectoParqueo.dto.VehiculoDTO;
import backendProyectoParqueo.model.Vehiculo;
import backendProyectoParqueo.service.VehiculoService;
import backendProyectoParqueo.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("vehiculo")
@RequiredArgsConstructor
public class VehiculoController {

    private final VehiculoService vehiculoService;

    @GetMapping
    public ResponseEntity<List<Vehiculo>> listarVehiculos() {
        return ResponseEntity.ok(vehiculoService.listarTodos());
    }

    @PostMapping("/activos")
    public ResponseEntity<ApiResponse<List<Object>>> listarVehiculosActivos(@RequestBody ClienteDTO request) {
        return ApiResponseUtil.success("Todos los vehículos pertenecientes al cliente",
                vehiculoService.obtenerVehiculosActivosPorClienteId(request.getId()));
    }

    @GetMapping("/reporte")
    public ResponseEntity<ApiResponse<List<VehiculoDTO>>> listarVehiculos(@RequestBody ClienteDTO request) {
        return ApiResponseUtil.success("Todos los vehículos pertenecientes al cliente",
                vehiculoService.obtenerVehiculosPorClienteId(request.getId()));
    }

    @PostMapping
    public ResponseEntity<?> crearVehiculo(@RequestBody Vehiculo vehiculo) {
        try {
            return ResponseEntity.ok(vehiculoService.crearVehiculo(vehiculo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarVehiculo(@PathVariable Long id) {
        try {
            vehiculoService.eliminarVehiculo(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

}