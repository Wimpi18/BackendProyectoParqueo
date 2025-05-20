package backendProyectoParqueo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backendProyectoParqueo.model.PagoParqueo;

@Repository
public interface PagoParqueoRepository extends JpaRepository<PagoParqueo, Long> {
}
