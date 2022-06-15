package com.miex.protocol;

import com.miex.cache.CacheManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InvokerManager {

    private static final ConcurrentHashMap<String,Object> INVOKER_MAP = new ConcurrentHashMap<>();

    private static class SingletonHolder {
        private static final InvokerManager INSTANCE = new InvokerManager();
    }

    private InvokerManager() {}

    public static InvokerManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public <T> T get(Class<T> c) {
        Object target =  INVOKER_MAP.get(c.getName());
        if (null == target) {
            return create(c);
        } else {
            return (T) target;
        }
    }

    public Map<String,Object> getAll() {
        return INVOKER_MAP;
    }

    public void createAll() {
        List<String> classList = CacheManager.APPLY_CACHE.getClassList();
        for (String className : classList) {
            create(className);
        }
    }

    private void create(String className) {
        try {
            Class<?> c = Class.forName(className);
            create(c);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public <T> T create(Class<T> type) {
        return create(type.getName(),type);
    }

    public <T> T create(String className, Class<T> type) {
        Invoker<T> invoker = ApplyFactory.createApplyByProtocol(type);
        T target = ApplyProxyFactory.proxy(invoker, type);
        INVOKER_MAP.put(className,target);
        return target;
    }

    static class ApplyFactory {
        private static final String PROTOCOL = CacheManager.PROTOCOL;
        public static <T> Invoker<T> createApplyByProtocol(Class<T> type) {
            switch (PROTOCOL) {
                default:
                    return new DefaultInvoker<>(type);
            }
        }
    }

}
