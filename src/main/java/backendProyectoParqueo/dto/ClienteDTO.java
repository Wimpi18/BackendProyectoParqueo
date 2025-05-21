package backendProyectoParqueo.dto;

import backendProyectoParqueo.model.TipoCliente;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
public class ClienteDTO {
    @NotBlank(message = "CI es obligatorio")
    private String ci;

    @NotBlank(message = "Nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "Apellido es obligatorio")
    private String apellido;

    @Email(message = "Correo debe ser válido")
    @NotBlank(message = "Correo es obligatorio")
    private String correo;

    @NotBlank(message = "Teléfono es obligatorio")
    private String telefono;

    @NotBlank(message = "Password es obligatorio")
    private String password;

    @NotBlank(message = "Entidad es obligatoria")
    private String entidad;

    @NotNull(message = "TipoCliente es obligatorio")
    private TipoCliente tipoCliente;

    @NotBlank(message = "Foto de usuario es obligatoria")
    private String fotoUsuarioBase64;
}
