package backendProyectoParqueo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backendProyectoParqueo.model.VehiculoEnParqueo;

@Repository
public interface VehiculoEnParqueoRepository extends JpaRepository<VehiculoEnParqueo, Long> {
}
