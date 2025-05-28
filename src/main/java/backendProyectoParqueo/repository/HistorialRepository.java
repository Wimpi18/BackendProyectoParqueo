package backendProyectoParqueo.repository;

import backendProyectoParqueo.dto.HistorialTarifaDTO;
import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.model.Tarifa;
import backendProyectoParqueo.repository.custom.HistorialRepositoryCustom;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistorialRepository extends JpaRepository<Tarifa, Integer>, HistorialRepositoryCustom {

    @Query("""
            SELECT new backendProyectoParqueo.dto.HistorialTarifaDTO(t.tipoVehiculo, t.tipoCliente, t.monto, CONCAT(u.nombre, CONCAT(' ', u.apellido)), t.fechaInicio)
            FROM Tarifa t
            JOIN t.administrador a
            JOIN a.usuario u
            ORDER BY t.id DESC
            """)
    List<HistorialTarifaDTO> obtenerHistorialTarifas();

    List<HistorialTarifaDTO> filtrarHistorialTarifas(
            TipoVehiculo tipoVehiculo,
            String tipoCliente,
            String nombreUsuario,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            BigDecimal montoMinimo,
            BigDecimal montoMaximo);
}
