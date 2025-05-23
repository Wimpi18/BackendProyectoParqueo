package backendProyectoParqueo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.model.Tarifa;
import backendProyectoParqueo.repository.TarifaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TarifaService {

    @Autowired
    private TarifaRepository tarifaRepository;

    public Tarifa findTarifaByTipoClienteYVehiculo(String tipoCliente, TipoVehiculo tipoVehiculo) {
        return tarifaRepository.obtenerTarifaVigente(tipoCliente, tipoVehiculo);
    }

}
