package com.miex.exchange.http;

import com.miex.exception.SrpcException;
import com.miex.exception.SrpcException.Enum;
import com.miex.exchange.AbstractExchange;

import com.miex.exchange.Client;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

public class HttpExchange extends AbstractExchange {

    com.sun.net.httpserver.HttpServer server;

    @Override
    public void init() {
        try {
            server  = com.sun.net.httpserver.HttpServer
                .create(new InetSocketAddress(config.getPort()),0);
            server.createContext("/", new DispatchHandler());
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Client getClient(String address) {
        try {
            String[] arr = address.split(":");
            URI uri = new URI("http",null,arr[0],Integer.parseInt(arr[1]),
                "/",null,null);
            return new HttpClient(uri);
        } catch (URISyntaxException e) {
            throw new SrpcException(Enum.CLIENT_ERROR, "create client error, host:" + address, e);
        }
    }
}
