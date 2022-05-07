package com.future.demo.robot.detection;

import com.ramostear.captcha.common.Fonts;
import com.ramostear.captcha.core.Captcha;
import com.ramostear.captcha.support.CaptchaType;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(value="/api/v1/captcha")
public class CaptchaController {

    @Autowired
    private CacheManagera cacheManager = null;

    private Cache cacheVerificationCode = null;
    private Cache cacheWhitelist = null;

    @PostConstruct
    public void init() {
        this.cacheVerificationCode = this.cacheManager.getCache("cacheVerificationCode");
        this.cacheWhitelist = this.cacheManager.getCache("cacheWhitelist");
    }

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
        Element element = new Element(clientId, captchaCode);
        this.cacheVerificationCode.put(element);

        String base64Str = captcha.toBase64();
        Map<String, String> mapReturn = new HashMap<>();
        mapReturn.put("imageBase64", base64Str);
        mapReturn.put("clientId", clientId);
        AjaxResponse response = new AjaxResponse();
        response.setDataObject(mapReturn);
        return response;
    }

    @RequestMapping("verify.do")
    public @ResponseBody AjaxResponse verify(HttpServletRequest request) {
        String clientId = request.getParameter("clientId");
        String code = request.getParameter("code");

        AjaxResponse response = new AjaxResponse();
        Element element = this.cacheVerificationCode.get(clientId);
        String codeStore = StringUtils.EMPTY;
        if(element != null) {
            codeStore = (String)element.getObjectValue();
        }
        if(!code.equalsIgnoreCase(codeStore)) {
            response.setErrorCode(50000);
            response.setErrorMessage("验证码错误");
        } else {
            String clientIp = RequestUtils.getRemoteAddress(request);
            element = new Element(clientIp, StringUtils.EMPTY);
            this.cacheWhitelist.put(element);

            Map<String, String> mapReturn = new HashMap<>();
            mapReturn.put("location", "/index.jsp");
            response.setDataObject(mapReturn);
        }
        return response;
    }
}
