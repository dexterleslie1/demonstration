package com.future.demo.common.vo;

import lombok.Data;

@Data
public class RefreshTokenVo {
    // refreshToken为空表示refreshToken没有更新，保持旧的refreshToken
    private String refreshToken;
    private String accessToken;
}
