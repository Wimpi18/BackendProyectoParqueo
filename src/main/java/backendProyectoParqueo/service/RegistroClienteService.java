package backendProyectoParqueo.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import backendProyectoParqueo.dto.ClienteDTO;
import backendProyectoParqueo.dto.ParqueoDTO;
import backendProyectoParqueo.dto.RegistroRequestDTO;
import backendProyectoParqueo.dto.VehiculoDTO;
import backendProyectoParqueo.enums.TipoCliente;
import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.model.Cliente;
import backendProyectoParqueo.model.Parqueo;
import backendProyectoParqueo.model.Usuario;
import backendProyectoParqueo.model.Vehiculo;
import backendProyectoParqueo.model.VehiculoEnParqueo;
import backendProyectoParqueo.repository.ClienteRepository;
import backendProyectoParqueo.repository.ParqueoRepository;
import backendProyectoParqueo.repository.VehiculoEnParqueoRepository;
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
    private final VehiculoEnParqueoRepository vehiculoEnParqueoRepo;

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

        Parqueo parqueo = new Parqueo();
        parqueo.setCliente(cliente);
        parqueo.setEstado(Parqueo.EstadoParqueo.Activo);
        parqueo.setFechaInicio(LocalDate.now());
        parqueo.setNroEspacio(espacioAsignado);

        // Se considera que los espacios 110, 111, 112 y 113 son para moto, si quiere
        // ser escalable es necesario crear otra tabla, estas lineas de codigo van en
        // relación a parqueoService en la función obtenerEspaciosDisponibles()
        // Esto se realizará siempre que el cliente no sea de tipo Cliente a tiempo
        // horario
        if (!clienteDTO.getTipo().trim().equals(TipoCliente.DOCENTE_TIEMPO_HORARIO.getLabel())) {
            List<Short> espacios = Arrays.asList((short) 110, (short) 111, (short) 112, (short) 113);
            if (espacios.contains(request.getParqueo().getNroEspacio()))
                parqueo.setTipo(TipoVehiculo.Moto);
            else
                parqueo.setTipo(TipoVehiculo.Auto);
        } else
            parqueo.setTipo(TipoVehiculo.Auto);

        parqueo = parqueoRepo.save(parqueo);

        for (VehiculoDTO v : vehiculos) {
            registrarVehiculoYParqueo(v, parqueo);
        }
    }

    private Short validarYObtenerEspacioAsignado(ParqueoDTO parqueoDTO, String tipoCliente) {
        if (tipoCliente == null)
            return null;

        // Esto implica que el docente a tiempo horario se registra sin necesidad de
        // número de parqueo
        if (tipoCliente.trim().equals(TipoCliente.DOCENTE_TIEMPO_HORARIO.getLabel()))
            return null;

        if (parqueoDTO == null || parqueoDTO.getNroEspacio() == null)
            throw new IllegalArgumentException("Debe proporcionar un número de espacio.");

        Short nroEspacio = parqueoDTO.getNroEspacio();
        List<Short> ocupados = parqueoRepo.findEspaciosOcupados();

        return ocupados.contains(nroEspacio) ? null : nroEspacio;
    }

    private void registrarVehiculoYParqueo(VehiculoDTO dto, Parqueo parqueo) {
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

        VehiculoEnParqueo relacion = new VehiculoEnParqueo();
        relacion.setParqueo(parqueo);
        relacion.setVehiculo(vehiculo);
        vehiculoEnParqueoRepo.save(relacion);
    }

}
