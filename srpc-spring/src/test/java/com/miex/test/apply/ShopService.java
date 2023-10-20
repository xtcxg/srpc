package com.miex.test.apply;

import com.miex.annotation.Apply;
import com.miex.test.provide.api.FruitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author 刘堂钊
 * @since 2023/10/20 16:43
 */
@Component
public class ShopService {
    @Apply
    @Resource
    @Autowired
    FruitService fruitService;

    @PostConstruct
    public void init() {
        List<String> nameList = fruitService.getNameList();
        System.out.println(nameList);
    }
}
