package com.miex.test.provide.impl;

import com.miex.annotation.Apply;
import com.miex.test.provide.api.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TestBean {

    @Apply
    ShopService shopService;

    @PostConstruct
    public void init() {
        System.out.println("testBean init");
        String name = shopService.buy(1L);
        System.out.println(name);
    }
}
