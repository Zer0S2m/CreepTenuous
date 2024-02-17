package com.zer0s2m.creeptenuous.validation;

import com.zer0s2m.creeptenuous.validation.constraints.EnumListValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumListValidatorConstraint implements ConstraintValidator<EnumListValidator, List<String>> {

    Set<String> values;

    @Override
    public void initialize(@NotNull EnumListValidator constraintAnnotation) {
        this.values = Stream.of(constraintAnnotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        if (value.isEmpty()) {
            return false;
        }

        for (String right : value) {
            if (!values.contains(right)) {
                return false;
            }
        }
        return true;
    }

}
