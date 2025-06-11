package backendProyectoParqueo.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import backendProyectoParqueo.dto.ClienteDTO;
import backendProyectoParqueo.dto.ParqueoDTO;
import backendProyectoParqueo.dto.RegistroRequestDTO;
import backendProyectoParqueo.dto.UsuarioDTO;
import backendProyectoParqueo.dto.VehiculoDTO;
import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.security.SecurityConfig;

@WebMvcTest(controllers = RegistroClienteController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class RegistroClienteControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private RegistroRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        // Usuario válido
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setCi("1234567");
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setCorreo("juan@example.com");
        usuario.setNroCelular("71234567");
        usuario.setPassword("Password1");

        // Cliente válido (hereda de UsuarioDTO)
        ClienteDTO cliente = new ClienteDTO();
        cliente.setCi("1234567");
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");
        cliente.setCorreo("juan@example.com");
        cliente.setNroCelular("71234567");
        cliente.setPassword("Password1");
        cliente.setTipo("Natural");

        // Vehículo válido
        VehiculoDTO vehiculo = new VehiculoDTO();
        vehiculo.setPlaca("123ABC");
        vehiculo.setTipo(TipoVehiculo.Auto);
        vehiculo.setMarca("Toyota");
        vehiculo.setModelo("Yaris");
        vehiculo.setColor("Rojo");

        // Parqueo válido
        ParqueoDTO parqueo = new ParqueoDTO();
        parqueo.setNroEspacio((short) 5);

        // Armamos el request
        requestDTO = new RegistroRequestDTO();
        requestDTO.setUsuario(usuario);
        requestDTO.setCliente(cliente);
        requestDTO.setVehiculos(List.of(vehiculo));
        requestDTO.setParqueo(parqueo);
    }

    @Test
    @DisplayName("Debe registrar cliente y devolver 201")
    void When_ValidRequest_Expect_CreatedStatus() throws Exception {
        mockMvc.perform(post("/clientes/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Debe devolver 400 si falta información del cliente")
    void When_InvalidRequest_Expect_BadRequest() throws Exception {
        requestDTO.getCliente().setCi(null); // El CI es obligatorio

        mockMvc.perform(post("/clientes/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
