package com.coldwindx.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RocketProvider {
    String name() default "rocket";
    String cluster() default "";
    String topic();
    String group() default "";
}
