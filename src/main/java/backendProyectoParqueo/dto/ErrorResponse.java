package backendProyectoParqueo.dto;

import java.util.List;

public class ErrorResponse {
    private String status;
    private int statusCode;
    private String message;
    private List<ErrorDetail> errors;

    public ErrorResponse(String status, int statusCode, String message, List<ErrorDetail> errors) {
        this.status = status;
        this.statusCode = statusCode;
        this.message = message;
        this.errors = errors;
    }

    // Getters y setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<ErrorDetail> getErrors() { return errors; }
    public void setErrors(List<ErrorDetail> errors) { this.errors = errors; }
}
