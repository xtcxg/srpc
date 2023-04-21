package com.miex.test.provide.impl;

import com.miex.test.provide.api.ShopService;

import com.miex.test.provide.api.WorkerService;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    WorkerService workerService;

    Map<Long,String> goods = new HashMap<>(){{
        put(1L,"apple");
        put(2L,"banana");
    }};

    @Override
    public String buy(Long id) {
        workerService.doWork();
        return goods.get(id);
    }
}
