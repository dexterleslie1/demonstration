package com.future.demo.service;

import com.future.common.exception.BusinessException;
import com.future.demo.util.Util;
import com.future.demo.entity.Storage;
import com.future.demo.mapper.StorageMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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

    /**
     * 准备性能测试数据
     *
     * @return
     */
    @PostConstruct
    public void init() {
        for (int i = 0; i < Util.PerfTestDatumProductIdTotalCount; i++) {
            long productId = Util.PerfTestDatumProductIdStart + i;
            Storage storage = new Storage();
            storage.setProductId(productId);
            storage.setTotal(Util.PerfTestDatumProductStockAmount);
            storage.setUsed(0);
            storage.setResidue(storage.getTotal());
            storageMapper.deleteByProductId(productId);
            storageMapper.insert(storage);
        }
    }
}
