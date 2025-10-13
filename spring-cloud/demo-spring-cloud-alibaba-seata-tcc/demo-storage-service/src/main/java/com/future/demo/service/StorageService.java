package com.future.demo.service;

import com.future.demo.entity.Storage;
import com.future.demo.mapper.StorageFreezeMapper;
import com.future.demo.mapper.StorageMapper;
import com.future.demo.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class StorageService {

    @Autowired
    StorageMapper storageMapper;
    @Autowired
    StorageFreezeMapper storageFreezeMapper;

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
