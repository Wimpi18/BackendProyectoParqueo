package backendProyectoParqueo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDetail {
    private String message;
    private String field;
    private String details;
}
