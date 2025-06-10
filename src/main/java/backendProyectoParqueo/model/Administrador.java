package backendProyectoParqueo.model;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

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

    
    @OneToMany(mappedBy = "administrador", fetch = FetchType.LAZY)
    private List<BitacoraEstado> bitacorasDeEstado = new ArrayList<>();

    @Column (name = "esActivo")
    private boolean esActivo;

    public List<BitacoraEstado> getBitacorasDeEstado() {
        return bitacorasDeEstado;
    }

}
