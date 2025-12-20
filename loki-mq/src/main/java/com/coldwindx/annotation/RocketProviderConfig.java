package com.coldwindx.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RocketProviderConfig {
    RocketProvider[] value() default {};
}
