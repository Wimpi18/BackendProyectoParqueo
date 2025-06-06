package backendProyectoParqueo.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import backendProyectoParqueo.dto.SignedInUser;
import backendProyectoParqueo.model.Usuario;
import backendProyectoParqueo.repository.UsuarioRepository;
import backendProyectoParqueo.security.JwtManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final JwtManager tokenManager;

    public Usuario findUserByUsername(String username) {
        final String uname = username.trim();
        Optional<Usuario> oUserEntity = usuarioRepository.findByUsername(uname);
        Usuario userEntity = oUserEntity.orElseThrow(
                () -> new UsernameNotFoundException(String.format("No se encontró al usuario %s.", uname)));
        return userEntity;
    }

    private SignedInUser createSignedInUser(Usuario usuario) {
        String token = tokenManager.create(org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                // .authorities(Objects.nonNull(usuario.getRole()) ? usuario.getRole().name() :
                // "")
                .build());
        return new SignedInUser().username(usuario.getUsername()).accessToken(token)
                .userId(usuario.getId().toString());
    }

    public SignedInUser getSignedInUser(Usuario usuario) {
        return createSignedInUser(usuario);
    }
}
