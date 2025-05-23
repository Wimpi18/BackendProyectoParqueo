package backendProyectoParqueo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import backendProyectoParqueo.dto.ClienteDTO;
import backendProyectoParqueo.dto.ParqueoDTO;
import backendProyectoParqueo.dto.RegistroRequestDTO;
import backendProyectoParqueo.dto.VehiculoDTO;
import backendProyectoParqueo.model.Cliente;
import backendProyectoParqueo.model.Parqueo;
import backendProyectoParqueo.model.Usuario;
import backendProyectoParqueo.model.Vehiculo;
import backendProyectoParqueo.repository.ClienteRepository;
import backendProyectoParqueo.repository.ParqueoRepository;
import backendProyectoParqueo.repository.UsuarioRepository;
import backendProyectoParqueo.repository.VehiculoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistroClienteService {

    private final UsuarioRepository usuarioRepo;
    private final ClienteRepository clienteRepo;
    private final VehiculoRepository vehiculoRepo;
    private final ParqueoRepository parqueoRepo;

    @Transactional
    public void registrarCliente(RegistroRequestDTO request) {
        ClienteDTO clienteDTO = request.getCliente();
        List<VehiculoDTO> vehiculos = request.getVehiculos();

        // Validar que haya al menos un vehículo
        if (vehiculos == null || vehiculos.isEmpty()) {
            throw new IllegalArgumentException("Debe registrar al menos un vehículo.");
        }

        // Validar CI y correo antes de guardar
        if (usuarioRepo.existsByCi(clienteDTO.getCi()))
            throw new IllegalArgumentException("CI ya registrado.");
        if (usuarioRepo.existsByCorreo(clienteDTO.getCorreo()))
            throw new IllegalArgumentException("Correo ya registrado.");

        // Validar espacio requerido antes de guardar nada
        Short espacioAsignado = null;
        String tipo = clienteDTO.getTipo() != null ? clienteDTO.getTipo().trim().toLowerCase() : "";
        boolean requiereEspacio = tipo.equals("administrativo") || tipo.equals("docente a dedicación exclusiva");

        if (requiereEspacio) {
            ParqueoDTO parqueoDTO = request.getParqueo();
            if (parqueoDTO == null || parqueoDTO.getNroEspacio() == null) {
                throw new IllegalArgumentException("Debe proporcionar un número de espacio.");
            }

            Short nroEspacio = parqueoDTO.getNroEspacio();
            if (nroEspacio != null) {
                List<Short> ocupados = parqueoRepo.findEspaciosOcupados();
                if (ocupados.contains(nroEspacio)) {
                    nroEspacio = null; // Espacio inválido, se ignora
                }
            }
            espacioAsignado = nroEspacio;

        }

        // ✅ Todo validado → ahora sí crear entidades

        Usuario usuario = new Usuario();
        usuario.setCi(clienteDTO.getCi());
        usuario.setNombre(clienteDTO.getNombre());
        usuario.setApellido(clienteDTO.getApellido());
        usuario.setCorreo(clienteDTO.getCorreo());
        usuario.setNroCelular(clienteDTO.getNroCelular());
        usuario.setUsername("usuario_" + UUID.randomUUID());
        usuario.setPassword(clienteDTO.getPassword());
        usuario = usuarioRepo.save(usuario);

        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setEntidad(clienteDTO.getEntidad());
        cliente.setFoto(clienteDTO.getFoto());
        cliente.setTipo(clienteDTO.getTipo());
        cliente = clienteRepo.save(cliente);

        for (VehiculoDTO vehiculoDTO : vehiculos) {
            if (vehiculoRepo.existsByPlaca(vehiculoDTO.getPlaca())) {
                throw new IllegalArgumentException("Placa ya registrada: " + vehiculoDTO.getPlaca());
            }

            Vehiculo vehiculo = new Vehiculo();
            vehiculo.setPlaca(vehiculoDTO.getPlaca());
            vehiculo.setTipo(vehiculoDTO.getTipo());
            vehiculo.setMarca(vehiculoDTO.getMarca());
            vehiculo.setModelo(vehiculoDTO.getModelo());
            vehiculo.setColor(vehiculoDTO.getColor());
            vehiculo.setFotoDelantera(vehiculoDTO.getFotoDelantera());
            vehiculo.setFotoTrasera(vehiculoDTO.getFotoTrasera());
            vehiculo = vehiculoRepo.save(vehiculo);

            Parqueo parqueo = new Parqueo();
            parqueo.setCliente(cliente);
            parqueo.setVehiculo(vehiculo);
            parqueo.setEstado(Parqueo.EstadoParqueo.Activo);
            parqueo.setFechaInicio(LocalDate.now());
            parqueo.setNroEspacio(espacioAsignado); // válido o null, pero siempre seteado
            parqueoRepo.save(parqueo);

        }
    }

}
