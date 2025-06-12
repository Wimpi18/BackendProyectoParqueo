package backendProyectoParqueo.service.admin;


import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import backendProyectoParqueo.model.Parqueo.EstadoParqueo;
import backendProyectoParqueo.enums.TipoAccion;
import backendProyectoParqueo.model.Administrador;
import backendProyectoParqueo.model.BitacoraEstado;
import backendProyectoParqueo.model.Cajero;
import backendProyectoParqueo.model.Cliente;
import backendProyectoParqueo.model.Parqueo;
import backendProyectoParqueo.model.Usuario;
import backendProyectoParqueo.repository.AdministradorRepository;
import backendProyectoParqueo.repository.BitacoraEstadoRepository;
import backendProyectoParqueo.repository.CajeroRepository;
import backendProyectoParqueo.repository.ClienteRepository;
import backendProyectoParqueo.repository.ParqueoRepository;
import backendProyectoParqueo.repository.UsuarioRepository;
import jakarta.transaction.Transactional;

@Service
public class GestionEstadoUsuario {
  private final ParqueoRepository parqueoRepository;
  private final AdministradorRepository administradorRepository;
  private final BitacoraEstadoRepository bitacoraEstadoRepository;
  private final ClienteRepository clienteRepository;
   private final CajeroRepository cajeroRepository; 
  private final UsuarioRepository usuarioRepository;

  @Autowired
  public GestionEstadoUsuario(ParqueoRepository parqueoRepository,
      AdministradorRepository administradorRepository,
      BitacoraEstadoRepository bitacoraEstadoRepository,
      ClienteRepository clienteRepository,
      CajeroRepository cajeroRepository, 
      UsuarioRepository usuarioRepository) { 
    this.parqueoRepository = parqueoRepository;
    this.administradorRepository = administradorRepository;
    this.bitacoraEstadoRepository = bitacoraEstadoRepository;
    this.clienteRepository = clienteRepository;
    this.cajeroRepository = cajeroRepository;
    this.usuarioRepository = usuarioRepository;
  }


  @Transactional
    public Object cambiarEstadoGeneralUsuario(UUID usuarioIdAfectado, boolean activar, String motivo, UUID adminQueEjecutaId) {
      Administrador adminEjecutor = administradorRepository.findById(adminQueEjecutaId)
              .orElseThrow(() -> new RuntimeException("Administrador ejecutor con ID " + adminQueEjecutaId + " no encontrado."));

      if (adminQueEjecutaId.equals(usuarioIdAfectado)) {
          throw new RuntimeException("Un administrador no puede modificar su propio estado.");
      }

      Usuario usuarioBaseAfectado = usuarioRepository.findById(usuarioIdAfectado)
            .orElseThrow(() -> new RuntimeException("Usuario con ID " + usuarioIdAfectado + " no encontrado."));

      Optional<Cliente> optCliente = clienteRepository.findById(usuarioIdAfectado);
      if (optCliente.isPresent()) {
          Cliente clienteAfectado = optCliente.get();
          Optional<Parqueo> optParqueo = parqueoRepository.findByCliente_Id(clienteAfectado.getId());
          if (optParqueo.isPresent()) {
              Parqueo parqueo = optParqueo.get();
              EstadoParqueo nuevoEstadoParqueo;
              EstadoParqueo estadoAnteriorParqueo = parqueo.getEstado();

              if (activar) {
                  if (estadoAnteriorParqueo == EstadoParqueo.Activo) {
                      throw new RuntimeException("El parqueo del cliente ya está activo.");
                  }
                  nuevoEstadoParqueo = EstadoParqueo.Activo;
              } else { 
                  if (estadoAnteriorParqueo == EstadoParqueo.Inactivo) {
                       throw new RuntimeException("El parqueo del cliente ya está inactivo.");
                  }
                  nuevoEstadoParqueo = EstadoParqueo.Inactivo; 
              }
              
              parqueo.setEstado(nuevoEstadoParqueo);
              if (nuevoEstadoParqueo == EstadoParqueo.Inactivo) {
                  parqueo.setNroEspacio(null);
              }
              parqueoRepository.save(parqueo);
              TipoAccion accionBitacora = determinarTipoAccionParaCambioEstadoParqueo(nuevoEstadoParqueo, estadoAnteriorParqueo);
              registrarBitacora(usuarioBaseAfectado, adminEjecutor, accionBitacora, motivo);
              return parqueo;
          }
          throw new RuntimeException("Cliente con ID " + usuarioIdAfectado + " no tiene un parqueo asociado para modificar.");
      }

      Optional<Administrador> optAdminAfectado = administradorRepository.findById(usuarioIdAfectado);
      if (optAdminAfectado.isPresent()) {
          Administrador adminAfectado = optAdminAfectado.get();
          if (adminAfectado.isEsActivo() == activar) {
              throw new RuntimeException("La cuenta del administrador ya se encuentra en el estado solicitado (" + (activar ? "Activa" : "Inactiva") + ").");
          }
          adminAfectado.setEsActivo(activar);
          administradorRepository.save(adminAfectado);
          TipoAccion accionBitacora = activar ? TipoAccion.ACTIVACION_CUENTA_ADMIN : TipoAccion.INACTIVACION_CUENTA_ADMIN;
          registrarBitacora(usuarioBaseAfectado, adminEjecutor, accionBitacora, motivo);
          return adminAfectado;
      }

      Optional<Cajero> optCajeroAfectado = cajeroRepository.findById(usuarioIdAfectado);
      if (optCajeroAfectado.isPresent()) {
          Cajero cajeroAfectado = optCajeroAfectado.get();
          if (cajeroAfectado.isEsActivo() == activar) {
              throw new RuntimeException("La cuenta del cajero ya se encuentra en el estado solicitado (" + (activar ? "Activa" : "Inactiva") + ").");
          }
          cajeroAfectado.setEsActivo(activar);
          cajeroRepository.save(cajeroAfectado);
          TipoAccion accionBitacora = activar ? TipoAccion.ACTIVACION_CUENTA_CAJERO : TipoAccion.INACTIVACION_CUENTA_CAJERO;
          registrarBitacora(usuarioBaseAfectado, adminEjecutor, accionBitacora, motivo);
          return cajeroAfectado;
      }

      throw new RuntimeException("No se encontró un perfil de Cliente con Parqueo, Administrador o Cajero para el Usuario ID " + usuarioIdAfectado + " para modificar su estado.");
  }


