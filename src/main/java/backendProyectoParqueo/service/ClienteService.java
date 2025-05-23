package backendProyectoParqueo.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import backendProyectoParqueo.dto.RegistroClienteDTO;
import backendProyectoParqueo.dto.RegistroVehiculoDTO;
import backendProyectoParqueo.model.Cliente;
import backendProyectoParqueo.model.Parqueo;
import backendProyectoParqueo.model.TipoCliente;
import backendProyectoParqueo.model.Usuario;
import backendProyectoParqueo.model.Vehiculo;
import backendProyectoParqueo.repository.ClienteRepository;
import backendProyectoParqueo.repository.ParqueoRepository;
import backendProyectoParqueo.repository.UsuarioRepository;
import backendProyectoParqueo.repository.VehiculoRepository;
import jakarta.transaction.Transactional;

@Service
public class ClienteService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private ParqueoRepository parqueoRepository;

    @Autowired
    private ParqueoService parqueoService;

    @Transactional
    public void registrarCliente(RegistroClienteDTO dto) throws IOException {
        // Validar que CI o correo no existan
        if (usuarioRepository.existsByCi(dto.getCi()) || usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException("CI o correo ya están registrados.");
        }

        // Validar placas duplicadas en la lista de vehículos enviada
        Set<String> placas = new HashSet<>();
        for (RegistroVehiculoDTO v : dto.getVehiculos()) {
            if (!placas.add(v.getPlaca())) {
                throw new IllegalArgumentException("Placas duplicadas en la lista enviada.");
            }
            // Validar si placa ya existe en BD
            if (vehiculoRepository.existsByPlaca(v.getPlaca())) {
                throw new IllegalArgumentException("La placa " + v.getPlaca() + " ya está registrada.");
            }

        }

        Usuario usuario = new Usuario();
        usuario.setCi(dto.getCi());
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setCorreo(dto.getCorreo());
        usuario.setUsername(dto.getCorreo());
        usuario.setNroCelular(dto.getTelefono());
        usuario.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
        usuario = usuarioRepository.save(usuario);

        // Crear cliente
        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setEntidad(null);
        cliente.setTipo(dto.getTipoCliente());
        cliente.setFoto(Base64.getDecoder().decode(dto.getFotoUsuarioBase64()));
        clienteRepository.save(cliente);

        // Registrar vehículos asociados
        for (RegistroVehiculoDTO vDto : dto.getVehiculos()) {
            Vehiculo vehiculo = new Vehiculo();
            vehiculo.setPlaca(vDto.getPlaca());
            vehiculo.setTipo(vDto.getTipo());
            vehiculo.setFotoDelantera(Base64.getDecoder().decode(vDto.getFotoDelanteraBase64()));
            vehiculo.setFotoTrasera(Base64.getDecoder().decode(vDto.getFotoTraseraBase64()));
            vehiculo.setMarca(vDto.getMarca());
            vehiculo.setModelo(vDto.getModelo());
            vehiculo.setColor(vDto.getColor());

            vehiculo = vehiculoRepository.save(vehiculo);

            // Si el tipoCliente es Administrativo o Docente exclusiva, asignar puesto
            // parqueo
            Parqueo parqueo = new Parqueo();
            parqueo.setCliente(cliente);
            parqueo.setVehiculo(vehiculo);
            parqueo.setEstado(Parqueo.EstadoParqueo.Activo);
            parqueo.setFechaInicio(LocalDate.now());

            if (dto.getTipoCliente() == TipoCliente.ADMINISTRATIVO
                    || dto.getTipoCliente() == TipoCliente.DOCENTE_EXCLUSIVA) {

                Short espacioSolicitado = dto.getNroEspacio();
                if (espacioSolicitado == null) {
                    throw new IllegalArgumentException("Debe seleccionar un espacio de parqueo.");
                }

                boolean ocupado = parqueoRepository.existsByNroEspacioAndEstado(espacioSolicitado,
                        Parqueo.EstadoParqueo.Activo);
                if (ocupado) {
                    throw new IllegalArgumentException("El espacio " + espacioSolicitado + " ya fue ocupado.");
                }

                parqueo.setNroEspacio(espacioSolicitado);
            } else {
                // DOCENTE_HORARIO: no se asigna número de espacio
                parqueo.setNroEspacio(null);
            }

            parqueoRepository.save(parqueo);

        }
    }

}
