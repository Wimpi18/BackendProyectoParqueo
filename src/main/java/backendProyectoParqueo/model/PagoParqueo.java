package backendProyectoParqueo.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity()
@Table(name = "pago_parqueo")
public class PagoParqueo {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idTarifa", nullable = false)
    private int idTarifa;

    @Column(name = "idParqueo", nullable = false)
    private int idParqueo;

    @Column(name = "idCajero", columnDefinition = "UUID")
    private UUID idCajero;

    @Column(name = "montoPagado", columnDefinition = "money", nullable = false)
    private double montoPagado;

    @Column(name = "fechaHoraPago", columnDefinition = "timestamp without time zone", nullable = false)
    private Timestamp fechaHoraPago;

    @Column(nullable = false)
    private Date[] meses;

    @Column(name = "nroEspacioPagado", columnDefinition = "smallint")
    private int nroEspacioPagado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getIdTarifa() {
        return idTarifa;
    }

    public void setIdTarifa(int idTarifa) {
        this.idTarifa = idTarifa;
    }

    public int getIdParqueo() {
        return idParqueo;
    }

    public void setIdParqueo(int idParqueo) {
        this.idParqueo = idParqueo;
    }

    public UUID getIdCajero() {
        return idCajero;
    }

    public void setIdCajero(UUID idCajero) {
        this.idCajero = idCajero;
    }

    public double getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(double montoPagado) {
        this.montoPagado = montoPagado;
    }

    public Timestamp getFechaHoraPago() {
        return fechaHoraPago;
    }

    public void setFechaHoraPago(Timestamp fechaHoraPago) {
        this.fechaHoraPago = fechaHoraPago;
    }

    public Date[] getMeses() {
        return meses;
    }

    public void setMeses(Date[] meses) {
        this.meses = meses;
    }

    public int getNroEspacioPagado() {
        return nroEspacioPagado;
    }

    public void setNroEspacioPagado(int nroEspacioPagado) {
        this.nroEspacioPagado = nroEspacioPagado;
    }
}
