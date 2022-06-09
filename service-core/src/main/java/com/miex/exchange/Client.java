package com.miex.exchange;

import com.miex.protocol.InvocationHandler;
import com.miex.protocol.Result;

public interface Client {

    Result send(InvocationHandler handler);
}
