package com.miex.exchange;

import com.miex.config.ServerConfig;
import com.miex.protocol.Exporter;
import com.miex.protocol.InvocationHandler;
import com.miex.protocol.ProtocolManager;
import com.miex.protocol.Result;

public abstract class AbstractServer implements Server {

    public static final ServerConfig SERVER_CONFIG = ExchangeManager.getServerConfig();

    public static final ProtocolManager protocolManager = ProtocolManager.getInstance();

    @Override
    public Result dispatch(InvocationHandler handler) {
        Exporter<?> exporter = protocolManager.getExporter(handler.getClassName());
        return exporter.invoke(handler);
    }
}
