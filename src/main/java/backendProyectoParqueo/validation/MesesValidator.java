package backendProyectoParqueo.validation;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MesesValidator implements ConstraintValidator<ValidMeses, LocalDate[]> {

    @Override
    public boolean isValid(LocalDate[] value, ConstraintValidatorContext context) {
        if (value == null || value.length == 0) {
            return false;
        }

        try {
            // Convertimos LocalDate a YearMonth y eliminamos nulos
            List<YearMonth> meses = Arrays.stream(value)
                    .filter(Objects::nonNull)
                    .map(YearMonth::from)
                    .sorted()
                    .collect(Collectors.toList());

            // Verificamos que no haya elementos duplicados o nulos perdidos
            if (meses.size() != value.length) {
                return false;
            }

            // Año actual y límite de 3 años hacia adelante
            Year currentYear = Year.now();
            Year maxYear = currentYear.plusYears(3);

            for (YearMonth mes : meses) {
                if (mes.isAfter(YearMonth.of(maxYear.getValue(), 12))) {
                    return false;
                }
            }

            // Verificamos que los meses sean consecutivos
            for (int i = 1; i < meses.size(); i++) {
                YearMonth anterior = meses.get(i - 1);
                YearMonth actual = meses.get(i);
                if (!actual.equals(anterior.plusMonths(1))) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
