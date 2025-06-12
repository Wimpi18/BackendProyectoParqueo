package backendProyectoParqueo.model;


import java.time.LocalDateTime;

import backendProyectoParqueo.enums.TipoAccion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bitacora_estado")
public class BitacoraEstado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", referencedColumnName = "id") // FK al Usuario base
    private Usuario usuarioAfectado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_administrador", referencedColumnName = "id") // FK al Administrador que ejecuta
    private Administrador adminEjecutor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_accion", nullable = false)
    private TipoAccion tipoAccion;

    @Column(name = "motivo", length = 500, nullable = false)
    private String motivo;

    @Column(name = "fecha_hora_cambio", nullable = false)
    private LocalDateTime fechaHoraCambio;

    // Constructores, Getters, Setters
    public BitacoraEstado() {
        this.fechaHoraCambio = LocalDateTime.now();
    }
     public BitacoraEstado(Usuario usuarioAfectado, Administrador adminEjecutor, TipoAccion tipoAccion, String motivo) {
        this();
        this.usuarioAfectado = usuarioAfectado;
        this.adminEjecutor = adminEjecutor;
        this.tipoAccion = tipoAccion;
        this.motivo = motivo;
    }
}