package com.miex.protocol;

import java.lang.reflect.InvocationTargetException;

public class DefaultExporter<T> extends AbstractExporter<T> {

  public DefaultExporter(String name, Class<T> type, T target) {
    super(name, type, target);
  }

  public DefaultExporter(Class<T> type, T target) {
    super(type, target);
  }

  @Override
  public Result doInvoke(InvocationHandler handler) {
    Result result;
    try {
      Object origResult = this.type
          .getMethod(handler.getMethodName(), handler.getParameterTypes())
          .invoke(this.target, handler.getParams());
      result = new Result(origResult);
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      e.printStackTrace();
      result = new Result();
      result.setException(e);
    }
    return result;
  }
}
