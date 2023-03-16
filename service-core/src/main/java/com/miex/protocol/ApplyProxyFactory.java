package com.miex.protocol;

import com.miex.exception.SrpcException;
import javassist.util.proxy.ProxyFactory;

public class ApplyProxyFactory {
    static ProxyFactory factory = new ProxyFactory();

    public static <T> T proxy(Invoker<T> invoker, Class<?> c){
        try {
            factory.setInterfaces(new Class[]{c});
            factory.setFilter(method -> true);
            factory.setHandler((target, method, method1, params) -> {
                InvocationHandler handler = new InvocationHandler();
                handler.setClassName(c.getName());
                handler.setMethodName(method.getName());
                handler.setTarget(target);
                handler.setParams(params);
                handler.setReturnType(method.getReturnType());
                handler.setParameterTypes(method.getParameterTypes());
                Result result = invoker.invoke(handler);
                return result.getValue();
            });
            Class<?> p = factory.createClass();
            return (T) p.newInstance();
        } catch ( InstantiationException | IllegalAccessException e) {
            throw new SrpcException();
        }
    }


}
