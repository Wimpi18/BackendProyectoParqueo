package backendProyectoParqueo.controller;

import backendProyectoParqueo.dto.HistorialTarifaDTO;
import backendProyectoParqueo.enums.TipoCliente;
import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.service.HistorialTarifaService;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/historial-tarifas")
@RequiredArgsConstructor
public class HistorialTarifaController {

    private final HistorialTarifaService historialTarifaService;

    @GetMapping
    public List<HistorialTarifaDTO> obtenerHistorialTarifas() {
        return historialTarifaService.obtenerHistorialTarifas();
    }

    // filtro
    @GetMapping("/filtrar")
    public List<HistorialTarifaDTO> obtenerHistorialTarifasFiltrado(
            @RequestParam(required = false) TipoVehiculo tipoVehiculo,
            @RequestParam(required = false) TipoCliente tipoCliente,
            @RequestParam(required = false) String nombreUsuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(required = false) BigDecimal montoMin,
            @RequestParam(required = false) BigDecimal montoMax) {
        return historialTarifaService.obtenerHistorialTarifasFiltrado(
                tipoVehiculo,
                tipoCliente,
                nombreUsuario,
                fechaInicio,
                fechaFin,
                montoMin,
                montoMax);
    }
}
