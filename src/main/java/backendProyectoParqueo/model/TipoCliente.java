package backendProyectoParqueo.model;

public enum TipoCliente {
    ADMINISTRATIVO("Administrativo"),
    DOCENTE_EXCLUSIVA("Docente a dedicación exclusiva"),
    DOCENTE_HORARIO("Docente a tiempo horario");

    private final String dbValue;

    TipoCliente(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static TipoCliente fromDbValue(String value) {
        for (TipoCliente tipo : TipoCliente.values()) {
            if (tipo.dbValue.equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + value);
    }
}
