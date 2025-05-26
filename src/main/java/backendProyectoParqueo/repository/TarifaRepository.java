package backendProyectoParqueo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
