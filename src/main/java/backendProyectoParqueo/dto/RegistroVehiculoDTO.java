package backendProyectoParqueo.dto;

import backendProyectoParqueo.model.Vehiculo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegistroVehiculoDTO {

    @NotBlank(message = "Placa es obligatoria")
    @Size(max = 10, message = "Placa debe tener máximo 10 caracteres")
    private String placa;

    @NotNull(message = "Tipo de vehículo es obligatorio")
    private Vehiculo.TipoVehiculo tipo;

    @NotBlank(message = "Foto delantera es obligatoria")
    private String fotoDelanteraBase64;

    @NotBlank(message = "Foto trasera es obligatoria")
    private String fotoTraseraBase64;

    @NotBlank(message = "Marca es obligatoria")
    @Size(max = 50)
    private String marca;

    @NotBlank(message = "Modelo es obligatorio")
    @Size(max = 50)
    private String modelo;

    @NotBlank(message = "Color es obligatorio")
    @Size(max = 30)
    private String color;
}
