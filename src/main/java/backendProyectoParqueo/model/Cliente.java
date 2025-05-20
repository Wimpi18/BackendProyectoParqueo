package backendProyectoParqueo.model;

import java.util.UUID;

import backendProyectoParqueo.utils.TipoClienteConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "entidad", nullable = true)
    private String entidad;

    @Column(name = "foto", nullable = false)
    private byte[] foto;

    @Convert(converter = TipoClienteConverter.class)
    @Column(name = "tipo", nullable = false)
    private TipoCliente tipo;

    public Cliente() {
    }

    public Cliente(String entidad, byte[] foto, TipoCliente tipo) {
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

    public TipoCliente getTipo() {
        return tipo;
    }

    public void setTipo(TipoCliente tipo) {
        this.tipo = tipo;
    }
}
