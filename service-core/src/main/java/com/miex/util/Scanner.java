package com.miex.util;

import com.miex.annotation.Apply;
import com.miex.annotation.Provide;
import com.miex.cache.PropertiesCache;
import com.miex.exception.SrpcException;
import com.miex.protocol.ProtocolManager;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class Scanner {

    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    private static final PathMatchingResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();

    private static final ProtocolManager PROTOCOL_MANAGER = ProtocolManager.getInstance();
    private static final PropertiesCache PROPERTIES_CACHE = PropertiesCache.getInstance();

    public static void scan() {
        String providePath = PROPERTIES_CACHE.get("srpc.scan.provide");
        String applyPath = PROPERTIES_CACHE.get("srpc.scan.apply");
        if (StringUtil.isEmpty(providePath) || "[]".equals(providePath)) {
            providePath = PROPERTIES_CACHE.get("srpc.scan");
        }
        if (StringUtil.isEmpty(applyPath) || "[]".equals(applyPath)) {
            applyPath = PROPERTIES_CACHE.get("srpc.scan");
        }

        if (providePath.equals(applyPath)) {
            scanAll(providePath);
        } else {
            scanProvide(providePath);
            scanApply(applyPath);
        }
    }

    public static void scanAll(String path) {
        try {
            List<String> paths = resolveBasePackage(path);
            for (String p : paths) {
                Resource[] resources = RESOURCE_PATTERN_RESOLVER.getResources(p);
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        MetadataReader meta =  new CachingMetadataReaderFactory().getMetadataReader(resource);
                        String className = meta.getClassMetadata().getClassName();
                        Class<?> c = Class.forName(className);
                        // 如果是 Provide 加入 ProvideCache
                        checkAndAddProvideCache(c);
                        // 如果是 Apply 加入 ApplyCache
                        checkAndAddApplyCache(c);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void scanApply(String path) {
        try {
            List<String> paths = resolveBasePackage(path);
            for (String p : paths) {
                Resource[] resources = RESOURCE_PATTERN_RESOLVER.getResources(p);
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        MetadataReader meta =  new CachingMetadataReaderFactory().getMetadataReader(resource);
                        String className = meta.getClassMetadata().getClassName();
                        Class<?> c = Class.forName(className);
                        // 如果是 Apply 加入 ApplyCache
                        checkAndAddApplyCache(c);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void scanProvide(String path) {
        try {
            List<String> paths = resolveBasePackage(path);
            for (String p : paths) {
                Resource[] resources = RESOURCE_PATTERN_RESOLVER.getResources(p);
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        MetadataReader meta =  new CachingMetadataReaderFactory().getMetadataReader(resource);
                        String className = meta.getClassMetadata().getClassName();
                        Class<?> c = Class.forName(className);
                        // 如果是 Provide 加入 ProvideCache
                        checkAndAddProvideCache(c);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将配置路径转为扫描路径
     * @param path
     * @return
     */
    private static List<String> resolveBasePackage(String path) {
        List<String> paths = CollectionUtil.toList(path);
        if (CollectionUtil.isEmpty(paths)) {
            throw new SrpcException(SrpcException.Enum.PARAM_ERROR);
        }
        return paths.stream().map( p -> {
            p = p.replace(".", "/");
            return ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + p + '/' + DEFAULT_RESOURCE_PATTERN;
        }).collect(Collectors.toList());
    }

    /**
     * 如果对象是Provide，将对象信息加入ProvideCache
     * @param c 扫描对象
     */
    public static String checkAndAddProvideCache(Class<?> c) {
        if (c.isInterface() || c.getAnnotation(Provide.class) == null) {
            return null;
        }
        Provide annotation = c.getAnnotation(Provide.class);
        String name = annotation.value();
        if (StringUtil.isEmpty(name)) {
            name = annotation.name();
        }
        if (StringUtil.isEmpty(name)) {
            Class<?>[] interfaces = c.getInterfaces();
            if (0 == interfaces.length) {
                name = c.getSimpleName();
            } else {
                name = interfaces[0].getSimpleName();
            }
            name = StringUtil.lowerFirstCase(name);
        }
        PROTOCOL_MANAGER.addProviderClass(name, c.getName());
        return name;
    }

    /**
     * 如果对象中引用了服务，将引用的服务信息加入ApplyCache
     * @param c 扫描对象
     */
    private static void checkAndAddApplyCache(Class<?> c) {
        if (c.isInterface()) {
            return;
        }
        boolean mark = false;
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(Apply.class) != null) {
                PROTOCOL_MANAGER.addApply(field.getType().getName());
                mark = true;
            }
        }
        if (mark) {
            PROTOCOL_MANAGER.addApplyCache(c.getName());
        }
    }
}
