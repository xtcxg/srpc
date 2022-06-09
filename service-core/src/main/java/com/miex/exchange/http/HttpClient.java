package com.miex.exchange.http;

import com.miex.exception.SrpcException;
import com.miex.exchange.Client;
import com.miex.protocol.InvocationHandler;
import com.miex.protocol.Result;

import java.io.*;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpClient implements Client {
    private static final java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder().version(java.net.http.HttpClient.Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(2)).build();

    private URI uri;

    public HttpClient(URI uri) {
        this.uri = uri;
    }

    @Override
    public Result send(InvocationHandler handler) {
        try {
            handler.setTarget(null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(handler);
            baos.flush();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(baos.toByteArray()))
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            byte[] body = response.body();
            ByteArrayInputStream bais = new ByteArrayInputStream(body);
            ObjectInputStream ois = new ObjectInputStream(bais);

            baos.close();
            oos.close();
            bais.close();
            ois.close();
            return (Result) ois.readObject();
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new SrpcException(SrpcException.Enum.SEND_REQUEST_ERROR);
        }
    }
}
