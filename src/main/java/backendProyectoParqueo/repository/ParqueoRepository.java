// src/main/java/backendProyectoParqueo/repository/ParqueoRepository.java
package backendProyectoParqueo.repository;

import backendProyectoParqueo.dto.VehiculoParqueoActivoDTO; // Importar el nuevo DTO
import backendProyectoParqueo.model.Parqueo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository // Asegúrate que ParqueoRepository tenga @Repository si no lo tiene
public interface ParqueoRepository extends JpaRepository<Parqueo, Long> {
        List<Parqueo> findByVehiculo_Id(Long id); // Cambiado el nombre del parámetro para claridad

        @Query("""
                        SELECT new backendProyectoParqueo.dto.VehiculoParqueoActivoDTO(p.id, v.placa, v.tipo)
                        FROM Parqueo p
                        JOIN p.vehiculo v
                        WHERE p.cliente.id = :clienteId AND p.estado = backendProyectoParqueo.model.Parqueo.EstadoParqueo.Activo
                        """)
        List<VehiculoParqueoActivoDTO> findVehiculosActivosByClienteId(@Param("clienteId") UUID clienteId);

        // Método para obtener el parqueo activo por cliente y placa, cargando datos
        // relacionados
        @Query("""
                        SELECT p
                        FROM Parqueo p
                        JOIN FETCH p.vehiculo v
                        JOIN FETCH p.cliente cl
                        JOIN FETCH cl.usuario u
                        WHERE p.cliente.id = :clienteId AND v.placa = :placa AND p.estado = backendProyectoParqueo.model.Parqueo.EstadoParqueo.Activo
                        """)
        Optional<Parqueo> findActivoByClienteIdAndVehiculoPlacaWithDetails(@Param("clienteId") UUID clienteId,
                        @Param("placa") String placa);

        @Query("SELECT p.nroEspacio FROM Parqueo p WHERE p.estado = 'Activo' or p.estado = 'Bloqueado'")
        List<Short> findEspaciosOcupados();

 @      Query("""
            SELECT p
            FROM Parqueo p
            JOIN FETCH p.vehiculo v
            JOIN FETCH p.cliente c
            WHERE v.id = :vehiculoId
            """)
        List<Parqueo> findAllByVehiculoIdWithDetails(@Param("vehiculoId") Long vehiculoId);
        
@Query("""
            SELECT p
            FROM Parqueo p
            JOIN FETCH p.vehiculo v
            JOIN FETCH p.cliente c
            WHERE c.id = :clienteId AND v.placa = :placa
            """)
    List<Parqueo> findAllByClienteIdAndVehiculoPlacaWithDetailsQuery(
            @Param("clienteId") UUID clienteId,
            @Param("placa") String placa
    );
        
}