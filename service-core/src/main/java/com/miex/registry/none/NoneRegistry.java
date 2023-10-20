package com.miex.registry.none;

import com.miex.registry.Registry;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NoneRegistry implements Registry {

  @Override
  public void connect(String host, Integer port, String name, String password) {

  }

  @Override
  public void register() {

  }

  @Override
  public void refresh() {

  }

  @Override
  public void clean() {

  }

  @Override
  public Map<String, List<String>> pull(String[] names) {
    return null;
  }

  @Override
  public void destroy() {

  }

  @Override
  public List<String> getHosts(String className) {
    return null;
  }

  @Override
  public ConcurrentHashMap<String, List<String>> getServices() {
    return null;
  }
}
