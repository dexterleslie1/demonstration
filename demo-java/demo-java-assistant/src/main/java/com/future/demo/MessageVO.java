package com.future.demo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

/**
 * 发送消息载体
 * 包含：websocket推送消息、iOS apns离线消息、华为apns离线消息等
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageVO {
    private String clientId;
    // 消息对应业务系统的id
    // 使用场景1: yyd-websocket服务使用webhook通知azp系统消息已通过apns推送
    private String bizId;
    private ApnsCommonProperties apnsCommonProperties;
    private AppleIOSApnsProperties appleIOSApnsProperties;
    private AndroidHuaweiApnsProperties androidHuaweiApnsProperties;
    private AndroidOppoApnsProperties androidOppoApnsProperties;
    private AndroidVivoApnsProperties androidVivoApnsProperties;
    private AndroidXiaomiApnsProperties androidXiaomiApnsProperties;
    private WebsocketMessage websocketMessage;

//    // 消息是否支持apns推送
//    @Builder.Default /* 使用builder时候默认值为true */
//    private boolean supportPushedByApns = true;
    @Builder.Default
    private PushType pushType = PushType.Both;

    /**
     * 支持消息推送给特定类型的目标设备，不指定则表示推送给所有目标设备
     * NOTE: 这个功能只支持PushType.WebsocketOnly情况生效
     */
    private List<ClientDeviceType> targetClientDeviceTypes;

    /**
     * apns消息公共属性
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApnsCommonProperties {
        private String title;
        private String content;
        /**
         * 自定义数据
         */
        private Map<String, String> extraData;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApnsPropertiesBase {
        /**
         * 支持不同客户端离线消息内容个性化需求，例如：iOS内容显示为: 吃饭了吗？，小米内容显示为: [5条]吃饭了吗？
         */
        private String content;
        /**
         * 同一个消息iOS和android推送自定义字段不一样
         */
        private Map<String, String> extraData;
    }

    /**
     * iOS apns消息属性
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AppleIOSApnsProperties extends ApnsPropertiesBase {
        private int badge;
        private String sound;
        /**
         * 是否使用NotificationServiceExtension技术
         */
        @Builder.Default
        private boolean notificationServiceExtension = false;
    }

    /**
     * 华为apns消息属性
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AndroidHuaweiApnsProperties extends ApnsPropertiesBase {
        private String tag;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AndroidOppoApnsProperties extends ApnsPropertiesBase {
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AndroidVivoApnsProperties extends ApnsPropertiesBase {
//        /**
//         * 推送模式 0：正式推送；1：测试推送，不填默认为0
//         */
//        @Builder.Default
//        private int pushMode=1;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AndroidXiaomiApnsProperties extends ApnsPropertiesBase {
        private int notifyId;
    }

    /**
     * websocket推送消息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WebsocketMessage {
        private String data;
    }

    /**
     * 消息推送类型
     */
    public enum PushType {
        /**
         * 后端处理该类型方式如下：
         * 1、当前没有websocket在线使用apns直接推送，处理完毕。
         * 2、当前有websocket在线使用websocket推送并得到客户端ack，处理完毕。
         * 3、当前有websocket在线使用websocket推送但没有得到客户端ack，间隔一定时间再使用websocket重新推送依然没有得到客户端ack，
         * 多次尝试后依旧没有得到客户端ack，自动切换使用apns推送，处理完毕。
         */
        Both,
        /**
         * 只使用websocket推送
         */
        WebsocketOnly,
        /**
         * 只使用apns推送
         */
        ApnsOnly,
        /**
         * 拆分为两种推送类型后，分别使用WebsocketOnly和ApnsOnly推送，NOTE：后台逻辑会判断当前活跃的apns设备没有对应的websocket在线才会拆分一条ApnsOnly消息推送，否则会出现重复推情况
         */
        WebsocketOnlyAndApnsOnly
    }
}
