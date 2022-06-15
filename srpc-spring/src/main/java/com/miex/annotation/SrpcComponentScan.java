package com.miex.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SrpcComponentScanRegistrar.class)
public @interface SrpcComponentScan {

    String[] value() default {};

    String[] basePackages() default {};

    String[] providePackages() default {};

    String[] applyPackages() default {};
}
