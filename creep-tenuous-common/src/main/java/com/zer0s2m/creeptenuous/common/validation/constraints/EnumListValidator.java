package com.zer0s2m.creeptenuous.common.validation.constraints;

import com.zer0s2m.creeptenuous.common.validation.EnumListValidatorConstraint;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.*;

/**
 * The annotated element must be included in the {@literal enum}.
 */
@Documented
@Constraint(validatedBy = EnumListValidatorConstraint.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@NotNull
public @interface EnumListValidator {

    Class<? extends Enum<?>> enumClass();

    String message() default "not blank: {enum}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
