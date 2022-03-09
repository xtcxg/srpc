package com.miex;

import com.miex.exception.SrpcException;
import com.miex.service.SrpcNettyService;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liutz
 * @since 2022/3/8
 */
public class ServiceStarter {
	static SrpcNettyService service;
	static Map<String,String> prop;

	static {
		RollingFileAppender appender = new RollingFileAppender();
		appender.setFile("/Users/miex/logs/srpc.log");
		appender.setMaxBackupIndex(3);
		appender.setMaxFileSize("5MB");
		appender.setAppend(true);
		appender.setLayout(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN));
		appender.activateOptions();
		Logger rootLogger = Logger.getRootLogger();
		rootLogger.setLevel(Level.DEBUG);
		rootLogger.addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
	}


	public ServiceStarter() {
		prop = collectProperties();
		service = new SrpcNettyService(prop);
	}

	/**
	 * 处理配置信息
	 * @return
	 */
	private Map<String,String> collectProperties() {
		Map<String,String> prop = new HashMap<>();
		prop.put("parent_group","4");
		prop.put("child_group","20");
		prop.put("port","3695");
		return prop;
	}

	public static void main(String[] args) {
//		ServiceStarter start = new ServiceStarter();
		try {
			throw new SrpcException();
		} catch (SrpcException e) {

			System.out.println(e.getCode());
		}
	}
}
