package com.miex.loadbalance;

import com.miex.exception.SrpcException;
import com.miex.exception.SrpcException.Enum;
import com.miex.exchange.Client;
import com.miex.exchange.Exchange;
import com.miex.protocol.InvocationHandler;
import com.miex.protocol.Result;
import com.miex.util.CollectionUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TryBestLoadBalance implements LoadBalance {

  private final Map<String, Long> count = new HashMap<>();

  @Override
  public Result dispatch(InvocationHandler handler, Exchange exchange)  {
    String serverName = handler.getClassName();
    final List<Client> clients = null != classMap.get(serverName)
        ? classMap.get(serverName) : new ArrayList<>();
    if (0 == clients.size()) {
      List<String> adds = serverMap.get(serverName);
      if (CollectionUtil.isEmpty(adds)) {
        throw new SrpcException(Enum.LOAD_BALANCE_ERROR, "can't find host, server: " + serverName);
      }
      for (String add : adds) {
        clients.add(exchange.getClient(add));
      }
      classMap.put(serverName, clients);
    }
    try {
      Result result = CompletableFuture.supplyAsync(() -> {
        Long size = count.get(serverName);
        if (null == size) {
          size = 0L;
        }
        count.put(serverName, size + 1);
        for (int i = 0; i < count.size(); i++) {
          try {
            Client c = clients.get((int)(size % (clients.size() - 1)));
            return c.send(handler);
          } catch (Exception e) {
            size++;
            log.error("send request error, server:" + serverName, e);
          }
        }
        return null;
      }).get(5, TimeUnit.SECONDS);
      if (null == result) {
        throw new SrpcException(Enum.EXCHANGE_ERROR, serverName + " don't have available server");
      }
      return result;
    } catch (ExecutionException | InterruptedException e) {
      throw new SrpcException(Enum.SERVER_ERROR, e);
    } catch (TimeoutException e) {
      throw new SrpcException(Enum.EXCHANGE_ERROR, "timeout");
    }
  }
}
