package com.future.demo.filter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MyHttpRequest extends HttpServletRequestWrapper {
//    /**
//     * 保存流数据
//     */
//    private final byte[] body;
//    /**
//     * 保存参数
//     */
//    private Map<String, String[]> params = new HashMap<>();
//
//    public MyHttpRequest(HttpServletRequest request) {
//        super(request);
//        body = getBodyString(request).getBytes(StandardCharsets.UTF_8);
//        this.params.putAll(request.getParameterMap());
//    }
//
//    public MyHttpRequest(HttpServletRequest request, byte[] body) {
//        super(request);
//        this.body = body;
//        this.params.putAll(request.getParameterMap());
//    }
//
//    @Override
//    public ServletInputStream getInputStream() {
//        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
//        return new ServletInputStream() {
//            @Override
//            public boolean isFinished() {
//                return false;
//            }
//
//            @Override
//            public boolean isReady() {
//                return false;
//            }
//
//            @Override
//            public void setReadListener(ReadListener readListener) {
//            }
//
//            @Override
//            public int read() {
//                return bais.read();
//            }
//        };
//
//    }
//    @Override
//    public BufferedReader getReader() {
//        return new BufferedReader(new InputStreamReader(this.getInputStream()));
//    }
//
//    @Override
//    public String getParameter(String name) {
//        String[] values = params.get(name);
//        if(values == null || values.length == 0) {
//            return null;
//        }
//        return values[0];
//    }
//
//    @Override
//    public Enumeration<String> getParameterNames() {
//        return Collections.enumeration(params.keySet());
//    }
//
//    @Override
//    public String[] getParameterValues(String name) {
//        return params.get(name);
//    }
//
//    @Override
//    public Map<String, String[]> getParameterMap() {
//        return this.params;
//    }
//
//    public void setParameter(Map<String, String> map) {
//        params.clear();
//        map.forEach((key, value) -> params.put(key, new String[]{value}));
//    }
//
//    public String getBody() {
//        return new String(body);
//    }
//
//    public static String getBodyString(ServletRequest request) {
//        StringBuilder sb = new StringBuilder();
//        InputStream inputStream = null;
//        BufferedReader reader = null;
//        try {
//            inputStream = request.getInputStream();
//            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
//            String line = "";
//            while ((line = reader.readLine()) != null) {
//                sb.append(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (inputStream != null) {
//                try {
//                    inputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return sb.toString();
//    }

    //这里map需要指定为 Map<String, String[]> 类型
    private Map<String, String[]> params = new HashMap<>();

    /**
     * 构造方法，将原有请求中的参数复制到当前类的params中
     * @param request
     */
    public MyHttpRequest(HttpServletRequest request) {
        super(request);
        params.putAll(request.getParameterMap());
    }


    public void addParameter(String key, Object value) {
        if(value != null) {
            this.params.put(key, new String[] {String.valueOf(value)});
        }
    }

    /**
     * 如果在SpringBoot中用对象来接收参数，这个方法就必须重写
     * @return
     */
    @Override
    public Enumeration<String> getParameterNames() {
        return new Vector(this.params.keySet()).elements();
    }

    /**
     * 这个方法必须重写
     * @param name
     * @return
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = this.params.get(name);
        if((values == null) || (values.length == 0)) {
            return null;
        }
        return values;
    }

}
