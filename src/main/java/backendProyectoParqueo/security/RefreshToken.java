package backendProyectoParqueo.security;

import java.time.Instant;

import backendProyectoParqueo.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshToken {
    private String refreshToken;
    private Instant expiration;
    private Usuario user;
}
