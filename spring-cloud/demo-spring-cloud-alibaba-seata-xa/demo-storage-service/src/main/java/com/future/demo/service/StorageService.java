package com.future.demo.service;

import com.future.common.exception.BusinessException;
import com.future.demo.mapper.StorageMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class StorageService {
    @Resource
    StorageMapper storageMapper;

    public void deduct(Long productId, int count) throws BusinessException {
        int affectRow = storageMapper.deduct(productId, count);
        if (affectRow == 0) {
            String errorMessage = String.format("商品 %d 库存不足导致扣减库存失败！", productId);
            throw new BusinessException(errorMessage);
        }
    }
}
