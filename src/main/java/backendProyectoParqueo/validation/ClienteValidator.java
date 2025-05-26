package backendProyectoParqueo.validation;

import backendProyectoParqueo.dto.PagoParqueoDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClienteValidator implements ConstraintValidator<ValidCliente, PagoParqueoDTO> {

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

        // Validar que la tarifa sea para el tipo de cliente y vehículo correcto
        /*
         * boolean tarifaCompatible =
         * tarifa.getTipoCliente().equalsIgnoreCase(cliente.getTipo()) &&
         * tarifa.getTipoVehiculo().equals(parqueo.getVehiculo().getTipo());
         */

        /*
         * if (!tarifaCompatible) {
         * buildViolation(context,
         * "La tarifa seleccionada no corresponde al tipo de usuario y vehículo.",
         * "idTarifa");
         * return false;
         * }
         */

        return true;
    }

    private void buildViolation(ConstraintValidatorContext context, String message, String property) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(property)
                .addConstraintViolation();
    }
}
