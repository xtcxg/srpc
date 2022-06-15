package com.miex.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@SrpcComponentScan
public @interface EnableSrpc {

    @AliasFor(annotation = SrpcComponentScan.class, attribute = "basePackages")
    String[] basePackages() default {};


    @AliasFor(annotation = SrpcComponentScan.class, attribute = "providePackages")
    String[] providePackages() default {};

    @AliasFor(annotation = SrpcComponentScan.class, attribute = "applyPackages")
    String[] applyPackages() default {};

}
