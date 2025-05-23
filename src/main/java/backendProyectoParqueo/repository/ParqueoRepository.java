package backendProyectoParqueo.repository;

import backendProyectoParqueo.dto.VehiculoParqueoActivoDTO;
import backendProyectoParqueo.model.Parqueo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParqueoRepository extends JpaRepository<Parqueo, Long> {

    List<Parqueo> findByVehiculo_Id(Long id);

    List<Parqueo> findByEstado(Parqueo.EstadoParqueo estado);

    boolean existsByNroEspacioAndEstado(Short nroEspacio, Parqueo.EstadoParqueo estado);

    @Query("""
        SELECT new backendProyectoParqueo.dto.VehiculoParqueoActivoDTO(p.id, v.placa, v.tipo)
        FROM Parqueo p
        JOIN p.vehiculo v
        WHERE p.cliente.id = :clienteId AND p.estado = backendProyectoParqueo.model.Parqueo.EstadoParqueo.Activo
    """)
    List<VehiculoParqueoActivoDTO> findVehiculosActivosByClienteId(@Param("clienteId") UUID clienteId);

    @Query("""
        SELECT p
        FROM Parqueo p
        JOIN FETCH p.vehiculo v
        JOIN FETCH p.cliente cl
        JOIN FETCH cl.usuario u
        WHERE p.cliente.id = :clienteId AND v.placa = :placa AND p.estado = backendProyectoParqueo.model.Parqueo.EstadoParqueo.Activo
    """)
    Optional<Parqueo> findActivoByClienteIdAndVehiculoPlacaWithDetails(@Param("clienteId") UUID clienteId, @Param("placa") String placa);
}
