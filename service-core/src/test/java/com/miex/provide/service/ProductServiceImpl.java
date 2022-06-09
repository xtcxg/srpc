package com.miex.provide.service;

import com.miex.annotation.Provide;
import com.miex.provide.api.ProductService;

@Provide
public class ProductServiceImpl implements ProductService {



    @Override
    public String getName(Long id) {
        return "miex";
    }
}
