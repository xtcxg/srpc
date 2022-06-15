package com.miex.config;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

public class SrpcEnvironment implements EnvironmentAware {
    ConfigurableEnvironment environment;

    @Override
    public void setEnvironment(Environment environment) {
        if (environment instanceof ConfigurableEnvironment) {
            this.environment = ((ConfigurableEnvironment) environment);
        }
    }

    public String getProperty(String key) {
        return environment.getProperty(key);
    }
}
