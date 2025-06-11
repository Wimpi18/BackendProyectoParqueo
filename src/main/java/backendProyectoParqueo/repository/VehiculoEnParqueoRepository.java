package backendProyectoParqueo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backendProyectoParqueo.model.Vehiculo;
import backendProyectoParqueo.model.VehiculoEnParqueo;

@Repository
public interface VehiculoEnParqueoRepository extends JpaRepository<VehiculoEnParqueo, Long> {
    List<VehiculoEnParqueo> findByVehiculo(Vehiculo id);
}
