package backendProyectoParqueo.repository.custom;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import backendProyectoParqueo.dto.HistorialTarifaDTO;
import backendProyectoParqueo.enums.TipoVehiculo;

public interface HistorialRepositoryCustom {
    List<HistorialTarifaDTO> filtrarHistorialTarifas(
            TipoVehiculo tipoVehiculo,
            String tipoCliente,
            String nombreUsuario,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            BigDecimal montoMinimo,
            BigDecimal montoMaximo);
}
