package com.miex.test;

import com.miex.config.ApplicationConfig;
import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class JsonExchangeTest {
  static ApplicationConfig config;
  @BeforeAll
  public static void before() {
    Properties properties = new Properties();
    properties.setProperty("srpc.server.port","3695");

    properties.setProperty("srpc.scan.provide","com.miex.provide");
    properties.setProperty("srpc.scan.apply","com.miex.provide");

    properties.setProperty("srpc.exchange.protocol", "json");

    properties.setProperty("srpc.registry.type","redis");
    properties.setProperty("srpc.registry.host","127.0.0.1");
    properties.setProperty("srpc.registry.port","6379");
    config = new ApplicationConfig(properties);
  }

  @Test
  public void start() throws InterruptedException {
    config.getProtocolManager();

    Thread.sleep(999999);
  }

  @Test
  public void t1() {
    String icon = "";
    String[] split = icon.split(",");
  }
}
