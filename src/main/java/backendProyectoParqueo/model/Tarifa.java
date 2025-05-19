package backendProyectoParqueo.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tarifa", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tipovehiculo", "tipocliente", "fechainicio"})
})
public class Tarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Enumerated(EnumType.STRING) 
    @Column(name = "tipovehiculo", nullable = false)
    private TipoVehiculo tipoVehiculo; 

    @Enumerated(EnumType.STRING) 
    @Column(name = "tipocliente", nullable = false)
    private TipoCliente tipoCliente; 

    @Column(name = "monto", nullable = false) 
    private BigDecimal monto;

    @Column(name = "fechainicio", nullable = false)
    private LocalDate fechaInicio;

    // Constructores
    public Tarifa() {
    }

    public Tarifa(TipoVehiculo tipoVehiculo, TipoCliente tipoCliente, BigDecimal monto, LocalDate fechaInicio) {
        this.tipoVehiculo = tipoVehiculo;
        this.tipoCliente = tipoCliente;
        this.monto = monto;
        this.fechaInicio = fechaInicio;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(TipoVehiculo tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public TipoCliente getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(TipoCliente tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    @Override
    public String toString() {
        return "Tarifa{" +
                "id=" + id +
                ", tipoVehiculo=" + tipoVehiculo +
                ", tipoCliente=" + tipoCliente +
                ", monto=" + monto +
                ", fechaInicio=" + fechaInicio +
                '}';
    }
}