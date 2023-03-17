package com.miex.loadbalance;

import com.miex.exception.SrpcException;
import com.miex.exception.SrpcException.Enum;
import com.miex.exchange.Client;
import com.miex.exchange.ExchangeManager;
import com.miex.util.CollectionUtil;
import java.util.ArrayList;
import java.util.List;

public class SimpleLoadBalance implements LoadBalance {

  @Override
  public Client getClient(String serverName) {
    List<Client> clients = classMap.get(serverName);
    if (CollectionUtil.isEmpty(clients)) {
      clients = new ArrayList<>();
      List<String> hosts = serverMap.get(serverName);
      if (null != hosts) {
        for (String host : hosts) {
          clients.add(ExchangeManager.getExchange().getClient(host));
        }
      } else {
        throw new SrpcException(Enum.CLIENT_ERROR, "create client error, server name:" + serverName);
      }
    }
    return clients.get(0);
  }
}
