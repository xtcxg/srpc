package com.miex.annotation;

import com.miex.protocol.ProtocolManager;
import com.miex.util.StringUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class ProtocolBeanPostProcessor implements BeanPostProcessor {
    private final Map<String, Class<?>> classCache = ProtocolManager.getInstance().getProvideClasses();
    private final Set<String> applyCache = ProtocolManager.getInstance().getAllApplyCache();
    private final Map<String, Object> applyMap = ProtocolManager.getInstance().getAllApply();
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> c = classCache.get(beanName);
        if (null != c) {
            // todo
//            ProtocolManager.getInstance().getExporter(beanName, bean.getClass());
        }
        String className = bean.getClass().getName();
        if (applyCache.contains(className)) {
            setValue(bean);
        }
        return bean;
    }

    private void setValue(Object bean) {
        Field[] fields = bean.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                if (null != field.getAnnotation(Apply.class)) {
                    Class<?> type = field.getType();
                    Object apply = applyMap.get(type.getName());
                    field.setAccessible(true);
                    field.set(bean,apply);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
