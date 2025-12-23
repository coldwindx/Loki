package com.coldwindx.loki.annotation;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 实现组内共享同一实例的bean注解
 * ps: 不支持自定义bean名称
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component
public @interface Shared {
    @AliasFor(annotation = Component.class, attribute = "value")
    String value() default "";

    @AliasFor(annotation = Scope.class, attribute = "scopeName")
    String scope();
}