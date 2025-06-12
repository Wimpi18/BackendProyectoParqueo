package backendProyectoParqueo.model;

import java.util.UUID;

import org.hibernate.annotations.Check;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente")
// Revisar que el TipoCliente.java contenga los mismo valores en sus enums
@Check(constraints = "tipo IN ('Administrativo', 'Docente a dedicación exclusiva', 'Docente a tiempo horario')")
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

    public Cliente() {
    }

    public Cliente(String entidad, String tipo) {
        this.entidad = entidad;
        this.tipo = tipo;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Parqueo getParqueos() {
        return parqueo;
    }
}
