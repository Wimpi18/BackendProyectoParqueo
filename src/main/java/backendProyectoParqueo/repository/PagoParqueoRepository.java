package backendProyectoParqueo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import backendProyectoParqueo.model.PagoParqueo;

public interface PagoParqueoRepository extends JpaRepository<PagoParqueo, Long> {
}
