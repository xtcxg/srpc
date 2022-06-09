package com.miex.test;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.miex.cache.CacheManager;
import com.miex.registry.Registry;
import com.miex.registry.redis.RedisRegistry;
import com.miex.registry.redis.RegistryManager;
import com.miex.util.CollectionUtil;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

public class RegistryTest {
    static String ip;
    static RedisRegistry registry;

    @BeforeAll
    private static void before() throws UnknownHostException {
        CacheManager.PROPERTIES_CACHE.put("srpc.registry.host","127.0.0.1");
        CacheManager.PROPERTIES_CACHE.put("srpc.registry.port","6379");
        registry = (RedisRegistry) RegistryManager.createRegistry();
        ip = Inet4Address.getLocalHost().getHostAddress();

    }


    @Test
    public void connect() throws UnknownHostException {
        String ip = Inet4Address.getLocalHost().getHostAddress();
        Jedis jedis = new Jedis(ip,6379);
        jedis.connect();
//        String[] ss = new String[] {"com.miex.provider.api.ProductService", "com.miex.provider.api.UserService"};
//        List<String> list = jedis.hmget("srpc", ss);
//        System.out.println(list);

        Map<String, String> srpc = jedis.hgetAll("srpc");
        System.out.println(srpc);
    }

    @Test
    public void registry() throws IOException {
        registry.register();
//        registry.destroy();
//        System.in.read();
    }

    @Test
    public void lock() {
//        boolean r = registry.lock();
//        registry.unlock();
//        System.out.println(registry.lock(3000L));
//        registry.unlock();
    }

    @Test
    public void keepAlive() {
        long times = 23123/100;
        System.out.println(times);

    }

    public static void main(String[] args) {
        CacheManager.PROPERTIES_CACHE.put("srpc.registry.host","127.0.0.1");
        CacheManager.PROPERTIES_CACHE.put("srpc.registry.port","6379");
        RedisRegistry registry = (RedisRegistry) RegistryManager.createRegistry();
        registry.register();
        registry.destroy();
    }
}
