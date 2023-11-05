package com.zer0s2m.creeptenuous.common.validation;

import com.zer0s2m.creeptenuous.common.validation.constraints.UUIDCollectionValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.regex.Pattern;

public class UUIDCollectionValidatorConstraint
        implements ConstraintValidator<UUIDCollectionValidator, Collection<String>> {

    public final static Pattern REGEX_UUID = Pattern.compile(
            "^[a-f0-9]{8}(?:-[a-f0-9]{4}){4}[a-f0-9]{8}$");

    /**
     * {@inheritDoc}
     * @param value object to validate
     * @param context context in which the constraint is evaluated
     *
     * @return
     */
    @Override
    public boolean isValid(@NotNull Collection<String> value, ConstraintValidatorContext context) {
        for (String obj : value) {
            if (!REGEX_UUID.matcher(obj).matches()) {
                return false;
            }
        }
        return true;
    }

}
