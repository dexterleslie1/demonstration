package com.future.demo;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DemoTests {
    private final static Random RANDOM = new Random();
    @Test
    public void test() throws IOException, InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();

        for(int k=0; k<100; k++) {
            for (int i = 0; i < 90; i++) {
                executorService.submit(new Runnable() {
                    public void run() {
                        try {
                            //                        for(int j=0; j<99; j++) {
                            String xForwardedFor = RANDOM.nextInt(255) + "." + RANDOM.nextInt(255) + "." + RANDOM.nextInt(255) + "." + RANDOM.nextInt(255);
                            CloseableHttpClient httpclient = HttpClients.createDefault();
                            HttpGet httpGet = new HttpGet("http://xxxx");
                            httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
                            httpGet.setHeader("Accept-Encoding", "gzip, deflate");
                            httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
                            httpGet.setHeader("Cache-Control", "max-age=0");
                            httpGet.setHeader("Connection", "keep-alive");
                            httpGet.setHeader("Host", "xxx.tyty2323.com");
                            httpGet.setHeader("Upgrade-Insecure-Requests", "1");
                            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:105.0) Gecko/20100101 Firefox/105.0");
                            httpGet.setHeader("x-forwarded-for", xForwardedFor);
                            CloseableHttpResponse response1 = httpclient.execute(httpGet);
                            try {
                                //            System.out.println(response1.getStatusLine());
                                StatusLine statusLine = response1.getStatusLine();
                                if (statusLine.getStatusCode() != 200) {
                                    System.out.println("statuscode=" + statusLine);
                                }
                                org.apache.http.HttpEntity entity1 = response1.getEntity();
                                String response = EntityUtils.toString(entity1);
                                EntityUtils.consume(entity1);
                                //            System.out.println(response);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            } finally {
                                response1.close();
                            }

                            //                            Thread.sleep(10000);
                            //                        }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }

            Thread.sleep(16000);
        }

        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS));
    }

}
