package com.miex.exchange.http;

import com.miex.exchange.AbstractServer;

import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpServer extends AbstractServer {

    com.sun.net.httpserver.HttpServer server;

    @Override
    public void start() {
        try {
            server  = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(SERVER_CONFIG.getPort()),0);
            server.createContext("/", new DispatchHandler());
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
