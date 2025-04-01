package com.future.demo.controller;

import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.demo.service.StorageService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class StorageController {
    @Resource
    StorageService storageService;

    @PutMapping("/storage/deduct")
    public ObjectResponse<String> deduct(@RequestParam("productId") Long productId,
                                         @RequestParam("amount") Integer amount) throws BusinessException {
        this.storageService.deduct(productId, amount);
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("成功扣减库存");
        return response;
    }
}
