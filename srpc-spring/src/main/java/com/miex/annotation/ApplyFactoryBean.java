package com.miex.annotation;

import org.springframework.beans.factory.FactoryBean;

public class ApplyFactoryBean<T> implements FactoryBean<T> {
    T target;

    public ApplyFactoryBean(T target) {
        this.target = target;
    }

    @Override
    public T getObject() throws Exception {
        return this.target;
    }

    @Override
    public Class<?> getObjectType() {
        return this.target.getClass();
    }
}
