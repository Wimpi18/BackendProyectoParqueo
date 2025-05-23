// src/main/java/backendProyectoParqueo/dto/DetalleMesEstadoCuentaDTO.java
package backendProyectoParqueo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetalleMesEstadoCuentaDTO {
    private String periodo; // "MM/yyyy"
    private String estado; // "Pagado", "Pendiente"
    private BigDecimal monto;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaPago; // Solo para "Pagado"
    // Podrías añadir fechaVencimiento para pendientes si es necesario
}