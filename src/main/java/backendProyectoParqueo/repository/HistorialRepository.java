// src/main/java/backendProyectoParqueo/repository/HistorialRepository.java
package backendProyectoParqueo.repository;

import backendProyectoParqueo.dto.HistorialTarifaDTO;
import backendProyectoParqueo.model.Tarifa;

import org.springframework.data.jpa.repository.JpaRepository;
import backendProyectoParqueo.repository.custom.HistorialRepositoryCustom;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialRepository extends JpaRepository<Tarifa, Integer>, HistorialRepositoryCustom {

    @Query("""
            SELECT
                t.tipoVehiculo,
                t.tipoCliente,
                t.monto,
                CONCAT(u.nombre, ' ', u.apellido),
                MAX(t.fechaInicio)
            FROM Tarifa t
            JOIN t.administrador a
            JOIN a.usuario u
            GROUP BY
                t.tipoVehiculo,
                t.tipoCliente,
                t.monto,
                u.nombre,
                u.apellido,
                t.fechaInicio
            ORDER BY MAX(t.fechaInicio) DESC

            """)

    List<Object[]> obtenerHistorialTarifas();

    long count();
}
