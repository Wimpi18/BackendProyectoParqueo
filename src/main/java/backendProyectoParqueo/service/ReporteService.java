// src/main/java/backendProyectoParqueo/service/ReporteService.java
package backendProyectoParqueo.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backendProyectoParqueo.dto.DetalleMesEstadoCuentaDTO;
import backendProyectoParqueo.dto.ReporteEstadoCuentaVehiculoDTO;
import backendProyectoParqueo.model.PagoParqueo;
import backendProyectoParqueo.model.Parqueo;
import backendProyectoParqueo.model.Tarifa;
import backendProyectoParqueo.model.Vehiculo; // Importar Vehiculo
import backendProyectoParqueo.repository.PagoParqueoRepository;
import backendProyectoParqueo.repository.ParqueoRepository;
import backendProyectoParqueo.repository.TarifaRepository;
import backendProyectoParqueo.repository.VehiculoRepository; // Importar VehiculoRepository
import jakarta.persistence.EntityNotFoundException;

@Service
public class ReporteService {

  private final ParqueoRepository parqueoRepository;
  private final PagoParqueoRepository pagoParqueoRepository;
  private final TarifaRepository tarifaRepository;
  private final VehiculoRepository vehiculoRepository; // Inyectar VehiculoRepository

  @Autowired
  public ReporteService(ParqueoRepository parqueoRepository,
      PagoParqueoRepository pagoParqueoRepository,
      TarifaRepository tarifaRepository,
      VehiculoRepository vehiculoRepository) { // Añadir al constructor
    this.parqueoRepository = parqueoRepository;
    this.pagoParqueoRepository = pagoParqueoRepository;
    this.tarifaRepository = tarifaRepository;
    this.vehiculoRepository = vehiculoRepository; // Asignar
  }

  @Transactional(readOnly = true)
  public ReporteEstadoCuentaVehiculoDTO getEstadoCuentaVehiculo(UUID clienteId, String placa) {
    Parqueo parqueo = parqueoRepository.findActivoByClienteIdAndVehiculoPlacaWithDetails(clienteId, placa)
        .orElseThrow(() -> new EntityNotFoundException(
            "Parqueo activo no encontrado para el cliente " + clienteId + " y placa " + placa));
    return generarReporteParaParqueo(parqueo);
  }

  @Transactional(readOnly = true)
  public List<Object> getTodosVehiculosDTOPorCliente(UUID clienteId) {
    // Asumiendo que vehiculoRepository tiene el método que devuelve
    // List<VehiculoDTO>
    return vehiculoRepository.obtenerVehiculosPorClienteId(clienteId);
  }

