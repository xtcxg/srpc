package com.miex.util;

public class ClassUtil {

    public static String getShortName(Class<?> c) {
        char[] chars = c.getSimpleName().toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
