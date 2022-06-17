package com.miex.cache;

import com.miex.exception.SrpcException;
import com.miex.util.Assert;
import com.miex.util.StringUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Properties cache
 */
public class PropertiesCache {

    private static final PropertiesCache PROVIDE_CACHE = new PropertiesCache();

    private final ConcurrentHashMap<String, String> CACHE = new ConcurrentHashMap<>();

    public Map<String,String> getProperties() {
        return CACHE;
    }

    public static PropertiesCache getInstance() {
        return PROVIDE_CACHE;
    }

    private PropertiesCache() {
        CACHE.put("srpc.registry.type", "redis");
        CACHE.put("srpc.registry.name", "");
        CACHE.put("srpc.registry.password", "");
        CACHE.put("srpc.registry.serverIndexName","srpc"); // 存储 [{server:hosts}]
        CACHE.put("srpc.registry.ttl","30"); // 服务在注册中心保持时间/s
        CACHE.put("srpc.registry.lock","srpcLock");
        CACHE.put("srpc.registry.hostIndexName","srpcHosts"); // 存储 [{host:services}]

        CACHE.put("srpc.scan", "");
        CACHE.put("srpc.scan.provide", "");
        CACHE.put("srpc.scan.apply", "");

        CACHE.put("srpc.server.port", "3695");
        CACHE.put("srpc.server.protocol", "http");

        CACHE.put("srpc.mapping.registry.redis","com.miex.registry.redis.RedisRegistry");
        CACHE.put("srpc.mapping.registry.zk","com.miex.registry.zk.ZkRegistry");
        CACHE.put("srpc.mapping.registry.local","com.miex.registry.local.LocalRegistry");
        CACHE.put("srpc.mapping.server.http","com.miex.exchange.http.HttpServer");
        CACHE.put("srpc.mapping.client.http","com.miex.exchange.http.HttpClient");
        CACHE.put("srpc.mapping.exporter.java","com.miex.protocol.DefaultExporter");
        CACHE.put("srpc.mapping.invoker.java","com.miex.protocol.DefaultInvoker");
    }

    public void put(String key, String value) {
        if (StringUtil.isEmpty(key) || StringUtil.isEmpty(value)) {
            throw new SrpcException(SrpcException.Enum.PARAM_ERROR,"property can't be empty,key [" + key +"],value [" + value + "]");
        }
        CACHE.put(key, value);
    }

    public String get(String key) {
        String value = CACHE.get(key);
        if (value == null) {
            throw new SrpcException(SrpcException.Enum.RESOURCE_NOT_FOUND, "property [" + key + "] not found");
        }
        return value;
    }

    public static void checkProperties() {
//        Assert.EmptyString(PROVIDE_CACHE.get("srpc.registry.type"),"property [srpc.registry.type] not found");
//        Assert.EmptyString(PROVIDE_CACHE.get("srpc.server.protocol"),"property [srpc.server.protocol] not found");
    }
}
