package backendProyectoParqueo.resolvers;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.auth0.jwt.interfaces.DecodedJWT;

import backendProyectoParqueo.dto.JwtUserPayload;
import static backendProyectoParqueo.security.Constants.AUTHORIZATION;
import static backendProyectoParqueo.security.Constants.ID_CLAIM;
import static backendProyectoParqueo.security.Constants.ROLE_CLAIM;
import static backendProyectoParqueo.security.Constants.TOKEN_PREFIX;
import backendProyectoParqueo.security.JwtManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtManager jwtManager;

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UserGuard.class)
                && parameter.getParameterType().equals(JwtUserPayload.class);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter,
            @Nullable ModelAndViewContainer mavContainer,
            @NonNull NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        if (request == null)
            return null;

        String authHeader = request.getHeader(AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            String token = authHeader.substring(7);
            DecodedJWT decoded = jwtManager.decode(token);
            UUID userId = UUID.fromString(decoded.getClaim(ID_CLAIM).asString());
            String username = decoded.getSubject();
            List<String> roles = Arrays.asList(decoded.getClaim(ROLE_CLAIM).asArray(String.class));

            return new JwtUserPayload(userId, username, roles);
        }

        return null;
    }
}