package backendProyectoParqueo.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = MesesValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMeses {
    String message() default "El campo 'meses' es obligatorio y debe tener al menos una fecha válida";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
