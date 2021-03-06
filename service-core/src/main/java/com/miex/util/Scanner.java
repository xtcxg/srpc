package com.miex.util;

import com.miex.annotation.Apply;
import com.miex.annotation.Provide;
import com.miex.cache.ApplyCache;
import com.miex.cache.CacheManager;
import com.miex.cache.PropertiesCache;
import com.miex.cache.ProvideCache;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Scanner {

    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    private static final PathMatchingResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();

    private static PropertiesCache PROPERTIES_CACHE = CacheManager.PROPERTIES_CACHE;
    private static ApplyCache APPLY_CACHE = CacheManager.APPLY_CACHE;
    private static ProvideCache PROVIDE_CACHE = CacheManager.PROVIDE_CACHE;

    public static void scan() {
        String providePath = PROPERTIES_CACHE.get("srpc.scan.provide");
        String applyPath = PROPERTIES_CACHE.get("srpc.scan.apply");
        if (StringUtil.isEmpty(providePath)) {
            providePath = PROPERTIES_CACHE.get("srpc.scan");
        }
        Assert.EmptyString(providePath);
        if (StringUtil.isEmpty(applyPath)) {
            applyPath = PROPERTIES_CACHE.get("srpc.scan");
        }
        Assert.EmptyString(applyPath);

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
                        // ????????? Provide ?????? ProvideCache
                        checkAndAddProvideCache(c);
                        // ????????? Apply ?????? ApplyCache
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
                        // ????????? Apply ?????? ApplyCache
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
                        // ????????? Provide ?????? ProvideCache
                        checkAndAddProvideCache(c);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * ?????????????????????????????????
     * @param path
     * @return
     */
    private static List<String> resolveBasePackage(String path) {
        List<String> paths = new ArrayList<>();
        if (path.startsWith("{")) {
            paths = Arrays.asList(path.substring(1,path.length()-1).split(","));
        } else {
            paths.add(path);
        }

        return paths.stream().map( p -> {
            p = p.replace(".", "/");
            return ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + p + '/' + DEFAULT_RESOURCE_PATTERN;
        }).collect(Collectors.toList());
    }

    /**
     * ???????????????Provide????????????????????????ProvideCache
     * @param c ????????????
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
        PROVIDE_CACHE.addClass(name, c.getName());
        return name;
    }

    /**
     * ???????????????????????????????????????????????????????????????ApplyCache
     * @param c ????????????
     */
    private static void checkAndAddApplyCache(Class<?> c) {
        if (c.isInterface()) {
            return;
        }
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(Apply.class) != null) {
                APPLY_CACHE.addClass(field.getType().getName());
            }
        }
    }
}
