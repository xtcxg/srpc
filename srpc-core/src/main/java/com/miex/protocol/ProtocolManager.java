package com.miex.protocol;

import com.miex.exception.SrpcException;
import com.miex.exception.SrpcException.Enum;
import com.miex.util.Scanner;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ProtocolManager {

//  private static String PROTOCOL = "java";
  /* 存储需要创建 invoker 的接口名 */
  private static final List<Class<?>> APPLY_CLASS = new ArrayList<>();
  /* 存储使用 @Apply 的类 */
  private static final Set<String> APPLY_CACHE = new HashSet<>();
  /* className : proxyObject */
  private static final ConcurrentHashMap<Class<?>, Object> APPLY_MAP = new ConcurrentHashMap<>();

  /**
   * name -> interface - -> Exporter (proxy)
   *                   - -> Class (implement)
   */
  /* 存储使用 @Provide 的类, name : interface<?> */
  private static final ConcurrentHashMap<String, Class<?>> PROVIDER_CLASS = new ConcurrentHashMap<>();
  /* interface : Exporter<?> */
  private static final ConcurrentHashMap<Class<?>, Exporter<?>> EXPORTER_MAP = new ConcurrentHashMap<>();
  /*  interface : class */
  private static final ConcurrentHashMap<Class<?>, Class<?>> PROVIDER_MAP = new ConcurrentHashMap<>();

  private static final ProtocolManager protocolManager = new ProtocolManager();

  private ProtocolManager() {
  }

  public static ProtocolManager getInstance() {
    return protocolManager;
  }

  public void addApply(Class<?> c) {
    if (!APPLY_CLASS.contains(c))
      APPLY_CLASS.add(c);
  }

  public List<Class<?>> getAllApplyClass() {
    return APPLY_CLASS;
  }


  public void addApplyCache(String className) {
    APPLY_CACHE.add(className);
  }

  public Set<String> getAllApplyCache() {
    return APPLY_CACHE;
  }

  /**
   * 获取代理服务
   */
  public <T> T getApply(Class<T> c) {
    Object target = APPLY_MAP.get(c.getName());
    if (null == target) {
      return createApply(c);
    } else {
      return (T) target;
    }
  }

  /**
   * 获取全部代理服务
   */
  public Map<Class<?>, Object> getAllApply() {
    return APPLY_MAP;
  }

  public void createAllApply() {
    for (Class<?> c : APPLY_CLASS) {
      createApply(c);
    }
  }

  private void createApply(String className) {
    try {
      Class<?> c = Class.forName(className);
      createApply(c);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public <T> T createApply(Class<T> type) {
    return createApply(type.getName(), type);
  }

  public <T> T createApply(String className, Class<T> type) {
    Invoker<T> invoker = new DefaultInvoker<>(type);
    T target = ApplyProxyFactory.proxy(invoker, type);
    APPLY_MAP.put(type, target);
    return target;
  }

  public void createAllExporter() {
    try {
      List<String> emptyList = new ArrayList<>();
      for (Map.Entry<String, Class<?>> entry : PROVIDER_CLASS.entrySet()) {
        String name = entry.getKey();
        Class<?> type = entry.getValue();
        Class<?> impl = PROVIDER_MAP.get(type);
        if (null == impl) {
          emptyList.add(name);
          continue;
        }
        Object target = impl.getDeclaredConstructor(new Class[]{}).newInstance();
        Exporter<?> exporter = createExporter(name, type, target);
        exporter.export();
        EXPORTER_MAP.put(type, exporter);
      }
      for (String name : emptyList) {
        PROVIDER_CLASS.remove(name);
      }
    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException
             | IllegalAccessException e) {
      throw new SrpcException(Enum.EXPORTER_ERROR, "create exporter target error",e);
    }
  }

  private <T> Exporter<T> createExporter(String name, Class<T> type, Object target) {
    return new DefaultExporter<>(name, type, (T) target);
  }

  public <T> Exporter<T> getExporter(String name) {
    Class<?> type = PROVIDER_CLASS.get(name);
    if (null == type) {
      throw new SrpcException(Enum.EXPORTER_ERROR, "can't find provider by name");
    }
    Exporter<?> exporter = getExporter(name, type);
    if (null == exporter) {
      throw new SrpcException(Enum.EXPORTER_ERROR, "can't create export by "
          + type.getSimpleName());
    }
    return (Exporter<T>) exporter;
  }

  public <T> Exporter<T> getExporter(Class<T> type) {
    return getExporter(null , type);
  }

  private <T> Exporter<T> getExporter(String name, Class<T> type) {
    try {
      if (null != EXPORTER_MAP.get(type)) {
        return (Exporter<T>) EXPORTER_MAP.get(type);
      }
      Scanner.checkAndAddProvideCache(type);
      Class<T> impl = Scanner.scanProvideImpl(type);
      if (null == impl) {
        throw new SrpcException(Enum.EXPORTER_ERROR, "can't find implement by type "
            + type.getName());
      }
      Exporter<T> exporter;
      T target = impl.getDeclaredConstructor(new Class[]{}).newInstance();
      exporter = new DefaultExporter<>(name, type, target);
      exporter.export();
      EXPORTER_MAP.put(type, exporter);
      return exporter;
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
             NoSuchMethodException e) {
      e.printStackTrace();
      throw new SrpcException(SrpcException.Enum.EXPORTER_ERROR,
          "can't create exporter,class [" + type.getName() + "]");
    }
  }

  public Map<Class<?>, Object> getAllProvide() {
    createAllExporter();
    Map<Class<?>, Object> provideMap = new HashMap<>();
    EXPORTER_MAP.forEach((k, v) -> {
      provideMap.put(k, v.getTarget());
    });
    return provideMap;
  }

  public void addProviderClass(String name, Class<?> c) {
    PROVIDER_CLASS.put(name, c);
  }

  public Map<String, Class<?>> getProvideClasses() {
    return PROVIDER_CLASS;
  }

  public List<Class<?>> getLocalKeys() {
    return new ArrayList<>(EXPORTER_MAP.keySet());
  }

  public void addProvideMap (Class<?> i, Class<?> c) {
    PROVIDER_MAP.put(i, c);
  }

  public ConcurrentHashMap<Class<?>, Class<?>> getProvideMap() {
    return PROVIDER_MAP;
  }
}