    private void registrarBitacora(Usuario usuarioAfectado, Administrador adminEjecutor, TipoAccion tipoAccion, String motivo) {
        BitacoraEstado bitacora = new BitacoraEstado(
                usuarioAfectado,
                adminEjecutor,
                tipoAccion,
                motivo
        );
        bitacoraEstadoRepository.save(bitacora);
    }

   
    private TipoAccion determinarTipoAccionParaCambioEstadoParqueo(EstadoParqueo nuevoEstado, EstadoParqueo estadoAnterior) {
        switch (nuevoEstado) {
            case Bloqueado: 
                return TipoAccion.BLOQUEO_CLIENTE_POR_PARQUEO;
            case Inactivo:
                return TipoAccion.INACTIVACION_CLIENTE_POR_PARQUEO;
            case Activo:
                if (estadoAnterior == EstadoParqueo.Bloqueado) {
                    return TipoAccion.ACTIVACION_CLIENTE_POR_PARQUEO;
                } else if (estadoAnterior == EstadoParqueo.Inactivo) {
                    return TipoAccion.ACTIVACION_CLIENTE_POR_PARQUEO;
                } else {
                    throw new IllegalStateException("Transición inesperada al estado Activo desde: " + estadoAnterior);
                }
            default:
                throw new IllegalArgumentException("Estado de parqueo nuevo no reconocido: " + nuevoEstado);
        }
    }

    public Object inactivarCuenta(UUID usuarioIdAfectado, String motivo, UUID adminQueEjecutaId) {
        return cambiarEstadoGeneralUsuario(usuarioIdAfectado, false /*activar=false*/, motivo, adminQueEjecutaId);
    }

    public Object activarCuenta(UUID usuarioIdAfectado, String motivo, UUID adminQueEjecutaId) {
        return cambiarEstadoGeneralUsuario(usuarioIdAfectado, true /*activar=true*/, motivo, adminQueEjecutaId);
    }

 
    @Transactional
    public Parqueo cambiarEstadoEspecificoParqueo(UUID usuarioIdCliente, EstadoParqueo estadoDeseadoParqueo, String motivo, UUID adminQueEjecutaId) {
        
        Administrador adminEjecutor = administradorRepository.findById(adminQueEjecutaId)
              .orElseThrow(() -> new RuntimeException("Administrador ejecutor con ID " + adminQueEjecutaId + " no encontrado."));
        
        Cliente clienteAfectado = clienteRepository.findById(usuarioIdCliente)
              .orElseThrow(() -> new RuntimeException("Cliente con Usuario ID " + usuarioIdCliente + " no encontrado."));
        
        Parqueo parqueo = parqueoRepository.findByCliente_Id(clienteAfectado.getId())
              .orElseThrow(() -> new RuntimeException("No se encontró un Parqueo asociado al Cliente con Usuario ID " + usuarioIdCliente));
        
        Usuario usuarioDelClienteAfectado = clienteAfectado.getUsuario();

        if (adminEjecutor.getUsuario().getId().equals(usuarioDelClienteAfectado.getId())) {
            throw new RuntimeException("Un administrador no puede modificar el estado de un parqueo que le pertenece como cliente.");
        }

        if (parqueo.getEstado() == estadoDeseadoParqueo) {
            throw new RuntimeException("El parqueo ya se encuentra en el estado " + estadoDeseadoParqueo + ".");
        }
        EstadoParqueo estadoAnterior = parqueo.getEstado();
        parqueo.setEstado(estadoDeseadoParqueo);

        if (estadoDeseadoParqueo == EstadoParqueo.Inactivo) {
            parqueo.setNroEspacio(null);
        }
        
        parqueoRepository.save(parqueo);
        TipoAccion accionBitacora = determinarTipoAccionParaCambioEstadoParqueo(estadoDeseadoParqueo, estadoAnterior);
        registrarBitacora(usuarioDelClienteAfectado, adminEjecutor, accionBitacora, motivo);
        return parqueo;
    }

    public Parqueo bloquearParqueoDelCliente(UUID usuarioIdCliente, String motivo, UUID adminQueEjecutaId) {
        return cambiarEstadoEspecificoParqueo(usuarioIdCliente, EstadoParqueo.Bloqueado, motivo, adminQueEjecutaId);
    }

    public Parqueo desbloquearParqueoDelCliente(UUID usuarioIdCliente, String motivo, UUID adminQueEjecutaId) {
        return cambiarEstadoEspecificoParqueo(usuarioIdCliente, EstadoParqueo.Activo, motivo, adminQueEjecutaId);
    }

}
