package com.miex.provide.service;

import com.miex.annotation.Provide;
import com.miex.provide.api.Fruit;
import com.miex.provide.api.ProductService;

import java.util.HashMap;
import java.util.Map;

public class ProductServiceImpl implements ProductService {

  Map<Long, String> map = new HashMap<>() {{
    put(1L, "bowling");
    put(2L, "football");
  }};

  @Override
  public String getName(Long id) {
    return map.get(id);
  }

  @Override
  public String addFruit(Fruit fruit) {
    return null;
  }

  @Override
  public Fruit detail(Long id) {
    Fruit fruit = new Fruit();
    fruit.setPrice(1.34);
    fruit.setName("banana");
    return fruit;
  }

  @Override
  public void addFruit(double price, String name) {

  }
}
