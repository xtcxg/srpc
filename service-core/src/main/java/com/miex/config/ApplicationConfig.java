package com.miex.config;

import com.miex.cache.PropertiesCache;
import com.miex.exchange.ExchangeManager;
import com.miex.exchange.Exchange;
import com.miex.protocol.ProtocolManager;
import com.miex.registry.Registry;
import com.miex.registry.RegistryManager;
import com.miex.util.Scanner;

import java.util.Enumeration;
import java.util.Properties;

public class ApplicationConfig {

  private final PropertiesCache propertiesCache = PropertiesCache.getInstance();
  private final ProtocolManager protocolManager;
  private final ExchangeManager exchangeManager;
  private final RegistryManager registryManager;

  public ApplicationConfig(Properties properties) {
    this(properties, null);
  }

  public ApplicationConfig(Properties properties, Class<?> main) {
    if (null != main) {
      properties.put("srpc.classpath", main.getPackageName());
    }

    setProperties(properties);

    this.protocolManager = ProtocolManager.getInstance();

    this.exchangeManager = ExchangeManager.getInstance();

    this.registryManager = RegistryManager.getInstance();

    init();

    System.err.println("SRPC start complete");
  }

  private void setProperties(Properties properties) {
    Enumeration<?> enumeration = properties.propertyNames();
    String key;
    while (enumeration.hasMoreElements()) {
      key = enumeration.nextElement().toString();
      propertiesCache.put(key, properties.getProperty(key));
    }
    PropertiesCache.checkProperties();
  }

  private void init() {
    // 扫描
    Scanner.scan();

    // 创建本地服务
    this.protocolManager.createAllExporter();

    // 开启服务器
    this.exchangeManager.openServer();

    // 注册服务
    this.registryManager.register();
  }

  public ProtocolManager getProtocolManager() {
    return this.protocolManager;
  }
}
