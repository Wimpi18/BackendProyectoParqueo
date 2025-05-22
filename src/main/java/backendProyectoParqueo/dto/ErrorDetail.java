package backendProyectoParqueo.dto;

public class ErrorDetail {
    private String message;
    private String field;
    private String details;

    public ErrorDetail(String message, String field, String details) {
        this.message = message;
        this.field = field;
        this.details = details;
    }

    // Getters y setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
