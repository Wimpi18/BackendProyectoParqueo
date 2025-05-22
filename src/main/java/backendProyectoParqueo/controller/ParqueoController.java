package backendProyectoParqueo.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backendProyectoParqueo.model.Parqueo;
import backendProyectoParqueo.service.ParqueoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("parqueo")
@RequiredArgsConstructor
public class ParqueoController {

    private final ParqueoService parqueoService;

    @GetMapping
    public ResponseEntity<List<Parqueo>> listarParqueos() {
        return ResponseEntity.ok(parqueoService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<?> crearParqueo(@RequestBody Parqueo parqueo) {
        try {
            Parqueo nuevo = parqueoService.crearParqueo(parqueo);
            return ResponseEntity.ok(nuevo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body("Ya existe un parqueo para este cliente y vehículo.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarParqueo(@PathVariable Long id) {
        try {
            parqueoService.eliminarParqueo(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

}