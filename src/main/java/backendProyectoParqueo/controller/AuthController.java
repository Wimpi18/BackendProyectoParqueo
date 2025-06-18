package backendProyectoParqueo.controller;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import backendProyectoParqueo.dto.ApiResponse;
import backendProyectoParqueo.dto.SignInReq;
import backendProyectoParqueo.dto.SignedInUser;
import backendProyectoParqueo.model.Usuario;
import backendProyectoParqueo.security.Constants;
import backendProyectoParqueo.service.LoginAttemptService;
import backendProyectoParqueo.service.UsuarioService;
import backendProyectoParqueo.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;

    @PostMapping("/signIn")
    public ResponseEntity<SignedInUser> signIn(@RequestBody @Valid SignInReq signInReq) {

        if (loginAttemptService.estaBloqueado(signInReq.getUsername())) {
            throw new LockedException("Cuenta bloqueada temporalmente. Intenta nuevamente más tarde.");
        }

        Usuario usuario = usuarioService.findUserByUsername(signInReq.getUsername());

        if (passwordEncoder.matches(signInReq.getPassword(),
                usuario.getPassword())) {
            SignedInUser signedInUser = usuarioService.getSignedInUser(usuario);
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", signedInUser.getRefreshToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(Duration.ofMillis(Constants.EXPIRATION_TIME_REFRESH_TOKEN))
                    .sameSite("None")
                    .build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(signedInUser);
        }

        loginAttemptService.loginFallido(signInReq.getUsername());
        throw new InsufficientAuthenticationException("Credenciales inválidas.");
    }

    @GetMapping("/refresh")
    public ResponseEntity<ApiResponse<SignedInUser>> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {

        if (refreshToken != null) {

            try {
                DecodedJWT decoded = usuarioService.decodeRefreshToken(refreshToken);
                Usuario usuario = usuarioService.findUserByUsername(decoded.getSubject());

                SignedInUser signedInUser = usuarioService.getSignedInUser(usuario);
                signedInUser.setRefreshToken(null);
                return ApiResponseUtil.success("Autorizado", signedInUser);

            } catch (JWTVerificationException ex) {
                throw new InsufficientAuthenticationException("Refresh token inválido o expirado");
            }
        }

        throw new InsufficientAuthenticationException("No se encontró el refresh token");

    }

    @PostMapping("/signOut")
    public ResponseEntity<ResponseEntity<ApiResponse<Void>>> signOut(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {

        if (refreshToken != null) {
            ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(0)
                    .sameSite("None")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                    .body(ApiResponseUtil.successMessage("Sesión cerrada correctamente"));
        }

        throw new InsufficientAuthenticationException("No se encontró el refresh token");
    }
}
