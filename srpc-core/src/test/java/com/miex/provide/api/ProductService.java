package com.miex.provide.api;


import com.miex.annotation.Provide;

@Provide
public interface ProductService {

    String getName(Long id);

    String addFruit(Fruit fruit);

    Fruit detail(Long id);

    void addFruit(double price, String name);
}
