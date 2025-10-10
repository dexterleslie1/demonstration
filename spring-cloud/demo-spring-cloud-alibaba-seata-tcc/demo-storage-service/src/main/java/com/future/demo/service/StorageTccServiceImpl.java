package com.future.demo.service;

import com.future.common.exception.BusinessException;
import com.future.demo.entity.StorageFreeze;
import com.future.demo.entity.TccTransactionState;
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
public class StorageTccServiceImpl implements StorageTccService {

    @Autowired
    StorageMapper storageMapper;
    @Autowired
    StorageFreezeMapper storageFreezeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deduct(@BusinessActionContextParameter(paramName = "productId") Long productId,
                       @BusinessActionContextParameter(paramName = "count") int count) throws BusinessException {
        // 扣减库存和事务状态记录
        String xid = RootContext.getXID();

        // 避免业务悬挂
        StorageFreeze storageFreeze = storageFreezeMapper.getByXid(xid);
        if (storageFreeze != null) {
            return;
        }

        storageFreeze = new StorageFreeze();
        storageFreeze.setXid(xid);
        storageFreeze.setProductId(productId);
        storageFreeze.setFreezeStock(count);
        storageFreeze.setState(TccTransactionState.Try);
        int affectRow = storageFreezeMapper.insert(storageFreeze);
        // 幂等处理：事务之前没有尝试过锁定资源才扣减库存
        if (affectRow == 1) {
            // 扣减库存
            affectRow = storageMapper.deduct(productId, count);
            if (affectRow == 0) {
                String errorMessage = String.format("商品 %d 库存不足导致扣减库存失败！", productId);
                throw new BusinessException(errorMessage);
            }
        }
    }

    @Override
    public boolean confirm(BusinessActionContext context) {
        String xid = context.getXid();
        // 删除扣减库存和事务状态记录
        int affectRow = storageFreezeMapper.delete(xid);
        return affectRow == 1;
    }

    @Override
    public boolean cancel(BusinessActionContext context) {
        String xid = context.getXid();
        StorageFreeze storageFreeze = storageFreezeMapper.getByXid(xid);

        // 空回滚判断：deduct 中插入 storageFreeze 还没有执行就 Cancel 了
        if (storageFreeze == null) {
            Long productId = context.getActionContext("productId", Long.class);
            storageFreeze = new StorageFreeze();
            storageFreeze.setXid(xid);
            storageFreeze.setProductId(productId);
            storageFreeze.setFreezeStock(0);
            storageFreeze.setState(TccTransactionState.Cancel);
            int affectRow = storageFreezeMapper.insert(storageFreeze);
            return affectRow == 1;
        }

        // 幂等判断：之前已经 Cancel 过
        if (storageFreeze.getState() == TccTransactionState.Cancel) {
            return true;
        }

        Long productId = storageFreeze.getProductId();
        int count = storageFreeze.getFreezeStock();

        // 恢复库存
        storageMapper.refund(productId, count);

        // 将冻结金额修改为 0 ，事务状态修改为 Cancel
        storageFreeze.setFreezeStock(0);
        storageFreeze.setState(TccTransactionState.Cancel);
        int affectRow = storageFreezeMapper.update(storageFreeze);
        return affectRow == 1;
    }
}
