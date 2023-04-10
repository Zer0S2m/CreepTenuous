package com.zer0s2m.CreepTenuous.api.core.version.v1;

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
public @interface V1APIController {
    @AliasFor(annotation = Component.class)
    String value() default "";
}
