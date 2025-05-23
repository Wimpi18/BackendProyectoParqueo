package backendProyectoParqueo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import backendProyectoParqueo.model.Parqueo;

public interface ParqueoRepository extends JpaRepository<Parqueo, Long> {
    List<Parqueo> findByVehiculo_Id(Long id);

    List<Parqueo> findByEstado(Parqueo.EstadoParqueo estado);

    boolean existsByNroEspacioAndEstado(Short nroEspacio, Parqueo.EstadoParqueo estado);

}