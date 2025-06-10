package backendProyectoParqueo.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import backendProyectoParqueo.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    boolean existsByCi(String ci);

    boolean existsByUsername(String username);

    boolean existsByCorreo(String correo);

    Optional<Usuario> findByCi(String ci);
    @Query(value = """
                SELECT
                    u.ci,
                    u.nombre,
                    u.apellido,
                    u.correo,
                    u.nro_celular,
                    u.password,
                    c.tipo AS rol_asignado,
                    c.foto AS foto_usuario,
                    p.estado AS estado_parqueo,
                    v.id,
                    p.id,
                    v.placa,
                    v.tipo AS tipo_vehiculo,
                    v.marca,
                    v.foto_delantera,
                    v.foto_trasera,
                    v.modelo,
                    v.color
                FROM usuario u
                JOIN cliente c ON u.id = c.id
                JOIN parqueo p ON c.id = p.id_cliente
                JOIN vehiculo v ON p.id_vehiculo = v.id
                WHERE u.id = :usuarioId
            """, nativeQuery = true)
    List<Object[]> obtenerDetallesUsuarioPorId(@Param("usuarioId") UUID usuarioId);
}
