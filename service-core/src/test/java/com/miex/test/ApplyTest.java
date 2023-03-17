package com.miex.test;

import com.miex.config.ApplicationConfig;
import com.miex.protocol.ProtocolManager;
import com.miex.provide.api.ProductService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Properties;

public class ApplyTest {
    static ApplicationConfig config;

    @BeforeAll
    public static void before() {
        Properties properties = new Properties();
        properties.setProperty("srpc.exchange.port","3696");

        properties.setProperty("srpc.scan","com.miex.apply");

        properties.setProperty("srpc.exchange.protocol", "json");

        properties.setProperty("srpc.registry.type","redis");
        properties.setProperty("srpc.registry.host","127.0.0.1");
        properties.setProperty("srpc.registry.port","6379");

        config = new ApplicationConfig(properties);
    }

    @Test
    public void create() {
        ProtocolManager protocolManager = config.getProtocolManager();

        ProductService productService = protocolManager.getApply(ProductService.class);
        String name = productService.getName(2L);
        System.out.println(name);
    }
}
