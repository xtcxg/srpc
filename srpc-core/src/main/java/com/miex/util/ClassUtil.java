package com.miex.util;

import com.miex.exception.SrpcException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ClassUtil {

    public static String getShortName(Class<?> c) {
        char[] chars = c.getSimpleName().toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    /**
     * 根据配置创建配置对象
     */
    public static <T> T buildFromProperties(String prefix, Class<T> c, Map<String,String> prop) {
        try {
            T target = c.getConstructor(new Class[]{}).newInstance();
            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                Class<?> type = field.getType();
                String value = prop.get(prefix + field.getName());
                if (null == value) {
                    continue;
                }
                field.setAccessible(true);
                if (type == String.class) {
                    field.set(target, prop.get(prefix + field.getName()));
                } else if (type == Integer.class) {
                    field.set(target, Integer.parseInt(value));
                } else if (type == Long.class) {
                    field.set(target,Long.parseLong(value));
                }
            }
            return target;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        throw new SrpcException(SrpcException.Enum.SYSTEM_ERROR,"create config fail:" + c.getName());
    }

    public static  <T> T createObject(String className) {
        try {
            Class<T> c = (Class<T>) Class.forName(className);
            return createObject(c);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SrpcException(SrpcException.Enum.SYSTEM_ERROR,"get class type error,class name [" + className + "]");
        }
    }

    /**
     * 创造无参对象
     * @param c
     * @param <T>
     * @return
     */
    public static  <T> T createObject(Class<T> c) {
        return createObject(c,new Class[]{},new Object[]{});
    }

    public static  <T> T createObject(String className,Class<?>[] parameterTypes,Object[] args) {
        try {
            Class<T> c = (Class<T>) Class.forName(className);
            return createObject(c,parameterTypes,args);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SrpcException(SrpcException.Enum.SYSTEM_ERROR,"get class type error,class name [" + className + "]");
        }
    }

    public static  <T> T createObject(Class<T> c,Class<?>[] parameterTypes,Object[] args) {
        try {
            return c.getConstructor(parameterTypes).newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new SrpcException(SrpcException.Enum.SYSTEM_ERROR,"create object error,class [" + c.getName() + "]");
        }
    }
}
