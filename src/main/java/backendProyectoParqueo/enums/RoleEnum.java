package backendProyectoParqueo.enums;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RoleEnum implements GrantedAuthority {
    CLIENTE(Const.CLIENTE),

    ADMINISTRADOR(Const.ADMINISTRADOR),

    CAJERO(Const.CAJERO);

    private final String authority;

    RoleEnum(String authority) {
        this.authority = authority;
    }

    @JsonCreator
    public static RoleEnum fromAuthority(String authority) {
        for (RoleEnum b : RoleEnum.values()) {
            if (b.authority.equals(authority)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + authority + "'");
    }

    @Override
    public String toString() {
        return String.valueOf(authority);
    }

    @Override
    @JsonValue
    public String getAuthority() {
        return authority;
    }

    public class Const {
        public static final String ADMINISTRADOR = "ROLE_ADMINISTRADOR";
        public static final String CLIENTE = "ROLE_CLIENTE";
        public static final String CAJERO = "ROLE_CAJERO";
    }
}