package com.miex.test;

import com.miex.config.ApplicationConfig;
import com.miex.protocol.InvokerManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Properties;

public class ProvideTest {

    static ApplicationConfig config;

    @BeforeAll
    public static void before() {
        Properties properties = new Properties();
        properties.setProperty("srpc.port","3695");

        properties.setProperty("srpc.scan.provide","com.miex.provide");
        properties.setProperty("srpc.scan.apply","com.miex.provide");

        properties.setProperty("srpc.registry.type","redis");
        properties.setProperty("srpc.registry.host","127.0.0.1");
        properties.setProperty("srpc.registry.port","6379");
        config = new ApplicationConfig(properties);
    }

    @Test
    public void providerTest() throws InterruptedException, IOException, URISyntaxException, ClassNotFoundException {
        InvokerManager invokerManager = config.getInvokerManager();
    }

    @AfterAll
    private static void after() throws InterruptedException {
        for(;;) {
            Thread.sleep(99999);
        }
    }
}
