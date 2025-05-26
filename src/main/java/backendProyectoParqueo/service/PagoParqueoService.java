package backendProyectoParqueo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import backendProyectoParqueo.dto.PagoParqueoDTO;
import backendProyectoParqueo.model.Cajero;
import backendProyectoParqueo.model.PagoParqueo;
import backendProyectoParqueo.model.Parqueo;
import backendProyectoParqueo.model.Tarifa;
import backendProyectoParqueo.repository.PagoParqueoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PagoParqueoService {

    @Autowired
    private PagoParqueoRepository pagoParqueoRepository;
    @Autowired
    private TarifaService tarifaService;
    @Autowired
    private ParqueoService parqueoService;
    @Autowired
    private CajeroService cajeroService;

    public List<PagoParqueo> findAll() {
        return pagoParqueoRepository.findAll();
    }

    @Transactional
    public PagoParqueo create(PagoParqueoDTO pagoParqueoDTO) {
        PagoParqueo pagoParqueoEntity = new PagoParqueo();

        pagoParqueoEntity.setMontoPagado(pagoParqueoDTO.getMontoPagado());
        pagoParqueoEntity.setFechaHoraPago(pagoParqueoDTO.getFechaHoraPago());
        pagoParqueoEntity.setMeses(pagoParqueoDTO.getMeses());
        pagoParqueoEntity.setNroEspacioPagado(pagoParqueoDTO.getNroEspacioPagado());

        Tarifa tarifa = tarifaService.findById(pagoParqueoDTO.getIdTarifa())
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada"));
        Parqueo parqueo = parqueoService.findById(pagoParqueoDTO.getIdParqueo())
                .orElseThrow(() -> new RuntimeException("Parqueo no encontrado"));

        Cajero cajero;
        if (pagoParqueoDTO.getIdCajero() != null) {
            cajero = cajeroService.findById(pagoParqueoDTO.getIdCajero())
                    .orElseThrow(() -> new RuntimeException("Cajero no encontrado"));
        } else
            cajero = null;

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
