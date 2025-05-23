package backendProyectoParqueo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backendProyectoParqueo.model.Vehiculo;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    boolean existsByPlaca(String placa);

    @Query("""
                SELECT new backendProyectoParqueo.dto.VehiculoDTO(p.id, v.placa, v.tipo)
                FROM Vehiculo v
                JOIN Parqueo p ON p.vehiculo.id = v.id AND p.estado != 'Inactivo'
                JOIN Cliente c ON c.id = p.cliente.id
                WHERE c.id = :id
            """)
    List<Object> obtenerVehiculosActivosPorClienteId(@Param("id") UUID id);
}
