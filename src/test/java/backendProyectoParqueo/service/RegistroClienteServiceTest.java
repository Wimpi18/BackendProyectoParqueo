package backendProyectoParqueo.service;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import backendProyectoParqueo.dto.ClienteDTO;
import backendProyectoParqueo.dto.ParqueoDTO;
import backendProyectoParqueo.dto.RegistroRequestDTO;
import backendProyectoParqueo.dto.VehiculoDTO;
import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.model.Cliente;
import backendProyectoParqueo.model.Parqueo;
import backendProyectoParqueo.model.Usuario;
import backendProyectoParqueo.model.Vehiculo;
import backendProyectoParqueo.repository.ClienteRepository;
import backendProyectoParqueo.repository.ParqueoRepository;
import backendProyectoParqueo.repository.UsuarioRepository;
import backendProyectoParqueo.repository.VehiculoRepository;

@ExtendWith(MockitoExtension.class)
class RegistroClienteServiceTest {

    @Mock
    private UsuarioRepository usuarioRepo;

    @Mock
    private ClienteRepository clienteRepo;

    @Mock
    private VehiculoRepository vehiculoRepo;

    @Mock
    private ParqueoRepository parqueoRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistroClienteService registroClienteService;

    private RegistroRequestDTO request;
    private ClienteDTO clienteDTO;
    private VehiculoDTO vehiculoDTO;
    private ParqueoDTO parqueoDTO;

    @BeforeEach
    void setUp() {

        clienteDTO = new ClienteDTO();
        clienteDTO.setCi("12345678");
        clienteDTO.setNombre("Juan");
        clienteDTO.setApellido("Pérez");
        clienteDTO.setCorreo("juan@example.com");
        clienteDTO.setNroCelular("76543210");
        clienteDTO.setPassword("1234");
        clienteDTO.setTipo("Administrativo");
        clienteDTO.setEntidad("UMSS");

        vehiculoDTO = new VehiculoDTO();
        vehiculoDTO.setPlaca("ABC-123");
        vehiculoDTO.setTipo(TipoVehiculo.Auto);
        vehiculoDTO.setMarca("Toyota");
        vehiculoDTO.setModelo("Corolla");
        vehiculoDTO.setColor("Blanco");

        parqueoDTO = new ParqueoDTO();
        parqueoDTO.setNroEspacio((short) 10);

        request = new RegistroRequestDTO();
        request.setCliente(clienteDTO);
        request.setVehiculos(List.of(vehiculoDTO));
        request.setParqueo(parqueoDTO);
    }

    @Test
    @DisplayName("Debe registrar un cliente y sus vehículos correctamente")
    void When_DatosValidos_Expect_RegistroExitoso() {
        // Arrange
        when(usuarioRepo.existsByCi(clienteDTO.getCi())).thenReturn(false);
        when(usuarioRepo.existsByCorreo(clienteDTO.getCorreo())).thenReturn(false);
        when(parqueoRepo.findEspaciosOcupados()).thenReturn(List.of((short) 5));
        when(passwordEncoder.encode(clienteDTO.getPassword())).thenReturn("hashed-password");

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId(UUID.randomUUID());

        when(usuarioRepo.save(any(Usuario.class))).thenReturn(usuarioGuardado);
        when(clienteRepo.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));
        when(vehiculoRepo.existsByPlaca("ABC-123")).thenReturn(false);
        when(vehiculoRepo.save(any(Vehiculo.class))).thenAnswer(inv -> inv.getArgument(0));
        when(parqueoRepo.save(any(Parqueo.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        registroClienteService.registrarCliente(request);

        // Assert
        verify(usuarioRepo).save(any(Usuario.class));
        verify(clienteRepo).save(any(Cliente.class));
        verify(vehiculoRepo).save(any(Vehiculo.class));
        verify(parqueoRepo).save(any(Parqueo.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el CI ya está registrado")
    void When_CIExistente_Expect_IllegalArgumentException() {
        // Arrange
        when(usuarioRepo.existsByCi(clienteDTO.getCi())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> registroClienteService.registrarCliente(request));

        assertEquals("CI ya registrado.", exception.getMessage());

        verify(usuarioRepo, never()).save(any());
        verify(clienteRepo, never()).save(any());
        verify(vehiculoRepo, never()).save(any());
        verify(parqueoRepo, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el correo ya está registrado")
    void When_CorreoExistente_Expect_IllegalArgumentException() {
        // Arrange
        when(usuarioRepo.existsByCi(clienteDTO.getCi())).thenReturn(false);
        when(usuarioRepo.existsByCorreo(clienteDTO.getCorreo())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> registroClienteService.registrarCliente(request));

        assertEquals("Correo ya registrado.", exception.getMessage());

        verify(usuarioRepo, never()).save(any());
        verify(clienteRepo, never()).save(any());
        verify(vehiculoRepo, never()).save(any());
        verify(parqueoRepo, never()).save(any());
    }

}
