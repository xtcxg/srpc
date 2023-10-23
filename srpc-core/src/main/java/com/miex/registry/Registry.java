package com.miex.registry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册中心
 */
public interface Registry {

    /**
     * 连接注册中心
     *
     * @param host
     * @param port
     * @param name
     * @param password
     */
    void connect(String host, Integer port, String name, String password);

    /**
     * 注册本地服务
     */
    void register();

    /**
     * 同步注册中心最新的数据
     */
    void refresh();

    /**
     * 清除提供的服务
     */
    void clean();

    /**
     * 获取注册中心中的接口数据,并刷新本地数据
     *
     * @return 注册中心的数据
     */
    Map<String, List<String>> pull(String[] names);

    /**
     * 获取本地接口数据
     * @return
     */
    ConcurrentHashMap<String, List<String>> getServices();

    /**
     * 注销
     */
    void destroy();

    /**
     * 获取服务器地址
     *
     * @param className
     * @return
     */
    List<String> getHosts(String className);


}
