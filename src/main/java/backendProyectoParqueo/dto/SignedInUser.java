package backendProyectoParqueo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SignedInUser {
    private String username;
    private String accessToken;
    private String userId;

    public SignedInUser username(String username) {
        this.username = username;
        return this;
    }

    public SignedInUser accessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public SignedInUser userId(String userId) {
        this.userId = userId;
        return this;
    }
}
