package backendProyectoParqueo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClienteDTO extends UsuarioDTO {
    private String entidad;
    private byte[] foto;
    private String tipo;

    public ClienteDTO(String ci, String nombreCompleto, String tipo) {
        super(ci, nombreCompleto);
        this.tipo = tipo;
    }
}
