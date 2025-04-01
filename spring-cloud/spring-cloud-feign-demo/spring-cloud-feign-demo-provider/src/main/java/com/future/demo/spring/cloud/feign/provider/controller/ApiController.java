package com.future.demo.spring.cloud.feign.provider.controller;

import com.future.common.http.ObjectResponse;
import com.future.demo.spring.cloud.feign.common.entity.Product;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/v1/product")
public class ApiController {
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
}
