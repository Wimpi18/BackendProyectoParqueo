package backendProyectoParqueo.repository.custom;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import backendProyectoParqueo.dto.HistorialDTO;
import backendProyectoParqueo.enums.TipoVehiculo;

public interface HistorialRepositoryCustom {
    List<HistorialDTO> filtrarHistorialTarifas(
            Integer id,
            TipoVehiculo tipoVehiculo,
            String tipoCliente,
            String nombreUsuario,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            BigDecimal montoMinimo,
            BigDecimal montoMaximo);
}
