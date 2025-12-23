package com.coldwindx.loki.annotation;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.lang.annotation.*;

/**
 * 实现组内共享同一实例的bean注解
 * ps: 不支持自定义bean名称
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scope(value = "shared", proxyMode = ScopedProxyMode.TARGET_CLASS)
public @interface Shared {}