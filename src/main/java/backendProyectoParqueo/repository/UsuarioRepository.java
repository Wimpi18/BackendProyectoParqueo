package backendProyectoParqueo.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import backendProyectoParqueo.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    boolean existsByCi(String ci);

    boolean existsByUsername(String username);

    boolean existsByCorreo(String correo);

    @Query(value = """
                SELECT u.id, u.ci, u.nombre, u.apellido, u.correo,
                       u.nro_celular, u.password, u.username,
                       ARRAY_REMOVE(ARRAY[
                           CASE WHEN c.id IS NOT NULL THEN 'ROLE_CLIENTE' ELSE NULL END,
                           CASE WHEN a.id IS NOT NULL THEN 'ROLE_ADMINISTRADOR' ELSE NULL END,
                           CASE WHEN j.id IS NOT NULL THEN 'ROLE_CAJERO' ELSE NULL END
                       ], NULL) AS roles
                FROM usuario u
                LEFT JOIN cliente c ON c.id = u.id
                LEFT JOIN administrador a ON a.id = u.id
                LEFT JOIN cajero j ON j.id = u.id
                WHERE u.username = :username
                LIMIT 1;
            """, nativeQuery = true)
    Optional<List<Object[]>> findRawUsuarioConRolesByUsername(@Param("username") String username);
}
