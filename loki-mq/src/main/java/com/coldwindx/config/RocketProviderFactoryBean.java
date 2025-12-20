package com.coldwindx.config;

import com.coldwindx.handler.DefaultRocketProvider;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.FactoryBean;

@Slf4j
public class RocketProviderFactoryBean implements FactoryBean<DefaultRocketProvider> {

    @Override
    public @Nullable DefaultRocketProvider getObject() throws Exception {
        log.info("RocketProviderFactoryBean.getObject()");
        return null;
    }

    @Override
    public @Nullable Class<?> getObjectType() {
        log.info("RocketProviderFactoryBean.getObjectType()");
        return null;
    }
}
