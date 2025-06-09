package backendProyectoParqueo.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import backendProyectoParqueo.dto.SignedInUser;
import backendProyectoParqueo.enums.RoleEnum;
import backendProyectoParqueo.model.Usuario;
import backendProyectoParqueo.repository.UsuarioRepository;
import static backendProyectoParqueo.security.Constants.EXPIRATION_TIME_ACCESS_TOKEN;
import static backendProyectoParqueo.security.Constants.EXPIRATION_TIME_REFRESH_TOKEN;
import backendProyectoParqueo.security.JwtManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
        private final UsuarioRepository usuarioRepository;
        private final JwtManager tokenManager;

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
                                mapRolesArray((String[]) row[8]) // roles
                );
        }

        private SignedInUser createSignedUserWithRefreshToken(
                        Usuario userEntity) {
                return createSignedInUser(userEntity)
                                .refreshToken(createRefreshToken(userEntity));
        }

        private SignedInUser createSignedInUser(Usuario usuario) {
                String token = tokenManager.create(usuario, EXPIRATION_TIME_ACCESS_TOKEN);
                return new SignedInUser().accessToken(token)
                                .roles(usuario.getRoles());
        }

        private String createRefreshToken(Usuario usuario) {
                String token = tokenManager.create(usuario, EXPIRATION_TIME_REFRESH_TOKEN);
                return token;
        }

        public SignedInUser getSignedInUser(Usuario usuario) {
                return createSignedUserWithRefreshToken(usuario);
        }
}
