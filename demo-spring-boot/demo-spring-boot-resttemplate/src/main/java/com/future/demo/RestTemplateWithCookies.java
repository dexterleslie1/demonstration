package com.future.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yyd.common.exception.BusinessException;
import com.yyd.common.http.response.ObjectResponse;
import com.yyd.common.json.JSONUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RestTemplateWithCookies extends RestTemplate {

    private final List<HttpCookie> cookies = new ArrayList<>();

    public RestTemplateWithCookies() {
        setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                InputStream inputStream = null;
                try {
                    inputStream = response.getBody();
                    String responseStr = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

                    ObjectResponse<String> responseO =
                            JSONUtil.ObjectMapperInstance.readValue(responseStr, new TypeReference<ObjectResponse<String>>() {});
                    BusinessException businessException = new BusinessException(responseO.getErrorCode(), responseO.getErrorMessage());
                    businessException.setData(responseO.getData());
                    throw businessException;
                } finally {
                    if(inputStream!=null) {
                        inputStream.close();
                        inputStream = null;
                    }
                }
            }
        });
    }

    public synchronized List<HttpCookie> getCookies() {
        return cookies;
    }

    public synchronized void resetCookies() {
        cookies.clear();
    }

    private void processHeaders(HttpHeaders headers) {
        final List<String> cooks = headers.get("Set-Cookie");
        if (cooks != null && !cooks.isEmpty()) {
            cooks.stream().map((c) -> HttpCookie.parse(c)).forEachOrdered((cook) -> {
                cook.forEach((a) -> {
                    HttpCookie cookieExists = cookies.stream().filter(x -> a.getName().equals(x.getName())).findAny().orElse(null);
                    if (cookieExists != null) {
                        cookies.remove(cookieExists);
                    }
                    cookies.add(a);
                });
            });
        }
    }

    @Override
    protected <T extends Object> T doExecute(URI url, HttpMethod method, final RequestCallback requestCallback, final ResponseExtractor<T> responseExtractor) throws RestClientException {
        final List<HttpCookie> cookies = getCookies();

        return super.doExecute(url, method, new RequestCallback() {
            @Override
            public void doWithRequest(ClientHttpRequest chr) throws IOException {
                if (cookies != null) {
                    List<String> cookieList = new ArrayList<>();
                    for (HttpCookie cookie : cookies) {
                        cookieList.add(cookie.getName() + "=" + cookie.getValue());
                    }
                    chr.getHeaders().put(HttpHeaders.COOKIE, cookieList);
                }
                requestCallback.doWithRequest(chr);
            }

        }, new ResponseExtractor<T>() {
            @Override
            public T extractData(ClientHttpResponse chr) throws IOException {
                processHeaders(chr.getHeaders());
                return responseExtractor.extractData(chr);
            }
        });
    }
}
