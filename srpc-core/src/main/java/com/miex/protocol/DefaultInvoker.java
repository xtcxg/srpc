package com.miex.protocol;

public class DefaultInvoker<T> extends AbstractInvoker<T> {

    public DefaultInvoker(Class<T> type) {
        super(type);
    }

    @Override
    public Result doInvoke(InvocationHandler handler) {
        return super.doInvoke(handler);
    }
}
