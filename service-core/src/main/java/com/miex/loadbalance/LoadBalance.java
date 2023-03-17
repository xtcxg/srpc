package com.miex.loadbalance;

import com.miex.config.LoadBalanceConfig;
import com.miex.exchange.Client;
import com.miex.exchange.Exchange;
import com.miex.registry.RegistryManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface LoadBalance {

  /**
   * address : [server list]
   */
  Map<String, List<String>> serverMap = new HashMap<>();
  /**
   * class name : [client list]
   */
  Map<String, List<Client>> classMap = new HashMap<>();

  LoadBalanceConfig config = new LoadBalanceConfig();

  default void refreshServer(Map<String, List<String>> serverMap) {
    this.serverMap.clear();
    this.serverMap.putAll(serverMap);
  }

  default void setConfig(Class<? extends LoadBalance> type, Map<String, String> extra) {
    config.setType(type);
    config.setExtra(extra);
  }

  Client getClient(String serverName);
}
