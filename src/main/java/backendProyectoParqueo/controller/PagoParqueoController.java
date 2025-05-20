package backendProyectoParqueo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backendProyectoParqueo.dto.ApiResponse;
import backendProyectoParqueo.model.PagoParqueo;
import backendProyectoParqueo.service.PagoParqueoService;
import backendProyectoParqueo.util.ApiResponseUtil;

@RestController
@RequestMapping("/api/pago-parqueo")
public class PagoParqueoController {

    private final PagoParqueoService pagoParqueoService;

    @Autowired
    public PagoParqueoController(PagoParqueoService pagoParqueoService) {
        this.pagoParqueoService = pagoParqueoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PagoParqueo>>> getAllPagoParqueos() {
        return ApiResponseUtil.success("Todos los pagos realizados al parqueo", pagoParqueoService.findAll());
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<PagoParqueo>> createPagoParqueo(@RequestBody PagoParqueo pagoParqueo) {
        return ApiResponseUtil.success("El pago del parqueo fue realizado correctamente",
                pagoParqueoService.create(pagoParqueo));
    }
}
