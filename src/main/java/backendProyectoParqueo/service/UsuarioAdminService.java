package backendProyectoParqueo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import backendProyectoParqueo.dto.RegistroUsuarioAdminRequestDTO;
import backendProyectoParqueo.dto.UsuarioDTO;
import backendProyectoParqueo.model.Administrador;
import backendProyectoParqueo.enums.RolAdmin;
import backendProyectoParqueo.model.Cajero;
import backendProyectoParqueo.model.Usuario;
import backendProyectoParqueo.repository.AdministradorRepository;
import backendProyectoParqueo.repository.CajeroRepository;
import backendProyectoParqueo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioAdminService {

    private final UsuarioRepository usuarioRepository;
    private final AdministradorRepository administradorRepository;
    private final CajeroRepository cajeroRepository;
    private final PasswordEncoder passwordEncoder;

    public String registrarUsuarioAdmin(RegistroUsuarioAdminRequestDTO request) {
        UsuarioDTO dto = request.getUsuario();
        Usuario usuario = usuarioRepository.findByCi(dto.getCi()).orElse(null);

        // 1. Si no existe el usuario, lo creamos
        if (usuario == null) {
            usuario = new Usuario();
            usuario.setCi(dto.getCi());
            usuario.setNombre(dto.getNombre());
            usuario.setApellido(dto.getApellido());
            usuario.setCorreo(dto.getCorreo());
            usuario.setNroCelular(dto.getNroCelular());
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));

            String username = generarUsername(dto.getNombre(), dto.getApellido(), dto.getCi());
            usuario.setUsername(username);

            usuarioRepository.save(usuario);
        }

        // 2. Verificamos que el rol no esté duplicado
        if (request.getRol() == RolAdmin.ADMINISTRADOR &&
                administradorRepository.existsById(usuario.getId())) {
            throw new IllegalStateException("Este usuario ya es administrador");
        }

        if (request.getRol() == RolAdmin.CAJERO &&
                cajeroRepository.existsById(usuario.getId())) {
            throw new IllegalStateException("Este usuario ya es cajero");
        }

        // 3. Asignamos el rol
        if (request.getRol() == RolAdmin.ADMINISTRADOR) {
            Administrador administrador = new Administrador();
            administrador.setId(usuario.getId());
            administrador.setUsuario(usuario);
            administradorRepository.save(administrador);
        } else {
            Cajero cajero = new Cajero();
            cajero.setId(usuario.getId());
            cajero.setUsuario(usuario);
            cajeroRepository.save(cajero);
        }

        return usuario.getUsername();
    }

    private String generarUsername(String nombre, String apellido, String ci) {
        String base = nombre.split(" ")[0].toLowerCase() + "." +
                apellido.split(" ")[0].toLowerCase() + "." + ci;
        String username = base;
        int i = 1;

        while (usuarioRepository.existsByUsername(username)) {
            username = base + "." + i++;
        }

        return username;
    }
}
