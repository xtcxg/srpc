package com.miex.util;

public class StringUtil {

    public static boolean isEmpty(String s) {
        return null == s ||"".equals(s);
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String lowerFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0] +=32;
        return String.valueOf(chars);
    }
}
