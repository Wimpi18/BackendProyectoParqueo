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

    @Column(name="idTarifa", nullable=false)
    private int idTarifa;

    @Column(name="idParqueo", nullable=false)
    private int idParqueo;
    
    @Column(name = "idCajero", columnDefinition = "UUID")
    private UUID idCajero;

    @Column(name = "montoPagado", columnDefinition = "money", nullable=false)
    private double montoPagado;

    @Column(name = "fechaHoraPago", columnDefinition = "timestamp without time zone", nullable=false)
    private Timestamp fechaHoraPago;

    @Column(nullable=false)
    private Date[] meses;

    @Column(name="nroEspacioPagado", columnDefinition = "smallint")
    private int nroEspacioPagado;
}
