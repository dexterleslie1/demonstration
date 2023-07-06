package com.future.demo;

import com.yyd.common.http.ResponseUtils;
import com.yyd.common.http.response.ObjectResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Dexterleslie.Chan
 */
@RestController
@RequestMapping(value = "/api/v1")
// 启用springboot validation
@Validated
public class ApiController {

    /**
     * @param p1
     * @return
     */
    @GetMapping(value = "testSingleParam")
    // springboot validation i18n
    // https://zhuanlan.zhihu.com/p/451266561
    // 1、
    public ObjectResponse<String> testSingleParam(@NotNull(message = "{param.required.p1}")
                                                  @NotBlank(message = "{param.required.p1}")
                                                  @RequestParam(value = "p1", defaultValue = "") String p1) {
        return ResponseUtils.successObject("成功调用");
    }
}
