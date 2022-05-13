package com.future.demo.robot.detection;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(value="/api/v1/captcha")
public class CaptchaController {

    private final static int TimeoutInSeconds = 3600;

    @Autowired
    JedisPool jedisPool = null;
    @Autowired
    CaptchaCacheService captchaCacheService;

    @RequestMapping("get.do")
    public @ResponseBody AjaxResponse get() throws IOException {
        String clientId = UUID.randomUUID().toString();

        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();

            CaptchaCacheService.CaptchaEntry entry = this.captchaCacheService.get();
            String code = StringUtils.EMPTY;
            String imageBase64 = StringUtils.EMPTY;
            if(entry != null) {
                code = entry.getCode();
                imageBase64 = entry.getImageBase64();
            }

            jedis.setex(clientId, TimeoutInSeconds, code);

            Map<String, String> mapReturn = new HashMap<>();
            mapReturn.put("imageBase64", imageBase64);
            mapReturn.put("clientId", clientId);
            AjaxResponse response = new AjaxResponse();
            response.setDataObject(mapReturn);
            return response;
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    @RequestMapping("verify.do")
    public @ResponseBody AjaxResponse verify(HttpServletRequest request) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();

            String clientId = request.getParameter("clientId");
            String code = request.getParameter("code");

            AjaxResponse response = new AjaxResponse();
            String codeStore = jedis.get(clientId);
            if (StringUtils.isEmpty(codeStore)) {
                codeStore = StringUtils.EMPTY;
            }
            if (!code.equalsIgnoreCase(codeStore)) {
                response.setErrorCode(50000);
                response.setErrorMessage("验证码错误");
            } else {
                String clientIp = RequestUtils.getRemoteAddress(request);
                String key = Const.CacheKeyPrefixWhitelist + clientIp;
                jedis.del(clientId);
                jedis.setex(key, TimeoutInSeconds, StringUtils.EMPTY);

                Map<String, String> mapReturn = new HashMap<>();
                mapReturn.put("location", "/index.jsp");
                response.setDataObject(mapReturn);
            }
            return response;
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }
}
