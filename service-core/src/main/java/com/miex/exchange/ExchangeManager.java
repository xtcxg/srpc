package com.miex.exchange;

import com.miex.cache.PropertiesCache;
import com.miex.config.ExchangeConfig;
import com.miex.exception.SrpcException;
import com.miex.protocol.InvocationHandler;
import com.miex.protocol.Result;
import com.miex.registry.RegistryManager;
import com.miex.util.ClassUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExchangeManager {

    private static final ExchangeConfig exchangeConfig;
    private static final String PREFIX = "srpc.exchange.";
    private static final String PROTOCOL;
    private static Exchange exchange;
    private static final Map<String,List<Client>> SERVICE_MAP = new ConcurrentHashMap<>();

    static {
        exchangeConfig = ClassUtil.buildFromProperties(PREFIX,
            ExchangeConfig.class, PropertiesCache.getInstance().getProperties());
        PROTOCOL = exchangeConfig.getProtocol();
        exchange = buildServer();
    }

    public static ExchangeConfig getExchangeConfig() {
        return exchangeConfig;
    }

    public static synchronized Exchange getServer() {
        if (null == exchange) {
            exchange = buildServer();
        }
        return exchange;
    }

    private static Exchange buildServer() {
        String className = PropertiesCache.getInstance().get("srpc.mapping.exchange." + PROTOCOL);
        return ClassUtil.createObject(className);
    }

    public static Result dispatch(InvocationHandler handler) {
        List<Client> clients = getClients(handler.getClassName());
        // todo load balance
        Client client = clients.get(0);
        return exchange.send(handler, client);
    }

    public static List<Client> getClients(String className) {
        List<Client> clients = SERVICE_MAP.get(className);
        if (null == clients) {
            clients = new ArrayList<>();
        }
        List<String> hosts = RegistryManager.getRegistry().getHosts(className);
        for (String host : hosts) {
            clients.add(exchange.getClient(host));
        }
        SERVICE_MAP.put(className,clients);
        return clients;
    }

    private static Client buildClient(String host) {
        String className = PropertiesCache.getInstance().get("srpc.mapping.client." + PROTOCOL);
        String[] arr = host.split(":");
        try {
            URI uri = new URI("http",null,arr[0],Integer.parseInt(arr[1]),"/",null,null);
            return ClassUtil.createObject(className,new Class[]{URI.class},new Object[]{uri});
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new SrpcException(SrpcException.Enum.SYSTEM_ERROR,"build uri error,host [" + host + "]");
        }
    }
}
