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
            SELECT new backendProyectoParqueo.dto.HistorialTarifaDTO(t.tipoVehiculo, t.tipoCliente, t.monto, CONCAT(u.nombre, CONCAT(' ', u.apellido)), t.fechaInicio)
            FROM Tarifa t
            JOIN t.administrador a
            JOIN a.usuario u
            ORDER BY t.id DESC
            """)
    List<HistorialTarifaDTO> obtenerHistorialTarifas();
}
