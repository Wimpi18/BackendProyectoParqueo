package backendProyectoParqueo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import backendProyectoParqueo.model.Vehiculo;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    boolean existsByPlaca(String placa);
}
