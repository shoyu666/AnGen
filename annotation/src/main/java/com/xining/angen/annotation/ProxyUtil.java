package com.xining.angen.annotation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @since 2019/5/27
 */
public class ProxyUtil {
    public static <T> T proxy(InvocationHandler invocationHandler, Class<T> tClass) {
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class<?>[]{tClass}, invocationHandler);
    }
}
