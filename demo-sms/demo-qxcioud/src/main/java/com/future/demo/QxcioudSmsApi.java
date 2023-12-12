package com.future.demo;

import feign.Headers;
import feign.RequestLine;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;

public interface QxcioudSmsApi {
    @Data
    public static class SendSmsParamVo {
        private String appkey;
        private String appcode;
        private String timestamp;
        private String phone;
        private String msg;
        private String sign;
//        private String extend;
    }

    public static SendSmsParamVo CreateSendSmsParamVo(
            String appkey,
            String appsecret,
            String appcode,
            String phone,
            String msg
    ) {
        long currentTimeMillis = System.currentTimeMillis();
        String sign = DigestUtils.md5Hex(appkey + appsecret + currentTimeMillis);
        return new SendSmsParamVo() {{
            setAppkey(appkey);
            setAppcode(appcode);
            setPhone(phone);
            setMsg(msg);
            setSign(sign);
            setTimestamp(String.valueOf(currentTimeMillis));
        }};
    }

    @Data
    public static class SendSmsResultVo {
        // 返回码
        private String code;
        // 返回码描述
        private String desc;
        // 提交返回的唯一标识（32位字符串，用于获取短信回执）
        private String uid;
        // 提交短信的详细状态数据
        private List<SendSmsResultDetailVo> result;
    }
    @Data
    public static class SendSmsResultDetailVo {
        // 状态码
        private String status;
        // 状态码描述
        private String desc;
        // 手机号码
        private String phone;
    }

    @RequestLine("POST /sms/batch/v1")
    @Headers(value = {"Content-Type: application/json"})
    SendSmsResultVo send(SendSmsParamVo sendSmsParamVo) throws Exception;
}
