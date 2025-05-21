package backendProyectoParqueo.dto;

public class ClienteDTO {
    private String ci;
    private String nombreCompleto;
    private String tipo;

    public ClienteDTO(String ci, String nombreCompleto, String tipo) {
        this.ci = ci;
        this.nombreCompleto = nombreCompleto;
        this.tipo = tipo;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
