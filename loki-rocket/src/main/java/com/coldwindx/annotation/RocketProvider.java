package com.coldwindx.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RocketProvider.Container.class)
public @interface RocketProvider {
    String cluster() default "";
    String topic();
    String group() default "";

    @Documented
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Container {
        RocketProvider[] value();
    }
}
