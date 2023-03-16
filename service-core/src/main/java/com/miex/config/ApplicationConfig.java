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

  private final PropertiesCache PROPERTIES_CACHE = PropertiesCache.getInstance();

  private final Registry registry;
  private final Exchange exchange;
  private final ProtocolManager protocolManager;

  public ApplicationConfig(Properties properties) {
    setProperties(properties);

    this.protocolManager = ProtocolManager.getInstance();

    this.exchange = ExchangeManager.getServer();

    this.registry = RegistryManager.getRegistry();

    init();
    System.err.println("SRPC start complete");
  }

  private void setProperties(Properties properties) {
    Enumeration<?> enumeration = properties.propertyNames();
    String key;
    while (enumeration.hasMoreElements()) {
      key = enumeration.nextElement().toString();
      PROPERTIES_CACHE.put(key, properties.getProperty(key));
    }
    PropertiesCache.checkProperties();
  }

  private void init() {
    // 扫描
    Scanner.scan();

    // 创建本地服务
    this.protocolManager.createAllExporter();

    // 开启服务器
    this.exchange.init();

    // 注册服务
    this.registry.register();
  }

  public ProtocolManager getProtocolManager() {
    return this.protocolManager;
  }
}
