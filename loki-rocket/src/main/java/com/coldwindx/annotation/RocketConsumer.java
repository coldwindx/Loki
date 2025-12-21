package com.coldwindx.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RocketConsumer.Container.class)
public @interface RocketConsumer {
    String cluster() default "";
    String topic();
    String group() default "";
    String[] tags() default {};

    @Documented
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Container {
        RocketConsumer[] value();
    }
}
