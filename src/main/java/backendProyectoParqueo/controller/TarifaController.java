package backendProyectoParqueo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backendProyectoParqueo.dto.TarifaDTO;

import backendProyectoParqueo.service.TarifaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tarifa")
@RequiredArgsConstructor
public class TarifaController {
    private final TarifaService tarifaService;

    // obtener todas las tarifas
    @GetMapping
    public ResponseEntity<List<TarifaDTO>> listarTarifas() {
        List<TarifaDTO> tarifas = tarifaService.listarTarifas();
        return ResponseEntity.ok(tarifas);
    }

    // Crear una nueva tarifa
    @PostMapping
    public ResponseEntity<TarifaDTO> crearTarifa(@RequestBody TarifaDTO dto) {
        TarifaDTO nuevaTarifa = tarifaService.crearTarifa(dto);
        return ResponseEntity.ok(nuevaTarifa);
    }
}