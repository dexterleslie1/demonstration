package com.future.demo;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 8080;
        HttpClient httpClient = null;
        try {
            httpClient = new HttpClient(host, port);
            httpClient.connect();

            HttpClient.Response response = httpClient.get("/api/v1/testGetSubmitParamByUrl1?param1=v1");
            System.out.println("响应：" + response);
            System.out.println();

            response = httpClient.get("/api/v1/testGetSubmitParamByUrl2?param1=v1-2");
            System.out.println("响应：" + response);
            System.out.println();
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
    }
}
