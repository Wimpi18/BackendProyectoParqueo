package backendProyectoParqueo.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtUserPayload {
    private final UUID userId;
    private final String username;
    private final List<String> roles;
}
