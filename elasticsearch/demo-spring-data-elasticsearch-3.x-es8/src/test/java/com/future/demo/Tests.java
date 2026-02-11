package com.future.demo;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;

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
    public void test() {
        /*------------------------------- 新增文档 */
        NoteItem noteItem1 = new NoteItem();
        noteItem1.setId("1");
        noteItem1.setContent("测试偶然中文");
        IndexQuery indexQuery = new IndexQueryBuilder().withObject(noteItem1).build();
        String resultStr = this.elasticsearchOperations.index(indexQuery, IndexCoordinates.of("index_noteitem"));
        Assertions.assertEquals("1", resultStr);

        // 刷新索引马上写入es，以便后续search能够查询到数据
        this.elasticsearchOperations.indexOps(NoteItem.class).refresh();

        // 根据id验证文档是否新增成功
        co.elastic.clients.elasticsearch._types.query_dsl.Query idsQuery = QueryBuilders.ids().values("1").build()._toQuery();
        Query searchQuery = NativeQuery.builder().withQuery(idsQuery).build();
        SearchHits<NoteItem> searchHits = this.elasticsearchOperations.search(searchQuery, NoteItem.class);
        List<NoteItem> noteItemList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        Assertions.assertEquals(1, noteItemList.size());
        Assertions.assertEquals("1", noteItemList.get(0).getId());

        // match_all方式验证文档是否新增成功
        co.elastic.clients.elasticsearch._types.query_dsl.Query matchAllQuery = QueryBuilders.matchAll().build()._toQuery();
        searchQuery = NativeQuery.builder().withQuery(matchAllQuery).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, NoteItem.class);
        noteItemList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(java.util.stream.Collectors.toList());
        Assertions.assertEquals(1, noteItemList.size());
        Assertions.assertEquals("1", noteItemList.get(0).getId());

        /*------------------------------- 批量新增文档 */
        List<IndexQuery> indexQueryList = new ArrayList<>();
        NoteItem noteItem2 = new NoteItem();
        noteItem2.setId("2");
        noteItem2.setContent("哭否偶遇而");
        indexQueryList.add(new IndexQueryBuilder().withObject(noteItem2).build());
        NoteItem noteItem3 = new NoteItem();
        noteItem3.setId("3");
        noteItem3.setContent("及偶尔uore");
        indexQueryList.add(new IndexQueryBuilder().withObject(noteItem3).build());
        this.elasticsearchOperations.bulkIndex(indexQueryList, NoteItem.class);

        this.elasticsearchOperations.indexOps(NoteItem.class).refresh();

        searchQuery = NativeQuery.builder().withQuery(matchAllQuery).withSort(Sort.by(Sort.Direction.DESC, "id.keyword")).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, NoteItem.class);
        noteItemList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(java.util.stream.Collectors.toList());
        Assertions.assertEquals(3, noteItemList.size());
        Assertions.assertEquals("3", noteItemList.get(0).getId());
        Assertions.assertEquals("2", noteItemList.get(1).getId());
        Assertions.assertEquals("1", noteItemList.get(2).getId());

        /*------------------------------- 全局模糊查询，不指定列，match_all */
        co.elastic.clients.elasticsearch._types.query_dsl.Query queryStringQuery = QueryBuilders.queryString().query("偶尔").analyzer("ik_max_word").build()._toQuery();
        searchQuery = NativeQuery.builder().withQuery(queryStringQuery).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, NoteItem.class);
        noteItemList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(java.util.stream.Collectors.toList());
        Assertions.assertEquals(1, noteItemList.size());
        Assertions.assertEquals("3", noteItemList.get(0).getId());

        /*------------------------------- 指定列的match搜索 */
        co.elastic.clients.elasticsearch._types.query_dsl.Query matchQuery = QueryBuilders.match().field("content").query("偶尔间").analyzer("ik_max_word").build()._toQuery();
        searchQuery = NativeQuery.builder().withQuery(matchQuery).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, NoteItem.class);
        noteItemList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(java.util.stream.Collectors.toList());
        Assertions.assertEquals(1, noteItemList.size());
        Assertions.assertEquals("3", noteItemList.get(0).getId());

        /*------------------------------- matchPhrase搜索，不会对查询条件分词，field依旧分词 */
        co.elastic.clients.elasticsearch._types.query_dsl.Query matchPhraseQuery = QueryBuilders.matchPhrase().field("content").query("偶尔间").build()._toQuery();
        searchQuery = NativeQuery.builder().withQuery(matchPhraseQuery).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, NoteItem.class);
        noteItemList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(java.util.stream.Collectors.toList());
        Assertions.assertEquals(0, noteItemList.size());

        /*------------------------------- 多条件查询should，content contain '偶尔' or id='2' */
        co.elastic.clients.elasticsearch._types.query_dsl.Query boolQuery = QueryBuilders.bool()
                .should(QueryBuilders.match().field("content").query("偶尔").analyzer("ik_max_word").build()._toQuery())
                .should(QueryBuilders.term().field("id").value("2").build()._toQuery())
                .build()._toQuery();
        searchQuery = NativeQuery.builder().withQuery(boolQuery).withSort(Sort.by(Sort.Direction.ASC, "id.keyword")).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, NoteItem.class);
        noteItemList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(java.util.stream.Collectors.toList());
        Assertions.assertEquals(2, noteItemList.size());
        Assertions.assertEquals("2", noteItemList.get(0).getId());
        Assertions.assertEquals("3", noteItemList.get(1).getId());

        /*------------------------------- 分页和排序查询 */
        // 0开始的页码
        searchQuery = NativeQuery.builder()
                .withPageable(PageRequest.of(0, 2))
                .withSort(Sort.by(Sort.Direction.ASC, "id.keyword"))
                .build();
        searchHits = this.elasticsearchOperations.search(searchQuery, NoteItem.class);
        noteItemList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(java.util.stream.Collectors.toList());
        Assertions.assertEquals(2, noteItemList.size());
        Assertions.assertEquals("1", noteItemList.get(0).getId());
        Assertions.assertEquals("2", noteItemList.get(1).getId());
        // 查询第二页
        searchQuery = NativeQuery.builder()
                .withPageable(PageRequest.of(1, 2))
                .withSort(Sort.by(Sort.Direction.ASC, "id.keyword"))
                .build();
        searchHits = this.elasticsearchOperations.search(searchQuery, NoteItem.class);
        noteItemList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(java.util.stream.Collectors.toList());
        Assertions.assertEquals(1, noteItemList.size());
        Assertions.assertEquals("3", noteItemList.get(0).getId());

        /*------------------------------- 高亮查询 */
        // https://stackoverflow.com/questions/37049764/how-to-provide-highlighting-with-spring-data-elasticsearch
        co.elastic.clients.elasticsearch._types.query_dsl.Query highlightMatchQuery = QueryBuilders.match().field("content").query("偶尔").analyzer("ik_max_word").build()._toQuery();
        HighlightFieldParameters highlightFieldParameters = HighlightFieldParameters.builder()
                .withPreTags("##")
                .withPostTags("##")
                .build();
        HighlightField highlightField = new HighlightField("content", highlightFieldParameters);
        Highlight highlight = new Highlight(List.of(highlightField));
        HighlightQuery highlightQuery = new HighlightQuery(highlight, NoteItem.class);
        searchQuery = NativeQuery.builder()
                .withQuery(highlightMatchQuery)
                .withHighlightQuery(highlightQuery)
                .build();
        searchHits = this.elasticsearchOperations.search(searchQuery, NoteItem.class);
        List<NoteItem> noteItemListWithHighlight = new ArrayList<>();
        for (SearchHit<NoteItem> searchHit : searchHits) {
            NoteItem noteItem = searchHit.getContent();
            if (searchHit.getHighlightFields().containsKey("content")) {
                List<String> highlightFields = searchHit.getHighlightFields().get("content");
                if (highlightFields != null && !highlightFields.isEmpty()) {
                    noteItem.setContent(highlightFields.get(0));
                }
            }
            noteItemListWithHighlight.add(noteItem);
        }
        Assertions.assertEquals(1, noteItemListWithHighlight.size());
        Assertions.assertEquals("3", noteItemListWithHighlight.get(0).getId());
        Assertions.assertEquals("及##偶尔##uore", noteItemListWithHighlight.get(0).getContent());

        /*------------------------------- 修改文档，实质是创建文档操作，只要id一样就会替换文档 */
        String content = "建立家乐福人";
        NoteItem noteItem1Update = new NoteItem();
        noteItem1Update.setId("1");
        noteItem1Update.setContent(content);
        indexQuery = new IndexQueryBuilder().withObject(noteItem1Update).build();
        resultStr = this.elasticsearchOperations.index(indexQuery, IndexCoordinates.of("index_noteitem"));
        Assertions.assertEquals("1", resultStr);

        this.elasticsearchOperations.indexOps(NoteItem.class).refresh();

        idsQuery = QueryBuilders.ids().values("1").build()._toQuery();
        searchQuery = NativeQuery.builder().withQuery(idsQuery).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, NoteItem.class);
        noteItemList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(java.util.stream.Collectors.toList());
        Assertions.assertEquals(1, noteItemList.size());
        Assertions.assertEquals(content, noteItemList.get(0).getContent());

        /*------------------------------- 根据id删除文档 */
        resultStr = this.elasticsearchOperations.delete("3", NoteItem.class);
        Assertions.assertEquals("3", resultStr);

        this.elasticsearchOperations.indexOps(NoteItem.class).refresh();

        searchQuery = NativeQuery.builder().withQuery(matchAllQuery).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, NoteItem.class);
        noteItemList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(java.util.stream.Collectors.toList());
        Assertions.assertEquals(2, noteItemList.size());
        Assertions.assertEquals("2", noteItemList.get(0).getId());
        Assertions.assertEquals("1", noteItemList.get(1).getId());

        /*------------------------------- 根据多个id批量删除多个文档 */
        idsQuery = QueryBuilders.ids().values("1").build()._toQuery();
        searchQuery = NativeQuery.builder().withQuery(idsQuery).build();
        DeleteQuery deleteQuery = DeleteQuery.builder(searchQuery).build();
        this.elasticsearchOperations.delete(deleteQuery, NoteItem.class);

        this.elasticsearchOperations.indexOps(NoteItem.class).refresh();

        searchQuery = NativeQuery.builder().withQuery(matchAllQuery).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, NoteItem.class);
        noteItemList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(java.util.stream.Collectors.toList());
        Assertions.assertEquals(1, noteItemList.size());
        Assertions.assertEquals("2", noteItemList.get(0).getId());

        /*------------------------------- 根据自定义query删除数据 */
        co.elastic.clients.elasticsearch._types.query_dsl.Query termQuery = QueryBuilders.term().field("id").value("2").build()._toQuery();
        searchQuery = NativeQuery.builder().withQuery(termQuery).build();
        deleteQuery = DeleteQuery.builder(searchQuery).build();
        this.elasticsearchOperations.delete(deleteQuery, NoteItem.class);

        this.elasticsearchOperations.indexOps(NoteItem.class).refresh();

        searchQuery = NativeQuery.builder().withQuery(matchAllQuery).build();
        searchHits = this.elasticsearchOperations.search(searchQuery, NoteItem.class);
        noteItemList = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(java.util.stream.Collectors.toList());
        Assertions.assertEquals(0, noteItemList.size());
    }

}
