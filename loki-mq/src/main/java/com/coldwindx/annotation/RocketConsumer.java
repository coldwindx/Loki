package com.coldwindx.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RocketConsumer {
    RocketConfig[] value() default {};
}
