package com.miex.exchange;

import com.miex.config.ExchangeConfig;
import com.miex.protocol.Exporter;
import com.miex.protocol.InvocationHandler;
import com.miex.protocol.ProtocolManager;
import com.miex.protocol.Result;

public abstract class AbstractExchange implements Exchange {

  public static final ExchangeConfig exchangeConfig = ExchangeManager.getExchangeConfig();

  public static final ProtocolManager protocolManager = ProtocolManager.getInstance();

  @Override
  public Result dispatch(InvocationHandler handler) {
    try {
      Class<?> c = Class.forName(handler.getClassName());
      Exporter<?> exporter = protocolManager.getExporter(c);
      return exporter.invoke(handler);
    } catch (ClassNotFoundException e) {
      Result result = new Result();
      result.setCode(404);
      result.setMsg("don't hava class " + handler.getClassName());
      return result;
    }
  }

  @Override
  public Result send(InvocationHandler handler, Client client) {
    return client.send(handler);
  }
}
