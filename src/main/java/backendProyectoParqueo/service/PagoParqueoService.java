package backendProyectoParqueo.service;

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
        return pagoParqueoRepository.obtenerUltimoPago(clienteId, parqueoId);
    }
}
