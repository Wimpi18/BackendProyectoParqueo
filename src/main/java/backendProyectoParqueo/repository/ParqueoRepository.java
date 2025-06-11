package backendProyectoParqueo.repository;

import java.util.List; // Importar el nuevo DTO
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Importar el nuevo DTO
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backendProyectoParqueo.dto.VehiculoParqueoActivoDTO;
import backendProyectoParqueo.model.Parqueo;
import backendProyectoParqueo.model.Vehiculo;

@Repository // Asegúrate que ParqueoRepository tenga @Repository si no lo tiene
public interface ParqueoRepository extends JpaRepository<Parqueo, Long> {

        @Query("""
                        SELECT new backendProyectoParqueo.dto.VehiculoParqueoActivoDTO(p.id, v.placa, v.tipo)
                        FROM Parqueo p
                        JOIN p.vehiculosAsignados va
                        JOIN va.vehiculo v
                        WHERE p.cliente.id = :clienteId AND p.estado = backendProyectoParqueo.model.Parqueo.EstadoParqueo.Activo
                        """)
        List<VehiculoParqueoActivoDTO> findVehiculosActivosByClienteId(@Param("clienteId") UUID clienteId);

        // Método para obtener el parqueo activo por cliente y placa, cargando datos
        // relacionados
        @Query("""
                        SELECT p
                        FROM Parqueo p
                        JOIN FETCH p.vehiculosAsignados va
                        JOIN FETCH va.vehiculo v
                        JOIN FETCH p.cliente cl
                        JOIN FETCH cl.usuario u
                        WHERE p.cliente.id = :clienteId AND v.placa = :placa AND p.estado = backendProyectoParqueo.model.Parqueo.EstadoParqueo.Activo
                        """)
        List<Parqueo> findActivosByClienteIdAndVehiculoPlacaWithDetails(
                        @Param("clienteId") UUID clienteId,
                        @Param("placa") String placa);

        @Query("SELECT p.nroEspacio FROM Parqueo p WHERE p.estado = 'Activo' or p.estado = 'Bloqueado'")
        List<Short> findEspaciosOcupados();

        @Query("""
                        SELECT p
                        FROM Parqueo p
                        JOIN FETCH p.vehiculosAsignados va
                        JOIN FETCH va.vehiculo v
                        JOIN FETCH p.cliente c
                        WHERE v.id = :vehiculoId
                        """)
        List<Parqueo> findAllByVehiculoIdWithDetails(@Param("vehiculoId") Long vehiculoId);

        @Query("""
                        SELECT p
                        FROM Parqueo p
                        JOIN FETCH p.vehiculosAsignados va
                        JOIN FETCH va.vehiculo v
                        JOIN FETCH p.cliente c
                        WHERE c.id = :clienteId AND v.placa = :placa
                        """)
        List<Parqueo> findAllByClienteIdAndVehiculoPlacaWithDetailsQuery(
                        @Param("clienteId") UUID clienteId,
                        @Param("placa") String placa);

        @Query("""
                        SELECT p.vehiculosAsignados
                        FROM Parqueo p
                        WHERE p.cliente.id = :clienteId AND p.estado != 'Inactivo'
                        """)
        List<Vehiculo> obtenerVehiculosActivosPorClienteId(@Param("clienteId") UUID clienteId);
}