package com.miex.provide.service;

import com.miex.annotation.Provide;
import com.miex.provide.api.ProductService;

import java.util.HashMap;
import java.util.Map;

@Provide
public class ProductServiceImpl implements ProductService {

    Map<Long,String> map = new HashMap<>() {{
       put(1L,"bowling");
       put(2L,"football");
    }};

    @Override
    public String getName(Long id) {
        return map.get(id);
    }
}
