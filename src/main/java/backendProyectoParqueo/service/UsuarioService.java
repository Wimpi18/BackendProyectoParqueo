package backendProyectoParqueo.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.auth0.jwt.interfaces.DecodedJWT;

import backendProyectoParqueo.dto.AllUsuarioDTO;
import backendProyectoParqueo.dto.SignedInUser;
import backendProyectoParqueo.dto.UsuarioDetalleDTO;
import backendProyectoParqueo.dto.VehiculoDTO;
import backendProyectoParqueo.dto.AllUsuarioDTO;
import backendProyectoParqueo.enums.RoleEnum;
import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.model.Parqueo;
import backendProyectoParqueo.model.Usuario;
import backendProyectoParqueo.repository.UsuarioRepository;
import static backendProyectoParqueo.security.Constants.EXPIRATION_TIME_ACCESS_TOKEN;
import static backendProyectoParqueo.security.Constants.EXPIRATION_TIME_REFRESH_TOKEN;
import backendProyectoParqueo.security.JwtManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
        private final UsuarioRepository usuarioRepository;
        private final JwtManager tokenManager;

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

                        vehiculo.setId(((Number) fila[9]).longValue());
                        vehiculo.setPlaca((String) fila[10]);
                        vehiculo.setTipo(TipoVehiculo.valueOf((String) fila[11]));
                        vehiculo.setMarca((String) fila[12]);
                        vehiculo.setFotoDelantera((byte[]) fila[13]);
                        vehiculo.setFotoTrasera((byte[]) fila[14]);
                        vehiculo.setModelo((String) fila[15]);
                        vehiculo.setColor((String) fila[16]);

                        vehiculos.add(vehiculo);
                }
                dto.setVehiculos(vehiculos);
                List<UsuarioDetalleDTO> lista = new ArrayList<>();
                lista.add(dto);
                return lista;
        }

        private RoleEnum[] mapRolesArray(String[] rolesStr) {
                return Arrays.stream(rolesStr)
                                .map(RoleEnum::fromAuthority)
                                .toArray(RoleEnum[]::new);
        }

        public Usuario findUserByUsername(String username) {
                final String uname = username.trim();

                Optional<List<Object[]>> optionalResult = usuarioRepository.findRawUsuarioConRolesByUsername(uname);

                // Obtener la lista o lanzar excepción si no hay
                List<Object[]> resultList = optionalResult.orElseThrow(
                                () -> new UsernameNotFoundException(
                                                String.format("No se encontró al usuario %s.", uname)));

                // Verificar que la lista no esté vacía
                if (resultList.isEmpty()) {
                        throw new UsernameNotFoundException(String.format("No se encontró al usuario %s.", uname));
                }

                // Obtener la primera fila (el usuario)
                Object[] row = resultList.get(0);

                return new Usuario(
                                (UUID) row[0], // id
                                (String) row[1], // ci
                                (String) row[2], // nombre
                                (String) row[3], // apellido
                                (String) row[4], // correo
                                (String) row[5], // nroCelular
                                (String) row[6], // password
                                (String) row[7], // username
                                (byte[]) row[8], // foto
                                mapRolesArray((String[]) row[9]) // roles
                );
        }

        public Optional<Map<String, Object>> buscarPorCi(String ci) {
                return usuarioRepository.findByCi(ci).map(usuario -> {
                        Map<String, Object> response = new HashMap<>();
                        response.put("ci", usuario.getCi());
                        response.put("nombre", usuario.getNombre());
                        response.put("apellido", usuario.getApellido());
                        response.put("correo", usuario.getCorreo());
                        response.put("telefono", usuario.getNroCelular());
                        response.put("foto", usuario.getFoto());
                        response.put("password", usuario.getPassword());
                        return response;
                });
        }

        private SignedInUser createSignedUserWithRefreshToken(
                        Usuario userEntity) {
                return createSignedInUser(userEntity)
                                .refreshToken(createRefreshToken(userEntity));
        }

        private SignedInUser createSignedInUser(Usuario usuario) {
                String token = tokenManager.create(usuario, EXPIRATION_TIME_ACCESS_TOKEN);
                return new SignedInUser().accessToken(token)
                                .roles(Arrays.stream(usuario.getRoles())
                                                .map(RoleEnum::name)
                                                .toArray(String[]::new));
        }

        private String createRefreshToken(Usuario usuario) {
                String token = tokenManager.create(usuario, EXPIRATION_TIME_REFRESH_TOKEN);
                return token;
        }

        public DecodedJWT decodeRefreshToken(String token) {
                return tokenManager.decode(token);
        }

        public SignedInUser getSignedInUser(Usuario usuario) {
                return createSignedUserWithRefreshToken(usuario);
        }

        public List<AllUsuarioDTO> obtenerUsuariosVista() {
                List<Object[]> resultados = usuarioRepository.obtenerUsuarioClienteParqueoRaw();

                return resultados.stream()
                                .map(obj -> new AllUsuarioDTO(
                                                UUID.fromString(obj[0].toString()),
                                                (String) obj[1],
                                                (String) obj[2],
                                                (byte[]) obj[3],
                                                (String) obj[4],
                                                Parqueo.EstadoParqueo.valueOf((String) obj[5])))
                                .toList();
        }

}
