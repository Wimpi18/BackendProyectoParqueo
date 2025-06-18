package backendProyectoParqueo.dto;

import java.util.UUID;
import java.util.List;
import java.util.Set;

import backendProyectoParqueo.enums.RoleEnum;
import backendProyectoParqueo.model.Parqueo.EstadoParqueo;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
    private EstadoParqueo estadoParqueo; // solo aplica si es CLIENTE
    private String estaActivo; // <-- nuevo campo para admins y cajeros
    private Integer cantidadMesesDeuda; // solo aplica si es CLIENTE
}
