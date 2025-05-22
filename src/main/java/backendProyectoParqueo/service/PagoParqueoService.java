package backendProyectoParqueo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backendProyectoParqueo.model.PagoParqueo;
import backendProyectoParqueo.repository.PagoParqueoRepository;

@Service
public class PagoParqueoService {

    @Autowired
    private PagoParqueoRepository pagoParqueoRepository;

    public List<PagoParqueo> findAll() {
        return pagoParqueoRepository.findAll();
    }

    public PagoParqueo create(PagoParqueo entity) {
        return pagoParqueoRepository.save(entity);
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

        return null;
    }
}
