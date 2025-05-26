package backendProyectoParqueo.repository.custom;

import backendProyectoParqueo.dto.HistorialTarifaDTO;
import backendProyectoParqueo.enums.TipoCliente;
import backendProyectoParqueo.enums.TipoVehiculo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface HistorialRepositoryCustom {
    List<HistorialTarifaDTO> filtrarHistorialTarifas(
            TipoVehiculo tipoVehiculo,
            TipoCliente tipoCliente,
            String nombreUsuario,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            BigDecimal montoMinimo,
            BigDecimal montoMaximo);
}
