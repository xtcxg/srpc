package com.miex.config;

public class RegistryConfig {
    Integer port;

    String host;

    String name;

    String password;

    String key;

    /**
     * 注册中心类型
     */
    String type;

    /**
     * 存储 [{service:hosts}]
     */
    String serverIndexName;

    /**
     * 存储 [{host:services}]
     */
    String hostIndexName;

    String lock;
    /**
     * 服务注册过期时间/s
     */
    Long ttl;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTtl() {
        return ttl;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }

    public String getServerIndexName() {
        return serverIndexName;
    }

    public void setServerIndexName(String serverIndexName) {
        this.serverIndexName = serverIndexName;
    }

    public String getHostIndexName() {
        return hostIndexName;
    }

    public void setHostIndexName(String hostIndexName) {
        this.hostIndexName = hostIndexName;
    }

    public String getLock() {
        return lock;
    }

    public void setLock(String lock) {
        this.lock = lock;
    }
}
