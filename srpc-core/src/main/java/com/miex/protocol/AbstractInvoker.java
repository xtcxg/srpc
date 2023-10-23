package com.miex.protocol;


import com.miex.exchange.ExchangeManager;

public abstract class AbstractInvoker<T> implements Invoker<T> {

    Class<T> type;

    public AbstractInvoker(Class<T> type) {
        this.type = type;
    }

    @Override
    public Class<T> getInterface() {
        return type;
    }

    @Override
    public Result invoke(InvocationHandler handler) {
        return doInvoke(handler);
    }

    public Result doInvoke(InvocationHandler handler) {
        return ExchangeManager.dispatch(handler);
    }
}
