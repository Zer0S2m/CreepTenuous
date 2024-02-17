package com.zer0s2m.creeptenuous.validation.constraints;

import com.zer0s2m.creeptenuous.validation.EnumValidatorConstraint;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.*;

/**
 * The annotated element must be included in the {@literal enum}.
 */
@Documented
@Constraint(validatedBy = EnumValidatorConstraint.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@NotNull
public @interface EnumValidator {

    Class<? extends Enum<?>> enumClass();

    String message() default "not blank: {enum}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
