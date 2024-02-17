package com.zer0s2m.creeptenuous.api.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@RestController
@RequestMapping("/api/v1")
public @interface V1APIRestController {

    @AliasFor(annotation = Component.class)
    String value() default "";

}
