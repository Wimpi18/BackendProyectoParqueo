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
    private String nombre;
    private String apellido;
    private String correo;
    private String nroCelular;
    private String password;
    private String username;

    public UsuarioDTO(UUID id, String ci, String nombre, String apellido) {
        this.id = id;
        this.ci = ci;
        this.nombre = nombre;
        this.apellido = apellido;
    }
}
