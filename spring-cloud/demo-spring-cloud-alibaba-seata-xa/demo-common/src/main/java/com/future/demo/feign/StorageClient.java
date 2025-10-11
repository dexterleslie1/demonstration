package com.future.demo.feign;

import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("demo-storage-service")
public interface StorageClient {
    @PutMapping("/storage/deduct")
    ObjectResponse<String> deduct(@RequestParam("productId") Long productId,
                                  @RequestParam("amount") Integer amount) throws BusinessException;

    @GetMapping("/storage/reset")
    ObjectResponse<String> reset() throws BusinessException;

    @GetMapping("/storage/preparePerfTestDatum")
    ObjectResponse<String> preparePerfTestDatum() throws BusinessException;
}
