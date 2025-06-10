package backendProyectoParqueo.model;

import org.hibernate.validator.constraints.UUID;

import backendProyectoParqueo.enums.TipoAccion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bitacora_estado")
public class BitacoraEstado {
  @Id
  @GeneratedValue
  @Column(name = "id", columnDefinition = "uuid", unique = true, updatable = false)
  private Long id;
  
  @ManyToOne
  @Column (name = "idUsuario", columnDefinition = "uuid", updatable = false)
  private UUID idUsuario;


  @ManyToOne
  @Column (name = "idAdministrador", columnDefinition = "uuid",updatable = false)
  private UUID idAdministrador;

  @Column (name = "tipo_accion")
  private TipoAccion tipo;

  @Column (name = "motivo")
  private String motivo;

}
