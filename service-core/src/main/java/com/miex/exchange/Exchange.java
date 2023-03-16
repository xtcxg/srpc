package com.miex.exchange;

import com.miex.protocol.InvocationHandler;
import com.miex.protocol.Result;

public interface Exchange {

    void init();

    Client getClient(String host);

    Result send(InvocationHandler handler, Client client);

    Result dispatch(InvocationHandler handler);
}
