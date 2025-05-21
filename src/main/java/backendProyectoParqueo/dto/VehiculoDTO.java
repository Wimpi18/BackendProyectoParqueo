package backendProyectoParqueo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehiculoDTO {
    private Long id;
    private String placa;
    private String tipo;
    // private byte[] fotoDelantera;
    // private byte[] fotoTrasera;

    public VehiculoDTO(String placa) {
        this.placa = placa;
    }
}
