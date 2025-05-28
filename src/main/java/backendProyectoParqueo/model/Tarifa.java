package backendProyectoParqueo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import backendProyectoParqueo.enums.TipoVehiculo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "tarifa")
public class Tarifa {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_administrador", nullable = false)
    private Administrador administrador;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vehiculo", nullable = false)
    private TipoVehiculo tipoVehiculo;

    @Column(name = "tipo_cliente", nullable = false)
    private String tipoCliente;

    @Column(name = "monto", nullable = false)
    private BigDecimal monto;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    // Constructores
    @PrePersist
    protected void onCreate() {
        if (this.fechaInicio == null) {
            ZoneId zonaBolivia = ZoneId.of("America/La_Paz");
            ZonedDateTime fechaHoraBolivia = ZonedDateTime.now(zonaBolivia);
            this.fechaInicio = fechaHoraBolivia.toLocalDateTime();
        }
    }
}
