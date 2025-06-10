package backendProyectoParqueo.model;


import backendProyectoParqueo.enums.TipoAccion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
  @GeneratedValue (strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, updatable = false)
  private Long id;
  
  @ManyToOne
  @JoinColumn(name = "idUsuario", referencedColumnName = "id", nullable = true)
  private Usuario idUsuario;


  @ManyToOne
  @JoinColumn(name = "idAdministrador" , referencedColumnName = "id", nullable = true)
  private Administrador idAdministrador;

  @Column (name = "tipo_accion")
  private TipoAccion tipo;

  @Column (name = "motivo")
  private String motivo;

}
