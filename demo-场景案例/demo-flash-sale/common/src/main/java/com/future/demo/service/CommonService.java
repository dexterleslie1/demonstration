package com.future.demo.service;

import cn.hutool.core.util.RandomUtil;
import com.future.demo.dto.IncreaseCountDTO;
import com.future.demo.mapper.CommonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommonService {
    /**
     * t_count 表中 flag 和分片总数对照表
     */
    private final Map<String, Integer> flagToTotalShardsMap = new HashMap<>() {{
        // 订单 t_count 分片总数为 256
        this.put("order", 256);
        this.put("product", 256);
        this.put("orderListByUserId", 256);
        this.put("orderListByMerchantId", 256);
    }};

    @Resource
    CommonMapper commonMapper;

//    /**
//     * @param idempotentId
//     * @param flag
//     * @param count
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public void updateIncreaseCount(String idempotentId, String flag, long count) {
//        if (!flagToTotalShardsMap.containsKey(flag)) {
//            throw new RuntimeException("意料之外，不包含 flag " + flag + "的分片总数配置信息");
//        }
//        int totalShards = flagToTotalShardsMap.get(flag);
//
//        int affectRow = this.commonMapper.insertCountIdempotent(idempotentId);
//        // 判断是否已经递增过，以免重复递增
//        if (affectRow > 0) {
//            // 随机抽取分片
//            int shard = RandomUtil.randomInt(1, totalShards + 1);
//            String flagShard = flag + shard;
//            this.commonMapper.updateIncreaseCount(flagShard, count);
//        } else {
//            if (log.isInfoEnabled())
//                log.info("idempotentId {} flag {} count {} 已经递增过，导致放弃此次递增", idempotentId, flag, count);
//        }
//    }

    /**
     * @param increaseCountDTOList
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateIncreaseCount(List<IncreaseCountDTO> increaseCountDTOList) {
        if (increaseCountDTOList == null || increaseCountDTOList.isEmpty())
            return;

        List<String> flagList = increaseCountDTOList.stream().map(IncreaseCountDTO::getFlag).distinct().toList();
        for (String flag : flagList) {
            if (!flagToTotalShardsMap.containsKey(flag)) {
                throw new RuntimeException("意料之外，不包含 flag " + flag + "的分片总数配置信息");
            }
        }

        // 按 flag 分组
        Map<String, List<IncreaseCountDTO>> flagToIncreaseCountDTOListMap = increaseCountDTOList.stream().collect(Collectors.groupingBy(IncreaseCountDTO::getFlag));
        for (String flag : flagToIncreaseCountDTOListMap.keySet()) {
            int totalShards = flagToTotalShardsMap.get(flag);
            List<IncreaseCountDTO> increaseCountDTOListInternal = flagToIncreaseCountDTOListMap.get(flag);

            List<String> idempotentIdList = increaseCountDTOListInternal.stream().map(IncreaseCountDTO::getIdempotentUuid).toList();
            int affectRow = this.commonMapper.insertCountIdempotentBatch(idempotentIdList);
            if (affectRow > 0) {
                // 随机抽取分片
                int shard = RandomUtil.randomInt(1, totalShards + 1);
                String flagShard = flag + shard;
                commonMapper.updateIncreaseCount(flagShard, affectRow);
            }
        }
    }

    /**
     * 根据 flag 查询计数器总数
     *
     * @param flag
     * @return
     */
    public Long getCountByFlag(String flag) {
        if (!flagToTotalShardsMap.containsKey(flag)) {
            throw new RuntimeException("意料之外，不包含 flag " + flag + "的分片总数配置信息");
        }
        int totalShards = flagToTotalShardsMap.get(flag);

        List<String> flagShardList = new ArrayList<>();
        for (int i = 0; i < totalShards; i++)
            flagShardList.add(flag + (i + 1));

        return this.commonMapper.getCountByFlag(flagShardList);
    }
}
