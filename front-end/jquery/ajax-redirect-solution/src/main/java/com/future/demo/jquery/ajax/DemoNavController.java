package com.future.demo.jquery.ajax;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DemoNavController {
    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/robotDetection")
    public String robotDetection() {
        return "robot_detection";
    }

    @RequestMapping("/redirect")
    public String redirect() {
        return "redirect:/robotDetection";
    }

    /**
     * 模拟调用数据接口时被拦截返回自定义重定向JSON数据
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/api/v1/redirectJson")
    public @ResponseBody Map<String, String> redirectJson(
            HttpServletRequest request,
            HttpServletResponse response) {
        Map<String, String> params = new HashMap<>();
        params.put("location", "/robotDetection");
        return params;
    }
}
