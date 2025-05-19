package backendProyectoParqueo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backendProyectoParqueo.model.PagoParqueo;
import backendProyectoParqueo.service.PagoParqueoService;

@RestController
@RequestMapping("/api/pago-parqueo")
public class PagoParqueoController {

    private final PagoParqueoService pagoParqueoService;

    @Autowired
    public PagoParqueoController(PagoParqueoService pagoParqueoService) {
        this.pagoParqueoService = pagoParqueoService;
    }

    @GetMapping
    public List<PagoParqueo> getAllPagoParqueos() {
        return pagoParqueoService.findAll();
    }

    @PostMapping("/crear")
    public int createPagoParqueo(@RequestBody PagoParqueo pagoParqueo) {
        return 0;
    }
}
