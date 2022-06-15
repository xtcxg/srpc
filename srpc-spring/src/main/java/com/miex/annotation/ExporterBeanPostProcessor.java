package com.miex.annotation;

import com.miex.protocol.ExporterManager;
import com.miex.util.StringUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Map;

public class ExporterBeanPostProcessor implements BeanPostProcessor {
    Map<String, String> classCache = ExporterManager.getInstance().getClassCache();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        String className = classCache.get(beanName);
        if (!StringUtil.isEmpty(className)) {
            ExporterManager.getInstance().createExporter(beanName, bean);
        }
        return bean;
    }
}
