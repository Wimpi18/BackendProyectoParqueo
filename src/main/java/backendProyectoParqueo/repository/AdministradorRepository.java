package backendProyectoParqueo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import backendProyectoParqueo.model.Administrador;

public interface AdministradorRepository extends JpaRepository<Administrador, UUID> {
}
