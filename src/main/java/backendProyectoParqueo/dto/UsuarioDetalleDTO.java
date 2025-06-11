package backendProyectoParqueo.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class UsuarioDetalleDTO {
    private String ci;
    private String nombre;
    private String apellido;
    private String correo;
    private String nroCelular;
    private String password;
    private String rolAsignado;
    private byte[] fotoUsuario;
    private String estadoParqueo;
    private List<VehiculoDTO> vehiculos;

}