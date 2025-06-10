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
    private String placa;
    private String marca;
    private TipoVehiculo tipo;
    private byte[] fotoDelantera;
    private byte[] fotoTrasera;
    private String modelo;
    private String color;

    public VehiculoDTO(Long idParqueo, String placa, TipoVehiculo tipo, String marca, String modelo, String color) {
        this.placa = placa;
        this.tipo = tipo;
        this.marca = marca;
        this.modelo = modelo;
        this.color = color;
    }
}
