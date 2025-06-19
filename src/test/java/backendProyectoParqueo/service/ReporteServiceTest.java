package backendProyectoParqueo.service;

import java.math.BigDecimal;
import java.sql.Timestamp; // Usaremos este tipo si es lo que devuelve el repo
import java.time.LocalDate; // Para usar .getLabel()
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import backendProyectoParqueo.dto.ReporteEstadoCuentaVehiculoDTO;
import backendProyectoParqueo.enums.TipoCliente;
import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.model.Cliente;
import backendProyectoParqueo.model.PagoParqueo;
import backendProyectoParqueo.model.Parqueo;
import backendProyectoParqueo.model.Tarifa;
import backendProyectoParqueo.model.Vehiculo;
import backendProyectoParqueo.repository.PagoParqueoRepository;
import backendProyectoParqueo.repository.ParqueoRepository;
import backendProyectoParqueo.repository.TarifaRepository;
import backendProyectoParqueo.repository.VehiculoRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ReporteServiceTest {

  @Mock
  private ParqueoRepository parqueoRepository;
  @Mock
  private PagoParqueoRepository pagoParqueoRepository;
  @Mock
  private TarifaRepository tarifaRepository;
  @Mock
  private VehiculoRepository vehiculoRepository;

  @InjectMocks
  private ReporteService reporteService;

  private UUID clienteId;
  private String placa;
  private Vehiculo vehiculoModelo;
  private Cliente clienteModelo;
  private Parqueo parqueoActivoModelo;
  private Parqueo parqueoInactivoSinPagosModelo;
  private Parqueo parqueoInactivoConPagosModelo;
  private Tarifa tarifaModelo;
  private PagoParqueo pago1Modelo, pago2Modelo, pagoParaInactivoModelo;

  public static final DateTimeFormatter PERIODO_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy");

  @BeforeEach
  void setUp() {
    clienteId = UUID.randomUUID();
    placa = "ABC-123";

    clienteModelo = new Cliente();
    clienteModelo.setId(clienteId);
    clienteModelo.setTipo(TipoCliente.ADMINISTRATIVO.getLabel());

    vehiculoModelo = new Vehiculo();
    vehiculoModelo.setId(1L);
    vehiculoModelo.setPlaca(placa);
    vehiculoModelo.setTipo(TipoVehiculo.Auto);
    vehiculoModelo.setMarca("Toyota");
    vehiculoModelo.setModelo("Corolla");
    vehiculoModelo.setColor("Rojo");
    // No inicializamos fotoDelantera/Trasera a menos que sea necesario

    parqueoActivoModelo = new Parqueo();
    parqueoActivoModelo.setId(10L);
    parqueoActivoModelo.setCliente(clienteModelo);
    parqueoActivoModelo.setEstado(Parqueo.EstadoParqueo.Activo);
    parqueoActivoModelo.setFechaInicio(LocalDate.now().minusMonths(2).withDayOfMonth(1));

    parqueoInactivoSinPagosModelo = new Parqueo();
    parqueoInactivoSinPagosModelo.setId(11L);
    parqueoInactivoSinPagosModelo.setCliente(clienteModelo);
    parqueoInactivoSinPagosModelo.setEstado(Parqueo.EstadoParqueo.Inactivo);
    parqueoInactivoSinPagosModelo.setFechaInicio(LocalDate.now().minusMonths(5).withDayOfMonth(1));
    // Sin fechaFin explícita

    parqueoInactivoConPagosModelo = new Parqueo();
    parqueoInactivoConPagosModelo.setId(12L);
    parqueoInactivoConPagosModelo.setCliente(clienteModelo);
    parqueoInactivoConPagosModelo.setEstado(Parqueo.EstadoParqueo.Inactivo);
    parqueoInactivoConPagosModelo.setFechaInicio(LocalDate.now().minusMonths(6).withDayOfMonth(1));

    tarifaModelo = new Tarifa();
    tarifaModelo.setId(1);
    tarifaModelo.setTipoCliente(TipoCliente.ADMINISTRATIVO.getLabel());
    tarifaModelo.setTipoVehiculo(TipoVehiculo.Auto);
    tarifaModelo.setMonto(new BigDecimal("100.00"));
    tarifaModelo.setFechaInicio(LocalDateTime.now().minusYears(1));

    pago1Modelo = new PagoParqueo();
    pago1Modelo.setId(100L);
    pago1Modelo.setParqueo(parqueoActivoModelo);
    pago1Modelo.setTarifa(tarifaModelo);
    pago1Modelo.setMontoPagado(new BigDecimal("100.00")); // Monto total del pago
    pago1Modelo.setFechaHoraPago(Timestamp.valueOf(LocalDateTime.now().minusMonths(2).withDayOfMonth(5)));
    pago1Modelo.setMeses(new LocalDate[] { LocalDate.now().minusMonths(2).withDayOfMonth(1) });

    pago2Modelo = new PagoParqueo();
    pago2Modelo.setId(101L);
    pago2Modelo.setParqueo(parqueoActivoModelo);
    pago2Modelo.setTarifa(tarifaModelo);
    pago2Modelo.setMontoPagado(new BigDecimal("100.00")); // Monto total del pago
    pago2Modelo.setFechaHoraPago(Timestamp.valueOf(LocalDateTime.now().minusMonths(1).withDayOfMonth(5)));
    pago2Modelo.setMeses(new LocalDate[] { LocalDate.now().minusMonths(1).withDayOfMonth(1) });

    pagoParaInactivoModelo = new PagoParqueo();
    pagoParaInactivoModelo.setId(102L);
    pagoParaInactivoModelo.setParqueo(parqueoInactivoConPagosModelo);
    pagoParaInactivoModelo.setTarifa(tarifaModelo);
    pagoParaInactivoModelo.setMontoPagado(new BigDecimal("100.00"));
    pagoParaInactivoModelo.setFechaHoraPago(Timestamp.valueOf(LocalDateTime.now().minusMonths(5).withDayOfMonth(5)));
    pagoParaInactivoModelo.setMeses(new LocalDate[] { LocalDate.now().minusMonths(5).withDayOfMonth(1) });
  }


  @Test
  @DisplayName("getEstadoCuentaVehiculo - Devuelve reporte para parqueo activo")
  void getEstadoCuentaVehiculo_parqueoActivoExiste_devuelveReporte() {
    when(parqueoRepository.findActivosByClienteIdAndVehiculoPlacaWithDetails(clienteId, placa))
        .thenReturn(List.of(parqueoActivoModelo));
    when(pagoParqueoRepository.findAllByParqueoIdWithTarifa(parqueoActivoModelo.getId()))
        .thenReturn(List.of(pago1Modelo, pago2Modelo));
    when(tarifaRepository.obtenerTarifaVigente(clienteModelo.getTipo(), vehiculoModelo.getTipo()))
        .thenReturn(tarifaModelo);

    ReporteEstadoCuentaVehiculoDTO reporte = reporteService.getEstadoCuentaVehiculo(clienteId, placa);

    assertNotNull(reporte);
    assertEquals(placa, reporte.getPlacaVehiculo());
    assertEquals(TipoCliente.ADMINISTRATIVO.getLabel(), reporte.getTipoCliente());

    String periodoActual = YearMonth.now().format(PERIODO_FORMATTER);
    assertTrue(
        reporte.getDetallesMes().stream()
            .anyMatch(d -> d.getPeriodo().equals(periodoActual) && d.getEstado().equals("Pendiente")),
        "El mes actual debería estar pendiente");
    assertEquals(new BigDecimal("100.00"), reporte.getSaldoTotalPendiente());
  }

  @Test
  @DisplayName("getEstadosCuentaPorClienteYPlaca - Devuelve lista de reportes")
  void getEstadosCuentaPorClienteYPlaca_existenDatos_devuelveListaReportes() {
    when(parqueoRepository.findAllByClienteIdAndVehiculoPlacaWithDetailsQuery(clienteId, placa))
        .thenReturn(List.of(parqueoActivoModelo)); // Solo un parqueo para este cliente y placa
    when(pagoParqueoRepository.findAllByParqueoIdWithTarifa(parqueoActivoModelo.getId()))
        .thenReturn(List.of(pago1Modelo));
    when(tarifaRepository.obtenerTarifaVigente(clienteModelo.getTipo(), vehiculoModelo.getTipo()))
        .thenReturn(tarifaModelo);

    List<ReporteEstadoCuentaVehiculoDTO> reportes = reporteService.getEstadosCuentaPorClienteYPlaca(clienteId, placa);

    assertNotNull(reportes);
    assertEquals(1, reportes.size());
    assertEquals(placa, reportes.get(0).getPlacaVehiculo());
  }

  @Test
  @DisplayName("getEstadosCuentaPorPlacaVehiculo - Devuelve lista de reportes para una placa")
  void getEstadosCuentaPorPlacaVehiculo_vehiculoConParqueos_devuelveListaReportes() {
    when(vehiculoRepository.findByPlaca(placa)).thenReturn(Optional.of(vehiculoModelo));
    when(parqueoRepository.findAllByVehiculoIdWithDetails(vehiculoModelo.getId()))
        .thenReturn(List.of(parqueoActivoModelo, parqueoInactivoSinPagosModelo));

    when(pagoParqueoRepository.findAllByParqueoIdWithTarifa(parqueoActivoModelo.getId()))
        .thenReturn(List.of(pago1Modelo, pago2Modelo));
    when(pagoParqueoRepository.findAllByParqueoIdWithTarifa(parqueoInactivoSinPagosModelo.getId()))
        .thenReturn(Collections.emptyList());
    when(tarifaRepository.obtenerTarifaVigente(anyString(), any(TipoVehiculo.class))).thenReturn(tarifaModelo);

    List<ReporteEstadoCuentaVehiculoDTO> reportes = reporteService.getEstadosCuentaPorPlacaVehiculo(placa);

    assertNotNull(reportes);
    assertEquals(2, reportes.size());
  }

  @Test
  @DisplayName("generarReporteParaParqueo - Parqueo inactivo sin pagos, mesFinCalculo es mesInicioParqueo")
  void generarReporteParaParqueo_inactivoSinPagos_calculaCorrectamente() {
    when(pagoParqueoRepository.findAllByParqueoIdWithTarifa(parqueoInactivoSinPagosModelo.getId()))
        .thenReturn(Collections.emptyList());
    when(tarifaRepository.obtenerTarifaVigente(clienteModelo.getTipo(), vehiculoModelo.getTipo()))
        .thenReturn(tarifaModelo);

    // Llamamos a través de un método público que usa generarReporteParaParqueo
    when(vehiculoRepository.findByPlaca(placa)).thenReturn(Optional.of(vehiculoModelo));
    when(parqueoRepository.findAllByVehiculoIdWithDetails(vehiculoModelo.getId()))
        .thenReturn(List.of(parqueoInactivoSinPagosModelo));

    List<ReporteEstadoCuentaVehiculoDTO> reportes = reporteService.getEstadosCuentaPorPlacaVehiculo(placa);
    ReporteEstadoCuentaVehiculoDTO reporte = reportes.get(0);

    long mesesPendientes = reporte.getDetallesMes().stream().filter(d -> d.getEstado().equals("Pendiente")).count();
    // Inició hace 5 meses, inactivo, sin pagos. mesFinCalculo = mesInicioParqueo.
    // Solo debería haber 1 detalle (el de inicio), pendiente.
    assertEquals(1, reporte.getDetallesMes().size(), "Debería haber solo un detalle de mes (el de inicio)");
    assertEquals(1, mesesPendientes, "El único mes (de inicio) debería estar pendiente");
    assertEquals(tarifaModelo.getMonto(), reporte.getSaldoTotalPendiente());
  }

  @Test
  @DisplayName("generarReporteParaParqueo - Parqueo inactivo con un pago, mesFinCalculo es ultimoMesPagado")
  void generarReporteParaParqueo_inactivoConPagos_calculaCorrectamente() {
    // parqueoInactivoConPagosModelo inició hace 6 meses.
    // pagoParaInactivoModelo es para el mes de hace 5 meses.
    // mesFinCalculo será el mes de hace 5 meses.
    // Meses a considerar en el bucle de pendientes: -6 (inicio), -5 (pagado).
    when(pagoParqueoRepository.findAllByParqueoIdWithTarifa(parqueoInactivoConPagosModelo.getId()))
        .thenReturn(List.of(pagoParaInactivoModelo));
    when(tarifaRepository.obtenerTarifaVigente(clienteModelo.getTipo(), vehiculoModelo.getTipo()))
        .thenReturn(tarifaModelo);

    when(vehiculoRepository.findByPlaca(placa)).thenReturn(Optional.of(vehiculoModelo));
    when(parqueoRepository.findAllByVehiculoIdWithDetails(vehiculoModelo.getId()))
        .thenReturn(List.of(parqueoInactivoConPagosModelo));

    List<ReporteEstadoCuentaVehiculoDTO> reportes = reporteService.getEstadosCuentaPorPlacaVehiculo(placa);
    ReporteEstadoCuentaVehiculoDTO reporte = reportes.get(0);

    // Detalles: 1 pendiente (mes de inicio -6), 1 pagado (mes -5).
    assertEquals(2, reporte.getDetallesMes().size());

    long pagados = reporte.getDetallesMes().stream().filter(d -> d.getEstado().equals("Pagado")).count();
    long pendientes = reporte.getDetallesMes().stream().filter(d -> d.getEstado().equals("Pendiente")).count();

    assertEquals(1, pagados);
    assertEquals(1, pendientes); // El mes de inicio (hace 6 meses) queda pendiente.
    assertEquals(tarifaModelo.getMonto(), reporte.getSaldoTotalPendiente());
  }

  // Casos de excepción
  @Test
  @DisplayName("getEstadoCuentaVehiculo - Lanza EntityNotFoundException si parqueo activo no existe")
  void getEstadoCuentaVehiculo_parqueoNoExiste_lanzaExcepcion() {
    when(parqueoRepository.findActivosByClienteIdAndVehiculoPlacaWithDetails(clienteId, placa))
        .thenReturn(List.of(parqueoActivoModelo));
    assertThrows(EntityNotFoundException.class, () -> reporteService.getEstadoCuentaVehiculo(clienteId, placa));
  }

  @Test
  @DisplayName("getEstadosCuentaPorPlacaVehiculo - Lanza EntityNotFoundException si vehículo no existe")
  void getEstadosCuentaPorPlacaVehiculo_vehiculoNoExiste_lanzaExcepcion() {
    when(vehiculoRepository.findByPlaca(placa)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> reporteService.getEstadosCuentaPorPlacaVehiculo(placa));
  }
}