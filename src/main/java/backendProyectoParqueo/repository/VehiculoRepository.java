package backendProyectoParqueo.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backendProyectoParqueo.dto.VehiculoDTO;
import backendProyectoParqueo.model.Vehiculo;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    boolean existsByPlaca(String placa);

    @Query("""
                SELECT new backendProyectoParqueo.dto.VehiculoDTO(p.id, v.placa, v.tipo, v.marca, v.modelo, v.color)
                FROM Parqueo p
                JOIN p.vehiculosAsignados va
                JOIN va.vehiculo v
                JOIN p.cliente c
                WHERE c.id = :id AND p.estado != 'Inactivo'
            """)
    List<Object> obtenerVehiculosActivosPorClienteId(@Param("id") UUID id);

    @Query("""
                SELECT new backendProyectoParqueo.dto.VehiculoDTO(p.id, v.placa, v.tipo, v.marca, v.modelo, v.color)
                FROM Parqueo p
                JOIN p.vehiculosAsignados va
                JOIN va.vehiculo v
                JOIN p.cliente c
                WHERE c.id = :id
            """)
    List<VehiculoDTO> obtenerVehiculosPorClienteId(@Param("id") UUID id);

    Optional<Vehiculo> findByPlaca(String placa);

}
