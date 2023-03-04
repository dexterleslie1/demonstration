package com.future.demo.apns;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.util.ApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import com.yyd.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 提供直接测试各个提供商的apns推送功能
 * 例如：支持通过提供apple clientCredentials和apnsId直接测试apple apns推送功能
 */
@Service
@Slf4j
public class ApnsTesterService {

    public String testApple(String certificateBase64, String passphrase, boolean production,
                            String title, String content, String apnsId) throws IOException, ExecutionException, InterruptedException {
        if (StringUtils.isEmpty(certificateBase64)) {
            throw new IllegalArgumentException("没有配置certificateBase64");
        }
        if (StringUtils.isEmpty(passphrase)) {
            throw new IllegalArgumentException("没有配置passphrase");
        }
        if (StringUtils.isBlank(title))
            throw new IllegalArgumentException("title不能为空");
        if (StringUtils.isBlank(content))
            throw new IllegalArgumentException("content不能为空");
        if (StringUtils.isBlank(apnsId))
            throw new IllegalArgumentException("apnsId不能为空");

        ApnsClient apnsClient = null;
        ByteArrayInputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(certificateBase64));

            if (!production) {
                apnsClient = new ApnsClientBuilder()
                        .setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
                        .setClientCredentials(inputStream, passphrase)
                        .build();
            } else {
                apnsClient = new ApnsClientBuilder()
                        .setApnsServer(ApnsClientBuilder.PRODUCTION_APNS_HOST)
                        .setClientCredentials(inputStream, passphrase)
                        .build();
            }

//            if (!StringUtils.isBlank(title)) {
//                int length = title.length();
//                if (length > 17) {
//                    title = title.substring(0, 17);
//                    title = title + "...";
//                }
//            }
//
//            if (!StringUtils.isBlank(content)) {
//                int length = content.length();
//                if (length > 47) {
//                    content = content.substring(0, 47);
//                    content = content + "...";
//                }
//            }

//        int badge = (node.get("badge")==null?1:node.get("badge").asInt());
            int badge = 0;
            String sound = "default";
            boolean notificationServiceExtension = true;
            ApnsPayloadBuilder payloadBuilder = new SimpleApnsPayloadBuilder();
            payloadBuilder.setBadgeNumber(badge);
            payloadBuilder.setSound(sound);
            payloadBuilder.setAlertTitle(title);
            payloadBuilder.setAlertBody(content);

//        // 自定义通知id协助使用NotificationServiceExtension
//        if(!StringUtils.isEmpty(customPayloadId)) {
//            builder.customField("custom-payload-id", customPayloadId);
//        }

//            Map<String, String> extMap = message.getApnsCommonProperties()==null?null:message.getApnsCommonProperties().getExtraData();
//            if(extMap!=null && extMap.size()>0) {
//
//                StringBuilder builder = new StringBuilder();
//                builder.append("message.apnscommonproperties内容： ");
//                extMap.forEach((key, value) -> builder.append(key).append("=").append(value).append(","));
//                log.debug(builder.toString());
//
//                for (String key : extMap.keySet()) {
//                    payloadBuilder.addCustomProperty(key, extMap.get(key));
//                }
//            }
//            extMap = message.getAppleIOSApnsProperties()==null?null:message.getAppleIOSApnsProperties().getExtraData();
//            if(extMap!=null && extMap.size()>0) {
//
//                StringBuilder builder = new StringBuilder();
//                builder.append("message.appleiosapnsProperties内容： ");
//                extMap.forEach((key, value) -> builder.append(key).append("=").append(value).append(","));
//                log.debug(builder.toString());
//
//                for (String key : extMap.keySet()) {
//                    payloadBuilder.addCustomProperty(key, extMap.get(key));
//                }
//            }

            // 是否使用NotificationServiceExtension技术
            if (notificationServiceExtension) {
                payloadBuilder.setMutableContent(true);
            } else {
                payloadBuilder.setContentAvailable(true);
            }

            String payload = payloadBuilder.build();
            SimpleApnsPushNotification pushNotification = new SimpleApnsPushNotification(apnsId, "com.guangzhouyiyingda.Azp", payload);
            PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>>
                    sendNotificationFuture = apnsClient.sendNotification(pushNotification);
            final PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse =
                    sendNotificationFuture.get();

            if (pushNotificationResponse.isAccepted()) {
                String message = String.format("测试成功发送apns到设备deviceId=%s,payload=%s", apnsId, payload);
                log.debug(message);
                return message;
            } else {
                log.error("测试失败发送apns到设备deviceId={},payload={}，原因：{}", apnsId, payload, pushNotificationResponse.getRejectionReason());
                throw new BusinessException(pushNotificationResponse.getRejectionReason().orElse("未知原因"));
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw ex;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
                inputStream = null;
            }

            if (apnsClient != null) {
                apnsClient.close();
            }
        }
    }
}
