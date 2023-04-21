package com.miex.annotation;

import com.miex.exchange.ExchangeManager;
import com.miex.exchange.Exchange;
import com.miex.registry.Registry;
import com.miex.registry.RegistryManager;
import org.springframework.core.env.ConfigurableEnvironment;

import javax.annotation.PostConstruct;

public class SrpcSpringApplicationConfig {

    private ConfigurableEnvironment environment;
    private Registry registry;
    private Exchange exchange;

//    @PostConstruct
//    public void init() {
//        this.exchange = ExchangeManager.getExchange();
//        this.exchange.init();
//
//        this.registry = RegistryManager.getRegistry();
//        this.registry.register();
//    }
}
