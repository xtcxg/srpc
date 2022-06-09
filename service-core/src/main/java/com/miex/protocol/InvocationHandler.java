package com.miex.protocol;

import java.io.Serializable;

public class InvocationHandler implements Serializable {

    String name;

    String className;

    Object target;

    String methodName;

    Object[] params;

    Class<?>[] parameterTypes;

    public InvocationHandler() {}

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Class<?>[] getParameterTypes() {
        return this.parameterTypes;
    }



}
