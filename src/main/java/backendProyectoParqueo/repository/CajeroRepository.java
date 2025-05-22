package backendProyectoParqueo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import backendProyectoParqueo.model.Cajero;

public interface CajeroRepository extends JpaRepository<Cajero, UUID> {
}
