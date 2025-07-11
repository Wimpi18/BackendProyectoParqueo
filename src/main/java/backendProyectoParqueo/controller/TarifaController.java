package backendProyectoParqueo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backendProyectoParqueo.dto.ApiResponse;
import backendProyectoParqueo.dto.TarifaDTO;
import backendProyectoParqueo.dto.TarifaCreateDTO;
import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.model.Tarifa;
import backendProyectoParqueo.service.TarifaService;
import backendProyectoParqueo.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tarifa")
@RequiredArgsConstructor
public class TarifaController {
    private final TarifaService tarifaService;

    // obtener todas las tarifas
    @GetMapping("/vigentes")
    public ResponseEntity<List<TarifaDTO>> obtenerTarifasVigentes() {
        List<TarifaDTO> tarifas = tarifaService.listarTarifasVigentes();
        return ResponseEntity.ok(tarifas);
    }

    @GetMapping("/vigente")
    public ResponseEntity<ApiResponse<Tarifa>> findTarifaVigente(
            @RequestParam(required = true) String tipoCliente,
            @RequestParam(required = true) TipoVehiculo tipoVehiculo) {
        String message = "Última tarifa vigente para " + tipoCliente + " y "
                + tipoVehiculo;
        return ApiResponseUtil.success(message,
                tarifaService.findTarifaByTipoClienteYVehiculo(tipoCliente,
                        tipoVehiculo));
    }

    // Crear una nueva tarifa
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    public ResponseEntity<TarifaDTO> crearTarifa(@RequestBody TarifaCreateDTO dto) {
        TarifaDTO tarifa = tarifaService.crearTarifa(dto);
        return ResponseEntity.ok(tarifa);
    }

}