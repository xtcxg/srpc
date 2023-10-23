package com.miex.config;

import com.miex.exchange.Client;
import com.miex.exchange.Exchange;

public class ExchangeConfig {

    /**
     * 服务开启端口
     */
    public Integer port;

    /**
     * 本地服务器ip
     */
    public String host;

    private String address;

    /**
     * 使用的协议
     */
    public String protocol;

    private Class<? extends Exchange> exchange;

    private Class<? extends Client> client;

    public Class<? extends Exchange> getExchange() {
        return exchange;
    }

    public Class<? extends Client> getClient() {
        return client;
    }

    public void setExchange(Class<? extends Exchange> exchange) {
        this.exchange = exchange;
    }

    public void setClient(Class<? extends Client> client) {
        this.client = client;
    }

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
