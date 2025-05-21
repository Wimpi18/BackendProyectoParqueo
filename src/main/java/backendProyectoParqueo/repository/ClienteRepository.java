package backendProyectoParqueo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import backendProyectoParqueo.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    @Query("""
                SELECT new backendProyectoParqueo.dto.ClienteDTO(u.ci, u.nombreCompleto, c.tipo)
                FROM Cliente c
                JOIN Usuario u ON u.id = c.id
                JOIN Parqueo p ON p.cliente.id = c.id
                WHERE p.estado != 'Inactivo'
            """)
    List<Object> findAllClientesNoInactivos();

}
