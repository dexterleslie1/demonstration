package com.future.demo;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.MaxAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.MaxAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.List;

@Component
public class ConfigElasticsearchInit {

    @Resource
    ElasticsearchOperations elasticsearchOperations;

    @PostConstruct
    public void init() {
        IndexOperations indexOps = elasticsearchOperations.indexOps(ClothGoods.class);
        if (!indexOps.exists()) {
            indexOps.create();
            indexOps.putMapping();
        } else {
            // Elasticsearch 的 Put Mapping 会与现有 mapping 合并（可新增字段，不会改已有字段类型，也不会删数据），因此不会动到已有文档。
            indexOps.putMapping();
        }

        // 获取最大id
        Aggregation maxAggregation = Aggregation.of(a -> a
                .max(MaxAggregation.of(m -> m.field("goodsId"))));
        
        Query query = NativeQuery.builder()
                .withQuery(QueryBuilders.matchAll().build()._toQuery())
                .withAggregation("maxId", maxAggregation)
                .build();
        SearchHits<ClothGoods> searchHits = elasticsearchOperations.search(query, ClothGoods.class);
        if (searchHits.hasAggregations() && searchHits.getAggregations() != null) {
            org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations elasticsearchAggregations = 
                    (org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations) searchHits.getAggregations();
            List<org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation> aggregationsList = 
                    elasticsearchAggregations.aggregations();
            org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation maxIdAgg = aggregationsList.stream()
                    .filter(agg -> "maxId".equals(agg.aggregation().getName()))
                    .findFirst()
                    .orElse(null);
            if (maxIdAgg != null) {
                Aggregate aggregate = maxIdAgg.aggregation().getAggregate();
                if (aggregate.isMax()) {
                    MaxAggregate maxAgg = aggregate.max();
                    if (!Double.isInfinite(maxAgg.value()) && !Double.isNaN(maxAgg.value())) {
                        ClothGoodsController.idCounter.set((long) maxAgg.value() + 1);
                    }
                }
            }
        }
    }

}
