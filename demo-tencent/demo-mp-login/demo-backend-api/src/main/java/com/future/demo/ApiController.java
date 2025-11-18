package com.future.demo;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@PropertySource("file:${user.home}/wx.properties")
public class ApiController {

    @Value("${wx.appId}")
    private String appId;
    @Value("${wx.appSecret}")
    private String appSecret;

    /**
     * 授权码获取openid和session_key
     *
     * @param code
     * @return
     */
    @GetMapping("loginWithWXCode")
    public ResponseEntity<JSONObject> loginWithWXCode(@RequestParam(value = "code", defaultValue = "") String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" + appSecret + "&grant_type=authorization_code" + "&js_code=" + code;
        String json = HttpUtil.get(url);
        JSONObject jsonObject = JSONUtil.toBean(json, JSONObject.class);
        int errCode = jsonObject.containsKey("errcode") ? jsonObject.getInt("errcode") : 0;
        if (errCode == 0) {
            // 请求微信服务器没有错误，则执行自定义后续登录逻辑
        }
        return ResponseEntity.ok(jsonObject);
    }
}
