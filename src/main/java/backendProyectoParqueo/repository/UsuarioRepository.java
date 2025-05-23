package backendProyectoParqueo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import backendProyectoParqueo.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    boolean existsByCi(String ci);

    boolean existsByCorreo(String correo);
}
