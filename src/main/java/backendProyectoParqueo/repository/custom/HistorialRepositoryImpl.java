package backendProyectoParqueo.repository.custom;

import backendProyectoParqueo.dto.HistorialTarifaDTO;
import backendProyectoParqueo.enums.TipoVehiculo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class HistorialRepositoryImpl implements HistorialRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<HistorialTarifaDTO> filtrarHistorialTarifas(
            TipoVehiculo tipoVehiculo,
            String tipoCliente,
            String nombreUsuario,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            BigDecimal montoMinimo,
            BigDecimal montoMaximo) {

        StringBuilder jpql = new StringBuilder("""
                    SELECT new backendProyectoParqueo.dto.HistorialTarifaDTO(
                        t.tipoVehiculo,
                        t.tipoCliente,
                        t.monto,
                        CONCAT(u.nombre, CONCAT(' ', u.apellido)),
                        t.fechaInicio
                    )
                    FROM Tarifa t
                    JOIN t.administrador a
                    JOIN a.usuario u
                    WHERE 1 = 1
                """);

        // Filtros dinámicos
        if (tipoVehiculo != null) {
            jpql.append(" AND t.tipoVehiculo = :tipoVehiculo");
        }
        if (tipoCliente != null) {
            jpql.append(" AND LOWER(t.tipoCliente) = :tipoCliente");
        }
        if (nombreUsuario != null) {
            jpql.append(" AND LOWER(CONCAT(u.nombre, ' ', u.apellido)) LIKE :nombreUsuario");
        }
        if (fechaInicio != null) {
            jpql.append(" AND t.fechaInicio >= :fechaInicio");
        }
        if (fechaFin != null) {
            jpql.append(" AND t.fechaInicio <= :fechaFin");
        }
        if (montoMinimo != null) {
            jpql.append(" AND t.monto >= :montoMinimo");
        }
        if (montoMaximo != null) {
            jpql.append(" AND t.monto <= :montoMaximo");
        }

        jpql.append(" ORDER BY t.fechaInicio DESC");

        // Crear consulta
        var query = entityManager.createQuery(jpql.toString(), HistorialTarifaDTO.class);

        // Asignar parámetros
        if (tipoVehiculo != null) {
            query.setParameter("tipoVehiculo", tipoVehiculo);
        }
        if (tipoCliente != null) {
            query.setParameter("tipoCliente", tipoCliente.toLowerCase());
        }
        if (nombreUsuario != null) {
            query.setParameter("nombreUsuario", "%" + nombreUsuario.toLowerCase() + "%");
        }
        if (fechaInicio != null) {
            query.setParameter("fechaInicio", fechaInicio);
        }
        if (fechaFin != null) {
            query.setParameter("fechaFin", fechaFin);
        }
        if (montoMinimo != null) {
            query.setParameter("montoMinimo", montoMinimo);
        }
        if (montoMaximo != null) {
            query.setParameter("montoMaximo", montoMaximo);
        }

        return query.getResultList();
    }
}