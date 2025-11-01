package com.future.demo;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    /**
     * 协助测试ServletUtil
     *
     * @param response
     */
    @PostMapping("test1")
    public void test1(HttpServletResponse response) {
        // 使用ServletUtil往HttpServletResponse写响应数据
        ObjectResponse<String> objectResponse = ResponseUtils.failObject(90001, "测试失败");
        String json = JSONUtil.toJsonStr(objectResponse);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ServletUtil.write(response, json, MediaType.APPLICATION_JSON_UTF8_VALUE);
    }
}
