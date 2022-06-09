package com.miex.protocol;

import com.miex.cache.CacheManager;
import com.miex.cache.ProvideCache;
import com.miex.exception.SrpcException;
import com.miex.util.Scanner;
import com.miex.util.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExporterManager {

    private static final ProvideCache PROVIDE_CACHE = CacheManager.PROVIDE_CACHE;

    private static class SingletonHolder {
        private static final ExporterManager INSTANCE = new ExporterManager();
    }

    private ExporterManager() {}

    public static ExporterManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void createAll() {
        ConcurrentHashMap<String, String> classMap = PROVIDE_CACHE.getClassMap();
        for (Map.Entry<String,String> e:classMap.entrySet()) {
            createExporter(e.getKey(), e.getValue());
        }
    }

    public <T> Exporter<T> createExporter(Class<T> type) {
        return createExporter(null, type.getName(), type);
    }

    public <T> Exporter<T> createExporter(String className) {
        try {
            Class<T> c = (Class<T>) Class.forName(className);
            return createExporter(null, className, c);
        } catch (ClassNotFoundException e) {
            throw new SrpcException(SrpcException.Enum.RESOURCE_NOT_FOUND,"class [" + className + "] not found");
        }
    }

    private <T> Exporter<T> createExporter(String exportName, String className) {
        try {
            Class<T> c = (Class<T>) Class.forName(className);
            return createExporter(exportName, className, c);
        } catch (ClassNotFoundException e) {
            throw new SrpcException(SrpcException.Enum.RESOURCE_NOT_FOUND,"class [" + className + "] not found");
        }
    }

    public <T> Exporter<T> createExporter(String exportName, String className, Class<T> type) {
        try {
            if (StringUtil.isEmpty(exportName)) {
                exportName = Scanner.checkAndAddProvideCache(type);
            }
            Exporter<T> exporter;
            exporter =  PROVIDE_CACHE.get(className);
            if (null != exporter) {
                return exporter;
            }
            T target =  type.getDeclaredConstructor(new Class[]{}).newInstance();
            exporter = ExporterFactory.createExporterByProtocol(exportName, type, target);
            exporter.export();
            PROVIDE_CACHE.put(type.getInterfaces()[0].getName(),exporter);
            return exporter;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    static class ExporterFactory {
        private static final String PROTOCOL = CacheManager.PROTOCOL;
        public static <T> Exporter<T> createExporterByProtocol(String name, Class<T> type, T target) {
            switch (PROTOCOL) {
                default:
                    return new DefaultExporter<>(name, type, target);
            }
        }
    }
}
