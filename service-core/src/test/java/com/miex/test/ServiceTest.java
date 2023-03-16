package com.miex.test;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.miex.config.ApplicationConfig;
import com.miex.exchange.http.HttpExchange;
import com.miex.protocol.InvocationHandler;
import com.miex.protocol.Result;
import com.miex.provide.api.ProductService;
import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class ServiceTest {

    static ApplicationConfig config;

    @BeforeAll
    public static void before() {

        Properties properties = new Properties();
        properties.setProperty("srpc.server.port","3695");

        properties.setProperty("srpc.scan.provide","com.miex.provide");
        properties.setProperty("srpc.scan.apply","com.miex.provide");

        properties.setProperty("srpc.exchange.protocol", "json");

        properties.setProperty("srpc.registry.type","redis");
        properties.setProperty("srpc.registry.host","127.0.0.1");
        properties.setProperty("srpc.registry.port","6379");
        config = new ApplicationConfig(properties);
    }

    public void after() {
        while (true) {
            try {
                Thread.sleep(9999);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void requestTest() throws IOException {
        HttpExchange httpServer = new HttpExchange();
        httpServer.init();
        after();
    }

    @Test
    public void clientTest() throws URISyntaxException, IOException, InterruptedException, NoSuchMethodException, ClassNotFoundException {
        InvocationHandler handler = new InvocationHandler();
        handler.setClassName(ProductService.class.getName());
        handler.setName("productService");
//        handler.setMethod(ProductService.class.getMethod("getName", Long.class));
        handler.setMethodName("getName");
        handler.setParams(new Object[]{1L});
        handler.setParameterTypes(new Class[]{Long.class});
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(handler);
        baos.flush();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http",null,"127.0.0.1",3695,"/",null,null))
                .POST(HttpRequest.BodyPublishers.ofByteArray(baos.toByteArray()))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        byte[] body = response.body();
        ByteArrayInputStream bais = new ByteArrayInputStream(body);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Result result =(Result) ois.readObject();
        System.out.println(result.getValue());

    }

    @Test
    public void requestBodyTest() {
        try {
            byte[] bi = "apple".getBytes(StandardCharsets.UTF_8);
            ByteArrayInputStream bais = new ByteArrayInputStream(bi);
            DataInputStream din = new DataInputStream(bais);
            int sum = din.available();
            byte[] bs = new byte[sum];
            din.readFully(bs);
            System.out.println(new String(bs));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void gson() {
        Gson gson = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return fieldAttributes.getName().equals("throwable");
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        }).setExclusionStrategies(new ExclusionStrategy[]{
            new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                    return fieldAttributes.getName().equals("throwable");
                }

                @Override
                public boolean shouldSkipClass(Class<?> aClass) {
                    return false;
                }
            }
        }).create();
        Result result = new Result();
        result.setCode(200);
        String s = gson.toJson(result);
        System.out.println(s);

    }
}
