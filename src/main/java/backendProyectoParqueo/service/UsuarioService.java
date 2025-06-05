package backendProyectoParqueo.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import backendProyectoParqueo.model.Usuario;
import backendProyectoParqueo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public Usuario findUserByUsername(String username) {
        final String uname = username.trim();
        Optional<Usuario> oUserEntity = usuarioRepository.findByUsername(uname);
        Usuario userEntity = oUserEntity.orElseThrow(
                () -> new UsernameNotFoundException(String.format("No se encontró al usuario %s.", uname)));
        return userEntity;
    }
}
