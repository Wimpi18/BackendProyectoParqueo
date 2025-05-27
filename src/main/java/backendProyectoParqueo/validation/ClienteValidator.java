package backendProyectoParqueo.validation;

import backendProyectoParqueo.dto.PagoParqueoDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClienteValidator implements ConstraintValidator<ValidCliente, PagoParqueoDTO> {

    @Override
    public boolean isValid(PagoParqueoDTO dto, ConstraintValidatorContext context) {
        if (dto.getIdCajero() != null && dto.getIdCliente().equals(dto.getIdCajero())) {
            return buildViolation(context, "El cliente no puede ser el mismo que el cajero.", "idCliente");
        }
        return true;
    }

    private boolean buildViolation(ConstraintValidatorContext context, String message, String property) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(property)
                .addConstraintViolation();
        return false;
    }
}
