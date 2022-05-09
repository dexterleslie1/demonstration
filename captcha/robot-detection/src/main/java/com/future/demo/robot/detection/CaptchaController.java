package com.future.demo.robot.detection;

import com.ramostear.captcha.common.Fonts;
import com.ramostear.captcha.core.Captcha;
import com.ramostear.captcha.support.CaptchaType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(value="/api/v1/captcha")
public class CaptchaController {

    private final static int TimeoutInSeconds = 3600;

    @Autowired
    JedisPool jedisPool = null;

    private CaptchaType type = CaptchaType.DEFAULT;
    private Font font = Fonts.getInstance().defaultFont();
    private int width = 160;
    private int height = 50;
    private int length = 2;

    @RequestMapping("get.do")
    public @ResponseBody AjaxResponse get() {
        String clientId = UUID.randomUUID().toString();

        Captcha captcha = new Captcha();
        captcha.setType(this.type);
        captcha.setWidth(this.width);
        captcha.setHeight(this.height);
        captcha.setLength(this.length);
        captcha.setFont(this.font);

        String captchaCode = captcha.getCaptchaCode();
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();

            jedis.setex(clientId, TimeoutInSeconds, captchaCode);

            String base64Str = captcha.toBase64();
            Map<String, String> mapReturn = new HashMap<>();
            mapReturn.put("imageBase64", base64Str);
            mapReturn.put("clientId", clientId);
            AjaxResponse response = new AjaxResponse();
            response.setDataObject(mapReturn);
            return response;
        } finally {
            if(jedis != null) {
                jedis.close();
                jedis = null;
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
                jedis.setex(key, TimeoutInSeconds, StringUtils.EMPTY);

                Map<String, String> mapReturn = new HashMap<>();
                mapReturn.put("location", "/index.jsp");
                response.setDataObject(mapReturn);
            }
            return response;
        } finally {
            if(jedis != null) {
                jedis.close();
                jedis = null;
            }
        }
    }
}
