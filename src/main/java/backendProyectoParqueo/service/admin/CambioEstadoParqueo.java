package backendProyectoParqueo.service.admin;


import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import backendProyectoParqueo.model.Parqueo.EstadoParqueo;
import backendProyectoParqueo.enums.TipoAccion;
import backendProyectoParqueo.model.Administrador;
import backendProyectoParqueo.model.BitacoraEstado;
import backendProyectoParqueo.model.Cliente;
import backendProyectoParqueo.model.Parqueo;
import backendProyectoParqueo.model.Usuario;
import backendProyectoParqueo.repository.AdministradorRepository;
import backendProyectoParqueo.repository.BitacoraEstadoRepository;
import backendProyectoParqueo.repository.ClienteRepository;
import backendProyectoParqueo.repository.ParqueoRepository;
import jakarta.transaction.Transactional;

@Service
public class CambioEstadoParqueo {
  private final ParqueoRepository parqueoRepository;
  private final AdministradorRepository administradorRepository;
  private final BitacoraEstadoRepository bitacoraEstadoRepository;
  private final ClienteRepository clienteRepository;

  @Autowired
  public CambioEstadoParqueo(ParqueoRepository parqueoRepository,
      AdministradorRepository administradorRepository,
      BitacoraEstadoRepository bitacoraEstadoRepository,
      ClienteRepository clienteRepository) { 
    this.parqueoRepository = parqueoRepository;
    this.administradorRepository = administradorRepository;
    this.bitacoraEstadoRepository = bitacoraEstadoRepository;
    this.clienteRepository = clienteRepository;
  }


  @Transactional
    public Parqueo cambiarEstadoParqueoPorUsuarioId(UUID usuarioIdCliente, EstadoParqueo nuevoEstado, String motivo, UUID adminQueEjecutaId) {

      Administrador adminEjecutor = administradorRepository.findById(adminQueEjecutaId)
              .orElseThrow(() -> new RuntimeException("Administrador ejecutor con ID " + adminQueEjecutaId + " no encontrado.")); 

      Cliente clienteAfectado = clienteRepository.findById(usuarioIdCliente)
              .orElseThrow(() -> new RuntimeException("Cliente con Usuario ID " + usuarioIdCliente + " no encontrado."));
      
      Parqueo parqueo = parqueoRepository.findByCliente_Id(usuarioIdCliente) 
              .orElseThrow(() -> new RuntimeException("No se encontró un Parqueo asociado al Cliente con Usuario ID " + usuarioIdCliente));

      Usuario usuarioDelClienteAfectado = clienteAfectado.getUsuario();
      if (usuarioDelClienteAfectado == null) { // Esto no debería pasar con @MapsId
          throw new IllegalStateException("Error de integridad: Cliente ID " + clienteAfectado.getId() + " no tiene un usuario base.");
      }

      if (adminEjecutor.getUsuario().getId().equals(usuarioDelClienteAfectado.getId())) {
          throw new RuntimeException("Un administrador no puede modificar el estado de un parqueo que le pertenece como cliente.");
      }

      if (parqueo.getEstado() == nuevoEstado) {
          throw new RuntimeException("El parqueo ya se encuentra en el estado " + nuevoEstado + ". No se realizaron cambios.");
      }
      EstadoParqueo estadoAnterior = parqueo.getEstado();
      parqueo.setEstado(nuevoEstado);

      if (nuevoEstado == EstadoParqueo.Inactivo) {
          parqueo.setNroEspacio(null);
      }
      
      Parqueo parqueoGuardado = parqueoRepository.save(parqueo);
      TipoAccion tipoAccionEnBitacora = determinarTipoAccionParaCambioEstadoParqueo(nuevoEstado, estadoAnterior);
      BitacoraEstado bitacora = new BitacoraEstado(
              usuarioDelClienteAfectado, 
              adminEjecutor,             
              tipoAccionEnBitacora,
              motivo
      );
      
      bitacoraEstadoRepository.save(bitacora);

      return parqueoGuardado; 
  }

    private TipoAccion determinarTipoAccionParaCambioEstadoParqueo(EstadoParqueo nuevoEstado, EstadoParqueo estadoAnterior) {
        switch (nuevoEstado) {
            case Bloqueado:
                return TipoAccion.BLOQUEO_CLIENTE_POR_PARQUEO;
            case Inactivo:
                return TipoAccion.INACTIVACION_CLIENTE_POR_PARQUEO;
            case Activo:
                if (estadoAnterior == EstadoParqueo.Bloqueado) {
                    return TipoAccion.ACTIVACION_CLIENTE_POR_PARQUEO; // Corregido de ACTIVACION a DESBLOQUEO si viene de Bloqueado
                } else if (estadoAnterior == EstadoParqueo.Inactivo) {
                    return TipoAccion.ACTIVACION_CLIENTE_POR_PARQUEO;
                } else {
                    throw new IllegalStateException("Transición inesperada al estado Activo desde: " + estadoAnterior);
                }
            default:
                throw new IllegalArgumentException("Estado de parqueo nuevo no reconocido: " + nuevoEstado);
        }
    }

     public Parqueo bloquearCliente(UUID usuarioIdCliente, String motivo, UUID adminQueEjecutaId) {
        return cambiarEstadoParqueoPorUsuarioId(usuarioIdCliente, EstadoParqueo.Bloqueado, motivo, adminQueEjecutaId);
    }

    public Parqueo inactivarCliente(UUID usuarioIdCliente, String motivo, UUID adminQueEjecutaId) {
        return cambiarEstadoParqueoPorUsuarioId(usuarioIdCliente, EstadoParqueo.Inactivo, motivo, adminQueEjecutaId);
    }

    public Parqueo activarCliente(UUID usuarioIdCliente, String motivo, UUID adminQueEjecutaId) {
        return cambiarEstadoParqueoPorUsuarioId(usuarioIdCliente, EstadoParqueo.Activo, motivo, adminQueEjecutaId);
    }

}
