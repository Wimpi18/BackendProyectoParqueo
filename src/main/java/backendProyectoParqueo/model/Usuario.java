package backendProyectoParqueo.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "ci", nullable = false, unique = true)
    private String ci;

    // Se guardan los nombres
    @Column(name = "nombre", nullable = false)
    private String nombre;

    // Se guardan los apellidos
    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "correo", nullable = false, unique = true)
    private String correo;

    @Column(name = "nro_celular", nullable = false)
    private String nroCelular;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "username", nullable = true, unique = true)
    private String username;
}
