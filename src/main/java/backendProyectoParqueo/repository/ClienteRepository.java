package backendProyectoParqueo.repository;

import backendProyectoParqueo.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
}
