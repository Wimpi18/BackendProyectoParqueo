package backendProyectoParqueo.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Set;
import java.util.stream.Collectors;
import java.sql.Timestamp;
import java.sql.Date;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.auth0.jwt.interfaces.DecodedJWT;

import backendProyectoParqueo.dto.AllUsuarioDTO;
import backendProyectoParqueo.dto.SignedInUser;
import backendProyectoParqueo.dto.UsuarioDetalleDTO;
import backendProyectoParqueo.dto.VehiculoDTO;
import backendProyectoParqueo.enums.RoleEnum;
import backendProyectoParqueo.enums.TipoVehiculo;
import backendProyectoParqueo.model.Parqueo;
import backendProyectoParqueo.model.Parqueo.EstadoParqueo;
import backendProyectoParqueo.model.Usuario;
import backendProyectoParqueo.repository.UsuarioRepository;
import backendProyectoParqueo.repository.AdministradorRepository;
import backendProyectoParqueo.repository.CajeroRepository;
import backendProyectoParqueo.repository.ClienteRepository;
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

        public Map<UUID, Integer> calcularMesesDeudaClientes() {
                List<Object[]> datos = usuarioRepository.obtenerFechasYPagosClientes();
                Map<UUID, Integer> deudaPorCliente = new HashMap<>();

                for (Object[] fila : datos) {
                        UUID idCliente = (UUID) fila[0];
                        LocalDate fechaInicio = ((Date) fila[1]).toLocalDate();

                        // Puede venir null si nunca pagó
                        Date[] mesesPagadosRaw = (Date[]) fila[2];
                        List<LocalDate> mesesPagados = new ArrayList<>();
                        if (mesesPagadosRaw != null) {
                                for (Date d : mesesPagadosRaw) {
                                        if (d != null) {
                                                mesesPagados.add(d.toLocalDate().withDayOfMonth(1));

                                        }
                                }
                        }

                        // Calcular meses de deuda
                        int cantidadDeuda = calcularCantidadMesesDeuda(fechaInicio, mesesPagados);
                        if (cantidadDeuda > 0) {
                                deudaPorCliente.put(idCliente, cantidadDeuda);
                        }
                }

                return deudaPorCliente;
        }

        private int calcularCantidadMesesDeuda(LocalDate fechaInicio, List<LocalDate> mesesPagados) {
                LocalDate hoy = LocalDate.now();

                if (hoy.isBefore(fechaInicio.plusMonths(1))) {
                        return 0;
                }

                int mesesTranscurridos = (int) ChronoUnit.MONTHS.between(fechaInicio, hoy);

                // Crear lista de meses esperados (cada uno es la fechaInicio + n meses)
                List<LocalDate> mesesEsperados = new ArrayList<>();
                for (int i = 0; i < mesesTranscurridos; i++) {
                        mesesEsperados.add(fechaInicio.plusMonths(i).withDayOfMonth(1)); // normalizamos día 1
                }

                Set<LocalDate> pagadosNormalizados = mesesPagados.stream()
                                .map(m -> m.withDayOfMonth(1))
                                .collect(Collectors.toSet());

                int deuda = 0;
                for (LocalDate mes : mesesEsperados) {
                        if (!pagadosNormalizados.contains(mes)) {
                                deuda++;
                        }
                }

                return deuda;
        }

        public List<AllUsuarioDTO> obtenerUsuariosVista() {
                List<Object[]> resultados = usuarioRepository.obtenerUsuariosConRolesRaw();
                Map<UUID, Integer> deudaClientes = calcularMesesDeudaClientes();

                return resultados.stream()
                                .map(obj -> {
                                        UUID id = UUID.fromString(obj[0].toString());
                                        String nombre = (String) obj[1];
                                        String apellido = (String) obj[2];
                                        byte[] foto = (byte[]) obj[3];

                                        List<String> roles = new ArrayList<>();
                                        if (obj[6] != null)
                                                roles.add("ADMINISTRADOR");
                                        if (obj[7] != null)
                                                roles.add("CAJERO");
                                        if (obj[8] != null)
                                                roles.add("CLIENTE");

                                        // Asignar tipoCliente solo si tiene rol CLIENTE
                                        String tipoCliente = roles.contains("CLIENTE") ? (String) obj[4] : null;

                                        // Asignar estado parqueo (si no es nulo y es válido)
                                        EstadoParqueo estadoParqueo = null;
                                        if (obj[5] != null) {
                                                try {
                                                        estadoParqueo = EstadoParqueo.valueOf(obj[5].toString());
                                                } catch (IllegalArgumentException e) {
                                                        System.out.println("Estado inválido: " + obj[5]);
                                                }
                                        }
                                        Integer cantidadMesesDeuda = null;
                                        if (roles.contains("CLIENTE")) {
                                                cantidadMesesDeuda = deudaClientes.get(id);
                                        }
                                        return new AllUsuarioDTO(id, nombre, apellido, foto, roles, tipoCliente,
                                                        estadoParqueo, cantidadMesesDeuda);
                                })
                                .filter(dto -> dto.getCantidadMesesDeuda() == null || dto.getCantidadMesesDeuda() > 0)
                                .toList();
        }

}
