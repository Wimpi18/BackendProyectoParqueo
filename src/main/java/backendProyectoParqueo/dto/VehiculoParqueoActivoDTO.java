// src/main/java/backendProyectoParqueo/dto/VehiculoParqueoActivoDTO.java
package backendProyectoParqueo.dto;

import backendProyectoParqueo.enums.TipoVehiculo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoParqueoActivoDTO {
    private Long idParqueo; // ID del parqueo para usarlo como referencia si es necesario
    private String placa;
    private TipoVehiculo tipoVehiculo; // Puede ser útil para el frontend
}