package backendProyectoParqueo.repository;

import backendProyectoParqueo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    boolean existsByCi(String ci);

    boolean existsByCorreo(String correo);
}
