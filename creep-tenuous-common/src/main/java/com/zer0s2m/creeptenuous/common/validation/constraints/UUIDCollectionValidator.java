package com.zer0s2m.creeptenuous.common.validation.constraints;

import com.zer0s2m.creeptenuous.common.validation.UUIDCollectionValidatorConstraint;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.*;

/**
 * The annotated element must be in the UUID format.
 */
@Documented
@Constraint(validatedBy = UUIDCollectionValidatorConstraint.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@NotNull
public @interface UUIDCollectionValidator {

    String message() default "invalid format UUID: {uuid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
