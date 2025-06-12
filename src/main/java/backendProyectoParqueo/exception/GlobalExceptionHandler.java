package backendProyectoParqueo.exception;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
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
                String path = request.getDescription(false); // format: "uri=/favicon.ico"
                if (path != null && path.contains("/favicon.ico")) {
                        // Ignorar o devolver respuesta distinta si es recurso estático
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }

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
                                "Regla de negocio no cumplida");

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

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
                ErrorDetail error = new ErrorDetail(
                                "No autorizado",
                                "credentials",
                                ex.getMessage());

                ErrorResponse response = new ErrorResponse(
                                "error",
                                HttpStatus.UNAUTHORIZED.value(),
                                "Fallo de autenticación",
                                Collections.singletonList(error));

                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
                ErrorDetail error = new ErrorDetail(
                                "Acceso denegado",
                                null,
                                "Acceso no autorizado al recurso solicitado");

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(new ErrorResponse(
                                                "error",
                                                HttpStatus.FORBIDDEN.value(),
                                                "Operación no permitida",
                                                Collections.singletonList(error)));
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
                ErrorDetail error = new ErrorDetail(
                                ex.getMessage(),
                                null,
                                "Error en los datos enviados.");

                ErrorResponse response = new ErrorResponse(
                                "error",
                                HttpStatus.BAD_REQUEST.value(),
                                "Error de validación de datos.",
                                Collections.singletonList(error));

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(TarifaDuplicadaException.class)
        public ResponseEntity<ErrorResponse> handleTarifaDuplicada(TarifaDuplicadaException ex) {
                ErrorDetail error = new ErrorDetail(
                                ex.getMessage(), // Mensaje principal
                                null, // Campo relacionado (null si no aplica)
                                "La tarifa ingresada ya existe y no se puede duplicar." // Detalle explicativo
                );

                ErrorResponse response = new ErrorResponse(
                                "error", // Puedes quitar esto si no lo quieres
                                HttpStatus.CONFLICT.value(), // 409
                                "Conflicto al registrar tarifa.",
                                Collections.singletonList(error));

                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
}
