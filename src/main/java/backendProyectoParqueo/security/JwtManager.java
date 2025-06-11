package backendProyectoParqueo.security;

import java.util.Arrays;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import backendProyectoParqueo.enums.RoleEnum;
import backendProyectoParqueo.model.Usuario;
import static backendProyectoParqueo.security.Constants.ID_CLAIM;
import static backendProyectoParqueo.security.Constants.ROLE_CLAIM;

@Component
public class JwtManager {
    private final String SECRET_KEY;

    public JwtManager(@Value("${jwt.secret}") String secretKey) {
        this.SECRET_KEY = secretKey;
    }

    public String create(Usuario principal, long expirationMillis) {
        final long now = System.currentTimeMillis();

        // Convertir los enums de roles en un array de strings
        String[] roles = principal.getRoles() != null
                ? Arrays.stream(principal.getRoles())
                        .map(RoleEnum::name)
                        .toArray(String[]::new)
                : new String[] {};

        return JWT.create()
                .withIssuer("Sindicato parqueo UMSS")
                .withClaim(ID_CLAIM, principal.getId().toString())
                .withSubject(principal.getUsername())
                .withArrayClaim(ROLE_CLAIM, roles)
                .withIssuedAt(new Date(now))
                .withExpiresAt(new Date(now + expirationMillis))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    public DecodedJWT decode(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("Sindicato parqueo UMSS")
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException ex) {
            throw new IllegalArgumentException("Token inválido o expirado", ex);
        }
    }
}
