package backendProyectoParqueo.repository;

import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.model.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialRepository extends JpaRepository<Tarifa, Integer> {

    @Query("""
                SELECT
                    t.tipoVehiculo,
                    t.tipoCliente,
                    t.monto,
                    u.nombreCompleto,
                    t.fechaInicio,
                    COUNT(t.id)
                FROM Tarifa t
                JOIN t.administrador a
                JOIN a.usuario u
                AND t.tipoVehiculo = :tipoVehiculo
                GROUP BY
                    t.tipoVehiculo,
                    t.tipoCliente,
                    t.monto,
                    u.nombreCompleto,
                    t.fechaInicio
                ORDER BY COUNT(t.id) DESC, t.fechaInicio DESC
            """)
    List<Object[]> obtenerHistorialTarifas();
}