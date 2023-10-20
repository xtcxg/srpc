package com.miex.test.provide.service;

import com.miex.test.provide.api.FruitService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author 刘堂钊
 * @since 2023/10/20 16:49
 */
@Service
public class FruitServiceImpl implements FruitService {

    @Override
    public List<String> getNameList() {
        return Arrays.asList("apple", "banana", "cherry");
    }
}
