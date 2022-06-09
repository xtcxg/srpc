package com.miex.exchange;

import com.miex.cache.CacheManager;
import com.miex.protocol.Exporter;
import com.miex.protocol.ExporterManager;
import com.miex.protocol.InvocationHandler;
import com.miex.protocol.Result;

public abstract class AbstractServer implements Server {

    public static final String PORT = CacheManager.PROPERTIES_CACHE.get("srpc.port");

    public static final ExporterManager exporterManager = ExporterManager.getInstance();

    @Override
    public Result dispatch(InvocationHandler handler) {
        Exporter<?> exporter = exporterManager.createExporter(handler.getClassName());
        return exporter.invoke(handler);
    }
}
