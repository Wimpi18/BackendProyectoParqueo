package backendProyectoParqueo.dto;

import java.util.UUID;

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
public class AccionConMotivoOpcionalRequestDT {
    @NotNull(message = "El ID del usuario no puede ser nulo")
    private UUID usuarioId;

    @Size(max = 500, message = "El motivo no puede exceder los 500 caracteres") 
    private String motivo; 
}
