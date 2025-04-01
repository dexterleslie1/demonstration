package com.future.demo;

import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class Tests {
    @Test
    public void test() throws Exception {
        QxcioudSmsApi api = Feign.builder()
                // https://stackoverflow.com/questions/56987701/feign-client-retry-on-exception
                .retryer(Retryer.NEVER_RETRY)
                // https://qsli.github.io/2020/04/28/feign-method-timeout/
                .options(new Request.Options(15, TimeUnit.SECONDS, 15, TimeUnit.SECONDS, false))
                .encoder(new FormEncoder(new JacksonEncoder()))
                .decoder(new JacksonDecoder())
                // feign logger
                // https://cloud.tencent.com/developer/article/1588501
                .logger(new Logger.ErrorLogger()).logLevel(Logger.Level.FULL)
                .target(QxcioudSmsApi.class, "http://sms.qxcioud.com:9090");

        String appkey = "xxx";
        String appsecret = "xxx";
        String appcode = "1000";
        String phone = "+8613560189480";
//        String phone = "+8618129898010";
        // 联通
//        String phone = "+8616624793246";
        // 电信
//        String phone = "+8618902706107";
        // 香港
//        String phone = "+85251744943";
        String msg = "【聊天App】验证码：123456。有效期120秒，该验证码仅用于身份验证，勿泄露给他人使用";
        QxcioudSmsApi.SendSmsResultVo sendSmsResultVo=
                api.send(QxcioudSmsApi.CreateSendSmsParamVo(appkey, appsecret, appcode, phone, msg));
        if(!sendSmsResultVo.getCode().equals("00000")) {
            throw new Exception(sendSmsResultVo.getDesc());
        }
        System.out.println(sendSmsResultVo);
    }
}
