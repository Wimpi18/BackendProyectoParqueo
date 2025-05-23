package backendProyectoParqueo.validation;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MesesValidator implements ConstraintValidator<ValidMeses, Date[]> {

    @Override
    public boolean isValid(Date[] value, ConstraintValidatorContext context) {
        if (value == null || value.length == 0) {
            return false;
        }

        try {
            List<YearMonth> meses = Arrays.stream(value)
                    .filter(Objects::nonNull)
                    .map(date -> YearMonth.from(
                            date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()))
                    .collect(Collectors.toCollection(java.util.ArrayList::new));

            meses.sort(YearMonth::compareTo);

            if (meses.size() != value.length)
                return false;

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
