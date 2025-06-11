package backendProyectoParqueo.service;

import backendProyectoParqueo.dto.UsuarioDTO;
import backendProyectoParqueo.dto.UsuarioDetalleDTO;
import backendProyectoParqueo.dto.VehiculoDTO;
import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.model.Usuario;
import backendProyectoParqueo.model.Vehiculo;
import backendProyectoParqueo.repository.ParqueoRepository;
import backendProyectoParqueo.repository.UsuarioRepository;
import backendProyectoParqueo.repository.VehiculoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.StackWalker.Option;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ParqueoRepository parqueoRepository;

    @Transactional
    public List<UsuarioDetalleDTO> obtenerDetalleUsuario(UUID idUsuario) {
        List<Object[]> resultados = usuarioRepository.obtenerDetallesUsuarioPorId(idUsuario);
        if (resultados.isEmpty())
            return null;

        Object[] primer = resultados.get(0);
        UsuarioDetalleDTO dto = new UsuarioDetalleDTO();
        dto.setCi((String) primer[0]);
        dto.setNombre((String) primer[1]);
        dto.setApellido((String) primer[2]);
        dto.setCorreo((String) primer[3]);
        dto.setNroCelular((String) primer[4]);
        dto.setPassword((String) primer[5]);
        dto.setRolAsignado((String) primer[6]);
        dto.setFotoUsuario((byte[]) primer[7]);
        dto.setEstadoParqueo((String) primer[8]);

        List<VehiculoDTO> vehiculos = new ArrayList<>();
        for (Object[] fila : resultados) {
            VehiculoDTO vehiculo = new VehiculoDTO();
            vehiculo.setId(((Number) fila[9]).longValue());
            vehiculo.setIdParqueo(((Number) fila[10]).longValue());
            vehiculo.setPlaca((String) fila[11]);
            vehiculo.setTipo(TipoVehiculo.valueOf((String) fila[12]));
            vehiculo.setMarca((String) fila[13]);
            vehiculo.setFotoDelantera((byte[]) fila[14]);
            vehiculo.setFotoTrasera((byte[]) fila[15]);
            vehiculo.setModelo((String) fila[16]);
            vehiculo.setColor((String) fila[17]);

            vehiculos.add(vehiculo);
        }
        dto.setVehiculos(vehiculos);
        List<UsuarioDetalleDTO> lista = new ArrayList<>();
        lista.add(dto);
        return lista;
    }

    public Optional<Map<String, Object>> buscarPorCi(String ci) {
        Optional<Usuario> usuario = usuarioRepository.findByCi(ci);

        if (usuario.isPresent()) {
            Usuario u = usuario.get();
            UsuarioDTO usuarioDTO = new UsuarioDTO(
                    u.getId(),
                    u.getCi(),
                    u.getNombre(),
                    u.getApellido(),
                    u.getCorreo(),
                    u.getNroCelular(),
                    u.getPassword(),
                    u.getFoto());

            List<Vehiculo> vehiculos = parqueoRepository.obtenerVehiculosActivosPorClienteId(u.getId());
            List<VehiculoDTO> vehiculoDTOs = vehiculos.stream().map(v -> new VehiculoDTO(
                    v.getPlaca(),
                    v.getTipo(),
                    v.getMarca(),
                    v.getModelo(),
                    v.getColor(),
                    v.getFotoDelantera(),
                    v.getFotoTrasera())).collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("user", usuarioDTO);
            response.put("vehiculos", vehiculoDTOs);
            response.put("message", "Usuario encontrado. Se añadirán permisos administrativos a su cuenta actual.");

            return Optional.of(response);
        }

        return Optional.empty();
    }
}
