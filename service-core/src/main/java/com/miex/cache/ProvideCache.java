package com.miex.cache;

import com.miex.protocol.Exporter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ProvideCache implements Cache<String, Exporter> {

    private final ConcurrentHashMap<String,String> CLASS_MAP = new ConcurrentHashMap<>();

    /**
     * key: class name
     * value: Exporter
     */
    private final ConcurrentHashMap<String, Exporter> CACHE = new ConcurrentHashMap<>();

    public void addClass(String name, String className) {
        CLASS_MAP.put(name, className);
    }

    public ConcurrentHashMap<String,String> getClassMap() {
        return CLASS_MAP;
    }

    @Override
    public void put(String key, Exporter exporter) {
        CACHE.put(key, exporter);
    }

    @Override
    public Exporter get(String key) {
        return CACHE.get(key);
    }

    public List<String> keys() {
        List<String> keys = new ArrayList<>();
        Enumeration<String> ration = CACHE.keys();
        while (ration.hasMoreElements()) {
            keys.add(ration.nextElement());
        }
        return keys;
    }
}
