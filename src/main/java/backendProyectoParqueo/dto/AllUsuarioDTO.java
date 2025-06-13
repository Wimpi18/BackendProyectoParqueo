package backendProyectoParqueo.dto;

import java.util.UUID;

import backendProyectoParqueo.model.Parqueo.EstadoParqueo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AllUsuarioDTO {
    private UUID id;
    private String nombre;
    private String apellido;
    private byte[] foto;
    private String tipoCliente;
    private EstadoParqueo estadoParqueo;
}
