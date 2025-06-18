package backendProyectoParqueo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import backendProyectoParqueo.dto.RegistroUsuarioAdminRequestDTO;
import backendProyectoParqueo.dto.UsuarioDTO;
import backendProyectoParqueo.enums.RolAdmin;
import backendProyectoParqueo.service.RegistroAdminService;

@SpringBootTest
@AutoConfigureMockMvc
class RegistroAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private RegistroUsuarioAdminRequestDTO requestDTO;

    @MockBean
    private RegistroAdminService usuarioAdminService;

    @BeforeEach
    void setUp() {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setCi("12345678");
        usuario.setNombre("Admin");
        usuario.setApellido("Apellido");
        usuario.setCorreo("admin@example.com");
        usuario.setNroCelular("71234567");
        usuario.setPassword("Password1");
        usuario.setFoto(new byte[] {});

        requestDTO = new RegistroUsuarioAdminRequestDTO();
        requestDTO.setUsuario(usuario);
        requestDTO.setRol(RolAdmin.ADMINISTRADOR);
        requestDTO.setEsActivo(true);

        when(usuarioAdminService.registrarUsuarioAdmin(any()))
                .thenReturn("admin123");
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("Debe registrar admin y devolver 201")
    void When_ValidRequest_Expect_CreatedStatus() throws Exception {
        mockMvc.perform(post("/admin/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CLIENTE") // Rol que NO tiene permiso
    @DisplayName("Debe devolver 403 si el usuario no tiene el rol adecuado")
    void When_NoAdminRole_Expect_Forbidden() throws Exception {
        mockMvc.perform(post("/admin/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR") // Rol con permiso válido
    @DisplayName("Debe devolver 400 si falta información del admin")
    void When_InvalidRequest_Expect_BadRequest() throws Exception {
        requestDTO.setRol(null);

        mockMvc.perform(post("/admin/registrar")
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
