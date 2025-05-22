// src/main/java/backendProyectoParqueo/dto/ReporteEstadoCuentaVehiculoDTO.java
package backendProyectoParqueo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class ReporteEstadoCuentaVehiculoDTO {
    private String placaVehiculo;
    private List<DetalleMesEstadoCuentaDTO> detallesMes;
    private BigDecimal saldoTotalPendiente;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ultimaActualizacion;
    private String tipoCliente; // Añadido para obtener la tarifa
    private backendProyectoParqueo.enums.TipoVehiculo tipoVehiculo; // Añadido para obtener la tarifa
}