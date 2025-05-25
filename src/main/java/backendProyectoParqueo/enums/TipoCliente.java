package backendProyectoParqueo.enums;

public enum TipoCliente {
    ADMINISTRATIVO("Administrativo"),
    DOCENTE_DEDICACION_EXCLUSIVA("Docente a dedicación exclusiva"),
    DOCENTE_TIEMPO_HORARIO("Docente a tiempo horario");

    private final String label;

    TipoCliente(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static TipoCliente fromLabel(String label) {
        for (TipoCliente tipo : values()) {
            if (tipo.label.equalsIgnoreCase(label)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("TipoCliente inválido: " + label);
    }
}
