package com.miex.test;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.ToNumberStrategy;
import com.miex.protocol.Result;
import org.junit.jupiter.api.Test;

public class JsonTest {

  @Test
  public void doubleTest() {
    String body = "{\"value\":12.3}";
    Gson gson = new GsonBuilder()
        .setExclusionStrategies(new ExclusionStrategy() {
          @Override
          public boolean shouldSkipField(FieldAttributes fieldAttributes) {
            return fieldAttributes.getName().equals("throwable");
          }

          @Override
          public boolean shouldSkipClass(Class<?> aClass) {
            return false;
          }
        })
        .setNumberToNumberStrategy(ToNumberPolicy.LAZILY_PARSED_NUMBER)
        .setObjectToNumberStrategy(ToNumberPolicy.LAZILY_PARSED_NUMBER)
        .create();
    Result result = gson.fromJson(body, Result.class);
    System.out.println(result.getValue());
  }

}
