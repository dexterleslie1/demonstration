package com.future.demo;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.WildcardQuery;
import jakarta.annotation.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ClothGoodsController {

    @Resource
    private ElasticsearchOperations elasticsearchOperations;

    public static final AtomicLong idCounter = new AtomicLong(1);
    private static final Random random = new Random(System.currentTimeMillis());
    private static final String[] TYPES = {"cp", "pb"};

    @GetMapping("goods/generate")
    public String generateAndInsert() {
        try {
            // 生成1000个随机产品
            List<IndexQuery> indexQueryList = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                ClothGoods clothGoods = generateRandomClothGoods();
                IndexQuery indexQuery = new IndexQueryBuilder().withObject(clothGoods).build();
                indexQueryList.add(indexQuery);
            }

            // 批量插入到ES
            elasticsearchOperations.bulkIndex(indexQueryList, ClothGoods.class);

            return "成功生成并插入1000个产品到ES";
        } catch (Exception e) {
            return "插入失败: " + e.getMessage();
        }
    }

    private ClothGoods generateRandomClothGoods() {
        ClothGoods clothGoods = new ClothGoods();

        // 使用 AtomicLong 计数器递增生成 ID
        long goodsId = idCounter.getAndIncrement();
        clothGoods.setId(String.valueOf(goodsId));
        clothGoods.setGoodsId(goodsId);

        // 随机企业ID（1-10）
        clothGoods.setCompanyId((long) (1 + random.nextInt(11)));

        // 随机类型（cp或pb）
        clothGoods.setType(TYPES[random.nextInt(TYPES.length)]);

        // 生成编号：根据类型生成CP或PB开头的编号
        String prefix = clothGoods.getType().equals("cp") ? "CP" : "PB";
        String suffix = String.format("%09d", goodsId);
        String number = String.format("%s%s", prefix, suffix);
        clothGoods.setNumber(number);

        // 产品名称：产品000000001、产品000000002格式
        String name = String.format("%s%s", "产品", suffix);
        clothGoods.setName(name);
        clothGoods.setNameWildcard(name);

        return clothGoods;
    }

    /**
     * 查看当前counter
     *
     * @return
     */
    @GetMapping("goods/counter")
    public String getCounter() {
        return "当前counter为" + idCounter.get();
    }

    /**
     * 性能测试接口：根据companyId和type查询前100条产品数据（根据goodsId降序排序）
     * companyId随机生成，type随机抽取
     *
     * @return 产品列表
     */
    @GetMapping("goods/queryByCompanyIdAndType")
    public List<ClothGoods> queryByCompanyIdAndType() {
        // 随机生成企业ID（1-11）
        Long companyId = (long) (1 + random.nextInt(11));
        
        // 随机抽取类型（cp或pb）
        String type = TYPES[random.nextInt(TYPES.length)];
        
        // 构建查询条件
        co.elastic.clients.elasticsearch._types.query_dsl.Query boolQuery = QueryBuilders.bool()
                .must(QueryBuilders.term().field("companyId").value(companyId).build()._toQuery())
                .must(QueryBuilders.term().field("type").value(type).build()._toQuery())
                .build()._toQuery();

        // 构建查询，按goodsId降序排序，限制100条
        Query searchQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(0, 100))
                .withSort(Sort.by(Sort.Direction.DESC, "goodsId"))
                .build();

        // 执行查询
        SearchHits<ClothGoods> searchHits = elasticsearchOperations.search(searchQuery, ClothGoods.class);

        // 转换为列表返回
        return searchHits.stream()
                .map(org.springframework.data.elasticsearch.core.SearchHit::getContent)
                .collect(Collectors.toList());
    }

    /**
     * 性能测试接口：根据companyId和name（wildcard查询）查询前100条产品数据（根据goodsId降序排序）
     * companyId随机生成，name使用wildcard查询模式
     *
     * @return 产品列表
     */
    @GetMapping("goods/queryByCompanyIdAndNameWildcard")
    public List<ClothGoods> queryByCompanyIdAndNameWildcard() {
        // 随机生成企业ID（1-11）
        Long companyId = (long) (1 + random.nextInt(11));

        // "*" + 随机9位数字 + "*" - 匹配包含特定数字的
        int randomNum = random.nextInt((int)idCounter.get());
        String namePattern = "*" + String.format("%09d", randomNum) + "*";
        
        // 构建查询条件
        co.elastic.clients.elasticsearch._types.query_dsl.Query boolQuery = QueryBuilders.bool()
                .must(QueryBuilders.term().field("companyId").value(companyId).build()._toQuery())
                .must(QueryBuilders.wildcard().field("name").value(namePattern).build()._toQuery())
                .build()._toQuery();

        // 构建查询，按goodsId降序排序，限制100条
        Query searchQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(0, 100))
                .withSort(Sort.by(Sort.Direction.DESC, "goodsId"))
                .build();

        // 执行查询
        SearchHits<ClothGoods> searchHits = elasticsearchOperations.search(searchQuery, ClothGoods.class);

        // 转换为列表返回
        return searchHits.stream()
                .map(org.springframework.data.elasticsearch.core.SearchHit::getContent)
                .collect(Collectors.toList());
    }

    /**
     * 性能测试接口：根据companyId和nameWildcard（wildcard类型字段）通配符查询前100条产品数据（根据goodsId降序排序）
     * 使用 nameWildcard 字段（ES wildcard 类型），对比 queryByCompanyIdAndNameWildcard 可测试 wildcard 类型与 keyword 类型的查询性能差异
     *
     * @return 产品列表
     */
    @GetMapping("goods/queryByCompanyIdAndNameWildcardField")
    public List<ClothGoods> queryByCompanyIdAndNameWildcardField() {
        Long companyId = (long) (1 + random.nextInt(11));
        int randomNum = random.nextInt(Math.max(1, (int) idCounter.get()));
        String namePattern = "*" + String.format("%09d", randomNum) + "*";

        co.elastic.clients.elasticsearch._types.query_dsl.Query wildcardQuery =
                co.elastic.clients.elasticsearch._types.query_dsl.Query.of(q -> q.wildcard(
                        WildcardQuery.of(w -> w.field("nameWildcard").value(namePattern))));
        co.elastic.clients.elasticsearch._types.query_dsl.Query boolQuery = QueryBuilders.bool()
                .must(QueryBuilders.term().field("companyId").value(companyId).build()._toQuery())
                .must(wildcardQuery)
                .build()._toQuery();

        Query searchQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(0, 100))
                .withSort(Sort.by(Sort.Direction.DESC, "goodsId"))
                .build();

        SearchHits<ClothGoods> searchHits = elasticsearchOperations.search(searchQuery, ClothGoods.class);
        return searchHits.stream()
                .map(org.springframework.data.elasticsearch.core.SearchHit::getContent)
                .collect(Collectors.toList());
    }

    /**
     * 性能测试接口：根据companyId和name（prefix查询）查询前100条产品数据（根据goodsId降序排序）
     * companyId随机生成，name使用prefix查询模式
     *
     * @return 产品列表
     */
    @GetMapping("goods/queryByCompanyIdAndNamePrefix")
    public List<ClothGoods> queryByCompanyIdAndNamePrefix() {
        // 随机生成企业ID（1-11）
        Long companyId = (long) (1 + random.nextInt(11));

        // "产品" + 随机9位数字 - 匹配以"产品"开头且包含更长数字前缀的
        int randomNum = random.nextInt((int)idCounter.get());
        String namePrefix = "产品" + String.format("%09d", randomNum);

        // 构建查询条件
        co.elastic.clients.elasticsearch._types.query_dsl.Query boolQuery = QueryBuilders.bool()
                .must(QueryBuilders.term().field("companyId").value(companyId).build()._toQuery())
                .must(QueryBuilders.prefix().field("name").value(namePrefix).build()._toQuery())
                .build()._toQuery();

        // 构建查询，按goodsId降序排序，限制100条
        Query searchQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(0, 100))
                .withSort(Sort.by(Sort.Direction.DESC, "goodsId"))
                .build();

        // 执行查询
        SearchHits<ClothGoods> searchHits = elasticsearchOperations.search(searchQuery, ClothGoods.class);

        // 转换为列表返回
        return searchHits.stream()
                .map(org.springframework.data.elasticsearch.core.SearchHit::getContent)
                .collect(Collectors.toList());
    }

    /**
     * 性能测试接口：根据companyId和name（term查询）查询前100条产品数据（根据goodsId降序排序）
     * companyId随机生成，name使用term查询模式
     *
     * @return 产品列表
     */
    @GetMapping("goods/queryByCompanyIdAndNameTerm")
    public List<ClothGoods> queryByCompanyIdAndNameTerm() {
        // 随机生成企业ID（1-11）
        Long companyId = (long) (1 + random.nextInt(11));

        // "产品" + 随机9位数字
        int randomNum = random.nextInt((int)idCounter.get());
        String nameTerm = "产品" + String.format("%09d", randomNum);

        // 构建查询条件
        co.elastic.clients.elasticsearch._types.query_dsl.Query boolQuery = QueryBuilders.bool()
                .must(QueryBuilders.term().field("companyId").value(companyId).build()._toQuery())
                .must(QueryBuilders.term().field("name").value(nameTerm).build()._toQuery())
                .build()._toQuery();

        // 构建查询，按goodsId降序排序，限制100条
        Query searchQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(0, 100))
                .withSort(Sort.by(Sort.Direction.DESC, "goodsId"))
                .build();

        // 执行查询
        SearchHits<ClothGoods> searchHits = elasticsearchOperations.search(searchQuery, ClothGoods.class);

        // 转换为列表返回
        return searchHits.stream()
                .map(org.springframework.data.elasticsearch.core.SearchHit::getContent)
                .collect(Collectors.toList());
    }

    /**
     * 性能测试接口：根据companyId和number（wildcard查询）查询前100条产品数据（根据goodsId降序排序）
     * companyId随机生成，number使用wildcard查询模式
     *
     * @return 产品列表
     */
    @GetMapping("goods/queryByCompanyIdAndNumberWildcard")
    public List<ClothGoods> queryByCompanyIdAndNumberWildcard() {
        // 随机生成企业ID（1-11）
        Long companyId = (long) (1 + random.nextInt(11));

        // "*" + 随机9位数字 + "*" - 匹配包含特定数字的
        int randomNum = random.nextInt((int)idCounter.get());
        String numberPattern = "*" + String.format("%09d", randomNum) + "*";
        
        // 构建查询条件
        co.elastic.clients.elasticsearch._types.query_dsl.Query boolQuery = QueryBuilders.bool()
                .must(QueryBuilders.term().field("companyId").value(companyId).build()._toQuery())
                .must(QueryBuilders.wildcard().field("number").value(numberPattern).build()._toQuery())
                .build()._toQuery();

        // 构建查询，按goodsId降序排序，限制100条
        Query searchQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(0, 100))
                .withSort(Sort.by(Sort.Direction.DESC, "goodsId"))
                .build();

        // 执行查询
        SearchHits<ClothGoods> searchHits = elasticsearchOperations.search(searchQuery, ClothGoods.class);

        // 转换为列表返回
        return searchHits.stream()
                .map(org.springframework.data.elasticsearch.core.SearchHit::getContent)
                .collect(Collectors.toList());
    }

    /**
     * 性能测试接口：根据companyId和number（prefix查询）查询前100条产品数据（根据goodsId降序排序）
     * companyId随机生成，number使用prefix查询模式
     *
     * @return 产品列表
     */
    @GetMapping("goods/queryByCompanyIdAndNumberPrefix")
    public List<ClothGoods> queryByCompanyIdAndNumberPrefix() {
        // 随机生成企业ID（1-11）
        Long companyId = (long) (1 + random.nextInt(11));

        // 随机选择CP或PB前缀，然后加上随机数字
        String prefix = TYPES[random.nextInt(TYPES.length)].equals("cp") ? "CP" : "PB";
        int randomNum = random.nextInt((int)idCounter.get());
        String numberPrefix = prefix + String.format("%09d", randomNum);

        // 构建查询条件
        co.elastic.clients.elasticsearch._types.query_dsl.Query boolQuery = QueryBuilders.bool()
                .must(QueryBuilders.term().field("companyId").value(companyId).build()._toQuery())
                .must(QueryBuilders.prefix().field("number").value(numberPrefix).build()._toQuery())
                .build()._toQuery();

        // 构建查询，按goodsId降序排序，限制100条
        Query searchQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(0, 100))
                .withSort(Sort.by(Sort.Direction.DESC, "goodsId"))
                .build();

        // 执行查询
        SearchHits<ClothGoods> searchHits = elasticsearchOperations.search(searchQuery, ClothGoods.class);

        // 转换为列表返回
        return searchHits.stream()
                .map(org.springframework.data.elasticsearch.core.SearchHit::getContent)
                .collect(Collectors.toList());
    }

    /**
     * 性能测试接口：根据companyId和number（term查询）查询前100条产品数据（根据goodsId降序排序）
     * companyId随机生成，number使用term查询模式
     *
     * @return 产品列表
     */
    @GetMapping("goods/queryByCompanyIdAndNumberTerm")
    public List<ClothGoods> queryByCompanyIdAndNumberTerm() {
        // 随机生成企业ID（1-11）
        Long companyId = (long) (1 + random.nextInt(11));

        // 随机选择CP或PB前缀，然后加上随机数字
        String prefix = TYPES[random.nextInt(TYPES.length)].equals("cp") ? "CP" : "PB";
        int randomNum = random.nextInt((int)idCounter.get());
        String numberTerm = prefix + String.format("%09d", randomNum);

        // 构建查询条件
        co.elastic.clients.elasticsearch._types.query_dsl.Query boolQuery = QueryBuilders.bool()
                .must(QueryBuilders.term().field("companyId").value(companyId).build()._toQuery())
                .must(QueryBuilders.term().field("number").value(numberTerm).build()._toQuery())
                .build()._toQuery();

        // 构建查询，按goodsId降序排序，限制100条
        Query searchQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(0, 100))
                .withSort(Sort.by(Sort.Direction.DESC, "goodsId"))
                .build();

        // 执行查询
        SearchHits<ClothGoods> searchHits = elasticsearchOperations.search(searchQuery, ClothGoods.class);

        // 转换为列表返回
        return searchHits.stream()
                .map(org.springframework.data.elasticsearch.core.SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
