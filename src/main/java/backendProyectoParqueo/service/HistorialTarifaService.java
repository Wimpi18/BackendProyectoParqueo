package backendProyectoParqueo.service;

import backendProyectoParqueo.dto.HistorialTarifaDTO;
import backendProyectoParqueo.enums.TipoCliente;
import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.repository.HistorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class HistorialTarifaService {

    private final HistorialRepository historialRepository;

    public HistorialTarifaService(HistorialRepository historialRepository) {
        this.historialRepository = historialRepository;
    }

    public List<HistorialTarifaDTO> obtenerHistorialTarifas() {
        List<Object[]> resultados = historialRepository.obtenerHistorialTarifas();

        List<HistorialTarifaDTO> historialDTOs = new ArrayList<>();

        for (Object[] fila : resultados) {
            HistorialTarifaDTO dto = new HistorialTarifaDTO();
            dto.setTipoVehiculo((TipoVehiculo) fila[0]);
            dto.setTipoCliente(TipoCliente.valueOf((String) fila[1]));
            dto.setMonto((BigDecimal) fila[2]);
            dto.setNombreCompleto((String) fila[3]);
            dto.setFechaInicio((LocalDateTime) fila[4]);
            dto.setCantidadTarifas((Long) fila[5]);

            historialDTOs.add(dto);
        }

        return historialDTOs;
    }
}