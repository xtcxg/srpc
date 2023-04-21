package com.miex.annotation;

import com.miex.cache.PropertiesCache;
import com.miex.config.ApplicationConfig;
import com.miex.exception.SrpcException;
import com.miex.protocol.ProtocolManager;
import com.miex.util.ClassUtil;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.*;
import org.springframework.core.type.AnnotationMetadata;

import java.util.*;

public class SrpcComponentScanRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

  ApplicationConfig applicationConfig;

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
      BeanDefinitionRegistry registry) {

    setBasePath(importingClassMetadata);

    applicationConfig = new ApplicationConfig();

    ProtocolManager protocolManager = ProtocolManager.getInstance();

    protocolManager.createAllApply();
    Map<String, Object> allApply = protocolManager.getAllApply();
    registryInvokers(registry, allApply);

//        Map<Class<?>, Object> allProvide = protocolManager.getAllProvide();
//        registryProvide(registry, allProvide);

    registryRootBean(registry, ProtocolBeanPostProcessor.class);
  }

  /**
   * 获取扫描路径
   */
  private void setBasePath(AnnotationMetadata metadata) {
    AnnotationAttributes attributes = AnnotationAttributes.fromMap(
        metadata.getAnnotationAttributes(SrpcComponentScan.class.getName()));
    String[] basePackages = attributes.getStringArray("basePackages");
    String[] providePackages = attributes.getStringArray("providePackages");
    String[] applyPackages = attributes.getStringArray("applyPackages");

    PropertiesCache propertiesCache = PropertiesCache.getInstance();
    propertiesCache.put("srpc.scan", Arrays.toString(basePackages));
    propertiesCache.put("srpc.scan.provide", Arrays.toString(providePackages));
    propertiesCache.put("srpc.scan.apply", Arrays.toString(applyPackages));
  }

  private void registryRootBean(BeanDefinitionRegistry registry, Class<?> c) {
    String beanName = ClassUtil.getShortName(c);
    BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(c);
    AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
    registry.registerBeanDefinition(beanName, beanDefinition);
  }

  private void registryRootBeans(BeanDefinitionRegistry registry, Map<String, Class<?>> classMap) {
    for (Map.Entry<String, Class<?>> entry : classMap.entrySet()) {
      Class<?> c = entry.getValue();
      BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(c);
      registry.registerBeanDefinition(ClassUtil.getShortName(c), builder.getBeanDefinition());
    }
  }

  private void registryInvokers(BeanDefinitionRegistry registry, Map<String, Object> invokers) {
    for (Map.Entry<String, Object> entry : invokers.entrySet()) {
      Class<?> c = entry.getValue().getClass();
      BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(c);
      AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
      beanDefinition.setBeanClass(ApplyFactoryBean.class);
      beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(entry.getValue());
      registry.registerBeanDefinition(entry.getKey(), beanDefinition);
    }
  }

  @Override
  public void setEnvironment(Environment environment) {
    ConfigurableEnvironment ce;
    if (environment instanceof ConfigurableEnvironment) {
      ce = ((ConfigurableEnvironment) environment);
    } else {
      throw new SrpcException(SrpcException.Enum.PARAM_ERROR, "can't find environment");
    }

    PropertiesCache propertiesCache = PropertiesCache.getInstance();

    Map<String, Object> prefixProperties = getPrefixProperties(ce, "srpc");
    for (Map.Entry<String, Object> entry : prefixProperties.entrySet()) {
      propertiesCache.put(entry.getKey(), (String) entry.getValue());
    }
  }

  /**
   * 获取 "srpc" 开头的配置
   *
   * @param environment
   * @param prefix
   * @return
   */
  private Map<String, Object> getPrefixProperties(ConfigurableEnvironment environment,
      String prefix) {
    AbstractEnvironment ae = new AbstractEnvironment() {
    };
    MutablePropertySources cp = environment.getPropertySources();
    MutablePropertySources propertySources = ae.getPropertySources();

    for (PropertySource<?> source : cp) {
      propertySources.addLast(source);
    }
    Map<String, Object> subProperties = new LinkedHashMap<>();

    for (PropertySource<?> source : propertySources) {
      if (source instanceof EnumerablePropertySource) {
        for (String name : ((EnumerablePropertySource<?>) source).getPropertyNames()) {
          if (!subProperties.containsKey(name) && name.startsWith(prefix)) {
            String subName = name.substring(prefix.length());
            if (!subProperties.containsKey(subName)) { // take first one
              Object value = source.getProperty(name);
              if (value instanceof String) {
                value = environment.resolvePlaceholders((String) value);
              }
              subProperties.put(name, value);
            }
          }
        }
      }
    }
    return Collections.unmodifiableMap(subProperties);
  }
}
