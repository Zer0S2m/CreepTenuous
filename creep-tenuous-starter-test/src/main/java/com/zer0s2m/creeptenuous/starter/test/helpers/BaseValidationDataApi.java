package com.zer0s2m.creeptenuous.starter.test.helpers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Class for servicing component instances for testing
 * @param <T> object with restrictions settings {@link jakarta.validation.constraints}
 */
public class BaseValidationDataApi<T> {

    /**
     * Get a validator that validates component instances
     * @return validator
     */
    protected Validator getValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }

    /**
     * Set validation errors and check
     * @param validator validator that validates component instances
     * @param invalidData component instance with invalid validation data
     */
    protected void setErrorInvalidData(Validator validator, T invalidData) {
        Set<ConstraintViolation<T>> violations = validator.validate(invalidData);
        for (ConstraintViolation<T> violation : violations) {
            String error = violation.getMessage();
            assertThat(error).isNotEmpty();
        }
    }

}
