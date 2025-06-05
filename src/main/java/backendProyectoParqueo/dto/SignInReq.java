package backendProyectoParqueo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignInReq {
    @NotNull(message = "Debes colocar un username")
    private String username;

    @NotNull(message = "Debes colocar una contraseña")
    private String password;
}
