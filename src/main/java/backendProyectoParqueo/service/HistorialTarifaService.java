package backendProyectoParqueo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import backendProyectoParqueo.dto.HistorialDTO;
import backendProyectoParqueo.dto.HistorialTarifaDTO;
import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.repository.HistorialRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistorialTarifaService {

    private final HistorialRepository historialRepository;

    public List<HistorialDTO> obtenerHistorialTarifas() {
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
