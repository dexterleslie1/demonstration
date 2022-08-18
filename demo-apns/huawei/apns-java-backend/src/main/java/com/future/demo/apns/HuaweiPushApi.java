package com.future.demo.apns;

import com.fasterxml.jackson.databind.JsonNode;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface HuaweiPushApi {
    // https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides-V5/android-server-dev-0000001050040110-V5
    @RequestLine("POST /v1/{clientId}/messages:send")
    @Headers({
            "Content-Type: application/json; charset=UTF-8",
            "Authorization: Bearer {accessToken}"
    })
    JsonNode sendMessage(@Param("accessToken") String accessToken,
                         @Param("clientId") String clientId,
                         JsonNode message);
}
