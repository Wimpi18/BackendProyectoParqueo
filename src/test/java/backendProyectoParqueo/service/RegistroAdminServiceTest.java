package backendProyectoParqueo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import backendProyectoParqueo.dto.RegistroUsuarioAdminRequestDTO;
import backendProyectoParqueo.dto.UsuarioDTO;
import backendProyectoParqueo.enums.RolAdmin;
import backendProyectoParqueo.model.Administrador;
import backendProyectoParqueo.model.Cajero;
import backendProyectoParqueo.model.Usuario;
import backendProyectoParqueo.repository.AdministradorRepository;
import backendProyectoParqueo.repository.CajeroRepository;
import backendProyectoParqueo.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class RegistroAdminServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AdministradorRepository administradorRepository;

    @Mock
    private CajeroRepository cajeroRepository;

    @Mock
    private RegistroUsuarioService registroUsuarioService;

    @InjectMocks
    private RegistroAdminService registroAdminService;

    private RegistroUsuarioAdminRequestDTO requestDTO;
    private UsuarioDTO usuarioDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setCi("12345678");
        usuarioDTO.setNombre("Admin");
        usuarioDTO.setApellido("Apellido");
        usuarioDTO.setCorreo("admin@example.com");
        usuarioDTO.setNroCelular("71234567");
        usuarioDTO.setPassword("Password1");
        usuarioDTO.setFoto(new byte[] {});

        requestDTO = new RegistroUsuarioAdminRequestDTO();
        requestDTO.setUsuario(usuarioDTO);
        requestDTO.setRol(RolAdmin.ADMINISTRADOR);
        requestDTO.setEsActivo(true);

        usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setCi("12345678");
        usuario.setUsername("admin123");
    }

    @Test
    @DisplayName("Debe registrar nuevo administrador cuando el usuario no existe")
    void When_UsuarioNoExiste_Expect_RegistroAdminExitoso() {
        when(usuarioRepository.findByCi("12345678")).thenReturn(Optional.empty());
        when(registroUsuarioService.crearUsuario(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any()))
                .thenReturn(usuario);
        when(administradorRepository.existsById(usuario.getId())).thenReturn(false);
        when(cajeroRepository.existsById(usuario.getId())).thenReturn(false);

        String result = registroAdminService.registrarUsuarioAdmin(requestDTO);

        assertEquals("admin123", result);
        verify(administradorRepository).save(any(Administrador.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario ya es administrador")
    void When_YaEsAdmin_Expect_Exception() {
        when(usuarioRepository.findByCi("12345678")).thenReturn(Optional.of(usuario));
        when(administradorRepository.existsById(usuario.getId())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> registroAdminService.registrarUsuarioAdmin(requestDTO));

        assertEquals("Este usuario ya es administrador.", ex.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario ya es cajero")
    void When_YaEsCajero_Expect_Exception() {
        when(usuarioRepository.findByCi("12345678")).thenReturn(Optional.of(usuario));
        when(administradorRepository.existsById(usuario.getId())).thenReturn(false);
        when(cajeroRepository.existsById(usuario.getId())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> registroAdminService.registrarUsuarioAdmin(requestDTO));

        assertEquals("Este usuario ya tiene el rol de cajero. No puede ser administrador.", ex.getMessage());
    }

    @Test
    @DisplayName("Debe registrar nuevo cajero si no tiene roles previos")
    void When_RegistrarCajero_Expect_Exito() {
        requestDTO.setRol(RolAdmin.CAJERO);

        when(usuarioRepository.findByCi("12345678")).thenReturn(Optional.of(usuario));
        when(administradorRepository.existsById(usuario.getId())).thenReturn(false);
        when(cajeroRepository.existsById(usuario.getId())).thenReturn(false);

        String result = registroAdminService.registrarUsuarioAdmin(requestDTO);

        assertEquals("admin123", result);
        verify(cajeroRepository).save(any(Cajero.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si ya es admin y se intenta registrarlo como cajero")
    void When_UsuarioYaEsAdmin_Expect_ErrorAlRegistrarCajero() {
        requestDTO.setRol(RolAdmin.CAJERO);

        when(usuarioRepository.findByCi("12345678")).thenReturn(Optional.of(usuario));
        when(administradorRepository.existsById(usuario.getId())).thenReturn(true);
        when(cajeroRepository.existsById(usuario.getId())).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> registroAdminService.registrarUsuarioAdmin(requestDTO));

        assertEquals("Este usuario ya tiene el rol de administrador. No puede ser cajero.", ex.getMessage());
    }
}
