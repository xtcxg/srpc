package com.miex.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionUtil {

    public static List<String> toList(String str) {
        if (StringUtil.isEmpty(str)) {
            return new ArrayList<>();
        }
        str = str.replace("[","").replace("]","").replaceAll(" ","");
        if (StringUtil.isEmpty(str)) {
            return new ArrayList<>();
        } else {
            return Arrays.stream(str.split(",")).collect(Collectors.toList());
        }
    }
}
