package com.future.demo.spring.cloud.feign.provider.controller;

import cn.hutool.core.util.RandomUtil;
import com.future.common.constant.ErrorCodeConstant;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.spring.cloud.feign.common.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/v1/product")
public class ApiController {

    private int totalKey = 100000;
    private List<String> keyList = new ArrayList<>();

    @PostConstruct
    public void init() {
        // 生成10w个随机key放到内中
        for (int i = 0; i < totalKey; i++) {
            String key = UUID.randomUUID().toString();
            keyList.add(key);
        }
    }

    @Value("${server.port}")
    private int port;

    // 测试@PathVariable
    @GetMapping("{productId}")
    public ObjectResponse<Product> info(
            @PathVariable("productId") Integer productId,
            @RequestHeader(value = "my-header", defaultValue = "") String myHeader,
            @RequestParam(value = "contextUserId", required = false) Long contextUserId) {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();
        String contextUserIdFromRequest = request.getParameter("contextUserId");

        log.info("my-headder={},contextUserId={},contextUserIdFromRequest={}", myHeader, contextUserId, contextUserIdFromRequest);
        Product product = new Product();
        product.setId(productId);
        product.setName("测试产品，端口：" + port);
        product.setPrice(12.33);
        ObjectResponse<Product> response = new ObjectResponse<>();
        response.setData(product);
        return response;
    }

    // 测试@RequestParam
    @GetMapping("get")
    public Product get(@RequestParam(value = "productId", required = false) Integer productId) {
        Product product = new Product();
        product.setId(productId);
        product.setName("测试产品1");
        product.setPrice(15.33);
        return product;
    }

    // 测试@RequestBody
    // {"id":30004,"name":"产品222","price":34.6665}
    @PostMapping("add")
    public String add(@RequestHeader(value = "customHeader") String customHeader,
                      @RequestBody(required = false) Product product) {
        log.info("customHeader=" + customHeader);
        if (product != null) {
            log.info(product.toString());
        }

        if (product != null) {
            return "新增成功";
        } else {
            return "新增失败";
        }
    }

    @GetMapping("timeout")
    public String timeout() throws InterruptedException {
        TimeUnit.SECONDS.sleep(62);
        return "成功调用";
    }

    @GetMapping("test401Error")
    public ResponseEntity<ObjectResponse<String>> test401Error() {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorCode(90000);
        response.setErrorMessage("调用 /api/v1/product/test401Error 失败");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * 用于协助 OpenFeign 性能测试
     *
     * @return
     */
    @GetMapping(value = "testOpenFeignPerfAssist")
    public ObjectResponse<String> testOpenFeignPerfAssist() {
        int randomInt = RandomUtil.randomInt(0, totalKey);
        String uuid = this.keyList.get(randomInt);
        String str = "UUID:" + uuid;
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData(str);
        return response;
    }

    /**
     * 协助测试非 http 200 响应
     *
     * @return
     */
    @GetMapping("testAssistantFeignErrorNonHttp200")
    public ResponseEntity<ObjectResponse<String>> testAssistantFeignErrorNonHttp200() {
        ObjectResponse<String> response = ResponseUtils.failObject(ErrorCodeConstant.ErrorCodeCommon, "协助测试非 http 200 响应");
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 协助测试 http 200 响应业务异常
     *
     * @return
     */
    @GetMapping("testAssistantFeignErrorHttp200WithBusinessException")
    public ObjectResponse<String> testAssistantFeignErrorHttp200WithBusinessException() {
        return ResponseUtils.failObject(ErrorCodeConstant.ErrorCodeCommon, "协助测试 http 200 响应业务异常");
    }
}
