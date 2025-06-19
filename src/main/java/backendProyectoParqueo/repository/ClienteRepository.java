package backendProyectoParqueo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backendProyectoParqueo.dto.ClienteDTO;
import backendProyectoParqueo.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    @Query("""
                SELECT DISTINCT new backendProyectoParqueo.dto.ClienteDTO(u.id, u.ci, u.nombre, u.apellido, c.tipo, p.tipo)
                FROM Cliente c
                JOIN Usuario u ON u.id = c.id
                JOIN Parqueo p ON p.cliente.id = c.id
                WHERE p.estado != 'Inactivo' AND c.id != :id
            """)
    List<ClienteDTO> findAllClientesNoInactivos(@Param(value = "id") UUID id);
}
