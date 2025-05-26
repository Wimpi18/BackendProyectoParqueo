package backendProyectoParqueo.validation;

import backendProyectoParqueo.dto.PagoParqueoDTO;
import backendProyectoParqueo.model.Cliente;
import backendProyectoParqueo.model.Parqueo;
import backendProyectoParqueo.model.Tarifa;
import backendProyectoParqueo.repository.ClienteRepository;
import backendProyectoParqueo.repository.ParqueoRepository;
import backendProyectoParqueo.repository.TarifaRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClienteValidator implements ConstraintValidator<ValidCliente, PagoParqueoDTO> {

    private final ClienteRepository clienteRepository;
    private final ParqueoRepository parqueoRepository;
    private final TarifaRepository tarifaRepository;

    @Override
    public boolean isValid(PagoParqueoDTO dto, ConstraintValidatorContext context) {
        boolean valid = true;

        // Validación de nulls básicos
        if (dto.getIdCliente() == null || dto.getIdParqueo() == null || dto.getIdTarifa() == null) {
            return false;
        }

        // 1. Verificar que cliente != cajero
        if (dto.getIdCajero() != null && dto.getIdCliente().equals(dto.getIdCajero())) {
            buildViolation(context, "El cliente no puede ser el mismo que el cajero.", "idCliente");
            valid = false;
        }

        // 2. Verificar que el usuario exista y sea un cliente válido
        Cliente cliente = clienteRepository.findById(dto.getIdCliente()).orElse(null);
        if (cliente == null) {
            buildViolation(context, "El ID proporcionado no corresponde a un cliente válido.", "idCliente");
            valid = false;
        }

        // 3. Verificar que el parqueo existe y obtener tipo de vehículo
        Parqueo parqueo = parqueoRepository.findById(dto.getIdParqueo()).orElse(null);
        if (parqueo == null) {
            buildViolation(context, "El parqueo seleccionado no existe.", "idParqueo");
            valid = false;
        }

        // 4. Verificar que la tarifa corresponde al tipo de usuario y tipo de vehículo
        Tarifa tarifa = tarifaRepository.findById(dto.getIdTarifa()).orElse(null);
        if (tarifa == null) {
            buildViolation(context, "La tarifa seleccionada no existe.", "idTarifa");
            valid = false;
        }

        // Si alguno es null, no se puede continuar
        if (!valid || cliente == null || parqueo == null || tarifa == null) {
            return false;
        }

        // Validar que la tarifa sea para el tipo de cliente y vehículo correcto
        boolean tarifaCompatible = tarifa.getTipoCliente().equalsIgnoreCase(cliente.getTipo()) &&
                tarifa.getTipoVehiculo().equals(parqueo.getVehiculo().getTipo());

        if (!tarifaCompatible) {
            buildViolation(context, "La tarifa seleccionada no corresponde al tipo de usuario y vehículo.", "idTarifa");
            return false;
        }

        return true;
    }

    private void buildViolation(ConstraintValidatorContext context, String message, String property) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(property)
                .addConstraintViolation();
    }
}
