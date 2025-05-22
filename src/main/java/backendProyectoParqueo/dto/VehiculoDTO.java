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
    private Long idParqueo;
    private String placa;
    private TipoVehiculo tipo;
    private byte[] fotoDelantera;
    private byte[] fotoTrasera;

    public VehiculoDTO(Long idParqueo, String placa, TipoVehiculo tipo) {
        this.idParqueo = idParqueo;
        this.placa = placa;
        this.tipo = tipo;
    }
}
