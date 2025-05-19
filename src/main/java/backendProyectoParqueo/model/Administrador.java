package backendProyectoParqueo.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "administrador")
public class Administrador {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Usuario usuario;

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
}
