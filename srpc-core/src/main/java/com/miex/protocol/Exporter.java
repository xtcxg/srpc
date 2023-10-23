package com.miex.protocol;

public interface Exporter<T> {

    void export();

    Result invoke(InvocationHandler handler);

    T getTarget();

    void setTarget(Object target);
}
