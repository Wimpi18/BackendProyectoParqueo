package backendProyectoParqueo.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.Check;

import com.fasterxml.jackson.annotation.JsonIgnore;

import backendProyectoParqueo.enums.TipoVehiculo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "tarifa", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "tipo_vehiculo", "tipo_cliente", "fecha_inicio" }) })
// Revisar que el TipoCliente.java contenga los mismo valores en sus enums
@Check(constraints = "tipo_cliente IN ('Administrativo', 'Docente a dedicación exclusiva', 'Docente a tiempo horario')")
public class Tarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "tarifa")
    @JsonIgnore
    private List<PagoParqueo> pagoParqueos;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vehiculo", nullable = false)
    private TipoVehiculo tipoVehiculo;

    @Column(name = "tipo_cliente", nullable = false)
    private String tipoCliente;

    @Column(name = "monto", nullable = false)
    private BigDecimal monto;

    @Column(name = "fecha_inicio", nullable = false)

    private LocalDate fechaInicio;

    // Constructores
    public Tarifa() {
    }

    public Tarifa(TipoVehiculo tipoVehiculo, String tipoCliente, BigDecimal monto, LocalDate fechaInicio) {
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

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
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

    public List<PagoParqueo> getPagoParqueos() {
        return pagoParqueos;
    }

    public void setPagoParqueos(List<PagoParqueo> pagoParqueos) {
        this.pagoParqueos = pagoParqueos;
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