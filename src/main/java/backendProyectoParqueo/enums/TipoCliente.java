package backendProyectoParqueo.enums;

public enum TipoCliente {
    ADMINISTRATIVO("Administrativo"),
    DOCENTE_EXCLUSIVA("Docente a dedicación exclusiva"),
    DOCENTE_HORARIO("Docente a tiempo horario");

    private final String label;

    TipoCliente(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}
