package com.miex.annotation;

import com.miex.exchange.ExchangeManager;
import com.miex.exchange.Exchange;
import com.miex.registry.Registry;
import com.miex.registry.RegistryManager;
import org.springframework.core.env.ConfigurableEnvironment;


public class SrpcSpringApplicationConfig {

    private ConfigurableEnvironment environment;
    private Registry registry;
    private Exchange exchange;
}
