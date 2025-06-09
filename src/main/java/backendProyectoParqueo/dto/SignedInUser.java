package backendProyectoParqueo.dto;

import backendProyectoParqueo.enums.RoleEnum;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SignedInUser {
    private String accessToken;
    private String refreshToken;
    private RoleEnum[] roles;

    public SignedInUser accessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public SignedInUser refreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public SignedInUser roles(RoleEnum[] roles) {
        this.roles = roles;
        return this;
    }
}
