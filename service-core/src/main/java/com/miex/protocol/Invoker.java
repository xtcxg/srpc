package com.miex.protocol;

public interface Invoker<T> {

    Class<T> getInterface();

    Result invoke(InvocationHandler handler);
}
