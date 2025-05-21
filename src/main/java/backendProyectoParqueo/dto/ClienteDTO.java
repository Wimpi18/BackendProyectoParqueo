package backendProyectoParqueo.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClienteDTO extends UsuarioDTO {
    private String entidad;
    private byte[] foto;
    private String tipo;

    public ClienteDTO(UUID id, String ci, String nombreCompleto, String tipo) {
        super(id, ci, nombreCompleto);
        this.tipo = tipo;
    }
}
