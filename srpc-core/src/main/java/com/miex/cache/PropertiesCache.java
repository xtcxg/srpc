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
        CACHE.put("srpc.classpath", "");

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

        CACHE.put("srpc.exchange.port", "3695");
        CACHE.put("srpc.exchange.host", "");
        CACHE.put("srpc.exchange.protocol", "http");

        CACHE.put("srpc.loadbalance.type", "simple");

        CACHE.put("srpc.mapping.protocol.java","com.miex.protocol.DefaultExporter");
        CACHE.put("srpc.mapping.invoker.java","com.miex.protocol.DefaultInvoker");
        /* register */
        CACHE.put("srpc.mapping.registry.redis","com.miex.registry.redis.RedisRegistry");
        CACHE.put("srpc.mapping.registry.zk","com.miex.registry.zk.ZkRegistry");
        CACHE.put("srpc.mapping.registry.none","com.miex.registry.none.NoneRegistry");
        /* exchange */
        CACHE.put("srpc.mapping.exchange.http","com.miex.exchange.http.HttpExchange");
        CACHE.put("srpc.mapping.client.http","com.miex.exchange.http.HttpClient");
        CACHE.put("srpc.mapping.exchange.json","com.miex.exchange.http.HttpJsonExchange");
        CACHE.put("srpc.mapping.client.json","com.miex.exchange.http.HttpJsonClient");
        /* load balance */
        CACHE.put("srpc.mapping.loadbalance.simple","com.miex.loadbalance.SimpleLoadBalance");
        CACHE.put("srpc.mapping.loadbalance.tryBest","com.miex.loadbalance.TryBestLoadBalance");

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

    public static void finish() {

    }
}
