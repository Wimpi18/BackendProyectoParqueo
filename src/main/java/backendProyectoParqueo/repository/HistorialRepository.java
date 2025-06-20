package backendProyectoParqueo.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import backendProyectoParqueo.dto.HistorialTarifaDTO;
import backendProyectoParqueo.dto.HistorialDTO;
import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.model.Tarifa;
import backendProyectoParqueo.repository.custom.HistorialRepositoryCustom;

@Repository
public interface HistorialRepository extends JpaRepository<Tarifa, Integer>, HistorialRepositoryCustom {

        @Query("""
                                        SELECT new backendProyectoParqueo.dto.HistorialDTO(t.id, t.tipoVehiculo, t.tipoCliente, t.monto, CONCAT(u.nombre, CONCAT(' ', u.apellido)), t.fechaInicio)
                                        FROM Tarifa t
                                        JOIN t.administrador a
                                        JOIN a.usuario u
                                        ORDER BY t.id DESC
                        """)
        List<HistorialDTO> obtenerHistorialTarifas();

        @Override
        List<HistorialTarifaDTO> filtrarHistorialTarifas(
                        TipoVehiculo tipoVehiculo,
                        String tipoCliente,
                        String nombreUsuario,
                        LocalDateTime fechaInicio,
                        LocalDateTime fechaFin,
                        BigDecimal montoMinimo,
                        BigDecimal montoMaximo);
}
