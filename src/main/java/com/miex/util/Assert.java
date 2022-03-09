package com.miex.util;

import com.miex.exception.SrpcException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liutz
 * @since 2022/3/8
 */
@Slf4j
public class Assert {

	public static void NotNull(Object obj,String info) {
		if (null == obj) {

			log.error(info);
			throw new SrpcException(SrpcException.Enum.PARAM_ERROR,info);
		}
	}

	public static void NotNull(Object obj) {
		if (null == obj) {
			throw new SrpcException(SrpcException.Enum.PARAM_ERROR);
		}
	}
}
