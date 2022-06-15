package com.miex.annotation;

import com.miex.cache.CacheManager;
import com.miex.exchange.ExchangeManager;
import com.miex.protocol.ExporterManager;
import com.miex.protocol.InvokerManager;
import com.miex.util.ClassUtil;
import com.miex.util.Scanner;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SrpcComponentScanRegistrar implements ImportBeanDefinitionRegistrar {



    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
//        ExchangeManager.getInstance();


        getProperties(importingClassMetadata);

        Scanner.scan();


        ExporterManager exporterManager = ExporterManager.getInstance();
        CacheManager.checkProperties();

        InvokerManager invokerManager = InvokerManager.getInstance();
        invokerManager.createAll();
        Map<String, Object> allInvoker = invokerManager.getAll();
        registryInvokers(registry,allInvoker);

        Map<String, String> classCache = exporterManager.getClassCache();

        registryRootBeans(registry,classCache);

        registryRootBean(registry,ExporterBeanPostProcessor.class);
        registryRootBean(registry,SrpcSpringApplicationConfig.class);

    }

    private Properties getProperties(AnnotationMetadata metadata) {
        Properties properties = new Properties();
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(SrpcComponentScan.class.getName()));
        String[] basePackages = attributes.getStringArray("basePackages");
        String[] providePackages = attributes.getStringArray("providePackages");
        String[] applyPackages = attributes.getStringArray("applyPackages");
        properties.setProperty("srpc.scan", List.of(basePackages).toString());
        properties.setProperty("srpc.scan.provide",List.of(providePackages).toString());
        properties.setProperty("srpc.scan.apply",List.of(applyPackages).toString());

//        Map<String, Object> systemProperties = environment.getSystemEnvironment();
//        MutablePropertySources propertySources = environment.getPropertySources();
//        OriginTrackedMapPropertySource originTrackedMapPropertySource = null;
//        for (PropertySource<?> propertySource : propertySources) {
//            if (propertySource instanceof OriginTrackedMapPropertySource) {
//                originTrackedMapPropertySource = (OriginTrackedMapPropertySource)propertySource;
//            }
//        }

        CacheManager.PROPERTIES_CACHE.put("srpc.scan", Arrays.toString(basePackages));
        CacheManager.PROPERTIES_CACHE.put("srpc.scan.provide", Arrays.toString(providePackages));
        CacheManager.PROPERTIES_CACHE.put("srpc.scan.apply",Arrays.toString(applyPackages));
        CacheManager.PROPERTIES_CACHE.put("srpc.registry.host","127.0.0.1");
        CacheManager.PROPERTIES_CACHE.put("srpc.registry.port","6379");
        return properties;
    }

    private void registryRootBean(BeanDefinitionRegistry registry,Class<?> c) {
        String beanName = ClassUtil.getShortName(c);
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(c);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        registry.registerBeanDefinition(beanName,beanDefinition);
    }

    private void registryRootBeans(BeanDefinitionRegistry registry,Map<String,String> classMap) {
        try {
            for(Map.Entry<String,String> entry : classMap.entrySet()) {
                Class<?> c = Class.forName(entry.getValue());
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(c);
                registry.registerBeanDefinition(entry.getKey(),builder.getBeanDefinition());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void registryInvokers(BeanDefinitionRegistry registry,Map<String,Object> invokers) {
        for(Map.Entry<String,Object> entry : invokers.entrySet()) {
            Class<?> c = entry.getValue().getClass();
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(c);
            AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
            beanDefinition.setBeanClass(ApplyFactoryBean.class);
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(entry.getValue());
            registry.registerBeanDefinition(entry.getKey(),beanDefinition);
        }
    }

    private static Environment getOrCreateEnvironment(BeanDefinitionRegistry registry) {
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        if (registry instanceof EnvironmentCapable) {
            return ((EnvironmentCapable) registry).getEnvironment();
        }
        return new StandardEnvironment();
    }


}
