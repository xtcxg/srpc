package com.miex.exchange;

import com.miex.cache.CacheManager;
import com.miex.exchange.http.HttpClient;
import com.miex.exchange.http.HttpServer;
import com.miex.registry.Registry;
import com.miex.registry.redis.RegistryManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeManager {

    private static final Registry REGISTRY = RegistryManager.createRegistry();
    private static final String PROTOCOL = CacheManager.PROPERTIES_CACHE.get("srpc.protocol");

    private static class SingletonHolder {
        private static final ExchangeManager INSTANCE = new ExchangeManager();
    }

    private ExchangeManager() {}

    public static ExchangeManager getInstance() {
        return ExchangeManager.SingletonHolder.INSTANCE;
    }


    public static Server createServer() {

        return HttpServer.getInstance();
    }

    public static List<Client> getClients(String className) {
        List<String> hosts = REGISTRY.getHosts(className);
        List<Client> clients = new ArrayList<>();
        for (String host : hosts) {
            clients.add(createClient(host));
        }
        return clients;
    }

    private static Client createClient(String host) {
        switch (PROTOCOL) {
            case "http":
                return buildHttpClient(host);
            default:
                return buildHttpClient(host);
        }
    }

    private static Client buildHttpClient(String host) {
        Client client = null;
        try {
            String[] arr = host.split(":");
            URI uri = new URI("http",null,arr[0],Integer.parseInt(arr[1]),"/",null,null);
            client = new HttpClient(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return client;
    }


}
