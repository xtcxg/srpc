package com.miex.exchange;

import com.miex.protocol.InvocationHandler;
import com.miex.protocol.Result;

public interface Server {

    void start();

    Result dispatch(InvocationHandler handler);
}
