package com.miex.exchange;

import com.miex.cache.PropertiesCache;
import com.miex.config.ExchangeConfig;
import com.miex.exception.SrpcException;
import com.miex.exception.SrpcException.Enum;
import com.miex.loadbalance.LoadBalance;
import com.miex.protocol.InvocationHandler;
import com.miex.protocol.Result;
import com.miex.util.ClassUtil;

import com.miex.util.StringUtil;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ExchangeManager {

  private static final String PROTOCOL;
  private static ExchangeManager exchangeManager;
  private static final Exchange exchange;
  private static final LoadBalance loadBalance;

  static {
    try {
      PropertiesCache properties = PropertiesCache.getInstance();
      PROTOCOL = properties.get("srpc.exchange.protocol");
      String exchangeClass = properties.get("srpc.mapping.exchange." + PROTOCOL);
      String clientClass = properties.get("srpc.mapping.client." + PROTOCOL);
      Class<? extends Exchange> ec = (Class<? extends Exchange>) Class.forName(exchangeClass);
      Class<? extends Client> cc = (Class<? extends Client>) Class.forName(clientClass);
      exchange = ClassUtil.createObject(ec);
      exchange.getConfig().setExchange(ec);
      exchange.getConfig().setClient(cc);
      exchange.getConfig().setProtocol(PROTOCOL);
      exchange.getConfig().setHost(StringUtil.isEmpty(properties.get("srpc.exchange.host")) ?
          Inet4Address.getLocalHost().getHostAddress() : properties.get("srpc.exchange.host"));
      exchange.getConfig().setPort(Integer.valueOf(properties.get("srpc.exchange.port")));

      String loadBalanceType = properties.get("srpc.loadbalance.type");
      String loadBalanceClass = properties.get("srpc.mapping.loadbalance." + loadBalanceType);
      Class<? extends LoadBalance> lbc =
          (Class<? extends LoadBalance>) Class.forName(loadBalanceClass);
      loadBalance = ClassUtil.createObject(lbc);
      loadBalance.setConfig(lbc, buildLoadBalanceExtra(properties, loadBalanceType));
    } catch (ClassNotFoundException e) {
      throw new SrpcException(Enum.EXCHANGE_ERROR, "class not found", e);
    } catch (UnknownHostException e) {
      throw new SrpcException(Enum.EXCHANGE_ERROR, "obtain server ip error", e);
    }
  }

  private ExchangeManager() {}

  public static ExchangeManager getInstance() {
    if (null == exchangeManager) {
      exchangeManager = new ExchangeManager();
    }
    return exchangeManager;
  }

  public void openServer() {
    exchange.init();
  }

  public static ExchangeConfig getExchangeConfig() {
    return exchange.getConfig();
  }

  public static Exchange getExchange() {
    return exchange;
  }

  public static Result dispatch(InvocationHandler handler) {
    Client client = loadBalance.getClient(handler.getClassName());
    return exchange.send(handler, client);
  }

  private static Map<String, String> buildLoadBalanceExtra(PropertiesCache properties, String type) {
    Map<String, String> map = properties.getProperties();
    Map<String, String> extra = new HashMap<>();
    String prefix = "srpc.loadbalance." + type + ".";
    for (Entry<String, String> entry : map.entrySet()) {
      String key = entry.getKey();
      if (key.startsWith(prefix)) {
        extra.put(key.substring(key.lastIndexOf(".") + 1), entry.getValue());
      }
    }
    return extra;
  }

  public void syncServer(Map<String, List<String>> serverMap) {
    loadBalance.refreshServer(serverMap);
  }
}
