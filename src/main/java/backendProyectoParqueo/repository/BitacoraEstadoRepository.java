package backendProyectoParqueo.repository;

import backendProyectoParqueo.model.BitacoraEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BitacoraEstadoRepository extends JpaRepository<BitacoraEstado, Long> {
  
}
