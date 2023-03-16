package com.miex.registry;

import com.miex.cache.PropertiesCache;
import com.miex.config.RegistryConfig;
import com.miex.util.ClassUtil;

public class RegistryManager {
    private static final PropertiesCache propertiesCache = PropertiesCache.getInstance();
    private static final RegistryConfig registryConfig;
    private static Registry registry;

    static {
        registryConfig = ClassUtil.buildFromProperties("srpc.registry.",
            RegistryConfig.class, propertiesCache.getProperties());
    }

    public static synchronized RegistryConfig getRegistryConfig() {
        return registryConfig;
    }

    public static synchronized Registry getRegistry() {
        if (registry == null) {
            registry = buildRegistry();
            registry.connect(registryConfig.getHost(), registryConfig.getPort(), registryConfig.getName(),
                registryConfig.getPassword());
        }
        return registry;
    }

    private static Registry buildRegistry() {
        String className = propertiesCache.get("srpc.mapping.registry." + registryConfig.getType());
        return ClassUtil.createObject(className);
    }
}
