package com.future.demo.apns;

import com.fasterxml.jackson.databind.JsonNode;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface HuaweiOAuth2Api {
    // https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides-V5/open-platform-oauth-0000001053629189-V5#ZH-CN_TOPIC_0000001053629189__section12493191334711
    @RequestLine("POST /oauth2/v3/token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    JsonNode oAuth2GetTokenV3(@Param("grant_type") String grantType,
                              @Param("client_id") String clientId,
                              @Param("client_secret") String clientSecret);

    default JsonNode oAuth2GetTokenV3(String clientId, String clientSecret){
        return oAuth2GetTokenV3("client_credentials", clientId, clientSecret);
    }
}
