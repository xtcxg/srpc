package com.miex.config;

public class ServerConfig {

    /**
     * 服务开启端口
     */
    public Integer port;

    /**
     * 本地服务器ip
     */
    public String host;

    /**
     * 使用的协议
     */
    public String protocol;

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

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
