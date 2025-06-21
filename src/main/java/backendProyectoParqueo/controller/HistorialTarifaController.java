package backendProyectoParqueo.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backendProyectoParqueo.dto.HistorialTarifaDTO;
import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.service.HistorialTarifaService;
import backendProyectoParqueo.dto.HistorialDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/historial-tarifas")
@RequiredArgsConstructor
public class HistorialTarifaController {

    private final HistorialTarifaService historialTarifaService;

    @GetMapping
    public List<HistorialDTO> obtenerHistorialTarifas() {
        return historialTarifaService.obtenerHistorialTarifas();
    }

    // filtro
    @GetMapping("/filtrar")
    public List<HistorialDTO> obtenerHistorialTarifasFiltrado(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) TipoVehiculo tipoVehiculo,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) String nombreUsuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(required = false) BigDecimal montoMin,
            @RequestParam(required = false) BigDecimal montoMax) {
        return historialTarifaService.obtenerHistorialTarifasFiltrado(
                id,
                tipoVehiculo,
                tipoCliente,
                nombreUsuario,
                fechaInicio,
                fechaFin,
                montoMin,
                montoMax);
    }
}
