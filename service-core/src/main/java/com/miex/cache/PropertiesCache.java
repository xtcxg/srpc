package com.miex.cache;

import com.miex.exception.SrpcException;
import com.miex.util.Assert;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Properties cache
 */
public class PropertiesCache implements Cache<String, String> {

    private final ConcurrentHashMap<String, String> CACHE = new ConcurrentHashMap<>();

    public PropertiesCache() {
        CACHE.put("srpc.registry.type", "redis");
        CACHE.put("srpc.registry.name", "");
        CACHE.put("srpc.registry.password", "");
        CACHE.put("srpc.registry.service.key","srpc");
        CACHE.put("srpc.registry.server.ttl","60"); // 服务在注册中心保持时间/s
        CACHE.put("srpc.registry.redis.lock.key","srpcLock");
        CACHE.put("srpc.registry.redis.hosts","srpcHosts");

        CACHE.put("srpc.ip", "");
        CACHE.put("srpc.scan", "");
        CACHE.put("srpc.scan.provide", "");
        CACHE.put("srpc.scan.apply", "");
        CACHE.put("srpc.port", "3695");
        CACHE.put("srpc.protocol", "http");
    }

    @Override
    public void put(String key, String value) {
        CACHE.put(key, value);
    }

    @Override
    public String get(String key) {
        String value = CACHE.get(key);
        if (value == null) {
            throw new SrpcException(SrpcException.Enum.RESOURCE_NOT_FOUND, "property [" + key + "] not found");
        }
        return value;
    }
}
