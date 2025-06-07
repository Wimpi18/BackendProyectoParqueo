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
public class RegistroAdminService {

    private final UsuarioRepository usuarioRepository;
    private final AdministradorRepository administradorRepository;
    private final CajeroRepository cajeroRepository;

    private final RegistroUsuarioService registroUsuarioService;

    public String registrarUsuarioAdmin(RegistroUsuarioAdminRequestDTO request) {
        UsuarioDTO dto = request.getUsuario();
        Usuario usuario = usuarioRepository.findByCi(dto.getCi()).orElse(null);

        // 1. Si no existe el usuario, lo creamos
        if (usuario == null) {
            usuario = registroUsuarioService.crearUsuario(
                    dto.getCi(),
                    dto.getNombre(),
                    dto.getApellido(),
                    dto.getCorreo(),
                    dto.getNroCelular(),
                    dto.getPassword());
        }

        if (request.getRol() == RolAdmin.ADMINISTRADOR &&
                administradorRepository.existsById(usuario.getId())) {
            throw new IllegalArgumentException("Este usuario ya es administrador");
        }

        if (request.getRol() == RolAdmin.CAJERO &&
                cajeroRepository.existsById(usuario.getId())) {
            throw new IllegalArgumentException("Este usuario ya es cajero");
        }

        // 3. Asignar rol
        if (request.getRol() == RolAdmin.ADMINISTRADOR) {
            Administrador administrador = new Administrador();
            administrador.setUsuario(usuario);
            administradorRepository.save(administrador);
        } else {
            Cajero cajero = new Cajero();
            cajero.setUsuario(usuario);
            cajeroRepository.save(cajero);
        }

        return usuario.getUsername();
    }
}
