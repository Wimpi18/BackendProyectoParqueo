package backendProyectoParqueo.service;

import java.time.LocalDate;
import java.util.List;

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
import backendProyectoParqueo.repository.VehiculoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistroClienteService {

    private final RegistroUsuarioService registroUsuarioService;

    private final ClienteRepository clienteRepo;
    private final VehiculoRepository vehiculoRepo;
    private final ParqueoRepository parqueoRepo;

    @Transactional
    public void registrarCliente(RegistroRequestDTO request) {
        ClienteDTO clienteDTO = request.getCliente();
        List<VehiculoDTO> vehiculos = request.getVehiculos();

        if (vehiculos == null || vehiculos.isEmpty()) {
            throw new IllegalArgumentException("Debe registrar al menos un vehículo.");
        }

        Short espacioAsignado = validarYObtenerEspacioAsignado(request.getParqueo(), clienteDTO.getTipo());

        Usuario usuario = registroUsuarioService.crearUsuario(
                clienteDTO.getCi(),
                clienteDTO.getNombre(),
                clienteDTO.getApellido(),
                clienteDTO.getCorreo(),
                clienteDTO.getNroCelular(),
                clienteDTO.getPassword(),
                clienteDTO.getFoto());

        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setEntidad(clienteDTO.getEntidad());
        cliente.setTipo(clienteDTO.getTipo());
        cliente = clienteRepo.save(cliente);

        for (VehiculoDTO v : vehiculos) {
            registrarVehiculoYParqueo(v, cliente, espacioAsignado);
        }
    }

    private Short validarYObtenerEspacioAsignado(ParqueoDTO parqueoDTO, String tipoCliente) {
        if (tipoCliente == null)
            return null;

        String tipo = tipoCliente.trim().toLowerCase();
        boolean requiereEspacio = tipo.equals("administrativo") || tipo.equals("docente a dedicación exclusiva");

        if (!requiereEspacio)
            return null;

        if (parqueoDTO == null || parqueoDTO.getNroEspacio() == null) {
            throw new IllegalArgumentException("Debe proporcionar un número de espacio.");
        }

        Short nroEspacio = parqueoDTO.getNroEspacio();
        List<Short> ocupados = parqueoRepo.findEspaciosOcupados();

        return ocupados.contains(nroEspacio) ? null : nroEspacio;
    }

    private void registrarVehiculoYParqueo(VehiculoDTO dto, Cliente cliente, Short espacioAsignado) {
        if (vehiculoRepo.existsByPlaca(dto.getPlaca())) {
            throw new IllegalArgumentException("Placa ya registrada: " + dto.getPlaca());
        }

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca(dto.getPlaca());
        vehiculo.setTipo(dto.getTipo());
        vehiculo.setMarca(dto.getMarca());
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setColor(dto.getColor());
        vehiculo.setFotoDelantera(dto.getFotoDelantera());
        vehiculo.setFotoTrasera(dto.getFotoTrasera());
        vehiculo = vehiculoRepo.save(vehiculo);

        Parqueo parqueo = new Parqueo();
        parqueo.setCliente(cliente);
        parqueo.setVehiculo(vehiculo);
        parqueo.setEstado(Parqueo.EstadoParqueo.Activo);
        parqueo.setFechaInicio(LocalDate.now());
        parqueo.setNroEspacio(espacioAsignado);
        parqueoRepo.save(parqueo);
    }

}
