package backendProyectoParqueo.dto;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;

@JsonInclude(Include.NON_NULL)
@Data
@AllArgsConstructor
public class AllUsuarioDTO {
    private UUID id;
    private String nombre;
    private String apellido;
    private String ci; // <-- nuevo campo
    private byte[] foto;

    private List<String> roles; // Ej: ["Cliente", "Cajero"]
    private String tipoCliente; // solo si rol incluye "Cliente"
    private String estado; // <-- nuevo campo para admins y cajeros
    private Integer cantidadMesesDeuda; // solo aplica si es CLIENTE
}
