package com.miex.test;

import com.miex.registry.redis.RedisRegistry;
import com.miex.registry.RegistryManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.*;

public class RegistryTest {
    static String ip;
    static RedisRegistry registry;

    @BeforeAll
    private static void before() throws UnknownHostException {

        registry = (RedisRegistry) RegistryManager.getRegistry();
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

        RedisRegistry registry = (RedisRegistry) RegistryManager.getRegistry();
        registry.register();
        registry.destroy();
    }
}
