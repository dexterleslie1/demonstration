package com.future.demo;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.Max;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

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
        Query query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .addAggregation(AggregationBuilders.max("maxId").field("goodsId"))
                .build();
        SearchHits<ClothGoods> searchHits = elasticsearchOperations.search(query, ClothGoods.class);
        if (searchHits.hasAggregations() && searchHits.getAggregations() != null) {
            org.elasticsearch.search.aggregations.Aggregations aggs =
                    (org.elasticsearch.search.aggregations.Aggregations) searchHits.getAggregations().aggregations();
            Max maxAgg = aggs.get("maxId");
            if (maxAgg != null && !Double.isInfinite(maxAgg.getValue()) && !Double.isNaN(maxAgg.getValue())) {
                ClothGoodsController.idCounter.set((long) maxAgg.getValue() + 1);
            }
        }
    }

}
