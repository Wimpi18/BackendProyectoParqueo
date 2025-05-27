package backendProyectoParqueo.exception;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String status;
    private int statusCode;
    private String message;
    private List<ErrorDetail> errors;
}
