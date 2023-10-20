package com.miex.test.provide.service;

import com.miex.test.provide.api.FruitService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 刘堂钊
 * @since 2023/10/20 17:26
 */
@Service
public class FruitServiceImpl2 implements FruitService {

    @Override
    public List<String> getNameList() {
        return null;
    }
}
