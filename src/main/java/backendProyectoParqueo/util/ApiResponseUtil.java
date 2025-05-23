package backendProyectoParqueo.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import backendProyectoParqueo.dto.ApiResponse;

public class ApiResponseUtil {

    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        return ResponseEntity.ok(
                new ApiResponse<>("success", HttpStatus.OK.value(), message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>("success", HttpStatus.CREATED.value(), message, data));
    }

    public static ResponseEntity<ApiResponse<Void>> successMessage(String message) {
        return ResponseEntity.ok(
                new ApiResponse<>("success", HttpStatus.OK.value(), message, null));
    }
}
