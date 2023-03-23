package com.miex.loadbalance;

import com.miex.exception.SrpcException;
import com.miex.exception.SrpcException.Enum;
import com.miex.exchange.Client;
import com.miex.exchange.Exchange;
import com.miex.exchange.ExchangeManager;
import com.miex.protocol.InvocationHandler;
import com.miex.protocol.Result;
import com.miex.util.CollectionUtil;
import java.util.ArrayList;
import java.util.List;

public class SimpleLoadBalance implements LoadBalance {

  public Client getClient(String serverName) {
    List<Client> clients = classMap.get(serverName);
    if (CollectionUtil.isEmpty(clients)) {
      clients = new ArrayList<>();
      List<String> hosts = serverMap.get(serverName);
      if (null != hosts) {
        for (String host : hosts) {
          clients.add(ExchangeManager.getExchange().getClient(host));
        }
        classMap.put(serverName, clients);
      } else {
        throw new SrpcException(Enum.CLIENT_ERROR, "create client error, server name:" + serverName);
      }
    }
    return clients.get(0);
  }

  @Override
  public Result dispatch(InvocationHandler handler, Exchange exchange) {
    Client client = getClient(handler.getClassName());
    return exchange.send(handler, client);
  }
}
