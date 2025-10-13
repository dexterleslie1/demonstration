package com.future.demo.service;

import com.future.common.exception.BusinessException;
import com.future.demo.entity.StorageFreeze;
import com.future.demo.mapper.StorageFreezeMapper;
import com.future.demo.mapper.StorageMapper;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class StorageTccActionImpl implements StorageTccAction {

    @Autowired
    StorageMapper storageMapper;
    @Autowired
    StorageFreezeMapper storageFreezeMapper;

    @Override
    // Try 方法需保证自身的本地事务实现，如通过 @Transactional 注解修饰。
    // 因为当前分支事务的 Try 操作出现异常，则回滚全局事务，触发其他分支（不包括当前 Try 操作异常的分支）事务的 Cancel 方法；
    @Transactional(rollbackFor = Exception.class)
    public void deduct(@BusinessActionContextParameter(paramName = "productId") Long productId,
                       @BusinessActionContextParameter(paramName = "count") int count) throws BusinessException {
        // 扣减库存
        String xid = RootContext.getXID();

        StorageFreeze storageFreeze = new StorageFreeze();
        storageFreeze.setXid(xid);
        storageFreeze.setProductId(productId);
        storageFreeze.setFreezeStock(count);
        storageFreezeMapper.insert(storageFreeze);
        // 扣减库存
        int affectRow = storageMapper.deduct(productId, count);
        if (affectRow == 0) {
            String errorMessage = String.format("商品 %d 库存不足导致扣减库存失败！", productId);
            throw new BusinessException(errorMessage);
        }
    }

    @Override
    public boolean confirm(BusinessActionContext context) {
        String xid = context.getXid();
        // 删除扣减库存
        int affectRow = storageFreezeMapper.delete(xid);
        return affectRow == 1;
    }

    @Override
    public boolean cancel(BusinessActionContext context) {
        String xid = context.getXid();
        StorageFreeze storageFreeze = storageFreezeMapper.getByXid(xid);

        Long productId = storageFreeze.getProductId();
        int count = storageFreeze.getFreezeStock();

        // 恢复库存
        storageMapper.refund(productId, count);

        // 将冻结金额修改为 0
        storageFreeze.setFreezeStock(0);
        int affectRow = storageFreezeMapper.update(storageFreeze);
        return affectRow == 1;
    }
}
