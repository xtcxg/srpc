package com.miex.exchange.http;

import com.miex.exception.SrpcException;
import com.miex.exception.SrpcException.Enum;
import com.miex.exchange.AbstractExchange;
import com.miex.exchange.Client;
import com.miex.protocol.Exporter;
import com.miex.protocol.InvocationHandler;
import com.miex.protocol.Result;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpJsonExchange extends AbstractExchange {

  HttpServer server;
  @Override
  public void init() {
    try {
      server  = HttpServer.create(new InetSocketAddress(config.getPort()),0);
      server.createContext("/", new JsonDispatchHandler());
      server.start();
    } catch (IOException e) {
      throw new SrpcException(Enum.CLIENT_ERROR, "create server error", e);
    }
  }

  @Override
  public Client getClient(String address) {
    String[] arr = address.split(":");
    return new HttpJsonClient(arr[0], Integer.parseInt(arr[1]));
  }

  @Override
  public Result dispatch(InvocationHandler handler) {
    try {
      Exporter<?> exporter = protocolManager.getExporter(handler.getClassName());
      return exporter.invoke(handler);
    } catch (SrpcException e) {
      try {
        Class<?> c = Class.forName(handler.getClassName());
        Exporter<?> exporter = protocolManager.getExporter(c);
        if (null == exporter) {
          Result result = new Result();
          result.setCode(403);
          result.setMsg("class not export");
          return result;
        }
        return exporter.invoke(handler);
      } catch (ClassNotFoundException e1) {
        Result result = new Result();
        result.setCode(404);
        result.setMsg("don't hava class " + handler.getClassName());
        return result;
      }
    }
  }
}
