// backendProyectoParqueo.dto.ClientePlacaRequestDTO.java
package backendProyectoParqueo.dto;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClientePlacaRequestDTO {
    private UUID clienteId;
    private String placa;
}