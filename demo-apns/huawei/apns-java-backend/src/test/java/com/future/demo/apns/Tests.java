package com.future.demo.apns;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import feign.*;
import feign.codec.ErrorDecoder;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Tests {
    HuaweiOAuth2Api huaweiOAuth2Api;
    HuaweiPushApi huaweiPushApi;

    @Before
    public void setup() {
        huaweiOAuth2Api = Feign.builder()
                // https://stackoverflow.com/questions/56987701/feign-client-retry-on-exception
                .retryer(Retryer.NEVER_RETRY)
                // https://qsli.github.io/2020/04/28/feign-method-timeout/
                .options(new Request.Options(15, TimeUnit.SECONDS, 15, TimeUnit.SECONDS, false))
                .encoder(new FormEncoder(new JacksonEncoder()))
                .decoder(new JacksonDecoder())
                // feign logger
                // https://cloud.tencent.com/developer/article/1588501
                .logger(new Logger.ErrorLogger()).logLevel(Logger.Level.FULL)
                // ErrorDecoder
                // https://cloud.tencent.com/developer/article/1588501
                .errorDecoder(new ErrorDecoder() {
                    @Override
                    public Exception decode(String methodKey, Response response) {
                        try {
                            String json = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);
                            throw new RuntimeException(json);
                        } catch (Exception e) {
                            return e;
                        }
                    }
                })
                .target(HuaweiOAuth2Api.class, "https://oauth-login.cloud.huawei.com");

        huaweiPushApi = Feign.builder()
                // https://stackoverflow.com/questions/56987701/feign-client-retry-on-exception
                .retryer(Retryer.NEVER_RETRY)
                // https://qsli.github.io/2020/04/28/feign-method-timeout/
                .options(new Request.Options(15, TimeUnit.SECONDS, 15, TimeUnit.SECONDS, false))
                .encoder(new FormEncoder(new JacksonEncoder()))
                .decoder(new JacksonDecoder())
                // feign logger
                // https://cloud.tencent.com/developer/article/1588501
                .logger(new Logger.ErrorLogger()).logLevel(Logger.Level.FULL)
                // ErrorDecoder
                // https://cloud.tencent.com/developer/article/1588501
                .errorDecoder(new ErrorDecoder() {
                    @Override
                    public Exception decode(String methodKey, Response response) {
                        try {
                            String json = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);
                            throw new RuntimeException(json);
                        } catch (Exception e) {
                            return e;
                        }
                    }
                })
                .target(HuaweiPushApi.class, "https://push-api.cloud.huawei.com");
    }

    @Test
    public void test() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        final Semaphore semaphore = new Semaphore(25);

        String clientId = System.getenv("clientId");
        String clientSecret = System.getenv("clientSecret");
        JsonNode result = this.huaweiOAuth2Api.oAuth2GetTokenV3(clientId, clientSecret);
        String accessToken = result.get("access_token").asText();

        String deviceId = "IQAAAACy0jJjAACNY3pQY3Yh565WIYMUlC7vhuEgZUTyYVlRfqyH4NdUi7mLpX8eSQ3AtRk7BOAjhxuJkCbOYlCYafk7oR1-7xJcUztoIiYK_n2jvQ";
        String title = "测试标题";
        String content = "测试内容";

        AtomicInteger successCounter = new AtomicInteger();
        AtomicInteger failedCounter = new AtomicInteger();

        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i= 0; i < 100; i++) {
            semaphore.acquire();

            String tag = "test-tag" + i;

            executorService.submit(() -> {
                ObjectNode androidNotification = JsonNodeFactory.instance.objectNode()
                        //自分类权益生效后: LOW=营销通知, NORMAL=系统通知
                        .put("importance", "NORMAL")
                        .put("title", title)
                        .put("body", content)
                        .set("click_action", JsonNodeFactory.instance.objectNode().put("type", 3));

                if (!StringUtils.isBlank(tag)) {
                    //华为支持分组显示(group,tag)! 使用title进行分组,相同title覆盖!
                    androidNotification = androidNotification.put("tag", tag);
                }

                ObjectNode androidConfig = JsonNodeFactory.instance.objectNode()
                        .set("notification", androidNotification);

                ObjectNode message = JsonNodeFactory.instance.objectNode();
                message.putArray("token").add(deviceId);
                message.set("android", androidConfig);

                ObjectNode messageObject = JsonNodeFactory.instance.objectNode();
                messageObject.put("validate_only", false);
                messageObject.set("message", message);
                try {
                    this.huaweiPushApi.sendMessage(accessToken, clientId, messageObject);
                    successCounter.incrementAndGet();
                } catch (Exception ex) {
                    failedCounter.incrementAndGet();
                }finally {
                    semaphore.release();
                }
            });
        }

        Thread.sleep(5000);
        System.out.println("成功发送消息" + successCounter.get() + "条，失败发送消息" + failedCounter.get() + "条");
    }
}
