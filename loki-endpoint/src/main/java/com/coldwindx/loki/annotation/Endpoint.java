package com.coldwindx.loki.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Endpoint {
    @AliasFor(annotation = Component.class, attribute = "value")
    String value() default "";
    String path();
}
