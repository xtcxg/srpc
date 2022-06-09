package com.miex.registry.redis;

import com.miex.cache.CacheManager;
import com.miex.cache.PropertiesCache;
import com.miex.exception.SrpcException;
import com.miex.registry.Registry;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class RegistryManager {
    private static final PropertiesCache PROPERTIES_CACHE = CacheManager.PROPERTIES_CACHE;

    private static Registry registry;

    public static synchronized Registry createRegistry() {
        if (registry != null) {
            return registry;
        }
        try {
            String ip = Inet4Address.getLocalHost().getHostAddress();
            PROPERTIES_CACHE.put("srpc.registry.ip", ip);
            String type = PROPERTIES_CACHE.get("srpc.registry.type");
            switch (type) {
                case "local" :
                    return null;
                default:
                    registry = buildRedisRegistry();
                    return registry;
            }
        } catch (UnknownHostException e) {
            throw new SrpcException(SrpcException.Enum.SYSTEM_ERROR,"acquire ip error");
        }
    }



    private static RedisRegistry buildRedisRegistry() {
        String host = PROPERTIES_CACHE.get("srpc.registry.host");
        Integer port = Integer.parseInt(PROPERTIES_CACHE.get("srpc.registry.port"));
        String name = PROPERTIES_CACHE.get("srpc.registry.name");
        String password = PROPERTIES_CACHE.get("srpc.registry.password");
        RedisRegistry registry = new RedisRegistry();
        registry.connect(host,port,name,password);
        return registry;
    }
}
