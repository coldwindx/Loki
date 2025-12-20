package com.coldwindx.annotation;

import com.coldwindx.config.RocketImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(value = {RocketImportBeanDefinitionRegistrar.class})
public @interface EnableRocket {
    String[] packages() default {};
}
