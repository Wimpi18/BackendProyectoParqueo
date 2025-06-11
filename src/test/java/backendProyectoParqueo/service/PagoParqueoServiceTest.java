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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import backendProyectoParqueo.dto.PagoParqueoDTO;
import backendProyectoParqueo.enums.TipoCliente;
import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.exception.BusinessException;
import backendProyectoParqueo.model.Cajero;
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

        @Mock
        private CajeroService cajeroService;

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

                tarifa = new Tarifa();
                tarifa.setTipoCliente(TipoCliente.ADMINISTRATIVO.getLabel());
                tarifa.setTipoVehiculo(TipoVehiculo.Auto);
                tarifa.setMonto(new BigDecimal("0"));

                dto = new PagoParqueoDTO();
                dto.setIdCliente(clienteId);
                dto.setIdParqueo(1L);
                dto.setMontoPagado(new BigDecimal("0"));
                dto.setFechaHoraPago(Timestamp.from(Instant.now()));
                dto.setMeses(new LocalDate[] { LocalDate.now(), LocalDate.now().plusMonths(1) });
        }

        @Test
        @DisplayName("Debe lanzar una excepción si el monto pagado no coincide con la tarifa por la cantidad de meses")
        void When_MontoIncorrectoParaCantidadMeses_Expect_ThrowBusinessException() {
                // Arrange
                tarifa.setMonto(new BigDecimal("10.00"));
                dto.setMontoPagado(new BigDecimal("1.00"));
                when(clienteService.findById(cliente.getId())).thenReturn(cliente);
                when(parqueoService.findById(parqueo.getId())).thenReturn(parqueo);
                when(tarifaService.findTarifaByTipoClienteYVehiculo(cliente.getTipo(), parqueo.getTipo()))
                                .thenReturn(tarifa);

                // Act & Assert
                BusinessException exception = assertThrows(BusinessException.class,
                                () -> pagoParqueoService.create(dto));
                assertEquals("El monto pagado no coincide con la tarifa multiplicada por la cantidad de meses.",
                                exception.getMessage());
        }

        @Test
        @DisplayName("Debe guardar correctamente el pago cuando el monto es el correcto")
        void When_MontoCorrectoParaCantidadMeses_Expect_GuardarPagoCorrectamente() {
                // Arrange
                tarifa.setMonto(new BigDecimal("10.00"));
                dto.setMontoPagado(new BigDecimal("20.00"));

                when(clienteService.findById(cliente.getId())).thenReturn(cliente);
                when(parqueoService.findById(parqueo.getId())).thenReturn(parqueo);
                when(tarifaService.findTarifaByTipoClienteYVehiculo(
                                cliente.getTipo(),
                                parqueo.getTipo()))
                                .thenReturn(tarifa);

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

        @Test
        @DisplayName("Debe lanzar una excepción si el cliente no existe")
        void When_ClienteNoExiste_Expect_ThrowBusinessException() {
                // Arrange
                UUID clienteId = UUID.randomUUID();
                dto.setIdCliente(clienteId);

                when(clienteService.findById(clienteId)).thenThrow(
                                new BusinessException("El ID proporcionado no corresponde a un cliente válido.",
                                                "idCliente"));

                // Act & Assert
                BusinessException ex = assertThrows(BusinessException.class, () -> pagoParqueoService.create(dto));

                assertEquals("El ID proporcionado no corresponde a un cliente válido.", ex.getMessage());
        }

        @Test
        @DisplayName("Debe lanzar una excepción si el parqueo no existe")
        void When_ParqueoNoExiste_Expect_ThrowBusinessException() {
                // Arrange
                when(clienteService.findById(cliente.getId())).thenReturn(cliente);
                when(parqueoService.findById(dto.getIdParqueo()))
                                .thenThrow(new BusinessException("Parqueo no encontrado", "idParqueo"));

                // Act & Assert
                BusinessException exception = assertThrows(BusinessException.class,
                                () -> pagoParqueoService.create(dto));
                assertEquals("Parqueo no encontrado", exception.getMessage());
        }

        @Test
        @DisplayName("Debe buscar el cajero si el ID de cajero está presente")
        void When_IdCajeroPresente_Expect_BuscarYCargarCajero() {
                // Arrange
                UUID cajeroId = UUID.randomUUID();
                dto.setIdCajero(cajeroId);

                Cajero cajero = new Cajero();
                cajero.setId(cajeroId);

                when(clienteService.findById(cliente.getId())).thenReturn(cliente);
                when(parqueoService.findById(parqueo.getId())).thenReturn(parqueo);
                when(tarifaService.findTarifaByTipoClienteYVehiculo(cliente.getTipo(), parqueo.getTipo()))
                                .thenReturn(tarifa);
                when(cajeroService.findById(cajeroId)).thenReturn(cajero);

                PagoParqueo pagoMock = new PagoParqueo();
                pagoMock.setCajero(cajero);
                pagoMock.setMontoPagado(dto.getMontoPagado());
                pagoMock.setFechaHoraPago(dto.getFechaHoraPago());
                pagoMock.setMeses(dto.getMeses());

                when(pagoParqueoRepository.save(org.mockito.ArgumentMatchers.any())).thenReturn(pagoMock);

                // Act
                PagoParqueo result = pagoParqueoService.create(dto);

                // Assert
                assertNotNull(result.getCajero());
                assertEquals(cajeroId, result.getCajero().getId());
        }

        @Test
        @DisplayName("Debe guardar el pago correctamente cuando el ID de cajero es null")
        void When_IdCajeroEsNull_Expect_GuardarPagoSinCajero() {
                // Arrange
                tarifa.setMonto(new BigDecimal("10.00"));
                dto.setMontoPagado(new BigDecimal("20.00"));
                dto.setIdCajero(null); // Cajero nulo

                when(clienteService.findById(cliente.getId())).thenReturn(cliente);
                when(parqueoService.findById(parqueo.getId())).thenReturn(parqueo);
                when(tarifaService.findTarifaByTipoClienteYVehiculo(cliente.getTipo(), parqueo.getTipo()))
                                .thenReturn(tarifa);
                when(pagoParqueoRepository.save(org.mockito.ArgumentMatchers.any(PagoParqueo.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // Act
                PagoParqueo result = pagoParqueoService.create(dto);

                // Assert
                assertNotNull(result);
                assertEquals(dto.getMontoPagado(), result.getMontoPagado());
                assertEquals(dto.getMeses().length, result.getMeses().length);
                assertEquals(parqueo.getNroEspacio(), result.getNroEspacioPagado());
                assertEquals(tarifa, result.getTarifa());
                assertEquals(parqueo, result.getParqueo());
                assertEquals(null, result.getCajero());
        }
}
