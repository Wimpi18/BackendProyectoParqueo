package backendProyectoParqueo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backendProyectoParqueo.enums.TipoCliente;
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

        // tipoVehiculo ya es del tipo enum, no necesita validación adicional
        return tarifaRepository.obtenerTarifaVigente(tipoClienteEnum.getLabel(), tipoVehiculo);
    }

}