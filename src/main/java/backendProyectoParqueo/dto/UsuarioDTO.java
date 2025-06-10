package backendProyectoParqueo.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioDTO {
    private UUID id;

    @NotBlank(message = "El campo de la cédula de identidad es obligatorio")
    @Size(min = 4, max = 15, message = "La cédula debe tener entre 4 y 15 caracteres")
    private String ci;

    @NotBlank(message = "El campo del nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;

    @NotBlank(message = "El campo del apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String apellido;

    @NotBlank(message = "El campo del correo es obligatorio")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Debe ingresar un correo válido")
    private String correo;

    @NotBlank(message = "El campo del número de celular es obligatorio")
    @Pattern(regexp = "^[67][0-9]{7}$", message = "Debe ingresar un número de celular boliviano válido (8 dígitos que comienzan en 6 o 7)")
    private String nroCelular;

    @NotBlank(message = "El campo de la contraseña es obligatorio")
    @Size(min = 6, max = 20, message = "La contraseña debe tener entre 6 y 20 caracteres")
    @Pattern(regexp = "^(?=.*[A-Z]).+$", message = "La contraseña debe contener al menos una letra mayúscula")
    private String password;

    @NotNull(message = "La foto es obligatoria")
    private byte[] foto;

    // Se genera automáticamente
    private String username;

    public UsuarioDTO(UUID id, String ci, String nombre, String apellido) {
        this.id = id;
        this.ci = ci;
        this.nombre = nombre;
        this.apellido = apellido;
    }
}
