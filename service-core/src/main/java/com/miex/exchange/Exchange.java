package com.miex.exchange;

import com.miex.config.ExchangeConfig;
import com.miex.protocol.InvocationHandler;
import com.miex.protocol.Result;

public interface Exchange {
    ExchangeConfig config = new ExchangeConfig();

    void init();

    Client getClient(String address);

    Result send(InvocationHandler handler, Client client);

    Result dispatch(InvocationHandler handler);

    default ExchangeConfig getConfig() {
        return config;
    }

    default String getProtocol() {
        return config.getProtocol();
    }
}
