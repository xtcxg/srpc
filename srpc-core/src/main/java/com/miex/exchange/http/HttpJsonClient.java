package com.miex.exchange.http;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.miex.exception.SrpcException;
import com.miex.exception.SrpcException.Enum;
import com.miex.exchange.Client;
import com.miex.protocol.InvocationHandler;
import com.miex.protocol.Result;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.net.http.HttpClient;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpJsonClient implements Client {
  private static final HttpClient client = HttpClient.newBuilder()
      .version(java.net.http.HttpClient.Version.HTTP_1_1)
      .connectTimeout(Duration.ofSeconds(2)).build();
  String host;
  int port;
  String address;
  Gson gson = new GsonBuilder()
      .setExclusionStrategies(new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes fieldAttributes) {
          return fieldAttributes.getName().equals("throwable");
        }

        @Override
        public boolean shouldSkipClass(Class<?> aClass) {
          return false;
        }
      })
      .setNumberToNumberStrategy(ToNumberPolicy.LAZILY_PARSED_NUMBER)
      .setObjectToNumberStrategy(ToNumberPolicy.LAZILY_PARSED_NUMBER)
      .create();


  public HttpJsonClient(String host, int port) {
    this.host = host;
    this.port = port;
    this.address = host + ":" + port;
  }

  @Override
  public Result send(InvocationHandler handler) {
    URI uri = null;
    String body = "{}";
    String res = "";
    try {
      uri = new URI("http", null, host, port,
          "/" + handler.getClassName() + "/" + handler.getMethodName(), null, null);

      Object[] params = handler.getParams();
      if (null != params && 0 < params.length) {
        Gson gson = new Gson();
        Map<String, Object> map = new LinkedHashMap<>();
        for (Object param : params) {
          map.put(param.getClass().getName(), param);
        }
        body = gson.toJson(map);
      }
      HttpRequest request = HttpRequest.newBuilder()
          .uri(uri)
          .POST(HttpRequest.BodyPublishers.ofString(body))
          .build();
      HttpResponse<String> response = client.send(request,
          BodyHandlers.ofString(StandardCharsets.UTF_8));
      res = response.body();
      return gson.fromJson(res, Result.class);
    } catch (SrpcException e) {
      throw new SrpcException(Enum.TRANSFER_ERROR,
          "rpc error, path:" + uri.getPath() + ", body:" + body + "response:" + res, e);
    } catch (URISyntaxException e) {
      throw new SrpcException(Enum.CLIENT_ERROR, "build uri error", e);
    } catch (IOException | InterruptedException e) {
      throw new SrpcException(Enum.CLIENT_ERROR, "send request error", e);
    }
  }

  public String getAddress() {
    return this.address;
  }
}
