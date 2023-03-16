package com.miex.test;

import com.miex.cache.PropertiesCache;
import com.miex.config.ApplicationConfig;
import com.miex.protocol.Exporter;
import com.miex.protocol.ProtocolManager;
import com.miex.provide.api.ProductService;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Properties;

public class ProvideTest {

    static ApplicationConfig config;

    @BeforeAll
    public static void before() {

        Properties properties = new Properties();
        properties.setProperty("srpc.exchange.port","3695");

        properties.setProperty("srpc.scan.provide","com.miex.provide");
        properties.setProperty("srpc.scan.apply","com.miex.provide");

        properties.setProperty("srpc.exchange.protocol", "json");

        properties.setProperty("srpc.registry.type","none");
        properties.setProperty("srpc.registry.host","127.0.0.1");
        properties.setProperty("srpc.registry.port","6379");
        config = new ApplicationConfig(properties);
    }

    @Test
    public void providerTest() {
        ProtocolManager protocolManager = config.getProtocolManager();
        Exporter<ProductService> exporter = protocolManager.getExporter(ProductService.class);
        ProductService target = exporter.getTarget();
        System.out.println(target.getName(1L));
//        ProductService apply = protocolManager.getApply(ProductService.class);
//        apply.getName(1L);
    }

    @Test
    public void jsonTest() {
        ProtocolManager protocolManager = config.getProtocolManager();
        Exporter<ProductService> exporter = protocolManager.getExporter(ProductService.class);
    }

    @AfterAll
    private static void after() throws InterruptedException {
        for(;;) {
            Thread.sleep(99999);
        }
    }
}
