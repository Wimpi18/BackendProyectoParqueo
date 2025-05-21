package backendProyectoParqueo.model;

import java.util.UUID;

import backendProyectoParqueo.util.TipoClienteConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "cliente")
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

    @Convert(converter = TipoClienteConverter.class)
    @Column(name = "tipo", nullable = false)
    private TipoCliente tipo;

}
