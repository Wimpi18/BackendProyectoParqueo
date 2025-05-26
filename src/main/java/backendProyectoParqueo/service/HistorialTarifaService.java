package backendProyectoParqueo.service;

import backendProyectoParqueo.dto.HistorialTarifaDTO;
import backendProyectoParqueo.enums.TipoCliente;
import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.repository.HistorialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistorialTarifaService {

    private final HistorialRepository historialRepository;

    public List<HistorialTarifaDTO> obtenerHistorialTarifas() {
        List<Object[]> resultados = historialRepository.obtenerHistorialTarifas();

        List<HistorialTarifaDTO> historialDTOs = new ArrayList<>();
        long contador = resultados.size();

        for (Object[] fila : resultados) {
            HistorialTarifaDTO dto = new HistorialTarifaDTO();
            dto.setTipoVehiculo((TipoVehiculo) fila[0]);
            dto.setTipoCliente(TipoCliente.fromLabel((String) fila[1]));
            dto.setMonto((BigDecimal) fila[2]);
            dto.setNombreCompleto((String) fila[3]); // Asumiendo que ya lo corrigiste en la query
            dto.setFechaInicio((LocalDateTime) fila[4]);
            dto.setCantidadTarifas(contador);

            historialDTOs.add(dto);
            contador--;

        }

        return historialDTOs;
    }

    public List<HistorialTarifaDTO> obtenerHistorialTarifasFiltrado(
            TipoVehiculo tipoVehiculo,
            TipoCliente tipoCliente,
            String nombreUsuario,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            BigDecimal montoMin,
            BigDecimal montoMax) {
        return historialRepository.filtrarHistorialTarifas(
                tipoVehiculo,
                tipoCliente,
                nombreUsuario,
                fechaInicio,
                fechaFin,
                montoMin,
                montoMax);
    }

}
