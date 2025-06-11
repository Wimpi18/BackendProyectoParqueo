package backendProyectoParqueo.security;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static backendProyectoParqueo.security.Constants.API_URL_PREFIX;
import static backendProyectoParqueo.security.Constants.AUTHORITY_PREFIX;
import static backendProyectoParqueo.security.Constants.ROLE_CLAIM;
import static backendProyectoParqueo.security.Constants.TOKEN_URL;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                antMatcher(API_URL_PREFIX)))
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .cors(withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, TOKEN_URL).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(getJwtAuthenticationConverter())))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    private Converter<Jwt, AbstractAuthenticationToken> getJwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorityConverter = new JwtGrantedAuthoritiesConverter();
        authorityConverter.setAuthorityPrefix(AUTHORITY_PREFIX);
        authorityConverter.setAuthoritiesClaimName(ROLE_CLAIM);
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorityConverter);
        return converter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:5173",
                "https://frontend-sindicato-io99.vercel.app"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
