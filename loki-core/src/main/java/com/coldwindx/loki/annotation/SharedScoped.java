package com.coldwindx.loki.annotation;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Scope(value = "shared", proxyMode = ScopedProxyMode.TARGET_CLASS)
public @interface SharedScoped {
}
