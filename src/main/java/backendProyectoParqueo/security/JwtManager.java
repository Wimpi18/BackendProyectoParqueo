package backendProyectoParqueo.security;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import backendProyectoParqueo.enums.RoleEnum;
import backendProyectoParqueo.model.Usuario;
import static backendProyectoParqueo.security.Constants.ID_CLAIM;
import static backendProyectoParqueo.security.Constants.ROLE_CLAIM;

@Component
public class JwtManager {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    public JwtManager(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public String create(Usuario principal, long expirationMillis) {
        final long now = System.currentTimeMillis();

        // Convertir los enums de roles en un array de strings
        String[] roles = principal.getRoles() != null
                ? Arrays.stream(principal.getRoles())
                        .map(RoleEnum::getAuthority)
                        .toArray(String[]::new)
                : new String[] {};

        return JWT.create()
                .withIssuer("Sindicato parqueo UMSS")
                .withClaim(ID_CLAIM, principal.getId().toString())
                .withSubject(principal.getUsername())
                .withArrayClaim(ROLE_CLAIM, roles)
                .withIssuedAt(new Date(now))
                .withExpiresAt(new Date(now + expirationMillis))
                .sign(Algorithm.RSA256(publicKey, privateKey));
    }
}