  @Transactional(readOnly = true)
  public List<ReporteEstadoCuentaVehiculoDTO> getEstadosCuentaPorClienteYPlaca(UUID clienteId, String placa) {
    List<Parqueo> parqueos = parqueoRepository.findAllByClienteIdAndVehiculoPlacaWithDetailsQuery(clienteId, placa); 

    if (parqueos.isEmpty()) {
      return new ArrayList<>();
    }

    return parqueos.stream()
        .map(this::generarReporteParaParqueo)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<ReporteEstadoCuentaVehiculoDTO> getEstadosCuentaPorPlacaVehiculo(String placa) {
    Vehiculo vehiculo = vehiculoRepository.findByPlaca(placa)
        .orElseThrow(() -> new EntityNotFoundException("Vehículo con placa " + placa + " no encontrado."));

    List<Parqueo> parqueosDelVehiculo = parqueoRepository.findAllByVehiculoIdWithDetails(vehiculo.getId());

    if (parqueosDelVehiculo.isEmpty()) {
      // Puedes decidir si devolver una lista vacía o lanzar una excepción/mensaje
      // específico
      // Por ahora, devolvemos lista vacía, el controller puede interpretarlo.
      return new ArrayList<>();
    }

    return parqueosDelVehiculo.stream()
        .map(this::generarReporteParaParqueo)
        .collect(Collectors.toList());
  }

  private ReporteEstadoCuentaVehiculoDTO generarReporteParaParqueo(Parqueo parqueo) {
    List<PagoParqueo> pagosDelParqueo = pagoParqueoRepository.findAllByParqueoIdWithTarifa(parqueo.getId());

    ReporteEstadoCuentaVehiculoDTO reporte = new ReporteEstadoCuentaVehiculoDTO();
    // ... (asignaciones iniciales a reporte: placa, tipoCliente, tipoVehiculo,
    // ultimaActualizacion) ...
    reporte.setPlacaVehiculo(parqueo.getVehiculo().getPlaca());

    if (parqueo.getCliente().getTipo() instanceof String) {
      reporte.setTipoCliente(parqueo.getCliente().getTipo());
    } else if (parqueo.getCliente().getTipo() instanceof String) {
      reporte.setTipoCliente((String) parqueo.getCliente().getTipo());
    } else if (parqueo.getCliente().getTipo() != null) {
      reporte.setTipoCliente(parqueo.getCliente().getTipo().toString());
    } else {
      reporte.setTipoCliente(null);
    }
    reporte.setTipoVehiculo(parqueo.getVehiculo().getTipo());
    reporte.setUltimaActualizacion(LocalDateTime.now());

    List<DetalleMesEstadoCuentaDTO> detallesMes = new ArrayList<>();
    Set<YearMonth> mesesPagadosRegistrados = new HashSet<>();
    DateTimeFormatter periodoFormatter = DateTimeFormatter.ofPattern("MM/yyyy");
    YearMonth ultimoMesPagadoRegistrado = null; // Para rastrear el último mes pagado

    for (PagoParqueo pago : pagosDelParqueo) {
      if (pago.getMeses() != null && pago.getMeses().length > 0) {
        BigDecimal montoPorMesEnEstePago = BigDecimal.valueOf(pago.getMontoPagado());

        for (Date mesSqlDate : pago.getMeses()) {
          LocalDate mesLocalDate = mesSqlDate.toLocalDate();
          YearMonth periodoPago = YearMonth.from(mesLocalDate);
          mesesPagadosRegistrados.add(periodoPago);

          // Actualizar el último mes pagado
          if (ultimoMesPagadoRegistrado == null || periodoPago.isAfter(ultimoMesPagadoRegistrado)) {
            ultimoMesPagadoRegistrado = periodoPago;
          }

          detallesMes.add(new DetalleMesEstadoCuentaDTO(
              periodoPago.format(periodoFormatter),
              "Pagado",
              montoPorMesEnEstePago,
              pago.getFechaHoraPago() != null ? pago.getFechaHoraPago().toLocalDateTime() : null));
        }
      }
    }

    BigDecimal saldoTotalPendiente = BigDecimal.ZERO;
    LocalDate fechaInicioParqueo = parqueo.getFechaInicio();
    YearMonth mesInicioParqueo = YearMonth.from(fechaInicioParqueo);

    YearMonth mesFinCalculo; // Declarar aquí

    if (parqueo.getEstado() == Parqueo.EstadoParqueo.Inactivo) {
      if (ultimoMesPagadoRegistrado != null) {
        // Si está inactivo y hay pagos, el fin de cálculo es el último mes pagado.
        mesFinCalculo = ultimoMesPagadoRegistrado;
      } else {
        // Si está inactivo y NO hay pagos, el fin de cálculo es el mes de inicio del
        // parqueo.
        // Esto significa que no se calcularán pendientes si se inactiva sin pagos.
        mesFinCalculo = mesInicioParqueo;
      }
    } else {
      // Si está Activo o Bloqueado, calcular hasta el mes actual.
      mesFinCalculo = YearMonth.now();
    }

    // Asegurarse de que mesFinCalculo no sea anterior a mesInicioParqueo
    // (podría pasar si se inactiva y el último pago fue antes del mes de inicio,
    // aunque es ilógico)
    if (mesFinCalculo.isBefore(mesInicioParqueo)) {
      mesFinCalculo = mesInicioParqueo;
    }

    Tarifa tarifaAplicable = tarifaRepository.obtenerTarifaVigente(
        parqueo.getCliente().getTipo(),
        parqueo.getVehiculo().getTipo());

    if (tarifaAplicable == null && parqueo.getEstado() != Parqueo.EstadoParqueo.Inactivo) {
      System.err.println("Advertencia: No se encontró tarifa vigente para cliente tipo " +
          parqueo.getCliente().getTipo() + " y vehículo tipo " +
          parqueo.getVehiculo().getTipo() + ". Los montos pendientes pueden no ser precisos para el parqueo ID: "
          + parqueo.getId());
    }

    YearMonth mesIterador = mesInicioParqueo;
    // Iterar solo hasta el mes de fin de cálculo determinado
    while (!mesIterador.isAfter(mesFinCalculo)) {
      if (!mesesPagadosRegistrados.contains(mesIterador)) {
        BigDecimal montoEsteMesPendiente = BigDecimal.ZERO;
        // Solo calcular pendiente si hay una tarifa aplicable
        // y el parqueo no estaba ya inactivo antes de este mesIterador (manejado por
        // mesFinCalculo)
        if (tarifaAplicable != null) {
          montoEsteMesPendiente = tarifaAplicable.getMonto();
        }
        detallesMes.add(new DetalleMesEstadoCuentaDTO(
            mesIterador.format(periodoFormatter),
            "Pendiente",
            montoEsteMesPendiente,
            null));
        saldoTotalPendiente = saldoTotalPendiente.add(montoEsteMesPendiente);
      }
      mesIterador = mesIterador.plusMonths(1);
    }

    detallesMes.sort(Comparator.comparing(detalle -> YearMonth.parse(detalle.getPeriodo(), periodoFormatter)));
    reporte.setDetallesMes(detallesMes);
    reporte.setSaldoTotalPendiente(saldoTotalPendiente);

    return reporte;
  }
}