package com.coldwindx.loki.annotation;

import com.coldwindx.loki.support.SharedBeanImporter;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SharedBeanImporter.class)
public @interface EnableShared {}
