package com.coldwindx.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;

@Slf4j
public class ClassHelper {

    public static <T> Constructor<T> getDefaultConstructor(Class<T> clazz){
        try {
            return clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> T construct(Constructor<T> constructor){
        try {
            return constructor.newInstance();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
