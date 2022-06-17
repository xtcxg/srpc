package com.miex.annotation;

import com.miex.exchange.ExchangeManager;
import com.miex.exchange.Server;
import com.miex.registry.Registry;
import com.miex.registry.RegistryManager;
import org.springframework.core.env.ConfigurableEnvironment;

import javax.annotation.PostConstruct;

public class SrpcSpringApplicationConfig {

    private ConfigurableEnvironment environment;
    private Registry registry;
    private Server server;

    @PostConstruct
    public void init() {
        this.server = ExchangeManager.getServer();
        this.server.start();

        this.registry = RegistryManager.getRegistry();
        this.registry.register();
    }
}
