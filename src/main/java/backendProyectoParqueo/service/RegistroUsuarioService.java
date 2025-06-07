package backendProyectoParqueo.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import backendProyectoParqueo.model.Usuario;
import backendProyectoParqueo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistroUsuarioService {
    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    Usuario crearUsuario(String ci, String nombre, String apellido, String correo, String celular,
            String password) {
        if (usuarioRepo.existsByCi(ci))
            throw new IllegalArgumentException("CI ya registrado.");
        if (usuarioRepo.existsByCorreo(correo))
            throw new IllegalArgumentException("Correo ya registrado.");

        Usuario usuario = new Usuario();
        usuario.setCi(ci);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setCorreo(correo);
        usuario.setNroCelular(celular);
        usuario.setUsername("usuario_" + UUID.randomUUID());
        usuario.setPassword(passwordEncoder.encode(password));

        return usuarioRepo.save(usuario);
    }

}
