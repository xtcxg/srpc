package com.miex.registry;

import com.miex.cache.PropertiesCache;
import com.miex.config.RegistryConfig;
import com.miex.util.ClassUtil;

public class RegistryManager {
    private static final PropertiesCache PROPERTIES_CACHE = PropertiesCache.getInstance();

    private static final String PREFIX = "srpc.registry.";

    private static final RegistryConfig REGISTRY_CONFIG;

    private static Registry registry;

    static {
        REGISTRY_CONFIG = ClassUtil.buildFromProperties(PREFIX,RegistryConfig.class,PROPERTIES_CACHE.getProperties());
    }

    public static synchronized RegistryConfig getRegistryConfig() {
        return REGISTRY_CONFIG;
    }

    public static synchronized Registry getRegistry() {
        if (registry == null) {
            registry = buildRegistry();
            registry.connect(REGISTRY_CONFIG.getHost(),REGISTRY_CONFIG.getPort(),REGISTRY_CONFIG.getName(),REGISTRY_CONFIG.getPassword());
        }
        return registry;
    }

    private static Registry buildRegistry() {
        String className = PROPERTIES_CACHE.get("srpc.mapping.registry." + REGISTRY_CONFIG.getType());
        return ClassUtil.createObject(className);
    }
}
