package com.coldwindx.loki.support;

import com.coldwindx.loki.annotation.Endpoint;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 自定义HandlerMapping，自动扫描并注册带有@Endpoint注解的WebSocketHandler
 */
@Component
public class EndpointHandlerMapping extends SimpleUrlHandlerMapping implements ApplicationContextAware {

    @Override
    public void initApplicationContext() throws BeansException {
        Map<String, WebSocketHandler> handlerMap = new LinkedHashMap<>();

        // 扫描所有带有@Endpoint注解的Bean
        ApplicationContext applicationContext = Objects.requireNonNull(getApplicationContext());
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(Endpoint.class);

        for (Object bean : beanMap.values()) {
            if (!(bean instanceof WebSocketHandler))
                throw new IllegalStateException("@Endpoint can only be used on a WebSocketHandler, bean: " + bean.getClass().getName());

            Endpoint annotation = AnnotationUtils.getAnnotation(bean.getClass(), Endpoint.class);
            String path = Objects.requireNonNull(annotation).value();
            handlerMap.put(path, (WebSocketHandler) bean);
        }

        // 设置最高优先级
        super.setOrder(Ordered.HIGHEST_PRECEDENCE);
        super.setUrlMap(handlerMap);
        super.initApplicationContext();
    }
}