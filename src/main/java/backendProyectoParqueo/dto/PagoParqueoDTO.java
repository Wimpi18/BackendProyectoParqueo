package backendProyectoParqueo.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import backendProyectoParqueo.validation.ValidCliente;
import backendProyectoParqueo.validation.ValidMeses;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ValidCliente
public class PagoParqueoDTO {
    private Long id;

    @NotNull(message = "Debe proporcionar un id de usuario cliente.")
    private UUID idCliente;

    @NotNull(message = "Debe proporcionar un parqueo válido.")
    private Long idParqueo;

    private UUID idCajero;

    @Min(value = 1, message = "El monto a pagar debe ser mayor o igual a 1 Bs.")
    @Max(value = 2000, message = "El monto a pagar debe ser menor o igual a 2000 Bs.")
    private BigDecimal montoPagado;

    private Timestamp fechaHoraPago;

    @ValidMeses(message = "Debe ingresar secuencia continua de meses válidos.")
    @NotNull(message = "Debe especificar al menos un mes.")
    @Size(min = 1, message = "Debe pagar al menos un mes.")
    private LocalDate[] meses;
}
