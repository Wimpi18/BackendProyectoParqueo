package backendProyectoParqueo.security;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import backendProyectoParqueo.resolvers.JwtUserArgumentResolver;

@Configuration
public class JwtUserArgumentResolverConfig implements WebMvcConfigurer {

    private final JwtUserArgumentResolver jwtUserArgumentResolver;

    public JwtUserArgumentResolverConfig(JwtUserArgumentResolver jwtUserArgumentResolver) {
        this.jwtUserArgumentResolver = jwtUserArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(jwtUserArgumentResolver);
    }
}