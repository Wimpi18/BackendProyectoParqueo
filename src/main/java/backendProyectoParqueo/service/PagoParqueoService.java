package backendProyectoParqueo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import backendProyectoParqueo.dto.PagoParqueoDTO;
import backendProyectoParqueo.exception.BusinessException;
import backendProyectoParqueo.model.Cajero;
import backendProyectoParqueo.model.Cliente;
import backendProyectoParqueo.model.PagoParqueo;
import backendProyectoParqueo.model.Parqueo;
import backendProyectoParqueo.model.Tarifa;
import backendProyectoParqueo.repository.ClienteRepository;
import backendProyectoParqueo.repository.PagoParqueoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PagoParqueoService {

    private final PagoParqueoRepository pagoParqueoRepository;
    private final ClienteRepository clienteRepository;
    private final TarifaService tarifaService;
    private final ParqueoService parqueoService;
    private final CajeroService cajeroService;

    public List<PagoParqueo> findAll() {
        return pagoParqueoRepository.findAll();
    }

    @Transactional
    public PagoParqueo create(PagoParqueoDTO dto) {
        PagoParqueo pagoParqueoEntity = new PagoParqueo();

        pagoParqueoEntity.setMontoPagado(dto.getMontoPagado());
        pagoParqueoEntity.setFechaHoraPago(dto.getFechaHoraPago());
        pagoParqueoEntity.setMeses(dto.getMeses());
        pagoParqueoEntity.setNroEspacioPagado(dto.getNroEspacioPagado());

        // Verificar que el usuario exista y sea un cliente válido
        Cliente cliente = clienteRepository.findById(dto.getIdCliente()).orElse(null);
        if (cliente == null) {
            throw new BusinessException("El ID proporcionado no corresponde a un cliente válido.", "idCliente");
        }

        Tarifa tarifa = tarifaService.findById(dto.getIdTarifa());
        Parqueo parqueo = parqueoService.findById(dto.getIdParqueo());

        Cajero cajero = null;
        if (dto.getIdCajero() != null)
            cajero = cajeroService.findById(dto.getIdCajero());

        pagoParqueoEntity.setTarifa(tarifa);
        pagoParqueoEntity.setParqueo(parqueo);
        pagoParqueoEntity.setCajero(cajero);

        return pagoParqueoRepository.save(pagoParqueoEntity);
    }

    public Object getFechaCorrespondienteDePagoParqueo(UUID clienteId, Long parqueoId) {
        Object result = pagoParqueoRepository.obtenerUltimoPago(clienteId, parqueoId);

        if (result instanceof Object[] row) {
            LocalDate fechaInicio = LocalDate.parse(row[0].toString());
            LocalDate ultimoMesPagado = row[1] != null ? LocalDate.parse(row[1].toString()) : null;

            if (ultimoMesPagado == null || fechaInicio.isAfter(ultimoMesPagado)) {
                return fechaInicio;
            } else {
                return ultimoMesPagado.plusMonths(1);
            }
        }

        if (result == null)
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "El cliente no tiene asociado el parqueo seleccionado a su cuenta.");

        return null;
    }
}
