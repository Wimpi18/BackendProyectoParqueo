package backendProyectoParqueo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import backendProyectoParqueo.enums.TipoVehiculo;
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
    private TipoVehiculo tipo;
    private byte[] fotoDelantera;
    private byte[] fotoTrasera;

    public VehiculoDTO(String placa, TipoVehiculo tipo) {
        this.placa = placa;
        this.tipo = tipo;
    }
}
