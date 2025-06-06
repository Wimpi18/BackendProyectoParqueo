package backendProyectoParqueo.controller;

// import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backendProyectoParqueo.dto.ApiResponse;
import backendProyectoParqueo.dto.SignInReq;
import backendProyectoParqueo.dto.SignedInUser;
import backendProyectoParqueo.model.Usuario;
import backendProyectoParqueo.service.UsuarioService;
import backendProyectoParqueo.util.ApiResponseUtil;
import jakarta.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<SignedInUser>> signIn(@RequestBody @Valid SignInReq signInReq) {
        Usuario usuario = usuarioService.findUserByUsername(signInReq.getUsername());

        if (passwordEncoder.matches(signInReq.getPassword(),
                usuario.getPassword())) {
            return ApiResponseUtil.success("Autorizado.", usuarioService.getSignedInUser(usuario));
        }
        throw new InsufficientAuthenticationException("Unauthorized.");
    }
}
