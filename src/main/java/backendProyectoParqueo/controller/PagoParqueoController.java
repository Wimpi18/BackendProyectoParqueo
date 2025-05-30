package backendProyectoParqueo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backendProyectoParqueo.dto.ApiResponse;
import backendProyectoParqueo.dto.PagoParqueoDTO;
import backendProyectoParqueo.model.PagoParqueo;
import backendProyectoParqueo.service.PagoParqueoService;
import backendProyectoParqueo.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("pago-parqueo")
@RequiredArgsConstructor
public class PagoParqueoController {

    private final PagoParqueoService pagoParqueoService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PagoParqueo>>> getAllPagoParqueos() {
        return ApiResponseUtil.success("Todos los pagos realizados al parqueo", pagoParqueoService.findAll());
    }

    @PostMapping("/fecha-correspondiente-pago-parqueo")
    public ResponseEntity<ApiResponse<Object>> fechaCorrespondienteDePagoParqueo(
            @RequestBody PagoParqueoDTO pagoParqueoDTO) {
        return ApiResponseUtil.success("Fecha inicial correspondiente para realizar el pago del parqueo",
                pagoParqueoService.getFechaCorrespondienteDePagoParqueo(pagoParqueoDTO.getIdCajero(),
                        pagoParqueoDTO.getIdParqueo()));
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<PagoParqueo>> createPagoParqueo(@RequestBody @Valid PagoParqueoDTO pagoParqueo) {
        return ApiResponseUtil.success("El pago del parqueo fue realizado correctamente",
                pagoParqueoService.create(pagoParqueo));
    }
}
