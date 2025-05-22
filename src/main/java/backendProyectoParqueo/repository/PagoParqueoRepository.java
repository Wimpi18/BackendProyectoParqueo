package backendProyectoParqueo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backendProyectoParqueo.model.PagoParqueo;

@Repository
public interface PagoParqueoRepository extends JpaRepository<PagoParqueo, Long> {
    @Query(value = """
            SELECT
                p.fecha_inicio AS fechaInicio,
                pp.meses[array_length(pp.meses, 1)] AS ultimoMesPagado
            FROM pago_parqueo pp
            RIGHT JOIN parqueo p ON p.id = pp.id_parqueo
            INNER JOIN cliente c ON c.id = p.id_cliente
            WHERE c.id = :clienteId AND p.id = :parqueoId
            ORDER BY pp.id DESC
            LIMIT 1
            """, nativeQuery = true)
    Object obtenerUltimoPago(
            @Param("clienteId") UUID clienteId,
            @Param("parqueoId") Long parqueoId);

    
}
