package com.miex.config;

import com.miex.loadbalance.LoadBalance;
import java.util.Map;

public class LoadBalanceConfig {

  Class<? extends LoadBalance> type;

  Map<String, String> extra;

  public Class<? extends LoadBalance> getType() {
    return type;
  }

  public void setType(Class<? extends LoadBalance> type) {
    this.type = type;
  }

  public Map<String, String> getExtra() {
    return extra;
  }

  public void setExtra(Map<String, String> extra) {
    this.extra = extra;
  }
}
