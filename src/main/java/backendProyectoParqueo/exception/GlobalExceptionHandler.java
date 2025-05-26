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

@ControllerAdvice
public class GlobalExceptionHandler {

        // Recurso no encontrado
        @ExceptionHandler(ResponseStatusException.class)
        public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex,
                        WebRequest request) {
                HttpStatusCode status = ex.getStatusCode();

                ErrorDetail error = new ErrorDetail(
                                "No se pudo encontrar el recurso solicitado.",
                                null, // Puedes extraer campo si lo manejas en tu lógica
                                ex.getReason() != null ? ex.getReason()
                                                : "No se pudo encontrar el recurso solicitado.");

                ErrorResponse response = new ErrorResponse(
                                "error",
                                status.value(),
                                "El recurso solicitado no existe.",
                                Collections.singletonList(error));

                return new ResponseEntity<>(response, status);
        }

        // Error de lógica de negocio
        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
                ErrorDetail error = new ErrorDetail(
                                ex.getMessage(),
                                ex.getField(),
                                "Violación de reglas de negocio.");

                ErrorResponse response = new ErrorResponse(
                                "error",
                                HttpStatus.BAD_REQUEST.value(),
                                "Se produjo un error de lógica de negocio.",
                                Collections.singletonList(error));

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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

        // Manejador genérico para errores del servidor
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleException(Exception ex) {
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
