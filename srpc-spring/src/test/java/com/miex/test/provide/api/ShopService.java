package com.miex.test.provide.api;

import com.miex.annotation.Provide;

@Provide
public interface ShopService {

    String buy(Long id);
}
