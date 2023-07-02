package com.future.demo;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// todo CriteriaQuery
// todo 聚合查询
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
public class Tests {

    @Resource
    ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void test() throws InterruptedException {
        /*------------------------------- 新增文档 */
        IndexQuery indexQuery = new IndexQueryBuilder().withObject(new NoteItem() {{
            setId("1");
            setContent("测试偶然中文");
        }}).build();
        String resultStr = this.elasticsearchTemplate.index(indexQuery);
        Assert.assertEquals("1", resultStr);

        // 刷新索引马上写入es，以便后续search能够查询到数据
        this.elasticsearchTemplate.refresh(NoteItem.class);

        // 根据id验证文档是否新增成功
        IdsQueryBuilder idsQueryBuilder = QueryBuilders.idsQuery().addIds("1");
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(idsQueryBuilder).build();
        List<NoteItem> noteItemList =
                this.elasticsearchTemplate.queryForList(searchQuery, NoteItem.class);
        Assert.assertEquals(1, noteItemList.size());
        Assert.assertEquals("1", noteItemList.get(0).getId());

        // match_all方式验证文档是否新增成功
        searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery()).build();
        noteItemList = this.elasticsearchTemplate.queryForList(searchQuery, NoteItem.class);
        Assert.assertEquals(1, noteItemList.size());
        Assert.assertEquals("1", noteItemList.get(0).getId());

        /*------------------------------- 批量新增文档 */
        List<IndexQuery> indexQueryList = new ArrayList<>();
        indexQueryList.add(new IndexQueryBuilder().withObject(new NoteItem() {{
            setId("2");
            setContent("哭否偶遇而");
        }}).build());
        indexQueryList.add(new IndexQueryBuilder().withObject(new NoteItem() {{
            setId("3");
            setContent("及偶尔uore");
        }}).build());
        this.elasticsearchTemplate.bulkIndex(indexQueryList);

        this.elasticsearchTemplate.refresh(NoteItem.class);

        searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery()).build();
        searchQuery.addSort(Sort.by(Sort.Direction.DESC, "_id"));
        noteItemList = this.elasticsearchTemplate.queryForList(searchQuery, NoteItem.class);
        Assert.assertEquals(3, noteItemList.size());
        Assert.assertEquals("3", noteItemList.get(0).getId());
        Assert.assertEquals("2", noteItemList.get(1).getId());
        Assert.assertEquals("1", noteItemList.get(2).getId());

        /*------------------------------- 全局模糊查询，不指定列，match_all */
        searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.queryStringQuery("偶尔").analyzer("ik_max_word")).build();
        noteItemList = this.elasticsearchTemplate.queryForList(searchQuery, NoteItem.class);
        Assert.assertEquals(1, noteItemList.size());
        Assert.assertEquals("3", noteItemList.get(0).getId());

        /*------------------------------- 指定列的match搜索 */
        searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchQuery("content", "偶尔间").analyzer("ik_max_word")).build();
        noteItemList = this.elasticsearchTemplate.queryForList(searchQuery, NoteItem.class);
        Assert.assertEquals(1, noteItemList.size());
        Assert.assertEquals("3", noteItemList.get(0).getId());

        /*------------------------------- matchPhrase搜索，不会对查询条件分词，field依旧分词 */
        searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchPhraseQuery("content", "偶尔间")).build();
        noteItemList = this.elasticsearchTemplate.queryForList(searchQuery, NoteItem.class);
        Assert.assertEquals(0, noteItemList.size());

        /*------------------------------- 多条件查询should，content contain '偶尔' or id='2' */
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should().addAll(Arrays.asList(
                QueryBuilders.matchQuery("content", "偶尔").analyzer("ik_max_word"),
                QueryBuilders.termQuery("id", "2")));
        searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
        searchQuery.addSort(Sort.by(Sort.Direction.ASC, "_id"));
        noteItemList = this.elasticsearchTemplate.queryForList(searchQuery, NoteItem.class);
        Assert.assertEquals(2, noteItemList.size());
        Assert.assertEquals("2", noteItemList.get(0).getId());
        Assert.assertEquals("3", noteItemList.get(1).getId());

        /*------------------------------- 分页和排序查询 */
        searchQuery = new NativeSearchQueryBuilder().build();
        // 0开始的页码
        searchQuery.setPageable(PageRequest.of(0, 2)).addSort(Sort.by(Sort.Direction.ASC, "_id"));
        noteItemList = this.elasticsearchTemplate.queryForList(searchQuery, NoteItem.class);
        Assert.assertEquals(2, noteItemList.size());
        Assert.assertEquals("1", noteItemList.get(0).getId());
        Assert.assertEquals("2", noteItemList.get(1).getId());
        // 查询第二页
        searchQuery = new NativeSearchQueryBuilder().build();
        searchQuery.setPageable(PageRequest.of(1, 2)).addSort(Sort.by(Sort.Direction.ASC, "_id"));
        noteItemList = this.elasticsearchTemplate.queryForList(searchQuery, NoteItem.class);
        Assert.assertEquals(1, noteItemList.size());
        Assert.assertEquals("3", noteItemList.get(0).getId());

        /*------------------------------- 高亮查询 */
        // https://stackoverflow.com/questions/37049764/how-to-provide-highlighting-with-spring-data-elasticsearch
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("content", "偶尔").analyzer("ik_max_word"))
                .withHighlightBuilder(new HighlightBuilder().field("content").preTags("##").postTags("##"))
                .build();
        AggregatedPage<NoteItem> noteItemAggregatedPage = this.elasticsearchTemplate.queryForPage(searchQuery, NoteItem.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                List<NoteItem> chunk = new ArrayList<>();
                for (SearchHit searchHit : response.getHits()) {
                    if (response.getHits().getHits().length <= 0) {
                        return null;
                    }
                    NoteItem noteItem = new NoteItem();
                    noteItem.setId(searchHit.getId());
                    noteItem.setPrimaryId((Long) searchHit.getSourceAsMap().get("primaryId"));
                    noteItem.setContent(searchHit.getHighlightFields().get("content").fragments()[0].toString());
                    chunk.add(noteItem);
                }
                if (chunk.size() > 0) {
                    return new AggregatedPageImpl<>((List<T>) chunk);
                }
                return null;
            }

            @Override
            public <T> T mapSearchHit(SearchHit searchHit, Class<T> type) {
                return null;
            }
        });
        Assert.assertEquals(1, noteItemAggregatedPage.getContent().size());
        Assert.assertEquals("3", noteItemAggregatedPage.getContent().get(0).getId());
        Assert.assertEquals("及##偶尔##uore", noteItemAggregatedPage.getContent().get(0).getContent());

        /*------------------------------- 修改文档，实质是创建文档操作，只要id一样就会替换文档 */
        String content = "建立家乐福人";
        indexQuery = new IndexQueryBuilder().withObject(new NoteItem() {{
            setId("1");
            setContent(content);
        }}).build();
        resultStr = this.elasticsearchTemplate.index(indexQuery);
        Assert.assertEquals("1", resultStr);

        this.elasticsearchTemplate.refresh(NoteItem.class);

        idsQueryBuilder = QueryBuilders.idsQuery().addIds("1");
        searchQuery = new NativeSearchQueryBuilder().withQuery(idsQueryBuilder).build();
        noteItemList = this.elasticsearchTemplate.queryForList(searchQuery, NoteItem.class);
        Assert.assertEquals(1, noteItemList.size());
        Assert.assertEquals(content, noteItemList.get(0).getContent());

        /*------------------------------- 根据id删除文档 */
        resultStr = this.elasticsearchTemplate.delete(NoteItem.class, "3");
        Assert.assertEquals("3", resultStr);

        this.elasticsearchTemplate.refresh(NoteItem.class);

        searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery()).build();
        noteItemList = this.elasticsearchTemplate.queryForList(searchQuery, NoteItem.class);
        Assert.assertEquals(2, noteItemList.size());
        Assert.assertEquals("2", noteItemList.get(0).getId());
        Assert.assertEquals("1", noteItemList.get(1).getId());

        /*------------------------------- 根据多个id批量删除多个文档 */
        idsQueryBuilder = QueryBuilders.idsQuery().addIds("1");
        DeleteQuery deleteQuery = new DeleteQuery();
        deleteQuery.setQuery(idsQueryBuilder);
        this.elasticsearchTemplate.delete(deleteQuery, NoteItem.class);

        this.elasticsearchTemplate.refresh(NoteItem.class);

        searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery()).build();
        noteItemList = this.elasticsearchTemplate.queryForList(searchQuery, NoteItem.class);
        Assert.assertEquals(1, noteItemList.size());
        Assert.assertEquals("2", noteItemList.get(0).getId());

        /*------------------------------- 根据自定义query删除数据 */
        deleteQuery.setQuery(QueryBuilders.termQuery("id", "2"));
        this.elasticsearchTemplate.delete(deleteQuery, NoteItem.class);

        this.elasticsearchTemplate.refresh(NoteItem.class);

        searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery()).build();
        noteItemList = this.elasticsearchTemplate.queryForList(searchQuery, NoteItem.class);
        Assert.assertEquals(0, noteItemList.size());
    }

}
