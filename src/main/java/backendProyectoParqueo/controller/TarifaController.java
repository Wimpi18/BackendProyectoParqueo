package backendProyectoParqueo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backendProyectoParqueo.dto.ApiResponse;
import backendProyectoParqueo.dto.TarifaDTO;
import backendProyectoParqueo.model.Tarifa;
import backendProyectoParqueo.repository.TarifaRepository;
import backendProyectoParqueo.service.TarifaService;
import backendProyectoParqueo.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tarifa") // Endpoint base para las tarifas
@RequiredArgsConstructor
public class TarifaController {

    @Autowired
    private TarifaRepository tarifaRepository;

    private final TarifaService tarifaService;

    // Obtener todas las tarifas
    @GetMapping
    public ResponseEntity<List<Tarifa>> getAllTarifas() {
        List<Tarifa> tarifas = tarifaRepository.findAll();
        if (tarifas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tarifas);
    }

    @GetMapping("/vigente")
    public ResponseEntity<ApiResponse<Tarifa>> findTarifaVigente(@RequestBody TarifaDTO tarifaDTO) {
        String message = "Última tarifa vigente para " + tarifaDTO.getTipoCliente() + " y " + tarifaDTO.getTipoVehiculo();
        return ApiResponseUtil.success(message,
                tarifaService.findTarifaByTipoClienteYVehiculo(tarifaDTO.getTipoCliente(), tarifaDTO.getTipoVehiculo()));
    }

    // Obtener una tarifa por ID
    @GetMapping("/{id}")
    public ResponseEntity<Tarifa> getTarifaById(@PathVariable Integer id) {
        Optional<Tarifa> tarifaData = tarifaRepository.findById(id);
        if (tarifaData.isPresent()) {
            return ResponseEntity.ok(tarifaData.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Crear una nueva tarifa
    @PostMapping
    public ResponseEntity<Tarifa> createTarifa(@RequestBody Tarifa tarifa) {
        try {

            Tarifa nuevaTarifa = tarifaRepository.save(new Tarifa(
                    tarifa.getTipoVehiculo(),
                    tarifa.getTipoCliente(),
                    tarifa.getMonto(),
                    tarifa.getFechaInicio()));
            return new ResponseEntity<>(nuevaTarifa, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Actualizar una tarifa existente
    @PutMapping("/{id}")
    public ResponseEntity<Tarifa> updateTarifa(@PathVariable Integer id, @RequestBody Tarifa tarifaDetails) {
        Optional<Tarifa> tarifaData = tarifaRepository.findById(id);

        if (tarifaData.isPresent()) {
            Tarifa tarifaExistente = tarifaData.get();
            tarifaExistente.setTipoVehiculo(tarifaDetails.getTipoVehiculo());
            tarifaExistente.setTipoCliente(tarifaDetails.getTipoCliente());
            tarifaExistente.setMonto(tarifaDetails.getMonto());
            tarifaExistente.setFechaInicio(tarifaDetails.getFechaInicio());
            try {
                return new ResponseEntity<>(tarifaRepository.save(tarifaExistente), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // O un error más específico
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar una tarifa
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteTarifa(@PathVariable Integer id) {
        try {
            if (!tarifaRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            tarifaRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}