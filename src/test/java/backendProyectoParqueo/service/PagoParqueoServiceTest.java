package backendProyectoParqueo.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import backendProyectoParqueo.dto.PagoParqueoDTO;
import backendProyectoParqueo.enums.TipoCliente;
import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.exception.BusinessException;
import backendProyectoParqueo.model.Cliente;
import backendProyectoParqueo.model.PagoParqueo;
import backendProyectoParqueo.model.Parqueo;
import backendProyectoParqueo.model.Tarifa;
import backendProyectoParqueo.model.Vehiculo;
import backendProyectoParqueo.repository.PagoParqueoRepository;

public class PagoParqueoServiceTest {
    @Mock
    private PagoParqueoRepository pagoParqueoRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private TarifaService tarifaService;

    @Mock
    private ParqueoService parqueoService;

    @InjectMocks
    private PagoParqueoService pagoParqueoService;

    Cliente cliente;
    Vehiculo vehiculo;
    Parqueo parqueo;
    Tarifa tarifa;
    PagoParqueoDTO dto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UUID clienteId = UUID.randomUUID();
        cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setTipo(TipoCliente.ADMINISTRATIVO.getLabel());

        vehiculo = new Vehiculo();
        vehiculo.setTipo(TipoVehiculo.Auto);

        parqueo = new Parqueo();
        parqueo.setId(1L);
        parqueo.setVehiculo(vehiculo);

        tarifa = new Tarifa();
        tarifa.setTipoCliente(TipoCliente.ADMINISTRATIVO.getLabel());
        tarifa.setTipoVehiculo(TipoVehiculo.Auto);
        tarifa.setMonto(new BigDecimal("0"));

        dto = new PagoParqueoDTO();
        dto.setIdCliente(clienteId);
        dto.setIdParqueo(1L);
        dto.setIdTarifa(1);
        dto.setMontoPagado(new BigDecimal("0"));
        dto.setFechaHoraPago(Timestamp.from(Instant.now()));
        dto.setMeses(new LocalDate[] { LocalDate.now(), LocalDate.now().plusMonths(1) });
        dto.setNroEspacioPagado(1);
    }

    @Test
    void create_DeberiaLanzarBusinessException_SiMontoNoEsCorrecto() {
        // Arrange pago de dos meses con una tarifa de 10
        tarifa.setMonto(new BigDecimal("10.00"));
        dto.setMontoPagado(new BigDecimal("1.00"));
        when(clienteService.findById(cliente.getId())).thenReturn(cliente);
        when(parqueoService.findById(parqueo.getId())).thenReturn(parqueo);
        when(tarifaService.findTarifaByTipoClienteYVehiculo(cliente.getTipo(), parqueo.getVehiculo().getTipo()))
                .thenReturn(tarifa);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> pagoParqueoService.create(dto));
        assertEquals("El monto pagado no coincide con la tarifa multiplicada por la cantidad de meses.",
                exception.getMessage());
    }

    @Test
    void create_DeberiaGuardarPagoCorrectamente_SiMontoEsCorrecto() {
        // Arrange pago de dos meses con una tarifa de 10
        tarifa.setMonto(new BigDecimal("10.00"));
        dto.setMontoPagado(new BigDecimal("20.00"));

        when(clienteService.findById(cliente.getId())).thenReturn(cliente);
        when(parqueoService.findById(parqueo.getId())).thenReturn(parqueo);
        when(tarifaService.findTarifaByTipoClienteYVehiculo(
                cliente.getTipo(),
                parqueo.getVehiculo().getTipo()))
                .thenReturn(tarifa);

        // Simula el objeto que se espera que retorne el repositorio
        PagoParqueo pagoGuardado = new PagoParqueo();
        pagoGuardado.setMontoPagado(dto.getMontoPagado());
        pagoGuardado.setFechaHoraPago(dto.getFechaHoraPago());
        pagoGuardado.setMeses(dto.getMeses());

        when(pagoParqueoRepository.save(org.mockito.ArgumentMatchers.any(PagoParqueo.class)))
                .thenReturn(pagoGuardado);

        // Act
        PagoParqueo result = pagoParqueoService.create(dto);

        // Assert
        assertNotNull(result);
        assertEquals(dto.getMontoPagado(), result.getMontoPagado());
        assertEquals(dto.getMeses().length, result.getMeses().length);
    }
}
