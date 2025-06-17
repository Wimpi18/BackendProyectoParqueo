package backendProyectoParqueo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import backendProyectoParqueo.dto.TarifaDTO;
import backendProyectoParqueo.dto.TarifaCreateDTO;
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

    public TarifaDTO crearTarifa(TarifaCreateDTO dto) {
        if (dto.getMonto() == null || dto.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }

        List<String> tiposValidos = List.of(
                "Administrativo",
                "Docente a dedicación exclusiva",
                "Docente a tiempo horario");

        if (!tiposValidos.contains(dto.getTipoCliente())) {
            throw new IllegalArgumentException("Tipo de cliente inválido: " + dto.getTipoCliente());
        }

        UUID adminId = getCurrentUserId();
        Administrador admin = administradorRepository.findById(adminId)
                .orElseThrow(() -> new NoSuchElementException("Administrador no encontrado."));

        Tarifa existente = tarifaRepository.obtenerTarifaVigente(dto.getTipoCliente(), dto.getTipoVehiculo());
        if (existente != null && existente.getMonto().compareTo(dto.getMonto()) == 0) {
            throw new TarifaDuplicadaException("Ya existe una tarifa vigente con el mismo monto.");
        }

        Tarifa nuevaTarifa = new Tarifa();
        nuevaTarifa.setTipoVehiculo(dto.getTipoVehiculo());
        nuevaTarifa.setTipoCliente(dto.getTipoCliente());
        nuevaTarifa.setMonto(dto.getMonto());
        nuevaTarifa.setAdministrador(admin);
        // Si tienes fechaInicio automática (ejemplo: ahora)
        nuevaTarifa.setFechaInicio(LocalDateTime.now());

        Tarifa guardada = tarifaRepository.save(nuevaTarifa);

        // Retorna el DTO con los datos ya guardados y asignados
        return new TarifaDTO(
                guardada.getId(),
                guardada.getAdministrador().getId(),
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
