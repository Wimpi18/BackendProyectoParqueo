package backendProyectoParqueo.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

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

    @Column(name = "montoPagado", columnDefinition = "numeric", nullable = false)
    @Min(value = 1, message = "El monto a pagar debe ser mayor o igual a 1 Bs.")
    @Max(value = 1000, message = "El monto a pagar debe ser menor o igual a 1000 Bs.")
    private double montoPagado;

    @Column(name = "fechaHoraPago", columnDefinition = "timestamp without time zone", nullable = false)
    private Timestamp fechaHoraPago = Timestamp.from(Instant.now());

    @Column(nullable = false)
    private Date[] meses;

    @Column(name = "nroEspacioPagado", columnDefinition = "smallint")
    private int nroEspacioPagado;

    // Métodos de persistencia
    @PrePersist
    protected void onCreate() {
        if (this.fechaHoraPago == null) {
            ZoneId zonaBolivia = ZoneId.of("America/La_Paz");
            ZonedDateTime fechaHoraBolivia = ZonedDateTime.now(zonaBolivia);
            this.fechaHoraPago = Timestamp.valueOf(fechaHoraBolivia.toLocalDateTime());
        }
    }

    // Métodos getters y setters
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
