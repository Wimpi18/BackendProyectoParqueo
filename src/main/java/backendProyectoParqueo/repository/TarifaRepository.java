package backendProyectoParqueo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backendProyectoParqueo.dto.TarifaDTO;
import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.model.Tarifa;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Integer> {
  @Query("""
        SELECT t FROM Tarifa t
        WHERE t.tipoCliente = :tipoCliente
          AND t.tipoVehiculo = :tipoVehiculo
          AND t.fechaInicio <= CURRENT_TIMESTAMP
        ORDER BY t.fechaInicio DESC
      """)
  Tarifa obtenerTarifaVigente(
      @Param("tipoCliente") String tipoCliente,
      @Param("tipoVehiculo") TipoVehiculo tipoVehiculo);

  @Query(value = """
      SELECT new backendProyectoParqueo.dto.TarifaDTO(
              t.id,
              t.administrador.id,
              t.tipoVehiculo,
              t.tipoCliente,
              t.monto,
              t.fechaInicio
          )
          FROM Tarifa t
          WHERE t.fechaInicio <= CURRENT_TIMESTAMP
            AND t.fechaInicio = (
                SELECT MAX(t2.fechaInicio)
                FROM Tarifa t2
                WHERE t2.tipoVehiculo = t.tipoVehiculo
                  AND t2.tipoCliente = t.tipoCliente
                  AND t2.fechaInicio <= CURRENT_TIMESTAMP
                  )
            """)
  List<TarifaDTO> obtenerTarifasVigentesNativo();

}
