package backendProyectoParqueo.dto;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import backendProyectoParqueo.validation.ValidMeses;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagoParqueoDTO {
    private Long id;
    private Integer idTarifa;
    private Long idParqueo;
    private UUID idCajero;

    @Min(value = 1, message = "El monto a pagar debe ser mayor o igual a 1 Bs.")
    @Max(value = 1000, message = "El monto a pagar debe ser menor o igual a 1000 Bs.")
    private double montoPagado;

    private Timestamp fechaHoraPago;

    @ValidMeses(message = "Debe ingresar al menos un mes válido o secuencia continua de meses válido")
    private LocalDate[] meses;

    private int nroEspacioPagado;
}
