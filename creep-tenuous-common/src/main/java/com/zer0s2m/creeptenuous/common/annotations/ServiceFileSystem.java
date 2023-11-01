package com.zer0s2m.creeptenuous.common.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceFileSystem {

    String value() default "";

}
