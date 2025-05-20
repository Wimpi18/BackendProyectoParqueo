package backendProyectoParqueo.service;

import java.util.List;

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
}
