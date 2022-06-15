package com.miex.annotation;

import com.miex.cache.CacheManager;
import com.miex.exchange.ExchangeManager;
import com.miex.exchange.Server;
import com.miex.registry.Registry;
import com.miex.registry.redis.RegistryManager;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

public class SrpcSpringApplicationConfig implements EnvironmentAware {

    private ConfigurableEnvironment environment;
    private Registry registry;
    private Server server;

    @PostConstruct
    public void init() {
        CacheManager.PROPERTIES_CACHE.put("srpc.port", environment.getProperty("srpc.port"));
        CacheManager.PROPERTIES_CACHE.put("srpc.registry.host", environment.getProperty("srpc.registry.host"));
        CacheManager.PROPERTIES_CACHE.put("srpc.registry.port", environment.getProperty("srpc.registry.port"));
        this.server = ExchangeManager.createServer();
        this.registry = RegistryManager.createRegistry();
        this.server.start();
        this.registry.register();

    }

    @Override
    public void setEnvironment(Environment environment) {
        if (environment instanceof ConfigurableEnvironment) {
            this.environment = ((ConfigurableEnvironment) environment);
        }
    }
}
