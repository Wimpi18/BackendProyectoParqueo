package backendProyectoParqueo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MotivoEstadoParqueoDTO {
    @NotBlank
    @Size(min = 20, message = "El motivo debe tener al menos 20 caracteres")
    private String motivo;
}
