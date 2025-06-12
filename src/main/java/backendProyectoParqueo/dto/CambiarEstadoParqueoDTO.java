package backendProyectoParqueo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CambiarEstadoParqueoDTO {
    @NotNull(message = "El ID del parqueo no puede ser nulo")
    private Long parqueoId; 

    @NotBlank(message = "El motivo no puede estar vacío")
    @Size(min = 20, message = "El motivo debe tener al menos 20 caracteres")
    private String motivo;
}
