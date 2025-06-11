package backendProyectoParqueo.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class RefreshToken {
    private String accessToken;
    private String[] roles;

    public RefreshToken accessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public RefreshToken roles(String[] roles) {
        this.roles = roles;
        return this;
    }
}
