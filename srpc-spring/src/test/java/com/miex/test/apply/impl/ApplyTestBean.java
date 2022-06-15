package com.miex.test.apply.impl;

import com.miex.annotation.Apply;
import com.miex.test.apply.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ApplyTestBean {

    @PostConstruct
    public void init() {
//        System.out.println(productService);
    }
}
