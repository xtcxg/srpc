package com.miex.test;

import com.miex.annotation.EnableSrpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ComponentScan("com.miex.test.provide")
@EnableSrpc(basePackages = "com.miex.test.provide")
public class ScanTest {

    public static void main(String[] args) {
        ConfigurableApplicationContext config = SpringApplication.run(ScanTest.class);
    }
}
