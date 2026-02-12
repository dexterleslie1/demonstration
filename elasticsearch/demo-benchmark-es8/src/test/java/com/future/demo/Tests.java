package com.future.demo;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQueryBuilders;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// todo CriteriaQuery
// todo 聚合查询
@SpringBootTest(
        classes = {Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
public class Tests {

    @Resource
    ElasticsearchOperations elasticsearchOperations;

    @Test
    public void testClothGoods() {
        // 删除之前的数据
        Query deleteQuery = NativeQuery.builder()
                .withQuery(QueryBuilders.matchAll().build()._toQuery())
                .build();
        DeleteQuery deleteQueryObj = DeleteQuery.builder(deleteQuery).build();
        this.elasticsearchOperations.delete(deleteQueryObj, ClothGoods.class);
        this.elasticsearchOperations.indexOps(ClothGoods.class).refresh();

        /*------------------------------- 新增产品文档 */
        ClothGoods clothGoods1 = new ClothGoods();
        clothGoods1.setId("1");
        clothGoods1.setGoodsId(1L);
        clothGoods1.setCompanyId(100L);
        clothGoods1.setType("cp");
        clothGoods1.setName("纯棉T恤");
        clothGoods1.setNumber("CP001");
        IndexQuery indexQuery = new IndexQueryBuilder().withObject(clothGoods1).build();
        String resultStr = this.elasticsearchOperations.index(indexQuery, IndexCoordinates.of("cloth_goods"));
        Assertions.assertEquals("1", resultStr);

        // 刷新索引马上写入es，以便后续search能够查询到数据
        this.elasticsearchOperations.indexOps(ClothGoods.class).refresh();

        // 根据id验证文档是否新增成功
        co.elastic.clients.elasticsearch._types.query_dsl.Query idsQuery = QueryBuilders.ids().values("1").build()._toQuery();
        Query searchQuery = NativeQuery.builder().withQuery(idsQuery).build();
        SearchHits<ClothGoods> searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        List<ClothGoods> clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(1, clothGoodsList.size());
        Assertions.assertEquals(1L, clothGoodsList.get(0).getGoodsId());

        /*------------------------------- 批量新增产品文档 */
        List<IndexQuery> indexQueryList = new ArrayList<>();
        ClothGoods clothGoods2 = new ClothGoods();
        clothGoods2.setId("2");
        clothGoods2.setGoodsId(2L);
        clothGoods2.setCompanyId(100L);
        clothGoods2.setType("cp");
        clothGoods2.setName("纯棉衬衫");
        clothGoods2.setNumber("CP002");
        indexQueryList.add(new IndexQueryBuilder().withObject(clothGoods2).build());

        ClothGoods clothGoods3 = new ClothGoods();
        clothGoods3.setId("3");
        clothGoods3.setGoodsId(3L);
        clothGoods3.setCompanyId(200L);
        clothGoods3.setType("pb");
        clothGoods3.setName("坯布A");
        clothGoods3.setNumber("PB001");
        indexQueryList.add(new IndexQueryBuilder().withObject(clothGoods3).build());

        ClothGoods clothGoods4 = new ClothGoods();
        clothGoods4.setId("4");
        clothGoods4.setGoodsId(4L);
        clothGoods4.setCompanyId(200L);
        clothGoods4.setType("cp");
        clothGoods4.setName("纯棉短裤");
        clothGoods4.setNumber("CP003");
        indexQueryList.add(new IndexQueryBuilder().withObject(clothGoods4).build());

        this.elasticsearchOperations.bulkIndex(indexQueryList, ClothGoods.class);
        this.elasticsearchOperations.indexOps(ClothGoods.class).refresh();

        searchQuery = NativeQuery.builder().withQuery(QueryBuilders.matchAll().build()._toQuery()).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(4, clothGoodsList.size());

        /*------------------------------- 根据goodsId字段查询 */
        searchQuery = NativeQuery.builder().withQuery(QueryBuilders.term().field("goodsId").value(1L).build()._toQuery()).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(1, clothGoodsList.size());
        Assertions.assertEquals(1L, clothGoodsList.get(0).getGoodsId());
        Assertions.assertEquals("CP001", clothGoodsList.get(0).getNumber());

        // 根据多个id查询
        idsQuery = QueryBuilders.ids().values("1", "2", "3").build()._toQuery();
        searchQuery = NativeQuery.builder().withQuery(idsQuery).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(3, clothGoodsList.size());

        /*------------------------------- 根据companyId字段查询 */
        // 精确查询companyId=100
        searchQuery = NativeQuery.builder().withQuery(QueryBuilders.term().field("companyId").value(100L).build()._toQuery()).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(2, clothGoodsList.size());
        Assertions.assertTrue(clothGoodsList.stream().allMatch(item -> item.getCompanyId().equals(100L)));

        // 范围查询companyId >= 200
        searchQuery = NativeQuery.builder().withQuery(RangeQueryBuilders.number().field("companyId").gte(200.0).build()._toRangeQuery()._toQuery()).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(2, clothGoodsList.size());
        Assertions.assertTrue(clothGoodsList.stream().allMatch(item -> item.getCompanyId() >= 200L));

        // 范围查询companyId在100到200之间（包含边界）
        searchQuery = NativeQuery.builder().withQuery(RangeQueryBuilders.number().field("companyId")
                .gte(100.0)
                .lte(200.0)
                .build()._toRangeQuery()._toQuery()).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(4, clothGoodsList.size());

        /*------------------------------- 根据type字段查询 */
        // 查询type='cp'的产品
        searchQuery = NativeQuery.builder().withQuery(QueryBuilders.term().field("type").value("cp").build()._toQuery()).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(3, clothGoodsList.size());
        Assertions.assertTrue(clothGoodsList.stream().allMatch(item -> "cp".equals(item.getType())));

        // 查询type='pb'的产品
        searchQuery = NativeQuery.builder().withQuery(QueryBuilders.term().field("type").value("pb").build()._toQuery()).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(1, clothGoodsList.size());
        Assertions.assertEquals("pb", clothGoodsList.get(0).getType());
        Assertions.assertEquals("PB001", clothGoodsList.get(0).getNumber());

        // 查询多个type值
        co.elastic.clients.elasticsearch._types.query_dsl.Query boolQuery = QueryBuilders.bool()
                .should(QueryBuilders.term().field("type").value("cp").build()._toQuery())
                .should(QueryBuilders.term().field("type").value("pb").build()._toQuery())
                .build()._toQuery();
        searchQuery = NativeQuery.builder().withQuery(boolQuery).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(4, clothGoodsList.size());

        /*------------------------------- 根据name字段查询 */
        // 精确查询name='纯棉T恤'
        searchQuery = NativeQuery.builder().withQuery(QueryBuilders.term().field("name").value("纯棉T恤").build()._toQuery()).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(1, clothGoodsList.size());
        Assertions.assertEquals("纯棉T恤", clothGoodsList.get(0).getName());

        // 查询name='纯棉衬衫'
        searchQuery = NativeQuery.builder().withQuery(QueryBuilders.term().field("name").value("纯棉衬衫").build()._toQuery()).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(1, clothGoodsList.size());
        Assertions.assertEquals("纯棉衬衫", clothGoodsList.get(0).getName());

        // 查询name='坯布A'
        searchQuery = NativeQuery.builder().withQuery(QueryBuilders.term().field("name").value("坯布A").build()._toQuery()).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(1, clothGoodsList.size());
        Assertions.assertEquals("坯布A", clothGoodsList.get(0).getName());

        // 查询多个name值
        boolQuery = QueryBuilders.bool()
                .should(QueryBuilders.term().field("name").value("纯棉T恤").build()._toQuery())
                .should(QueryBuilders.term().field("name").value("纯棉衬衫").build()._toQuery())
                .build()._toQuery();
        searchQuery = NativeQuery.builder().withQuery(boolQuery).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(2, clothGoodsList.size());

        // 查询name包含"棉"的产品（使用wildcard通配符查询）
        searchQuery = NativeQuery.builder().withQuery(QueryBuilders.wildcard().field("name").value("*棉*").build()._toQuery()).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(3, clothGoodsList.size());
        Assertions.assertTrue(clothGoodsList.stream().allMatch(item -> item.getName().contains("棉")));

        /*------------------------------- 根据number字段查询 */
        // 精确查询number='CP001'
        searchQuery = NativeQuery.builder().withQuery(QueryBuilders.term().field("number").value("CP001").build()._toQuery()).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(1, clothGoodsList.size());
        Assertions.assertEquals("CP001", clothGoodsList.get(0).getNumber());
        Assertions.assertEquals(1L, clothGoodsList.get(0).getGoodsId());

        // 查询number='CP002'
        searchQuery = NativeQuery.builder().withQuery(QueryBuilders.term().field("number").value("CP002").build()._toQuery()).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(1, clothGoodsList.size());
        Assertions.assertEquals("CP002", clothGoodsList.get(0).getNumber());

        // 查询number='PB001'
        searchQuery = NativeQuery.builder().withQuery(QueryBuilders.term().field("number").value("PB001").build()._toQuery()).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(1, clothGoodsList.size());
        Assertions.assertEquals("PB001", clothGoodsList.get(0).getNumber());

        // 查询number以'CP'开头的产品（使用prefix查询）
        searchQuery = NativeQuery.builder().withQuery(QueryBuilders.prefix().field("number").value("CP").build()._toQuery()).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(3, clothGoodsList.size());
        Assertions.assertTrue(clothGoodsList.stream().allMatch(item -> item.getNumber().startsWith("CP")));

        // 查询多个number值
        boolQuery = QueryBuilders.bool()
                .should(QueryBuilders.term().field("number").value("CP001").build()._toQuery())
                .should(QueryBuilders.term().field("number").value("PB001").build()._toQuery())
                .build()._toQuery();
        searchQuery = NativeQuery.builder().withQuery(boolQuery).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(2, clothGoodsList.size());

        // 查询number包含"P00"的产品（使用wildcard通配符查询）
        searchQuery = NativeQuery.builder().withQuery(QueryBuilders.wildcard().field("number").value("*P00*").build()._toQuery()).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(3, clothGoodsList.size());
        Assertions.assertTrue(clothGoodsList.stream().allMatch(item -> item.getNumber().contains("P00")));

        /*------------------------------- 多字段组合查询 */
        // 查询companyId=100且type='cp'的产品
        boolQuery = QueryBuilders.bool()
                .must(QueryBuilders.term().field("companyId").value(100L).build()._toQuery())
                .must(QueryBuilders.term().field("type").value("cp").build()._toQuery())
                .build()._toQuery();
        searchQuery = NativeQuery.builder().withQuery(boolQuery).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(2, clothGoodsList.size());
        Assertions.assertTrue(clothGoodsList.stream().allMatch(item -> item.getCompanyId().equals(100L) && "cp".equals(item.getType())));

        // 查询companyId=200或type='pb'的产品
        boolQuery = QueryBuilders.bool()
                .should(QueryBuilders.term().field("companyId").value(200L).build()._toQuery())
                .should(QueryBuilders.term().field("type").value("pb").build()._toQuery())
                .build()._toQuery();
        searchQuery = NativeQuery.builder().withQuery(boolQuery).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(2, clothGoodsList.size());

        /*------------------------------- 分页和排序查询 */
        searchQuery = NativeQuery.builder()
                .withPageable(PageRequest.of(0, 2))
                .withSort(Sort.by(Sort.Direction.ASC, "goodsId"))
                .build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(2, clothGoodsList.size());
        Assertions.assertEquals(1L, clothGoodsList.get(0).getGoodsId());
        Assertions.assertEquals(2L, clothGoodsList.get(1).getGoodsId());

        // 查询第二页
        searchQuery = NativeQuery.builder()
                .withPageable(PageRequest.of(1, 2))
                .withSort(Sort.by(Sort.Direction.ASC, "goodsId"))
                .build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(2, clothGoodsList.size());
        Assertions.assertEquals(3L, clothGoodsList.get(0).getGoodsId());
        Assertions.assertEquals(4L, clothGoodsList.get(1).getGoodsId());

        // 按companyId降序排序
        searchQuery = NativeQuery.builder()
                .withSort(Sort.by(Sort.Direction.DESC, "companyId"))
                .build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(4, clothGoodsList.size());
        Assertions.assertTrue(clothGoodsList.get(0).getCompanyId() >= clothGoodsList.get(1).getCompanyId());

        /*------------------------------- 修改产品文档，实质是创建文档操作，只要id一样就会替换文档 */
        ClothGoods clothGoods1Update = new ClothGoods();
        clothGoods1Update.setId("1");
        clothGoods1Update.setGoodsId(1L);
        clothGoods1Update.setCompanyId(100L);
        clothGoods1Update.setType("cp");
        clothGoods1Update.setName("纯棉T恤升级版");
        clothGoods1Update.setNumber("CP001");
        indexQuery = new IndexQueryBuilder().withObject(clothGoods1Update).build();
        resultStr = this.elasticsearchOperations.index(indexQuery, IndexCoordinates.of("cloth_goods"));
        Assertions.assertEquals("1", resultStr);

        this.elasticsearchOperations.indexOps(ClothGoods.class).refresh();

        idsQuery = QueryBuilders.ids().values("1").build()._toQuery();
        searchQuery = NativeQuery.builder().withQuery(idsQuery).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(1, clothGoodsList.size());
        Assertions.assertEquals("纯棉T恤升级版", clothGoodsList.get(0).getName());

        /*------------------------------- 根据id删除产品文档 */
        resultStr = this.elasticsearchOperations.delete("4", ClothGoods.class);
        Assertions.assertEquals("4", resultStr);

        this.elasticsearchOperations.indexOps(ClothGoods.class).refresh();

        searchQuery = NativeQuery.builder().withQuery(QueryBuilders.matchAll().build()._toQuery()).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(3, clothGoodsList.size());

        /*------------------------------- 根据多个id批量删除产品文档 */
        idsQuery = QueryBuilders.ids().values("2", "3").build()._toQuery();
        searchQuery = NativeQuery.builder().withQuery(idsQuery).build();
        DeleteQuery deleteQuery1 = DeleteQuery.builder(searchQuery).build();
        this.elasticsearchOperations.delete(deleteQuery1, ClothGoods.class);

        this.elasticsearchOperations.indexOps(ClothGoods.class).refresh();

        searchQuery = NativeQuery.builder().withQuery(QueryBuilders.matchAll().build()._toQuery()).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(1, clothGoodsList.size());
        Assertions.assertEquals(1L, clothGoodsList.get(0).getGoodsId());

        /*------------------------------- 根据自定义query删除产品数据 */
        searchQuery = NativeQuery.builder().withQuery(QueryBuilders.term().field("goodsId").value(1L).build()._toQuery()).build();
        deleteQuery1 = DeleteQuery.builder(searchQuery).build();
        this.elasticsearchOperations.delete(deleteQuery1, ClothGoods.class);

        this.elasticsearchOperations.indexOps(ClothGoods.class).refresh();

        searchQuery = NativeQuery.builder().withQuery(QueryBuilders.matchAll().build()._toQuery()).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, ClothGoods.class);
        clothGoodsList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(0, clothGoodsList.size());
    }

}
