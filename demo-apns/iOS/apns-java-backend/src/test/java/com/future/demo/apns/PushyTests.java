package com.future.demo.apns;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.util.ApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PushyTests {
    final static String PasswordPhrase = "123456";

    // iOS 12
    final static String token = "81793a3f20eb3995edb36a2cbcac942de23eec9ac3cf1b18b705319ed4893714";
//    // 我的iOS
//    final static String token = "7a1e7b7443317bb23ed80dbea71f3ae3dc7f586e2b238ea5547d2c92559d2449";
//    // iOS 13
//    final static String token = "59f6574d305b1c024c10f5396223b6f27f388bf3d72a6e2c58d716a2f609d087";
//    // iOS 15
//    final static String token = "aa61760f9b1bc2a43620db63f104dc0c1eb69d4385238fdaeb7f9d478194d1a4";

    /**
     * 测试普通apns推送
     * 普通apns推送需要aps.content-available=1
     */
    @Test
    public void testNormalApns() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        String userHome = System.getProperty("user.home");
        String filePath = userHome + File.separator + "com.future.demo.iOS.apns-dev.p12";

        final ApnsClient apnsClient = new ApnsClientBuilder()
                .setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
                .setClientCredentials(new File(filePath), PasswordPhrase)
                .build();

        String title = "Title";
        String content = "Your message here.";

        final SimpleApnsPushNotification pushNotification;
        {
            final ApnsPayloadBuilder payloadBuilder = new SimpleApnsPayloadBuilder();
            payloadBuilder.setAlertTitle(title);
            payloadBuilder.setAlertBody(content);
            payloadBuilder.setBadgeNumber(9);
            payloadBuilder.setSound("default");
            payloadBuilder.setContentAvailable(true);
            payloadBuilder.addCustomProperty("myField1", "value1");

            final String payload = payloadBuilder.build();

            pushNotification = new SimpleApnsPushNotification(token, "com.future.demo.iOS.apns", payload);
        }

        final PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>>
                sendNotificationFuture = apnsClient.sendNotification(pushNotification);

        try {
            final PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse =
                    sendNotificationFuture.get();

            if (pushNotificationResponse.isAccepted()) {
                System.out.println("Push notification accepted by APNs gateway.");
            } else {
                System.out.println("Notification rejected by the APNs gateway: " +
                        pushNotificationResponse.getRejectionReason());

                pushNotificationResponse.getTokenInvalidationTimestamp().ifPresent(timestamp -> {
                    System.out.println("\t…and the token is invalid as of " + timestamp);
                });
            }
        } catch (final ExecutionException e) {
            System.err.println("Failed to send push notification.");
            e.printStackTrace();
        } finally {
            final CompletableFuture<Void> closeFuture = apnsClient.close();
            closeFuture.get(5, TimeUnit.SECONDS);
        }
    }

    /**
     * https://github.com/jchambers/pushy/wiki/Best-practices
     *
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    @Test
    public void performanceTest() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        final Semaphore semaphore = new Semaphore(50);

        String userHome = System.getProperty("user.home");
        String filePath = userHome + File.separator + "com.future.demo.iOS.apns-dev.p12";

        final ApnsClient apnsClient = new ApnsClientBuilder()
                .setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
                .setClientCredentials(new File(filePath), PasswordPhrase)
                .build();

        String title = "Title";
        String content = "Your message here.";

        AtomicInteger successCounter = new AtomicInteger();
        AtomicInteger failedCounter = new AtomicInteger();

        try {
            for(int i= 0; i < 200; i++) {
                semaphore.acquire();

                final SimpleApnsPushNotification pushNotification;
                {
                    final ApnsPayloadBuilder payloadBuilder = new SimpleApnsPayloadBuilder();
                    payloadBuilder.setAlertTitle(title);
                    payloadBuilder.setAlertBody(content);
                    payloadBuilder.setBadgeNumber(9);
                    payloadBuilder.setSound("default");
                    payloadBuilder.setContentAvailable(true);
                    payloadBuilder.addCustomProperty("myField1", "value1");

                    final String payload = payloadBuilder.build();

                    pushNotification = new SimpleApnsPushNotification(token, "com.future.demo.iOS.apns", payload);
                }

                final PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>>
                        sendNotificationFuture = apnsClient.sendNotification(pushNotification);

                System.out.println("第" + (i+1) + "个消息已发出");

                sendNotificationFuture.whenComplete((pushNotificationResponse, cause) -> {
                    // Do whatever processing needs to be done
                    semaphore.release();

                    if (pushNotificationResponse.isAccepted()) {
//                        System.out.println("Push notification accepted by APNs gateway.");
                        successCounter.incrementAndGet();
                    } else {
                        failedCounter.incrementAndGet();
                        System.out.println("Notification rejected by the APNs gateway: " +
                                pushNotificationResponse.getRejectionReason());

                        pushNotificationResponse.getTokenInvalidationTimestamp().ifPresent(timestamp -> {
                            System.out.println("\t…and the token is invalid as of " + timestamp);
                        });
                    }
                });
            }

            Thread.sleep(5000);
            System.out.println("成功发送消息" + successCounter.get() + "条，失败发送消息" + failedCounter.get() + "条");
        } finally {
            final CompletableFuture<Void> closeFuture = apnsClient.close();
            closeFuture.get(5, TimeUnit.SECONDS);
        }
    }
}
