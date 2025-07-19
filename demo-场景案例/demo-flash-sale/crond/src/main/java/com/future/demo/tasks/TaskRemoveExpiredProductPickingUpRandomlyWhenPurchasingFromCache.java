package com.future.demo.tasks;

import com.future.demo.entity.ProductModel;
import com.future.demo.mapper.ProductMapper;
import com.future.demo.service.PickupProductRandomlyWhenPurchasingService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSet;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 从缓存中删除已经过期商品（下单时随机抽取）
 */
@Component
@Slf4j
public class TaskRemoveExpiredProductPickingUpRandomlyWhenPurchasingFromCache {
    @Resource
    ProductMapper productMapper;
    @Resource
    PickupProductRandomlyWhenPurchasingService pickupProductRandomlyWhenPurchasingService;

    @Scheduled(fixedRate = 120, initialDelay = 0, timeUnit = TimeUnit.SECONDS)
    public void execute() {
        RSet<Long> rSet = pickupProductRandomlyWhenPurchasingService.getRSetOrdinaryProductId();
        executeInternal(rSet);
        rSet = pickupProductRandomlyWhenPurchasingService.getRSetFlashSaleProductId();
        executeInternal(rSet);
    }

    void executeInternal(RSet<Long> rSet) {
        Set<Long> idSet = rSet.readAll();
        if (idSet.isEmpty())
            return;
        List<Long> idList = idSet.stream().toList();
        int fromIndex = 0;
        int size = 512;
        while (true) {
            int toIndex = fromIndex + size;
            if (toIndex > idList.size()) {
                toIndex = idList.size();
            }
            List<Long> idListSlice = idList.subList(fromIndex, toIndex);

            List<ProductModel> modelList = productMapper.list(idListSlice);
            List<Long> idListExpiredToRemove = new ArrayList<>();
            List<Long> idListFlashSaleEnded = new ArrayList<>();
            // 30分钟前
            LocalDateTime localDateTimeNow = LocalDateTime.now();
            LocalDateTime localDateTimeExpired = localDateTimeNow.minusMinutes(30);
            for (ProductModel model : modelList) {
                LocalDateTime createTime = model.getCreateTime();
                // 已经过期的商品
                if (createTime.isBefore(localDateTimeExpired)) {
                    idListExpiredToRemove.add(model.getId());
                }

                // 秒杀已经结束的商品
                if (model.isFlashSale()) {
                    LocalDateTime flashSaleEndTime = model.getFlashSaleEndTime();
                    if (flashSaleEndTime.isBefore(localDateTimeNow))
                        idListFlashSaleEnded.add(model.getId());
                }
            }
            if (!idListExpiredToRemove.isEmpty()) {
                rSet.removeAll(idListExpiredToRemove);
                if (log.isDebugEnabled()) {
                    log.debug("从 RSet {} 删除已经过期的商品id列表 {}", rSet.getName(), idListExpiredToRemove);
                }
            }
            if (!idListFlashSaleEnded.isEmpty()) {
                rSet.removeAll(idListFlashSaleEnded);
                if (log.isDebugEnabled()) {
                    log.debug("从 RSet {} 删除已经秒杀结束的商品id列表 {}", rSet.getName(), idListFlashSaleEnded);
                }
            }

            fromIndex = toIndex;
            if (toIndex >= idList.size())
                break;
        }
    }
}
