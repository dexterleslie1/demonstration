package com.future.demo;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {
    private String host;
    private int port;

    private Socket socket;

    public HttpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public synchronized void connect() throws IOException {
        if (socket != null) {
            throw new IllegalStateException("已连接");
        }

        InetSocketAddress address = new InetSocketAddress(host, port);
        socket = new Socket();
        socket.connect(address);
    }

    public synchronized void close() throws IOException {
        if (socket != null) {
            socket.close();
            socket = null;
        }
    }

    public Response get(String url) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        String requestLine = "GET " + url + " HTTP/1.1\r\n";
        writer.write(requestLine);
        System.out.print("> " + requestLine);
        // 添加Host头否则服务器响应400错误
        requestLine = "Host: " + host + ":" + port + "\r\n";
        writer.write(requestLine);
        System.out.print("> " + requestLine);
        // 请求完毕后关闭连接
        // writer.write("Connection: close\r\n");
        // 空行终止请求头
        requestLine = "\r\n";
        writer.write(requestLine);
        System.out.print("> " + requestLine);
        // 发送数据到服务器
        writer.flush();

        InputStream rawInputStream = socket.getInputStream();
        Response response = new Response();
        // 响应头Content-Length的值以根据长度读取内容
        int contentLength = -1;
        ByteArrayOutputStream linerBuffer = new ByteArrayOutputStream();
        while (true) {
            int dataByte = rawInputStream.read();

            // 服务器端关闭连接
            if (dataByte == -1) {
                break;
            }

            if (dataByte == '\r') {
                dataByte = rawInputStream.read();
                if (dataByte == '\n') {
                    if (linerBuffer.size() == 0) {
                        // 空行表示余下的响应内容为body
                        System.out.println("< ");
                        break;
                    } else {
                        // 一行响应
                        String line = linerBuffer.toString(StandardCharsets.UTF_8);
                        linerBuffer = new ByteArrayOutputStream();

                        System.out.println("< " + line);

                        if (line.startsWith("HTTP/")) {
                            String[] httpVersionAndStatusCode = line.split(" ");
                            String httpVersion = httpVersionAndStatusCode[0];
                            int statusCode = Integer.parseInt(httpVersionAndStatusCode[1]);
                            response.setHttpVersion(httpVersion);
                            response.setStatusCode(statusCode);
                        } else if (line.contains(":")) {
                            if (line.toLowerCase().startsWith("content-length:")) {
                                contentLength = Integer.parseInt(line.substring("Content-Length:".length()).trim());
                            }

                            String[] keyAndValue = line.split(":");
                            String key = keyAndValue[0];
                            String value = keyAndValue[1];
                            response.setHeader(key, value);
                        }
                    }
                }
            } else {
                linerBuffer.write(dataByte);
            }
        }

        String body = null;
        if (contentLength > 0) {
            // 有明确的Content-Length，精确读取指定长度的字符
            byte[] bodyBytes = new byte[contentLength];
            rawInputStream.read(bodyBytes, 0, contentLength);
            body = new String(bodyBytes, StandardCharsets.UTF_8);
        }
        response.setBody(body);
        System.out.println("< " + body);

        return response;
    }

    public static class Response {
        private String httpVersion;
        private int statusCode;
        private Map<String, String> headers = new HashMap<>();
        private String body;

        public Response() {

        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getBody() {
            return body;
        }

        public void setHeader(String key, String value) {
            headers.put(key, value);
        }

        public String getHeader(String key) {
            return headers.get(key);
        }

        public void setHttpVersion(String httpVersion) {
            this.httpVersion = httpVersion;
        }

        public String getHttpVersion() {
            return httpVersion;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "httpVersion='" + httpVersion + '\'' +
                    ", statusCode=" + statusCode +
                    ", headers=" + headers +
                    ", body='" + body + '\'' +
                    '}';
        }
    }
}
