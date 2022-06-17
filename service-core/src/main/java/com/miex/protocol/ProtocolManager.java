package com.miex.protocol;

import com.miex.exception.SrpcException;
import com.miex.util.Scanner;
import com.miex.util.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ProtocolManager {
    /* 存储需要创建 invoker 的接口名 */
    private static final List<String> APPLY_CLASS = new ArrayList<>();
    /* 存储使用 @Apply 的类 */
    private static final Set<String> APPLY_CACHE = new HashSet<>();
    /* className : proxyObject */
    private static final ConcurrentHashMap<String,Object> APPLY_MAP = new ConcurrentHashMap<>();
    /* className : exporterObject */
    private static final ConcurrentHashMap<String,Exporter<?>> EXPORTER_MAP = new ConcurrentHashMap<>();
    /* 存储使用 @Provide 的类, exporterName : className */
    private static final ConcurrentHashMap<String,String> PROVIDER_CLASS = new ConcurrentHashMap<>();

    private static final ProtocolManager protocolManager = new ProtocolManager();

    private ProtocolManager() {}

    public static ProtocolManager getInstance() {
        return protocolManager;
    }

    public void addApply(String className) {
        APPLY_CLASS.add(className);
    }


    public void addApplyCache(String className) {
        APPLY_CACHE.add(className);
    }

    public Set<String> getAllApplyCache() {
        return APPLY_CACHE;
    }

    /**
     * 获取代理服务
     */
    public <T> T getApply(Class<T> c) {
        Object target =  APPLY_MAP.get(c.getName());
        if (null == target) {
            return createApply(c);
        } else {
            return (T) target;
        }
    }

    /**
     * 获取全部代理服务
     */
    public Map<String,Object> getAllApply() {
        return APPLY_MAP;
    }

    public void createAllApply() {
        for (String className : APPLY_CLASS) {
            createApply(className);
        }
    }

    private void createApply(String className) {
        try {
            Class<?> c = Class.forName(className);
            createApply(c);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public <T> T createApply(Class<T> type) {
        return createApply(type.getName(),type);
    }

    public <T> T createApply(String className, Class<T> type) {
        Invoker<T> invoker = new DefaultInvoker<>(type);
        T target = ApplyProxyFactory.proxy(invoker, type);
        APPLY_MAP.put(className,target);
        return target;
    }

    public void createAllExporter() {
        for (Map.Entry<String,String> entry : PROVIDER_CLASS.entrySet()) {
            getExporter(entry.getKey(), entry.getValue());
        }
    }


    public <T> Exporter<T> getExporter(Class<T> type) {
        return getExporter(null, type.getName(), type);
    }

    public <T> Exporter<T> getExporter(String className) {
        try {
            Class<T> c = (Class<T>) Class.forName(className);
            return getExporter(null, className, c);
        } catch (ClassNotFoundException e) {
            throw new SrpcException(SrpcException.Enum.EXPORTER_ERROR,"class [" + className + "] not found");
        }
    }

    private <T> Exporter<T> getExporter(String exportName, String className) {
        try {
            Class<T> c = (Class<T>) Class.forName(className);
            return getExporter(exportName, className, c);
        } catch (ClassNotFoundException e) {
            throw new SrpcException(SrpcException.Enum.EXPORTER_ERROR,"exporter class [" + className + "] not found");
        }
    }

    public <T> Exporter<T> getExporter(String exportName, String className, Class<T> type) {
        try {
            if (StringUtil.isEmpty(exportName)) {
                exportName = Scanner.checkAndAddProvideCache(type);
            }
            Exporter<T> exporter;
            exporter = (Exporter<T>) EXPORTER_MAP.get(className);
            if (null != exporter) {
                return exporter;
            }
            T target =  type.getDeclaredConstructor(new Class[]{}).newInstance();
            // todo 处理协议
            exporter = new DefaultExporter<>(exportName, type, target);
            exporter.export();
            EXPORTER_MAP.put(type.getInterfaces()[0].getName(),exporter);
            return exporter;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new SrpcException(SrpcException.Enum.EXPORTER_ERROR,"can't create exporter,class [" + type.getName() + "]");
        }
    }

    public <T> Exporter<T> getExporter(String exportName,T target) {
        Exporter<T> exporter;
        Class<T> type = (Class<T>) target.getClass().getInterfaces()[0];
        exporter = (Exporter<T>) EXPORTER_MAP.get(type.getName());
        if (null != exporter) {
            return exporter;
        }
        // todo 处理协议
        exporter = new DefaultExporter<>(exportName, type, target);
        EXPORTER_MAP.put(type.getName(), exporter);
        return exporter;
    }

    public void addProviderClass(String providerName, String className) {
        PROVIDER_CLASS.put(providerName,className);
    }

    public void addProviderClass(String providerName,Class<?> c) {
        addProviderClass(providerName,c.getName());
    }

    public void addProviderClass(Class<?> c) {
        // todo
    }

    public Map<String,String> getProvideClasses() {
        return PROVIDER_CLASS;
    }

    public List<String> getLocalKeys() {
        return new ArrayList<>(EXPORTER_MAP.keySet());
    }
}
