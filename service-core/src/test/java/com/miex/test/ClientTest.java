package com.miex.test;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.miex.exception.SrpcException;
import com.miex.exception.SrpcException.Enum;
import com.miex.protocol.Result;
import com.miex.provide.api.Fruit;
import com.miex.provide.api.ProductService;
import com.miex.provide.service.ProductServiceImpl;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class ClientTest {

  @Test
  public void buildRequest() throws NoSuchMethodException, ClassNotFoundException {
    Class<ProductService> clazz = ProductService.class;
    Method method = clazz.getDeclaredMethod("addFruit", Fruit.class);
    Class<?> returnType = method.getReturnType();
    Object[] params = new Object[1];
    Fruit fruit = new Fruit();
    fruit.setName("apple");
    fruit.setPrice(1.23);
    params[0] = fruit;
    Map<Integer, Object> map = new HashMap<>();
    map.put(1, fruit);
    map.put(2, 2);
    map.put(3, null);
    Gson gson = new Gson();
    String body = gson.toJson(map);
    System.out.println(body);
    Type type = new TypeToken<HashMap<String,Object>>(){}.getType();
    Map<String,  Object> rmap =  gson.fromJson(body, type);
    System.out.println(rmap.get("1"));
    System.out.println(returnType);
    Class<Fruit> fruitClass = Fruit.class;
    System.out.println(fruitClass);
    Object rf = gson.fromJson("ddd", returnType);
    System.out.println(rf);

  }

  @Test
  public void returnType() throws IOException, ClassNotFoundException {
    Gson gson = new Gson();

    SrpcException exception = new SrpcException(Enum.SYSTEM_ERROR);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(exception);
    byte[] bytes = baos.toByteArray();

    Map<String, Object> map = new HashMap<>();
    map.put("value", 1);
    map.put("exception", bytes);


    String str = gson.toJson(map);
    System.out.println(str);
    Type type = new TypeToken<HashMap<String,Object>>(){}.getType();
    Map<String,  Object> rmap = gson.fromJson(str, type);
    ArrayList<Double> fs = (ArrayList<Double>) rmap.get("exception");
    System.out.println(fs);
    byte[] rbs = new byte[fs.size()];
    for (int i = 0; i < fs.size(); i++) {
      int a = (int) (double)fs.get(i);
      rbs[i] =  Byte.parseByte(String.valueOf(a));
    }
    System.out.println(Arrays.toString(rbs));
    ByteArrayInputStream bais = new ByteArrayInputStream(rbs);
    ObjectInputStream ois = new ObjectInputStream(bais);
    SrpcException rexception = (SrpcException) ois.readObject();
    System.out.println(rexception);

  }

  @Test
  public void returnString() {
    String value = "apple";
    Gson gson = new Gson();
    System.out.println(gson.toJson(value));

    Fruit fruit = new Fruit();
    fruit.setPrice(2.34);
    System.out.println(gson.toJson(fruit));
  }

  @Test
  public void invoke()
      throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException,
      IllegalAccessException {
    System.out.println(double[].class.getName());
    Gson gson = new Gson();
    ProductService product = new ProductServiceImpl();
    double price = 2.34;
    String name = "banana";
    Map<String, Object> map = new LinkedHashMap<>();
    map.put(double.class.getName(), price);
    map.put(name.getClass().getName(), name);
    Method method = ProductService.class.getMethod("addFruit", double.class, String.class);
    String str = gson.toJson(map);
    System.out.println(str);
    Type type = new TypeToken<LinkedHashMap<String,Object>>(){}.getType();
    LinkedHashMap<String, Object> rmap = gson.fromJson(str, type);
    Class<?>[] parameterTypes = new Class[rmap.size()];
    Object[] params = new Object[rmap.size()];
    int i = 0;
    for (Entry<String, Object> entry : rmap.entrySet()) {
//      parameterTypes[i] = Class.forName(entry.getKey());
      parameterTypes[0] = double.class;
      parameterTypes[1] = String.class;
      params[i] = gson.fromJson(entry.getValue().toString(), parameterTypes[i]);
      i++;
    }
    Object invoke = ProductService.class.getMethod(method.getName(), parameterTypes)
        .invoke(product, params);
    System.out.println(invoke);
  }

  @Test
  public void array() throws ClassNotFoundException {
    Gson gson = new Gson();
    Class<?> c = int.class;
    Class<?> aClass = Class.forName("[I");

    System.out.println(Double[].class.getName());
    String str = "[1,2,3,4]";
    Object array = gson.fromJson(str, aClass);
  }

}
