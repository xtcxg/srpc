package com.miex.exchange.http;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.miex.exception.SrpcException;
import com.miex.exception.SrpcException.Enum;
import com.miex.exchange.Client;
import com.miex.protocol.InvocationHandler;
import com.miex.protocol.Result;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpJsonClient implements Client {
  private static final HttpClient client = HttpClient.newBuilder()
      .version(java.net.http.HttpClient.Version.HTTP_1_1)
      .connectTimeout(Duration.ofSeconds(2)).build();

  String host;
  int port;

  public HttpJsonClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  @Override
  public Result send(InvocationHandler handler) {
    URI uri = null;
    String body = "{}";
    String res = "";
    try {
      uri = new URI("http", null, host, port,
          "/" + handler.getClassName() + "#" + handler.getMethodName(), null, null);

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
      return getBody(res, handler.getReturnType());
    } catch (SrpcException e) {
      throw new SrpcException(Enum.TRANSFER_ERROR,
          "rpc error, path:" + uri.getPath() + ", body:" + body + "response:" + res, e);
    } catch (URISyntaxException e) {
      throw new SrpcException(Enum.CLIENT_ERROR, "build uri error", e);
    } catch (IOException | InterruptedException e) {
      throw new SrpcException(Enum.CLIENT_ERROR, "send request error", e);
    }
  }

  private Result getBody(String res, Class<?> c) {
    Gson gson = new Gson();
    Result result = new Result();
    try {
      Object o = gson.fromJson(res, c);
      result.setValue(o);
      return result;
    } catch (Exception e) {
      try {
        Type type = new TypeToken<HashMap<String,Object>>(){}.getType();
        Map<String, Object> rmap = gson.fromJson(res, type);
        throw new SrpcException((Integer) rmap.get("code"), rmap.get("msg").toString());
      } catch (Exception ex) {
        throw new SrpcException(Enum.SYSTEM_ERROR, ex);
      }
    }
  }
}
