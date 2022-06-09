package com.miex.provide.api;


import com.miex.annotation.Provide;

@Provide
public interface ProductService {

    String getName(Long id);
}
