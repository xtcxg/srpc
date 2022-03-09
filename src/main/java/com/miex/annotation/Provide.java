package com.miex.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author liutz
 * @since 2022/3/8
 * 提供服务
 */
@Target({ElementType.TYPE})
public @interface Provide {
	String value();

	String name();
}
