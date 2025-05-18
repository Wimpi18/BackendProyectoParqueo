package backendProyectoParqueo.repository;

import backendProyectoParqueo.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdministradorRepository extends JpaRepository<Administrador, UUID> {
}
