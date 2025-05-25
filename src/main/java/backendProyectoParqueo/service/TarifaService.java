package backendProyectoParqueo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Service;

import backendProyectoParqueo.dto.TarifaDTO;
import backendProyectoParqueo.model.Tarifa;
import backendProyectoParqueo.repository.AdministradorRepository;
import backendProyectoParqueo.repository.TarifaRepository;
import backendProyectoParqueo.model.Administrador;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TarifaService {

    private final TarifaRepository tarifaRepository;
    private final AdministradorRepository administradorRepository;

    public List<TarifaDTO> listarTarifas() {
        return tarifaRepository.findAll().stream()
                .map(t -> new TarifaDTO(
                        null,
                        null,
                        t.getTipoVehiculo(),
                        t.getTipoCliente(),
                        t.getMonto(),
                        t.getFechaInicio()))
                .toList();
    }

    public TarifaDTO crearTarifa(TarifaDTO dto) {
        if (dto.getMonto() == null || dto.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }

        UUID adminId = dto.getIdAdministrador();
        Administrador admin = administradorRepository.findById(adminId)
                .orElseThrow(() -> new NoSuchElementException("Administrador no encontrado."));

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

}
