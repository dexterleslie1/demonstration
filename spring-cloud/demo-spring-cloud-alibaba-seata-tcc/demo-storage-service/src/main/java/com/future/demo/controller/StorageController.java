package com.future.demo.controller;

import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.mapper.StorageMapper;
import com.future.demo.service.StorageTccService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class StorageController {
    @Resource
    StorageTccService storageTccService;
    @Autowired
    StorageMapper storageMapper;

    @PutMapping("/storage/deduct")
    public ObjectResponse<String> deduct(@RequestParam("productId") Long productId,
                                         @RequestParam("amount") Integer amount) throws BusinessException {
        this.storageTccService.deduct(productId, amount);
        return ResponseUtils.successObject("成功扣减库存");
    }

    /**
     * 协助测试重置数据
     *
     * @return
     */
    @GetMapping("/storage/reset")
    public ObjectResponse<String> reset() {
        storageMapper.reset();
        return ResponseUtils.successObject("重置成功");
    }

    /**
     * 准备性能测试数据
     *
     * @return
     */
    @GetMapping("/storage/preparePerfTestDatum")
    ObjectResponse<String> preparePerfTestDatum() {
        storageTccService.preparePerfTestDatum();
        return ResponseUtils.successObject("准备成功");
    }
}
