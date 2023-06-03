package com.zer0s2m.creeptenuous.common.validation;

import com.zer0s2m.creeptenuous.common.validation.constraints.EnumValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Validator for <b>enum</b> types
 */
public class EnumValidatorConstraint implements ConstraintValidator<EnumValidator, String> {

    Set<String> values;

    /**
     * {@inheritDoc}
     * @param constraintAnnotation annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(@NotNull EnumValidator constraintAnnotation) {
        this.values = Stream.of(constraintAnnotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     * @param value object to validate
     * @param context context in which the constraint is evaluated
     *
     * @return
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return values.contains(value);
    }

}
