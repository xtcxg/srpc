package com.miex.test.provide.api;

import com.miex.annotation.Provide;

import java.util.List;

@Provide
public interface FruitService {
    List<String> getNameList();
}
