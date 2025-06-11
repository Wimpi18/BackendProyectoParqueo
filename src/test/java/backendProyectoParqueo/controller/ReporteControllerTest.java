package backendProyectoParqueo.controller;

import java.util.ArrayList;
import java.util.Collections; // Para simular el request body
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.BDDMockito.given;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import backendProyectoParqueo.dto.ClientePlacaRequestDTO;
import backendProyectoParqueo.dto.ReporteEstadoCuentaVehiculoDTO;
import backendProyectoParqueo.dto.VehiculoDTO;
import backendProyectoParqueo.model.Cliente;
import backendProyectoParqueo.service.ReporteService;
import jakarta.persistence.EntityNotFoundException;

@WebMvcTest(controllers = ReporteController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Mock
    private ReporteService reporteService;
    @Autowired
    private ObjectMapper objectMapper;

    private UUID clienteId;
    private String placa;
    private Cliente clienteRequest; // Para el body de /cliente/vehiculo
    private VehiculoDTO vehiculoDTORequest; // Para el body de /vehiculo/estados-cuenta
    private ClientePlacaRequestDTO clientePlacaRequestDTO; // Para el body de /cliente-vehiculo/estados-cuenta

    private List<Object> listaVehiculosObject; // Para la respuesta de getTodosVehiculosPorCliente
    private ReporteEstadoCuentaVehiculoDTO reporteEstadoCuentaDTOModelo;

    @BeforeEach
    void setUp() {
        clienteId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        placa = "BCD345";

        clienteRequest = new Cliente(); // Se envía la entidad Cliente completa según el controller
        clienteRequest.setId(clienteId);
        // Otros campos de Cliente no son estrictamente necesarios para este test si
        // solo se usa el ID

        // Para el endpoint /vehiculo/estados-cuenta, se espera VehiculoDTO en el body
        vehiculoDTORequest = new VehiculoDTO();
        vehiculoDTORequest.setPlaca(placa);
        // No es necesario poblar todos los campos de VehiculoDTO si solo se usa la
        // placa

        clientePlacaRequestDTO = new ClientePlacaRequestDTO();
        clientePlacaRequestDTO.setClienteId(clienteId);
        clientePlacaRequestDTO.setPlaca(placa);

        // Simulación de la respuesta del servicio para getTodosVehiculosDTOPorCliente
        // Aunque el servicio devuelve List<Object>, estos objetos deberían ser
        // VehiculoDTO
        VehiculoDTO vehiculoDTORespuesta = new VehiculoDTO(1L, placa, backendProyectoParqueo.enums.TipoVehiculo.Auto,
                "Marca", "Modelo", "Color");
        listaVehiculosObject = new ArrayList<>();
        listaVehiculosObject.add(vehiculoDTORespuesta);

        reporteEstadoCuentaDTOModelo = new ReporteEstadoCuentaVehiculoDTO();
        reporteEstadoCuentaDTOModelo.setPlacaVehiculo(placa);
        // ... poblar otros campos
    }

    @Test
    @DisplayName("POST /reporte/cliente/vehiculo - Devuelve lista de vehículos y status 200") // CORREGIDO DisplayName
                                                                                              // para que coincida
    void getTodosVehiculosPorCliente_devuelveListaYOk() throws Exception {
        given(reporteService.getTodosVehiculosDTOPorCliente(clienteId)).willReturn(listaVehiculosObject);

        mockMvc.perform(post("/reporte/cliente/vehiculo") // <-- CORREGIDA LA URL AQUÍ
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteRequest))) // Enviamos el objeto Cliente (esto es
                                                                           // correcto para /reporte/cliente/vehiculo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].placa", is(placa))); // Asumiendo que el Object es un VehiculoDTO

        verify(reporteService).getTodosVehiculosDTOPorCliente(clienteId);
    }

    @Test
    @DisplayName("POST /reporte/cliente/vehiculo - Cliente sin ID en body devuelve 400") // CORREGIDO DisplayName
    void getTodosVehiculosPorCliente_sinIdCliente_devuelve400() throws Exception {
        Cliente clienteSinId = new Cliente(); // ID es null

        mockMvc.perform(post("/reporte/cliente/vehiculo") // <-- CORREGIDA LA URL AQUÍ
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteSinId))) // Enviamos Cliente (correcto para este
                                                                         // endpoint)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is("El campo 'id' del cliente es requerido en el body."))); // Ahora el
                                                                                                             // mensaje
                                                                                                             // esperado
                                                                                                             // es
                                                                                                             // correcto
    }

    @Test
    @DisplayName("POST /reporte/cliente/vehiculo - Sin vehículos devuelve lista vacía y OK")
    void getTodosVehiculosPorCliente_sinVehiculos_devuelveVacioYOk() throws Exception {
        given(reporteService.getTodosVehiculosDTOPorCliente(clienteId)).willReturn(Collections.emptyList());

        mockMvc.perform(post("/reporte/cliente/vehiculo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("El cliente no tiene vehículos asociados.")))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    @DisplayName("POST /reporte/vehiculo/estados-cuenta - Devuelve lista de reportes y status 200")
    void getTodosEstadosCuentaPorPlacaEnBody_devuelveListaYOk() throws Exception {
        given(reporteService.getEstadosCuentaPorPlacaVehiculo(placa)).willReturn(List.of(reporteEstadoCuentaDTOModelo));

        mockMvc.perform(post("/reporte/vehiculo/estados-cuenta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehiculoDTORequest))) // Se envía VehiculoDTO
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].placaVehiculo", is(placa)));
    }

    @Test
    @DisplayName("POST /reporte/vehiculo/estados-cuenta - Placa no proporcionada devuelve 400")
    void getTodosEstadosCuentaPorPlacaEnBody_placaNull_devuelve400() throws Exception {
        VehiculoDTO vehiculoSinPlaca = new VehiculoDTO(); // placa es null

        mockMvc.perform(post("/reporte/vehiculo/estados-cuenta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehiculoSinPlaca)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is("El campo 'placa' es requerido en el cuerpo de la solicitud.")));
    }

    @Test
    @DisplayName("POST /reporte/vehiculo/estados-cuenta - Vehículo no encontrado devuelve 404")
    void getTodosEstadosCuentaPorPlacaEnBody_vehiculoNoEncontrado_devuelve404() throws Exception {
        String mensajeError = "Vehículo con placa " + placa + " no encontrado.";
        given(reporteService.getEstadosCuentaPorPlacaVehiculo(placa))
                .willThrow(new EntityNotFoundException(mensajeError));

        mockMvc.perform(post("/reporte/vehiculo/estados-cuenta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehiculoDTORequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is(mensajeError)));
    }

    @Test
    @DisplayName("POST /reporte/cliente-vehiculo/estados-cuenta - Devuelve lista de reportes y status 200")
    void getTodosEstadosCuentaPorClienteYPlacaEnBody_devuelveListaYOk() throws Exception {
        given(reporteService.getEstadosCuentaPorClienteYPlaca(clienteId, placa))
                .willReturn(List.of(reporteEstadoCuentaDTOModelo));

        mockMvc.perform(post("/reporte/cliente-vehiculo/estados-cuenta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientePlacaRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].placaVehiculo", is(placa)));
    }

    @Test
    @DisplayName("POST /reporte/cliente-vehiculo/estados-cuenta - Datos incompletos devuelve 400")
    void getTodosEstadosCuentaPorClienteYPlacaEnBody_datosIncompletos_devuelve400() throws Exception {
        ClientePlacaRequestDTO requestIncompleto = new ClientePlacaRequestDTO();
        requestIncompleto.setClienteId(clienteId); // Placa es null

        mockMvc.perform(post("/reporte/cliente-vehiculo/estados-cuenta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestIncompleto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is("Los campos 'clienteId' y 'placa' son requeridos.")));
    }
}