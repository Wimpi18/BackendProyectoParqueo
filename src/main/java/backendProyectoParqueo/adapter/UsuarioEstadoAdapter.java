package backendProyectoParqueo.adapter;

import java.util.List;

import backendProyectoParqueo.enums.EstadoParqueo;

public class UsuarioEstadoAdapter {

    // Este método adapta el estado que verá el frontend
    public static String adaptarEstadoSalida(List<String> roles, EstadoParqueo estadoParqueo, Boolean estaActivo) {
        if (roles.contains("CLIENTE") && estadoParqueo != null) {
            return estadoParqueo.name(); // "Activo" o "Inactivo"
        } else if ((roles.contains("ADMINISTRADOR") || roles.contains("CAJERO")) && estaActivo != null) {
            return estaActivo ? "Activo" : "Inactivo";
        }
        return null;
    }

    // Convierte "Activo" o "Inactivo" en EstadoParqueo
    public static EstadoParqueo adaptarEstadoParqueo(String estado) {
        try {
            return EstadoParqueo.valueOf(estado);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    // Convierte "Activo" o "Inactivo" en Boolean
    public static Boolean adaptarEstaActivo(String estado) {
        if (estado == null)
            return null;
        return estado.equalsIgnoreCase("Activo");
    }
}
