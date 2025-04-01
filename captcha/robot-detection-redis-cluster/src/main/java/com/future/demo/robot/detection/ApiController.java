package com.future.demo.robot.detection;

import com.dex.hm.common.utils.web.RequestUtils;
import com.yyd.common.ddos.AbstractCaptchaCacheService;
import com.yyd.common.http.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value="/api/v1/biz")
public class ApiController {

    @Autowired
    AbstractCaptchaCacheService captchaCacheService;

    @RequestMapping("test.do")
    public @ResponseBody
    AjaxResponse test() {
        AjaxResponse response = new AjaxResponse();
        response.setDataObject("接口调用成功");
        return response;
    }

    @RequestMapping("setEnable.do")
    public @ResponseBody
    AjaxResponse setEnable(
            @RequestParam(name = "enabled", required = false, defaultValue = "false") boolean enabled) {
        this.captchaCacheService.setEnable(enabled);

        AjaxResponse response = new AjaxResponse();
        response.setDataObject("设置成功");
        return response;
    }

    @RequestMapping("removeWhitelist.do")
    public @ResponseBody
    AjaxResponse removeWhitelist(HttpServletRequest request) {
        String clientIp = RequestUtils.getRemoteAddress(request);
        this.captchaCacheService.removeWhitelist(clientIp);

        AjaxResponse response = new AjaxResponse();
        response.setDataObject("调用成功");
        return response;
    }
}
