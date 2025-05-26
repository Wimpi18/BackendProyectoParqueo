package backendProyectoParqueo.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import backendProyectoParqueo.validation.ValidMeses;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Entity
@Data
@Table(name = "pago_parqueo")
public class PagoParqueo {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_tarifa", nullable = false)
    private Tarifa tarifa;

    @ManyToOne
    @JoinColumn(name = "id_parqueo", nullable = false)
    private Parqueo parqueo;

    @Column(name = "id_cajero", columnDefinition = "UUID")
    private UUID idCajero;

    @Column(name = "monto_pagado", columnDefinition = "numeric", nullable = false)
    @Min(value = 1, message = "El monto a pagar debe ser mayor o igual a 1 Bs.")
    @Max(value = 1000, message = "El monto a pagar debe ser menor o igual a 1000 Bs.")
    private double montoPagado;

    @Column(name = "fecha_hora_pago", columnDefinition = "timestamp without time zone", nullable = false)
    private Timestamp fechaHoraPago = Timestamp.from(Instant.now());

    @Column(nullable = false)
    @ValidMeses(message = "Debe ingresar al menos un mes válido")
    private Date[] meses;

    @Column(name = "nro_espacio_pagado", columnDefinition = "smallint")
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
}
