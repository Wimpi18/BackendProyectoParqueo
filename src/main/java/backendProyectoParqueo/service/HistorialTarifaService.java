package backendProyectoParqueo.service;

import backendProyectoParqueo.dto.HistorialTarifaDTO;
import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.repository.HistorialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistorialTarifaService {

    private final HistorialRepository historialRepository;

    public List<HistorialTarifaDTO> obtenerHistorialTarifas() {
        return historialRepository.obtenerHistorialTarifas();
    }

    public List<HistorialTarifaDTO> obtenerHistorialTarifasFiltrado(
            TipoVehiculo tipoVehiculo,
            String tipoCliente,
            String nombreUsuario,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            BigDecimal montoMin,
            BigDecimal montoMax) {
        return historialRepository.filtrarHistorialTarifas(
                tipoVehiculo,
                tipoCliente,
                nombreUsuario,
                fechaInicio,
                fechaFin,
                montoMin,
                montoMax);
    }

}
