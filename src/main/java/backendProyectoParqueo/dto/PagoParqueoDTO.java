package backendProyectoParqueo.dto;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

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
    private double montoPagado;
    private Timestamp fechaHoraPago;
    private Date[] meses;
    private int nroEspacioPagado;
}
