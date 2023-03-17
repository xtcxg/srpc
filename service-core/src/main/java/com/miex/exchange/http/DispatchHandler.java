package com.miex.exchange.http;

import com.miex.exception.SrpcException;
import com.miex.exchange.ExchangeManager;
import com.miex.exchange.Exchange;
import com.miex.protocol.InvocationHandler;
import com.miex.protocol.Result;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class DispatchHandler implements HttpHandler {

  private static final Log log = LogFactory.getLog(DispatchHandler.class);

  private static final Exchange exchange = ExchangeManager.getExchange();

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    try {
      InputStream requestBody = exchange.getRequestBody();
      ObjectInputStream oin = new ObjectInputStream(requestBody);
      InvocationHandler handler = (InvocationHandler) oin.readObject();
      log.debug("receive:" + handler);
      Result result = DispatchHandler.exchange.dispatch(handler);
      byte[] bs = serializable(result);
      exchange.sendResponseHeaders(200, bs.length);
      exchange.getResponseBody().write(bs);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SrpcException e) {
      Result result = new Result();
      result.setException(e);
      byte[] bs = serializable(result);
      exchange.sendResponseHeaders(e.getCode(), bs.length);
      exchange.getResponseBody().write(serializable(result));
    }
  }

  public byte[] serializable(Result result) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(result);
      baos.flush();
      return baos.toByteArray();
    } catch (IOException e) {
      throw new SrpcException(SrpcException.Enum.SERIALIZABLE_ERROR, "serializable Result error");
    }
  }
}
