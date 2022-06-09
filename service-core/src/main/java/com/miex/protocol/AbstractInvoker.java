package com.miex.protocol;


import com.miex.exchange.Client;
import com.miex.exchange.ExchangeManager;

import java.util.List;

public abstract class AbstractInvoker<T> implements Invoker<T> {

    Class<T> type;

    List<Client> clients;

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

    public abstract Result doInvoke(InvocationHandler handler);

    public Client getClient() {
        if (null == this.clients) {
            this.clients = ExchangeManager.getClients(type.getName());
        }
        // todo load balance
        return this.clients.get(0);
    }
}
