package backendProyectoParqueo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backendProyectoParqueo.dto.AllUsuarioDTO;
import backendProyectoParqueo.dto.ApiResponse;
import backendProyectoParqueo.dto.UsuarioDetalleDTO;
import backendProyectoParqueo.enums.RoleEnum;
import backendProyectoParqueo.model.Usuario;
import backendProyectoParqueo.repository.UsuarioRepository;
import backendProyectoParqueo.service.UsuarioService;
import backendProyectoParqueo.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("usuario")
@RequiredArgsConstructor
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @GetMapping("/check-ci/{ci}")
    @PreAuthorize("hasRole('" + RoleEnum.Const.ADMINISTRADOR + "')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkIfUserExist(@PathVariable String ci) {
        Optional<Map<String, Object>> usuario = usuarioService.buscarPorCi(ci);

        if (usuario.isPresent()) {
            return ApiResponseUtil.success("Datos del usuario", usuario.get());
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("exists", false);
            return ApiResponseUtil.success("Usuario no encontrado.", response);
        }
    }

    @PostMapping("/crear")
    public Usuario createUsuario(@RequestBody Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<UsuarioDetalleDTO>> obtenerDetallePorId(@PathVariable UUID id) {
        List<UsuarioDetalleDTO> detalles = usuarioService.obtenerDetalleUsuario(id);
        if (detalles.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(detalles);
    }

    @GetMapping("/vista")
    public List<AllUsuarioDTO> obtenerVistaUsuarios() {
        return usuarioService.obtenerUsuariosVista();
    }

}
