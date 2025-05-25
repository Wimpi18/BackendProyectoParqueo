package backendProyectoParqueo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backendProyectoParqueo.dto.HistorialTarifaDTO;
import backendProyectoParqueo.enums.TipoCliente;
import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.service.HistorialTarifaService;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/historialTarifa")
@RequiredArgsConstructor

public class HistorialTarifaController {

    @Autowired
    private HistorialTarifaService historialTarifaService;

    @GetMapping
    public ResponseEntity<List<HistorialTarifaDTO>> obtenerHistorialTarifas() {
        List<HistorialTarifaDTO> historial = historialTarifaService.obtenerHistorialTarifas();
        return ResponseEntity.ok(historial);
    }
}