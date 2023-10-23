package com.miex.registry;

import com.miex.cache.PropertiesCache;
import com.miex.config.RegistryConfig;
import com.miex.exchange.ExchangeManager;
import com.miex.util.ClassUtil;

public class RegistryManager {
    private static final PropertiesCache propertiesCache = PropertiesCache.getInstance();
    private static final RegistryConfig registryConfig;
    private static final RegistryManager registryManager = new RegistryManager();;
    private static Registry registry;

    static {
        registryConfig = ClassUtil.buildFromProperties("srpc.registry.",
            RegistryConfig.class, propertiesCache.getProperties());
        String className = propertiesCache.get("srpc.mapping.registry." + registryConfig.getType());
        try {
            Class<? extends Registry> rc = (Class<? extends Registry>) Class.forName(className);
            registryConfig.setRegistry(rc);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private RegistryManager() {
    }

    public static RegistryManager getInstance() {
        return registryManager;
    }

    public static synchronized RegistryConfig getRegistryConfig() {
        return registryConfig;
    }

    public static synchronized Registry getRegistry() {
        if (registry == null) {
            registry = ClassUtil.createObject(registryConfig.getRegistry());
            registry.connect(
                registryConfig.getHost(),
                registryConfig.getPort(),
                registryConfig.getName(),
                registryConfig.getPassword());
        }
        return registry;
    }

    /**
     * 注册服务到注册中心，并同步数据
     */
    public void register() {
        getRegistry().register();
        ExchangeManager.getInstance().syncServer(registry.getServices());
    }
}
