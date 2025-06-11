package backendProyectoParqueo.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
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
    @JsonBackReference
    private Parqueo parqueo;

    @ManyToOne
    @JoinColumn(name = "id_cajero", columnDefinition = "UUID")
    private Cajero cajero;

    @Column(name = "monto_pagado", columnDefinition = "numeric", nullable = false)
    private BigDecimal montoPagado;

    @Column(name = "fecha_hora_pago", columnDefinition = "timestamp without time zone", nullable = false)
    private Timestamp fechaHoraPago = Timestamp.from(Instant.now());

    @Column(nullable = false)

    private LocalDate[] meses;

    @Column(name = "nro_espacio_pagado", columnDefinition = "smallint")
    private Short nroEspacioPagado;

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
