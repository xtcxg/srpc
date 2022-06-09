package com.miex.exchange.http;

import com.miex.exchange.AbstractServer;
import com.miex.protocol.ExporterManager;

import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpServer extends AbstractServer {

    com.sun.net.httpserver.HttpServer server;

    private static class SingletonHolder {
        private static final HttpServer INSTANCE = new HttpServer();
    }

    public static HttpServer getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private HttpServer() {}

    @Override
    public void start() {
        try {
            int port = Integer.parseInt(PORT);
            server  = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(port),0);
            server.createContext("/", new DispatchHandler());
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
