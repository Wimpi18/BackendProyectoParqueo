package backendProyectoParqueo.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistroRequestDTO {

    @Valid
    private UsuarioDTO usuario;

    @Valid
    private ClienteDTO cliente;

    @NotNull(message = "Debe registrar al menos un vehículo")
    @Size(min = 1, message = "Debe registrar al menos un vehículo")
    @Valid
    private List<VehiculoDTO> vehiculos;

    @Valid
    private ParqueoDTO parqueo;

}