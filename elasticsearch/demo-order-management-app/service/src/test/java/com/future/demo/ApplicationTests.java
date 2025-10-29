package com.future.demo;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;
import com.future.demo.dto.OrderDTO;
import com.future.demo.entity.Status;
import com.future.demo.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
public class ApplicationTests {

    @Resource
    OrderService orderService;
    @Resource
    ElasticsearchClient client;

    @Test
    public void contextLoads() throws Exception {
        // 删除 t_order 和 t_order_detail 索引文档
        DeleteByQueryRequest deleteByQueryRequest = DeleteByQueryRequest.of(d -> d
                .index("t_order")
                .query(q -> q.matchAll(m -> m)));
        client.deleteByQuery(deleteByQueryRequest);
        deleteByQueryRequest = DeleteByQueryRequest.of(d -> d
                .index("t_order_detail")
                .query(q -> q.matchAll(m -> m)));
        client.deleteByQuery(deleteByQueryRequest);

        int batchSize = 1000;
        String articleSlice = "中华人民共和国";
        List<String> articleSliceList = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            articleSliceList.add(articleSlice);
        }

        // 插入 t_order 和 t_order_detail 数据
        Long userId = 1L;
        Long productId = 11L;
        Long merchantId = 111L;
        orderService.insertBatch(userId, productId, merchantId, articleSliceList.toArray(new String[0]), batchSize);
        TimeUnit.SECONDS.sleep(1);

        // 校验数据
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusMonths(1);
        List<OrderDTO> orderDTOList = orderService.listByUserIdAndWithoutStatus(userId, startTime, endTime);
        Assertions.assertEquals(batchSize, orderDTOList.size());

        orderDTOList = orderService.listByUserIdAndStatus(userId, Status.Unpay, startTime, endTime);
        Assertions.assertEquals(batchSize, orderDTOList.size());

        orderDTOList = orderService.listByMerchantIdAndWithoutStatus(merchantId, startTime, endTime);
        Assertions.assertEquals(batchSize, orderDTOList.size());

        orderDTOList = orderService.listByMerchantIdAndStatus(merchantId, Status.Unpay, startTime, endTime);
        Assertions.assertEquals(batchSize, orderDTOList.size());

        // 中文关键词和拼音搜索
        orderDTOList = orderService.listByKeyword("共和");
        Assertions.assertEquals(25, orderDTOList.size());
        orderDTOList = orderService.listByKeyword("中国");
        Assertions.assertEquals(25, orderDTOList.size());
        orderDTOList = orderService.listByKeyword("国人");
        Assertions.assertEquals(25, orderDTOList.size());
        orderDTOList = orderService.listByKeyword("华");
        Assertions.assertEquals(25, orderDTOList.size());
        orderDTOList = orderService.listByKeyword("座");
        Assertions.assertEquals(0, orderDTOList.size());
        orderDTOList = orderService.listByKeyword("zhongguo");
        Assertions.assertEquals(25, orderDTOList.size());
        orderDTOList = orderService.listByKeyword("zhon");
        Assertions.assertEquals(0, orderDTOList.size());
    }
}
