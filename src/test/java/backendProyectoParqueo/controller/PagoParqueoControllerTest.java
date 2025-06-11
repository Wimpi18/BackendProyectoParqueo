package backendProyectoParqueo.controller;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import backendProyectoParqueo.dto.PagoParqueoDTO;

public class PagoParqueoControllerTest {

    private PagoParqueoDTO pagoParqueoDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        pagoParqueoDTO = new PagoParqueoDTO();
        pagoParqueoDTO.setIdCliente(UUID.fromString("44444444-4444-4444-4444-444444444444"));
        pagoParqueoDTO.setIdParqueo(1L);
        pagoParqueoDTO.setIdCajero(UUID.fromString("33333333-3333-3333-3333-333333333333"));
        // pagoParqueoDTO.setMontoPagado(100.0);
        pagoParqueoDTO.setFechaHoraPago(Timestamp.from(Instant.now()));
        pagoParqueoDTO.setMeses(new LocalDate[] { LocalDate.of(2025, 1, 1), LocalDate.of(2025, 2, 1) });
    }

    @Test
    void testCreatePagoParqueo() {
        /* // Arrange: Simula el resultado esperado del servicio
        when(pagoParqueoService.create(pagoParqueoDTO))
                .thenReturn(ResponseEntity.ok("Pago registrado con éxito"));

        // Act: Llama al método del controlador
        ResponseEntity<?> response = pagoParqueoController.createPagoParqueo(pagoParqueoDTO);

        // Assert: Verifica que se haya llamado correctamente
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Pago registrado con éxito", response.getBody());

        verify(pagoParqueoService, times(1)).create(pagoParqueoDTO); */
    }
}
