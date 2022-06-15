package com.miex.test.apply.impl;

import com.miex.annotation.Apply;
import com.miex.test.apply.service.UserService;
import com.miex.test.provide.api.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class UserServiceImpl implements UserService {

    @Apply
    @Autowired
    ShopService shopService;

    @PostConstruct
    private void init(){
        System.out.println("user service init");

        System.out.println(shopService.buy(1L));
    }

    @Override
    public String getName(Long id) {
        return "miex";
    }
}
