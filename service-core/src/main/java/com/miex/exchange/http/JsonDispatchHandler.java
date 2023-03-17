package com.miex.exchange.http;

import com.google.common.reflect.TypeToken;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.miex.exception.SrpcException;
import com.miex.exception.SrpcException.Enum;
import com.miex.exchange.ExchangeManager;
import com.miex.protocol.InvocationHandler;
import com.miex.protocol.Result;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class JsonDispatchHandler implements HttpHandler {

  Type type = new TypeToken<LinkedHashMap<String, Object>>() {}.getType();
  private static final Map<String, Class<?>> classMapper = new HashMap<>();
  static Gson gson;

  static {
    classMapper.put(int.class.getName(), int.class);
    classMapper.put(long.class.getName(), long.class);
    classMapper.put(double.class.getName(), double.class);
    classMapper.put(float.class.getName(), float.class);
    classMapper.put(boolean.class.getName(), boolean.class);
    classMapper.put(byte.class.getName(), byte.class);
    classMapper.put(char.class.getName(), char.class);
    gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy[]{
        new ExclusionStrategy() {
          @Override
          public boolean shouldSkipField(FieldAttributes fieldAttributes) {
            return fieldAttributes.getName().equals("throwable");
          }

          @Override
          public boolean shouldSkipClass(Class<?> aClass) {
            return false;
          }
        }
    }).create();
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    try {
      InputStream requestBody = exchange.getRequestBody();
      Headers headers = exchange.getRequestHeaders();
      String body;
      String length = headers.getFirst("Content-Length");
      if (null != length) {
        ByteArrayInputStream bais = new ByteArrayInputStream(requestBody.readAllBytes());
        body = new String(bais.readAllBytes());
        bais.close();
      } else {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
        for (; ; ) {
          String line = reader.readLine();
          if (null != line) {
            builder.append(line);
          } else {
            body = builder.toString();
            break;
          }
        }
        reader.close();
      }

      String[] url = exchange.getRequestURI().getPath().substring(1).split("/");
      LinkedHashMap<String, Object> paramMap = gson.fromJson(body, type);
      Object[] params = new Object[paramMap.size()];
      Class<?>[] paramTypes = new Class[paramMap.size()];
      int i = 0;
      for (Entry<String, Object> entry : paramMap.entrySet()) {
        Class<?> c = classMapper.get(entry.getKey());
        if (null == c) {
          c = Class.forName(entry.getKey());
        }
        paramTypes[i] = c;
        params[i] = gson.fromJson(entry.getValue().toString(), c);
        i++;
      }
      InvocationHandler handler = new InvocationHandler();
      handler.setClassName(url[0]);
      handler.setMethodName(url[1]);
      handler.setParameterTypes(paramTypes);
      handler.setParams(params);
      Result result = ExchangeManager.getExchange().dispatch(handler);
      result.setCode(200);
//      result.setMsg("");
      byte[] bs = gson.toJson(result).getBytes(StandardCharsets.UTF_8);
      exchange.getResponseHeaders().set("Content-Type", "application/json");
      exchange.sendResponseHeaders(200, bs.length);
      exchange.getResponseBody().write(bs);
      requestBody.close();
    } catch (Exception e) {
      Result result = new Result();
      result.setCode(Enum.PARAM_ERROR.getCode());
      result.setMsg(e.getMessage());
      byte[] bs = gson.toJson(result).getBytes(StandardCharsets.UTF_8);
      exchange.sendResponseHeaders(501, bs.length);
      exchange.getResponseBody().write(bs);
      throw new SrpcException(Enum.PARAM_ERROR, e);
    } finally {
      exchange.close();
    }
  }
}
