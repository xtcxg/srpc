package com.miex.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ApplyCache implements Cache<String,Object> {

    /**
     * 存放引用服务的对象
     */
    private final List<String> CLASS_LIST = new ArrayList<>();

    private final ConcurrentHashMap<String,Object> CACHE = new ConcurrentHashMap<>();

    public void addClass(String className) {
        CLASS_LIST.add(className);
    }

    @Override
    public void put(String key, Object value) {
        CACHE.put(key, value);
    }

    @Override
    public Object get(String key) {
        Object value = CACHE.get(key);
        if (key == null) {
            // todo create apply
        }
        return value;
    }
}
