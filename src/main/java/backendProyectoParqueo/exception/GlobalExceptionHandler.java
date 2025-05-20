package backendProyectoParqueo.exception;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import backendProyectoParqueo.dto.ErrorDetail;
import backendProyectoParqueo.dto.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Recurso no encontrado
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        HttpStatusCode status = ex.getStatusCode();

        ErrorDetail error = new ErrorDetail(
                "The requested resource was not found.",
                null, // Puedes extraer campo si lo manejas en tu lógica
                ex.getReason() != null ? ex.getReason() : "No se pudo encontrar el recurso solicitado.");

        ErrorResponse response = new ErrorResponse(
                "error",
                status.value(),
                "El recurso solicitado no existe.",
                Collections.singletonList(error));

        return new ResponseEntity<>(response, status);
    }

    // Validación de formulario con @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<ErrorDetail> errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ErrorDetail(
                        fieldError.getDefaultMessage(),
                        fieldError.getField(),
                        "El valor proporcionado no es válido."))
                .collect(Collectors.toList());

        ErrorResponse response = new ErrorResponse(
                "error",
                HttpStatus.BAD_REQUEST.value(),
                "Errores de validación en el formulario.",
                errorDetails);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Manejador genérico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorDetail error = new ErrorDetail(
                "Error interno del servidor.",
                null,
                ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                "error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocurrió un error inesperado.",
                Collections.singletonList(error));

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
