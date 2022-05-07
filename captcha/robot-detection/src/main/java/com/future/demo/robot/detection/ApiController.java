package com.future.demo.robot.detection;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;

@Controller
@RequestMapping(value="/api/v1/biz")
public class ApiController {
    @Autowired
    private CacheManagera cacheManager = null;

    private Cache cacheEnable = null;

    @PostConstruct
    public void init() {
        this.cacheEnable = this.cacheManager.getCache("cacheEnable");
    }

    @RequestMapping("test.do")
    public @ResponseBody AjaxResponse test() {
        AjaxResponse response = new AjaxResponse();
        response.setDataObject("接口调用成功");
        return response;
    }

    @RequestMapping("setEnable.do")
    public @ResponseBody AjaxResponse setEnable(
            @RequestParam(name = "enabled", required = false, defaultValue = "false") boolean enabled) {
        Element element = new Element("enabled", enabled);
        this.cacheEnable.put(element);
        AjaxResponse response = new AjaxResponse();
        response.setDataObject("设置成功");
        return response;
    }
}
