package com.miex.test.provide.service;

import com.miex.test.provide.api.WorkerService;
import org.springframework.stereotype.Component;

@Component
public class WorkerServiceImpl implements WorkerService {

  @Override
  public void doWork() {
    System.out.println("produce potato");
  }
}
