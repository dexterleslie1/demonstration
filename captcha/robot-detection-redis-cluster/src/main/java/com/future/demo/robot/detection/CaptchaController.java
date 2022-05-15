package com.future.demo.robot.detection;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.JedisCluster;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value="/api/v1/captcha")
public class CaptchaController {
    private final static int TimeoutInSeconds = 3600;

    @Autowired
    JedisCluster jedisCluster;
    @Autowired
    CaptchaCacheService captchaCacheService;

    @Autowired
    CacheManagera cacheManagera;

    /**
     * 随机给客户端分配captcha
     *
     * @return
     */
    @RequestMapping("get.do")
    public @ResponseBody AjaxResponse get() {
        CaptchaCacheService.ClientCaptchaEntry clientCaptchaEntry = this.captchaCacheService.assignClient();
        String clientId = clientCaptchaEntry.getClientId();
        String imageBase64 = StringUtils.EMPTY;
        if(clientCaptchaEntry.getEntry() != null) {
            imageBase64 = clientCaptchaEntry.getEntry().getImageBase64();
        }

        Map<String, String> mapReturn = new HashMap<>();
        mapReturn.put("imageBase64", imageBase64);
        mapReturn.put("clientId", clientId);
        AjaxResponse response = new AjaxResponse();
        response.setDataObject(mapReturn);
        return response;
    }

    /**
     * 验证客户端提供的captcha
     *
     * @param request
     * @return
     */
    @RequestMapping("verify.do")
    public @ResponseBody AjaxResponse verify(HttpServletRequest request) {
        String clientId = request.getParameter("clientId");
        String code = request.getParameter("code");
        String clientIp = RequestUtils.getRemoteAddress(request);

        AjaxResponse response = new AjaxResponse();
        try {
            this.captchaCacheService.verifyClient(clientId, clientIp, code);

            Map<String, String> mapReturn = new HashMap<>();
            mapReturn.put("location", "/index.jsp");
            response.setDataObject(mapReturn);
        } catch (Exception ex) {
            response.setErrorCode(50000);
            response.setErrorMessage(ex.getMessage());
        }
        return response;
    }
}
