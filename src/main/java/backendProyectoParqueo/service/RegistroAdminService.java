package backendProyectoParqueo.service;

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

        if (usuario == null) {
            usuario = registroUsuarioService.crearUsuario(
                    dto.getCi(),
                    dto.getNombre(),
                    dto.getApellido(),
                    dto.getCorreo(),
                    dto.getNroCelular(),
                    dto.getPassword(),
                    dto.getFoto());
        } else {

            dto.setNombre(usuario.getNombre());
            dto.setApellido(usuario.getApellido());
            dto.setCorreo(usuario.getCorreo());
            dto.setNroCelular(usuario.getNroCelular());
            dto.setFoto(usuario.getFoto());
        }

        if (request.getRol() == RolAdmin.ADMINISTRADOR) {
            if (administradorRepository.existsById(usuario.getId())) {
                throw new IllegalArgumentException("Este usuario ya es administrador.");
            }
            Administrador administrador = new Administrador();
            administrador.setUsuario(usuario);
            administrador.setEsActivo(true);// por defecto
            administradorRepository.save(administrador);

        } else if (request.getRol() == RolAdmin.CAJERO) {
            if (cajeroRepository.existsById(usuario.getId())) {
                throw new IllegalArgumentException("Este usuario ya es cajero.");
            }
            Cajero cajero = new Cajero();
            cajero.setUsuario(usuario);
            cajero.setEsActivo(true); // por defecto
            cajeroRepository.save(cajero);
        }

        return usuario.getUsername();
    }
}
