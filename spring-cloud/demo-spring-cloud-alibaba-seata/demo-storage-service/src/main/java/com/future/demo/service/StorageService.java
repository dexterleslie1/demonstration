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
        int returnCount = storageMapper.deduct(productId, count);
        if (returnCount <= 0) {
            throw new BusinessException("扣减库存失败");
        }
    }
}
