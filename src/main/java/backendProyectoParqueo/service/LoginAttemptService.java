package backendProyectoParqueo.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {

    private static final int MAX_INTENTOS = 4;
    private static final Duration DURACION_BLOQUEO = Duration.ofMinutes(15);

    // Estructura: username -> [intentos, hora bloqueo]
    private final Map<String, Integer> intentosFallidos = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> bloqueoTemporal = new ConcurrentHashMap<>();

    public void loginFallido(String username) {
        int intentos = intentosFallidos.getOrDefault(username, 0) + 1;
        intentosFallidos.put(username, intentos);

        if (intentos >= MAX_INTENTOS) {
            bloqueoTemporal.put(username, LocalDateTime.now());
        }
    }

    public boolean estaBloqueado(String username) {
        if (!bloqueoTemporal.containsKey(username)) {
            return false;
        }

        LocalDateTime horaBloqueo = bloqueoTemporal.get(username);
        if (LocalDateTime.now().isAfter(horaBloqueo.plus(DURACION_BLOQUEO))) {
            desbloquear(username);
            return false;
        }

        return true;
    }

    public void loginExitoso(String username) {
        desbloquear(username);
    }

    private void desbloquear(String username) {
        intentosFallidos.remove(username);
        bloqueoTemporal.remove(username);
    }
}
