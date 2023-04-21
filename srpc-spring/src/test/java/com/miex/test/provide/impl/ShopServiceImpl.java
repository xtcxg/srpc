package com.miex.test.provide.impl;

import com.miex.test.provide.api.ShopService;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {
    Map<Long,String> goods = new HashMap<>(){{
        put(1L,"apple");
        put(2L,"banana");
    }};

    @PostConstruct
    private void init() {

    }

    @Override
    public String buy(Long id) {
        return goods.get(id);
    }
}
