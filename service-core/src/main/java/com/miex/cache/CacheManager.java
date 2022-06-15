package com.miex.cache;

import com.miex.util.Assert;

public class CacheManager {

    public static String PROTOCOL;

    public static String REGISTER_TYPE;

    public static final ProvideCache PROVIDE_CACHE = new ProvideCache();

    public static final ApplyCache APPLY_CACHE = new ApplyCache();

    public static final PropertiesCache PROPERTIES_CACHE = new PropertiesCache();

    public static void checkProperties() {
        Assert.EmptyString(PROPERTIES_CACHE.get("srpc.registry.type"),"property [srpc.registry.type] not found");
        REGISTER_TYPE = PROPERTIES_CACHE.get("srpc.registry.type");
        Assert.EmptyString(PROPERTIES_CACHE.get("srpc.protocol"),"property [srpc.protocol] not found");
        PROTOCOL = PROPERTIES_CACHE.get("srpc.protocol");
    }

}
