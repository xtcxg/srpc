package com.miex.config;

import com.miex.cache.ProvideCache;
import com.miex.cache.CacheManager;
import com.miex.cache.PropertiesCache;
import com.miex.exchange.ExchangeManager;
import com.miex.exchange.Server;
import com.miex.protocol.ExporterManager;
import com.miex.protocol.InvokerManager;
import com.miex.registry.Registry;
import com.miex.registry.redis.RegistryManager;
import com.miex.util.Scanner;

import java.util.Enumeration;
import java.util.Properties;

public class ApplicationConfig {

    private final PropertiesCache PROPERTIES_CACHE = CacheManager.PROPERTIES_CACHE;
    private final ProvideCache PROVIDE_CACHE = CacheManager.PROVIDE_CACHE;

    private Registry registry;
    private final ExporterManager exporterManager;
    private final InvokerManager invokerManager;
    private final Server server;

    public ApplicationConfig(Properties properties) {
        setProperties(properties);

        this.exporterManager = ExporterManager.getInstance();

        this.invokerManager = InvokerManager.getInstance();

        this.server = ExchangeManager.createService();

        this.registry = RegistryManager.createRegistry();

        init();
    }

    private void setProperties(Properties properties) {
        Enumeration<?> enumeration = properties.propertyNames();
        String key;
        while (enumeration.hasMoreElements()) {
            key = enumeration.nextElement().toString();
            PROPERTIES_CACHE.put(key,properties.getProperty(key));
        }
        CacheManager.checkProperties();

    }

    public void init() {
        // 扫描
        Scanner.scan();

        // 创建本地服务
        this.exporterManager.createAll();

        // 开启服务器
        this.server.start();

        // 注册服务
        this.registry.register();

        // 创建引入的服务
        this.invokerManager.createAll();

    }

    public Object getService(String name) {
        return PROVIDE_CACHE.get(name);
    }

    public InvokerManager getInvokerManager() {
        return this.invokerManager;
    }
}
