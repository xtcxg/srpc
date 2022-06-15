package com.miex.test;

import com.miex.annotation.EnableSrpc;
import com.miex.config.ApplicationConfig;
import com.miex.protocol.InvokerManager;
import com.miex.test.provide.api.ShopService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Properties;

@SpringBootApplication
@ComponentScan("com.miex.test.apply")
@EnableSrpc(basePackages = "com.miex.test.apply")
public class ApplyTest {
    public static void main(String[] args) {
        SpringApplication.run(ApplyTest.class);

//        Properties properties = new Properties();
//        properties.setProperty("srpc.port","3696");
//
//        properties.setProperty("srpc.scan","com.miex.apply");
//
//        properties.setProperty("srpc.registry.type","redis");
//        properties.setProperty("srpc.registry.host","127.0.0.1");
//        properties.setProperty("srpc.registry.port","6379");
//        ApplicationConfig config =  new ApplicationConfig(properties);
//        InvokerManager invokerManager = config.getInvokerManager();
//        ShopService shopService = invokerManager.get(ShopService.class);
//        System.out.println(shopService.buy(1L));
    }
}
