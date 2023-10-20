package com.miex.test.apply;

import com.miex.annotation.EnableSrpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Properties;

@SpringBootApplication
@EnableSrpc(basePackages = "com.miex.test.apply")
public class ApplyTest {
    public static void main(String[] args) {
        SpringApplication.run(ApplyTest.class);


//
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
