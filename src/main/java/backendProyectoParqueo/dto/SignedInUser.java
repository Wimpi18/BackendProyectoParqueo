package backendProyectoParqueo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SignedInUser {
    private String accessToken;
    private String refreshToken;
    private String[] roles;

    public SignedInUser accessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public SignedInUser refreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public SignedInUser roles(String[] roles) {
        this.roles = roles;
        return this;
    }
}
