package backendProyectoParqueo.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioDTO {
    private UUID id;
    private String ci;
    private String username;
    private String password;
    private String nombreCompleto;
    private String nroCelular;

    public UsuarioDTO(UUID id, String ci, String nombreCompleto) {
        this.id = id;
        this.ci = ci;
        this.nombreCompleto = nombreCompleto;
    }
}
