package com.coldwindx.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RocketConfig {
    String cluster() default "";
    String topic();
    String group() default "";
    String[] tags() default {};
}
