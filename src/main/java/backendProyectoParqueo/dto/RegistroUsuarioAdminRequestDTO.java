package backendProyectoParqueo.dto;

import backendProyectoParqueo.enums.RolAdmin;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistroUsuarioAdminRequestDTO {

    @Valid
    private UsuarioDTO usuario;

    @NotNull(message = "Debe especificar el rol")
    private RolAdmin rol;

    private Boolean esActivo = true;
}
