package backendProyectoParqueo.model;

import java.util.UUID;

import org.hibernate.annotations.Check;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cliente")
// Revisar que el TipoCliente.java contenga los mismo valores en sus enums
@Check(constraints = "tipo IN ('Administrativo', 'Docente a dedicación exclusiva', 'Docente a tiempo horario')")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Cliente {

    @Id
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Usuario usuario;

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private Parqueo parqueo;

    @Column(name = "entidad", nullable = true)
    private String entidad;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    public Cliente(String entidad, String tipo) {
        this.entidad = entidad;
        this.tipo = tipo;
    }
}
