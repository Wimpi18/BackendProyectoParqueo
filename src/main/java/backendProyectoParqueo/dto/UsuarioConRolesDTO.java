package backendProyectoParqueo.dto;

import java.util.UUID;

import backendProyectoParqueo.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioConRolesDTO {
    private UUID id;
    private String ci;
    private String nombre;
    private String apellido;
    private String correo;
    private String nroCelular;
    private String password;
    private String username;
    private RoleEnum[] roles;
}