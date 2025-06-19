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
                                            u.foto AS foto_usuario,
                                            p.estado AS estado_parqueo,
                                            v.id,
                                            v.placa,
                                            p.tipo AS tipo_vehiculo,
                                            v.marca,
                                            v.foto_delantera,
                                            v.foto_trasera,
                                            v.modelo,
                                            v.color
                        FROM usuario u
                        JOIN cliente c ON u.id = c.id
                        JOIN parqueo p ON c.id = p.id_cliente
                        JOIN vehiculo_en_parqueo vp ON vp.id_parqueo = p.id
                        JOIN vehiculo v ON vp.id_vehiculo = v.id
                                        WHERE u.id = :usuarioId
                                    """, nativeQuery = true)
    List<Object[]> obtenerDetallesUsuarioPorId(@Param("usuarioId") UUID usuarioId);

    @Query(value = """
            SELECT u.id, u.ci, u.nombre, u.apellido, u.correo,
                   u.nro_celular, u.password, u.username, u.foto,
                   ARRAY_REMOVE(ARRAY[
                       CASE
                           WHEN c.id IS NOT NULL AND p.id IS NOT NULL THEN 'ROLE_CLIENTE'
                           ELSE NULL
                       END,
                       CASE
                           WHEN a.id IS NOT NULL THEN 'ROLE_ADMINISTRADOR'
                           ELSE NULL
                       END,
                       CASE
                           WHEN j.id IS NOT NULL THEN 'ROLE_CAJERO'
                           ELSE NULL
                       END
                   ], NULL) AS roles
            FROM usuario u
            LEFT JOIN cliente c ON c.id = u.id
            LEFT JOIN parqueo p ON p.id_cliente = c.id AND p.estado != 'Inactivo'
            LEFT JOIN administrador a ON a.id = u.id AND a.es_activo = TRUE
            LEFT JOIN cajero j ON j.id = u.id AND j.es_activo = TRUE
            WHERE u.username = 'allRoles'
              AND (
                (c.id IS NOT NULL AND p.id IS NOT NULL) OR
                a.id IS NOT NULL OR
                j.id IS NOT NULL
            )
            LIMIT 1;
                        """, nativeQuery = true)
    Optional<List<Object[]>> findRawUsuarioConRolesByUsername(@Param("username") String username);

    @Query(value = """
            SELECT
              u.id, u.nombre, u.apellido,u.ci, u.foto, c.tipo, p.estado,
              CASE WHEN a.id IS NOT NULL THEN 'ADMINISTRADOR' ELSE NULL END AS rol_admin,
              CASE WHEN ca.id IS NOT NULL THEN 'CAJERO' ELSE NULL END AS rol_cajero,
              CASE WHEN cl.id IS NOT NULL THEN 'CLIENTE' ELSE NULL END AS rol_cliente,
              a.es_activo,     -- 10
              ca.es_activo     -- 11
              FROM usuario u
            LEFT JOIN administrador a ON u.id = a.id
            LEFT JOIN cajero ca ON u.id = ca.id
            LEFT JOIN cliente cl ON u.id = cl.id
            LEFT JOIN cliente c ON u.id = c.id
            LEFT JOIN parqueo p ON c.id = p.id_cliente
            ORDER BY u.ctid DESC;
            """, nativeQuery = true)
    List<Object[]> obtenerUsuariosConRolesRaw();

    @Query(value = """
            SELECT
                p.id_cliente,
                p.fecha_inicio,
                COALESCE(array_agg(pagos.mes_pagado ORDER BY pagos.mes_pagado), '{}') AS meses_pagados
            FROM parqueo p
            LEFT JOIN pago_parqueo pg ON pg.id_parqueo = p.id
            LEFT JOIN LATERAL (
                SELECT unnest(pg.meses) AS mes_pagado
            ) pagos ON true
            GROUP BY p.id_cliente, p.fecha_inicio
                            """, nativeQuery = true)
    List<Object[]> obtenerFechasYPagosClientes();

}
