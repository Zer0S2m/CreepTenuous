package com.zer0s2m.CreepTenuous.helpers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseValidationDataApi<T> {
    protected Validator getValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }

    protected void setErrorInvalidData(Validator validator, T invalidData) {
        Set<ConstraintViolation<T>> violations = validator.validate(invalidData);
        for (ConstraintViolation<T> violation : violations) {
            String error = violation.getMessage();
            assertThat(error).isNotEmpty();
        }
    }
}
