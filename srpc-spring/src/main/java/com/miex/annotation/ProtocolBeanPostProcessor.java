package com.miex.annotation;

import com.miex.protocol.Exporter;
import com.miex.protocol.ProtocolManager;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class ProtocolBeanPostProcessor implements BeanPostProcessor {
    ProtocolManager protocolManager = ProtocolManager.getInstance();
    private final Set<String> applyCache = protocolManager.getAllApplyCache();
    private final Map<Class<?>, Object> applyMap = protocolManager.getAllApply();
    private final ConcurrentHashMap<Class<?>, Class<?>> providerMap = protocolManager.getProvideMap();


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> bc = bean.getClass();
//        providerMap.forEach((i, c) -> {
//            if (c.equals(bc)) {
//                setEv(i, bean);
//            }
//        });
        if (applyCache.contains(bc.getName())) {
            setValue(bean);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> bc = bean.getClass();
        providerMap.forEach((i, c) -> {
            if (c.equals(bc)) {
                setEv(i, bean);
            }
        });
        return bean;
    }

    private void setEv(Class<?> i, Object bean) {
        Exporter<?> exporter = protocolManager.getExporter(i);
        exporter.setTarget(bean);
        exporter.export();
    }

    private void setValue(Object bean) {
        Field[] fields = bean.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                if (null != field.getAnnotation(Apply.class)) {
                    Class<?> type = field.getType();
                    Object apply = applyMap.get(type);
                    field.setAccessible(true);
                    field.set(bean,apply);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
