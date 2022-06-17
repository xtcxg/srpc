package com.miex.exchange;

import com.miex.cache.PropertiesCache;
import com.miex.config.ServerConfig;
import com.miex.exception.SrpcException;
import com.miex.registry.RegistryManager;
import com.miex.util.ClassUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExchangeManager {

    private static final ServerConfig SERVER_CONFIG;
    private static final String PREFIX = "srpc.server.";
    private static String PROTOCOL;
    private static Server server;
    private static final Map<String,List<Client>> SERVICE_MAP = new ConcurrentHashMap<>();

    static {
        SERVER_CONFIG = ClassUtil.buildFromProperties(PREFIX,ServerConfig.class, PropertiesCache.getInstance().getProperties());
        PROTOCOL = SERVER_CONFIG.getProtocol();
    }

    public static ServerConfig getServerConfig() {
        return SERVER_CONFIG;
    }

    public static synchronized Server getServer() {
        if (null == server) {
            server = buildServer();
        }
        return server;
    }

    private static Server buildServer() {
        String className = PropertiesCache.getInstance().get("srpc.mapping.server." + PROTOCOL);
        return ClassUtil.createObject(className);
    }

    public static List<Client> getClients(String className) {
        List<Client> clients = SERVICE_MAP.get(className);
        if (null == clients) {
            clients = new ArrayList<>();
        }
        List<String> hosts = RegistryManager.getRegistry().getHosts(className);
        for (String host : hosts) {
            clients.add(buildClient(host));
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
