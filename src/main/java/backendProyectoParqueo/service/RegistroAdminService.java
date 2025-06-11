package backendProyectoParqueo.service;

import org.springframework.stereotype.Service;

import backendProyectoParqueo.dto.RegistroUsuarioAdminRequestDTO;
import backendProyectoParqueo.dto.UsuarioDTO;
import backendProyectoParqueo.enums.RolAdmin;
import backendProyectoParqueo.model.Administrador;
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

        boolean yaEsAdmin = administradorRepository.existsById(usuario.getId());
        boolean yaEsCajero = cajeroRepository.existsById(usuario.getId());

        if (request.getRol() == RolAdmin.ADMINISTRADOR) {
            if (yaEsAdmin) {
                throw new IllegalArgumentException("Este usuario ya es administrador.");
            }
            if (yaEsCajero) {
                throw new IllegalArgumentException(
                        "Este usuario ya tiene el rol de cajero. No puede ser administrador.");
            }

            Administrador administrador = new Administrador();
            administrador.setUsuario(usuario);
            administrador.setEsActivo(true);
            administradorRepository.save(administrador);

        } else if (request.getRol() == RolAdmin.CAJERO) {
            if (yaEsCajero) {
                throw new IllegalArgumentException("Este usuario ya es cajero.");
            }
            if (yaEsAdmin) {
                throw new IllegalArgumentException(
                        "Este usuario ya tiene el rol de administrador. No puede ser cajero.");
            }

            Cajero cajero = new Cajero();
            cajero.setUsuario(usuario);
            cajero.setEsActivo(true);
            cajeroRepository.save(cajero);
        }

        return usuario.getUsername();
    }

}
