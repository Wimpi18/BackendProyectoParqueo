package backendProyectoParqueo.repository;

import backendProyectoParqueo.model.Cajero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CajeroRepository extends JpaRepository<Cajero, UUID> {
}
