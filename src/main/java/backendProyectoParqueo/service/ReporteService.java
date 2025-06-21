package backendProyectoParqueo.service;

import java.math.BigDecimal;
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
import backendProyectoParqueo.dto.VehiculoDTO;
import backendProyectoParqueo.model.PagoParqueo;
import backendProyectoParqueo.model.Parqueo;
import backendProyectoParqueo.model.Tarifa;
import backendProyectoParqueo.model.Vehiculo;
import backendProyectoParqueo.repository.PagoParqueoRepository;
import backendProyectoParqueo.repository.ParqueoRepository;
import backendProyectoParqueo.repository.TarifaRepository;
import backendProyectoParqueo.repository.VehiculoRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ReporteService {

  private final ParqueoRepository parqueoRepository;
  private final PagoParqueoRepository pagoParqueoRepository;
  private final TarifaRepository tarifaRepository;
  private final VehiculoRepository vehiculoRepository;

  private static final DateTimeFormatter PERIODO_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy");

  @Autowired
  public ReporteService(ParqueoRepository parqueoRepository,
      PagoParqueoRepository pagoParqueoRepository,
      TarifaRepository tarifaRepository,
      VehiculoRepository vehiculoRepository) {
    this.parqueoRepository = parqueoRepository;
    this.pagoParqueoRepository = pagoParqueoRepository;
    this.tarifaRepository = tarifaRepository;
    this.vehiculoRepository = vehiculoRepository;
  }

  @Transactional(readOnly = true)
  public ReporteEstadoCuentaVehiculoDTO getEstadoCuentaVehiculo(UUID clienteId, String placa) {
    List<Parqueo> parqueos = parqueoRepository.findActivosByClienteIdAndVehiculoPlacaWithDetails(clienteId, placa);

    if (parqueos.isEmpty()) {
      throw new EntityNotFoundException(
          "Parqueo activo no encontrado para el cliente " + clienteId + " y placa " + placa);
    }
    Parqueo parqueo = parqueos.stream()
        .max(Comparator.comparing(Parqueo::getFechaInicio))
        .orElseThrow(() -> new EntityNotFoundException("No se pudo determinar el parqueo más reciente para " + clienteId + " y placa " + placa));
    return generarReporteParaParqueo(parqueo);
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
      return new ArrayList<>();
    }

    return parqueosDelVehiculo.stream()
        .map(this::generarReporteParaParqueo)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<ReporteEstadoCuentaVehiculoDTO> getEstadosCuentaParaVehiculoPrincipalDeCliente(UUID clienteId) {
    List<VehiculoDTO> vehiculosActivosDTO = vehiculoRepository.obtenerVehiculosPorClienteId(clienteId);

    if (vehiculosActivosDTO.isEmpty()) {
      return new ArrayList<>();
    }
    VehiculoDTO vehiculoSeleccionado = vehiculosActivosDTO.get(0);
    String placaSeleccionada = vehiculoSeleccionado.getPlaca();

    if (placaSeleccionada == null || placaSeleccionada.trim().isEmpty()) {
      return new ArrayList<>();
    }
    return getEstadosCuentaPorClienteYPlaca(clienteId, placaSeleccionada);
  }


  private ReporteEstadoCuentaVehiculoDTO generarReporteParaParqueo(Parqueo parqueo) {
    ReporteEstadoCuentaVehiculoDTO reporte = new ReporteEstadoCuentaVehiculoDTO();

    if (parqueo.getVehiculosAsignados() == null || parqueo.getVehiculosAsignados().isEmpty() ||
        parqueo.getVehiculosAsignados().get(0).getVehiculo() == null) {
        reporte.setPlacaVehiculo("ERROR_VEHICULO_NO_ASIGNADO");
        reporte.setTipoVehiculo(parqueo.getTipo()); 
        reporte.setTipoCliente(parqueo.getCliente() != null ? parqueo.getCliente().getTipo() : "Desconocido");
        reporte.setUltimaActualizacion(LocalDateTime.now());
        reporte.setDetallesMes(new ArrayList<>());
        reporte.setSaldoTotalPendiente(BigDecimal.ZERO);
        return reporte;
    }
    reporte.setPlacaVehiculo(parqueo.getVehiculosAsignados().get(0).getVehiculo().getPlaca());

    if (parqueo.getCliente() != null && parqueo.getCliente().getTipo() != null) {
        reporte.setTipoCliente(parqueo.getCliente().getTipo());
    } else {
        reporte.setTipoCliente("Cliente Desconocido");
    }

    
    reporte.setTipoVehiculo(parqueo.getTipo());
    reporte.setUltimaActualizacion(LocalDateTime.now());
    Tarifa tarifaAplicable = null;

    if (reporte.getTipoCliente() != null && !reporte.getTipoCliente().contains("Desconocido") &&
        reporte.getTipoVehiculo() != null) { 
            tarifaAplicable = tarifaRepository.obtenerTarifaVigente(
                reporte.getTipoCliente(),
                reporte.getTipoVehiculo()
            );
    }

    BigDecimal montoMensualSegunTarifa = BigDecimal.ZERO;
    if (tarifaAplicable != null) {
      montoMensualSegunTarifa = tarifaAplicable.getMonto();
    } else {
        if (parqueo.getEstado() != Parqueo.EstadoParqueo.Inactivo &&
            (reporte.getTipoCliente() != null && !reporte.getTipoCliente().contains("Desconocido")) &&
            (reporte.getTipoVehiculo() != null) ) {
        }
    }

    List<DetalleMesEstadoCuentaDTO> detallesMes = new ArrayList<>();
    Set<YearMonth> mesesPagadosRegistrados = new HashSet<>();
    YearMonth ultimoMesPagadoRegistrado = null;

    List<PagoParqueo> pagosDelParqueo = pagoParqueoRepository.findAllByParqueoIdWithTarifa(parqueo.getId());

    for (PagoParqueo pago : pagosDelParqueo) {
      if (pago.getMeses() != null && pago.getMeses().length > 0) {
        for (LocalDate mesSqlDate : pago.getMeses()) {
          YearMonth periodoPago = YearMonth.from(mesSqlDate);
          mesesPagadosRegistrados.add(periodoPago);

          if (ultimoMesPagadoRegistrado == null || periodoPago.isAfter(ultimoMesPagadoRegistrado)) {
            ultimoMesPagadoRegistrado = periodoPago;
          }
          detallesMes.add(new DetalleMesEstadoCuentaDTO(
              periodoPago.format(PERIODO_FORMATTER),
              "Pagado",
              montoMensualSegunTarifa,
              pago.getFechaHoraPago() != null ? pago.getFechaHoraPago().toLocalDateTime() : null));
        }
      }
    }


    BigDecimal saldoTotalPendiente = BigDecimal.ZERO;
    LocalDate fechaInicioParqueo = parqueo.getFechaInicio();
    if (fechaInicioParqueo == null) {
        reporte.setDetallesMes(detallesMes);
        reporte.setSaldoTotalPendiente(BigDecimal.ZERO);
        return reporte;
    }
    YearMonth mesInicioParqueoReal = YearMonth.from(fechaInicioParqueo);
    YearMonth mesDeInicioParaPendientesCalculado;
    if (ultimoMesPagadoRegistrado != null) {
        mesDeInicioParaPendientesCalculado = ultimoMesPagadoRegistrado.plusMonths(1);
    } else {
        mesDeInicioParaPendientesCalculado = mesInicioParqueoReal;
    }


    YearMonth mesDeInicioParaPendientes = mesDeInicioParaPendientesCalculado;
    YearMonth mesFinCalculoGeneral;
    if (parqueo.getEstado() == Parqueo.EstadoParqueo.Inactivo) {
      mesFinCalculoGeneral = (ultimoMesPagadoRegistrado != null) ? ultimoMesPagadoRegistrado : mesInicioParqueoReal;
    } else {
      mesFinCalculoGeneral = YearMonth.now();
    }
    

    if (mesFinCalculoGeneral.isBefore(mesInicioParqueoReal)) {
      mesFinCalculoGeneral = mesInicioParqueoReal;
    }

    YearMonth mesIteradorPendientes = mesDeInicioParaPendientes;
    while (!mesIteradorPendientes.isAfter(mesFinCalculoGeneral)) {
        if (!mesesPagadosRegistrados.contains(mesIteradorPendientes)) {
            detallesMes.add(new DetalleMesEstadoCuentaDTO(
                mesIteradorPendientes.format(PERIODO_FORMATTER),
                "Pendiente",
                montoMensualSegunTarifa,
                null));
            saldoTotalPendiente = saldoTotalPendiente.add(montoMensualSegunTarifa);
        } 
        mesIteradorPendientes = mesIteradorPendientes.plusMonths(1);
    }

    detallesMes.sort(Comparator.comparing(detalle -> YearMonth.parse(detalle.getPeriodo(), PERIODO_FORMATTER)));
    reporte.setDetallesMes(detallesMes);
    reporte.setSaldoTotalPendiente(saldoTotalPendiente);

    return reporte;
  }
}