// src/main/java/backendProyectoParqueo/repository/custom/HistorialRepositoryImpl.java
package backendProyectoParqueo.repository.custom;

import backendProyectoParqueo.dto.HistorialTarifaDTO;
import backendProyectoParqueo.enums.TipoVehiculo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<HistorialTarifaDTO> query = cb.createQuery(HistorialTarifaDTO.class);
        Root<backendProyectoParqueo.model.Tarifa> tarifa = query.from(backendProyectoParqueo.model.Tarifa.class);
        Join<?, ?> administrador = tarifa.join("administrador");
        Join<?, ?> usuario = administrador.join("usuario");

        List<Predicate> predicates = new ArrayList<>();

        if (tipoVehiculo != null) {
            predicates.add(cb.equal(tarifa.get("tipoVehiculo"), tipoVehiculo));
        }

        if (tipoCliente != null) {
            predicates.add(cb.equal(cb.lower(tarifa.get("tipoCliente")), tipoCliente.toLowerCase()));
        }

        if (nombreUsuario != null) {
            Expression<String> nombreCompleto = cb.concat(usuario.get("nombre"),
                    cb.concat(" ", usuario.get("apellido")));
            predicates.add(cb.like(cb.lower(nombreCompleto), "%" + nombreUsuario.toLowerCase() + "%"));
        }

        if (fechaInicio != null) {
            predicates.add(cb.greaterThanOrEqualTo(tarifa.get("fechaInicio"), fechaInicio));
        }

        if (fechaFin != null) {
            predicates.add(cb.lessThanOrEqualTo(tarifa.get("fechaInicio"), fechaFin));
        }

        if (montoMinimo != null) {
            predicates.add(cb.greaterThanOrEqualTo(tarifa.get("monto"), montoMinimo));
        }

        if (montoMaximo != null) {
            predicates.add(cb.lessThanOrEqualTo(tarifa.get("monto"), montoMaximo));
        }

        query.multiselect(cb.construct(
                HistorialTarifaDTO.class,
                tarifa.get("tipoVehiculo"),
                tarifa.get("tipoCliente"),
                tarifa.get("monto"),
                cb.concat(usuario.get("nombre"), cb.concat(" ", usuario.get("apellido"))),
                tarifa.get("fechaInicio"))).where(cb.and(predicates.toArray(new Predicate[0])))
                .orderBy(cb.desc(tarifa.get("fechaInicio")));

        List<HistorialTarifaDTO> resultados = entityManager.createQuery(query).getResultList();
        return resultados;

    }
}