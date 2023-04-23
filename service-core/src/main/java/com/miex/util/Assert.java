package com.miex.util;

import com.miex.exception.SrpcException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author liutz
 * @since 2022/3/8
 */
public class Assert {
	private final static Log log = LogFactory.getLog(Assert.class);

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

	public static void EmptyString(String s) {
		if (null == s || "".equals(s)) {
			throw new SrpcException(SrpcException.Enum.PARAM_ERROR);
		}
	}

	public static void EmptyString(String s, String info) {
		if (null == s || "".equals(s)) {
			throw new SrpcException(SrpcException.Enum.PARAM_ERROR,info);
		}
	}
}
