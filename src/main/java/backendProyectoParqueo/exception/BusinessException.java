package backendProyectoParqueo.exception;

public class BusinessException extends RuntimeException {
    String field;

    public BusinessException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
