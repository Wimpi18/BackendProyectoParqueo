package backendProyectoParqueo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import backendProyectoParqueo.enums.TipoVehiculo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class HistorialTarifaDTO {
    private TipoVehiculo tipoVehiculo;
    private String tipoCliente;
    private BigDecimal monto;
    private String nombreCompleto;
    private LocalDateTime fechaInicio;
}