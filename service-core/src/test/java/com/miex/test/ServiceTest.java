package com.miex.test;

import com.miex.exchange.http.HttpServer;
import com.miex.protocol.InvocationHandler;
import com.miex.protocol.Result;
import com.miex.provide.api.ProductService;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class ServiceTest {



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
        HttpServer httpServer = new HttpServer();
        httpServer.start();
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
}
