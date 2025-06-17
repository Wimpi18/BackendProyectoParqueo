package backendProyectoParqueo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import backendProyectoParqueo.dto.TarifaDTO;
import backendProyectoParqueo.enums.TipoCliente;
import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.exception.BusinessException;
import backendProyectoParqueo.exception.TarifaDuplicadaException;
import backendProyectoParqueo.model.Administrador;
import backendProyectoParqueo.model.Tarifa;
import backendProyectoParqueo.repository.AdministradorRepository;
import backendProyectoParqueo.repository.TarifaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TarifaService {

    private final TarifaRepository tarifaRepository;
    private final AdministradorRepository administradorRepository;

    public List<TarifaDTO> listarTarifasVigentes() {
        return tarifaRepository.obtenerTarifasVigentesNativo();
    }

    public TarifaDTO crearTarifa(TarifaDTO dto) {
        if (dto.getMonto() == null || dto.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }

        List<String> tiposValidos = List.of(
                "Administrativo",
                "Docente a dedicación exclusiva",
                "Docente a tiempo horario");

        if (!tiposValidos.contains(dto.getTipoCliente())) {
            throw new IllegalArgumentException("El tipo de cliente no es válido. Debe ser uno de: " + tiposValidos);
        }

        UUID adminId = getCurrentUserId();
        Administrador admin = administradorRepository.findById(adminId)
                .orElseThrow(() -> new NoSuchElementException("Administrador autenticado no encontrado."));

        Tarifa tarifaActual = tarifaRepository.obtenerTarifaVigente(dto.getTipoCliente(), dto.getTipoVehiculo());
        if (tarifaActual != null && tarifaActual.getMonto().compareTo(dto.getMonto()) == 0) {
            throw new TarifaDuplicadaException("Ya existe una tarifa vigente con el mismo monto.");
        }
        Tarifa tarifa = new Tarifa();
        tarifa.setAdministrador(admin);
        tarifa.setTipoVehiculo(dto.getTipoVehiculo());
        tarifa.setTipoCliente(dto.getTipoCliente());
        tarifa.setMonto(dto.getMonto());

        Tarifa guardada = tarifaRepository.save(tarifa);
        return new TarifaDTO(
                guardada.getId(),
                admin.getId(),
                guardada.getTipoVehiculo(),
                guardada.getTipoCliente(),
                guardada.getMonto(),
                guardada.getFechaInicio());
    }

    public Tarifa findTarifaByTipoClienteYVehiculo(String tipoCliente, TipoVehiculo tipoVehiculo) {
        TipoCliente tipoClienteEnum = null;
        for (TipoCliente tc : TipoCliente.values()) {
            if (tc.getLabel().equalsIgnoreCase(tipoCliente)) {
                tipoClienteEnum = tc;
                break;
            }
        }

        if (tipoClienteEnum == null) {
            throw new IllegalArgumentException("Tipo de cliente inválido: " + tipoCliente);
        }

        return tarifaRepository.obtenerTarifaVigente(tipoClienteEnum.getLabel(), tipoVehiculo);
    }

    public Tarifa findById(Integer id) {
        return tarifaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Tarifa no encontrada", "idTarifa"));
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String id = jwt.getClaimAsString("userId");
            return UUID.fromString(id);
        }
        throw new IllegalStateException("No se pudo obtener el ID del usuario desde el token.");
    }
}
