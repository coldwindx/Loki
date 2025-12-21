package com.coldwindx.loki.annotation;

import com.coldwindx.loki.config.EndpointDefinitionImporter;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Import(EndpointDefinitionImporter.class)
public @interface EnableEndpoint {
    String[] packages();
}
