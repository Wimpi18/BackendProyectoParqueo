package backendProyectoParqueo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import backendProyectoParqueo.dto.HistorialDTO;
import backendProyectoParqueo.dto.HistorialTarifaDTO;
import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.repository.HistorialRepository;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistorialTarifaService {

    private final HistorialRepository historialRepository;

    public List<HistorialDTO> obtenerHistorialTarifas() {
        return historialRepository.obtenerHistorialTarifas();
    }

    public List<HistorialDTO> obtenerHistorialTarifasFiltrado(
            Integer id,
            TipoVehiculo tipoVehiculo,
            String tipoCliente,
            String nombreUsuario,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            BigDecimal montoMin,
            BigDecimal montoMax) {
        return historialRepository.filtrarHistorialTarifas(
                id,
                tipoVehiculo,
                tipoCliente,
                nombreUsuario,
                fechaInicio,
                fechaFin,
                montoMin,
                montoMax);
    }

}
