package backendProyectoParqueo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backendProyectoParqueo.model.PagoParqueo;
import backendProyectoParqueo.repository.PagoParqueoRepository;

@RestController
@RequestMapping("/api/pago-parqueo")
public class PagoParqueoController {

    @Autowired
    private PagoParqueoRepository pagoParqueoRepository;

    @GetMapping
    public List<PagoParqueo> getAllPagoParqueos() {
        return pagoParqueoRepository.findAll();
    }

    @PostMapping("/crear")
    public PagoParqueo createPagoParqueo(@RequestBody PagoParqueo pagoParqueo) {
        return pagoParqueoRepository.save(pagoParqueo);
    }
}
