package com.miex.test.apply.service;

import com.miex.annotation.Provide;

@Provide
public interface UserService {

    String getName(Long id);
}
