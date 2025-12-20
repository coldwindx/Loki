package com.coldwindx.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RocketConfig {
    String name() default "rocket";
    String cluster() default "";
    String topic();
    String group() default "";
    String[] tags() default {};
}
