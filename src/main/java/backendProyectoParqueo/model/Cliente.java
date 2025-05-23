package backendProyectoParqueo.model;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

@Data
@NoArgsConstructor
@Entity
@Table(name = "cliente")
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

    @OneToMany(mappedBy = "cliente")
    private List<Parqueo> parqueos;

    @Column(name = "entidad", nullable = true)
    private String entidad;

    @Column(name = "foto", nullable = false)
    private byte[] foto;

    @Column(name = "tipo", nullable = false)
    private String tipo;

  
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

    public List<Parqueo> getParqueos() {
        return parqueos;
    }

    public void setParqueos(List<Parqueo> parqueos) {
        this.parqueos = parqueos;
    }
}
