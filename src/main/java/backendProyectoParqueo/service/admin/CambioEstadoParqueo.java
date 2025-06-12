package backendProyectoParqueo.service.admin;


import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import backendProyectoParqueo.repository.ParqueoRepository;
import jakarta.transaction.Transactional;

@Service
public class CambioEstadoParqueo {
  private final ParqueoRepository parqueoRepository;
  private final AdministradorRepository administradorRepository;
  private final BitacoraEstadoRepository bitacoraEstadoRepository;

  @Autowired
  public CambioEstadoParqueo(ParqueoRepository parqueoRepository,
      AdministradorRepository administradorRepository,
      BitacoraEstadoRepository bitacoraEstadoRepository) {
    this.parqueoRepository = parqueoRepository;
    this.administradorRepository = administradorRepository;
    this.bitacoraEstadoRepository = bitacoraEstadoRepository;
  }


  @Transactional
    public Parqueo cambiarEstadoParqueo(Long parqueoId, EstadoParqueo nuevoEstado, String motivo) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("Se requiere autenticación de administrador para esta acción."); 
        }

        String adminIdStringDelToken = authentication.getName(); 
        UUID adminId;
        try {
            adminId = UUID.fromString(adminIdStringDelToken);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("ID de administrador en el token es inválido.", e); 
        }

        Administrador adminEjecutor = administradorRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Administrador ejecutor con ID " + adminId + " no encontrado.")); 

        Parqueo parqueo = parqueoRepository.findById(parqueoId)
                .orElseThrow(() -> new RuntimeException("Parqueo con ID " + parqueoId + " no encontrado.")); 

        Cliente clienteDelParqueo = parqueo.getCliente();
        if (clienteDelParqueo == null) {
            throw new IllegalStateException("Error de integridad de datos: Parqueo ID " + parqueoId + " no tiene un cliente asociado.");
        }
        Usuario usuarioDelClienteAfectado = clienteDelParqueo.getUsuario();
        if (usuarioDelClienteAfectado == null) {
            throw new IllegalStateException("Error de integridad de datos: Cliente ID " + clienteDelParqueo.getId() + " no tiene un usuario base asociado.");
        }

        if (adminEjecutor.getUsuario().getId().equals(usuarioDelClienteAfectado.getId())) {
            throw new RuntimeException("Un administrador no puede modificar el estado de un parqueo que le pertenece como cliente."); // Reemplazar con AccionNoPermitidaException
        }


        if (parqueo.getEstado() == nuevoEstado) {
            throw new RuntimeException("El parqueo ya se encuentra en el estado " + nuevoEstado + ". No se realizaron cambios."); // Reemplazar con EstadoInvalidoException
        }
        EstadoParqueo estadoAnterior = parqueo.getEstado();
        parqueo.setEstado(nuevoEstado);

        
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
                    return TipoAccion.DESBLOQUEO_CLIENTE_POR_PARQUEO;
                } else if (estadoAnterior == EstadoParqueo.Inactivo) {
                    return TipoAccion.ACTIVACION_CLIENTE_POR_PARQUEO;
                } else {
                    throw new IllegalStateException("Transición inesperada al estado Activo desde: " + estadoAnterior);
                }
            default:
                throw new IllegalArgumentException("Estado de parqueo nuevo no reconocido: " + nuevoEstado);
        }
    }



    public Parqueo bloquearParqueoCliente(Long parqueoId, String motivo) {
        return cambiarEstadoParqueo(parqueoId, EstadoParqueo.Bloqueado, motivo);
    }

    public Parqueo desbloquearParqueoCliente(Long parqueoId, String motivo) {
        return cambiarEstadoParqueo(parqueoId, EstadoParqueo.Activo, motivo);
    }

    public Parqueo inactivarParqueoCliente(Long parqueoId, String motivo) {
        return cambiarEstadoParqueo(parqueoId, EstadoParqueo.Inactivo, motivo);
    }

    public Parqueo activarParqueoCliente(Long parqueoId, String motivo) {
        return cambiarEstadoParqueo(parqueoId, EstadoParqueo.Activo, motivo);
    }

}
