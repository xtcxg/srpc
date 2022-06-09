package com.miex.test;

import com.miex.config.ApplicationConfig;
import org.apache.log4j.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyTest {
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


    public PropertyTest() {
        prop = collectProperties();
    }

    /**
     * 处理配置信息
     * @return
     */
    private Map<String,String> collectProperties() {
        Map<String,String> prop = new HashMap<>();
        prop.put("srpc.proxy.type","javassist");
        prop.put("srpc.port","3695");
        return prop;
    }

    public static void main(String[] args) throws NoSuchMethodException {
        Properties properties = new Properties();
        properties.setProperty("srpc.port","3695");

        properties.setProperty("srpc.scan.provide","com.miex.provider.service");
        properties.setProperty("srpc.scan.apply","com.miex.consumer");

        properties.setProperty("srpc.registry.type","redis");
        properties.setProperty("srpc.registry.host","127.0.0.1");
        properties.setProperty("srpc.registry.port","6379");

        ApplicationConfig config = new ApplicationConfig(properties);

    }
}
