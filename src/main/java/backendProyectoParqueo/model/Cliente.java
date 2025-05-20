package backendProyectoParqueo.model;

import java.util.UUID;

import org.hibernate.annotations.Check;

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
    @GeneratedValue
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Usuario usuario;

    @Column(name = "entidad", nullable = true)
    private String entidad;

    @Column(name = "foto", nullable = false)
    private byte[] foto;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    public Cliente() {
    }

    public Cliente(String entidad, byte[] foto, String tipo) {
        this.entidad = entidad;
        this.foto = foto;
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

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
