package backendProyectoParqueo.dto;

import java.math.BigDecimal;

import backendProyectoParqueo.enums.TipoVehiculo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TarifaCreateDTO {
    private TipoVehiculo tipoVehiculo;
    private String tipoCliente;
    private BigDecimal monto;
}
