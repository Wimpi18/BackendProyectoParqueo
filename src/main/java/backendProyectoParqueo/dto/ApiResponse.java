package backendProyectoParqueo.dto;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private String status;
    private int statusCode;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(String message, T data, HttpStatus httpStatus) {
        return new ApiResponse<>("success", httpStatus.value(), message, data);
    }

    public static <T> ApiResponse<T> success(String message, HttpStatus httpStatus) {
        return new ApiResponse<>("success", httpStatus.value(), message, null);
    }
    
    public static <T> ApiResponse<T> error(String message, HttpStatus httpStatus) {
        return new ApiResponse<>("error", httpStatus.value(), message, null);
    }
}
