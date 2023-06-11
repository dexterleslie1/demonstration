package com.future.demo;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        /*------------------------------- 判断索引是否存在，是则删除 */
        // 判断索引是否存在
        boolean exists = this.elasticsearchTemplate.indexExists(NoteItem.class);
        // 删除索引
        boolean b = this.elasticsearchTemplate.deleteIndex(NoteItem.class);
        if (exists) {
            Assert.assertTrue(b);
        } else {
            Assert.assertFalse(b);
        }

        /*------------------------------- 创建索引并修改索引的mappings */
        // 创建索引，但是没有mappings
        b = this.elasticsearchTemplate.createIndex(NoteItem.class);
        Assert.assertTrue(b);
        // 更新索引mappings
        b = this.elasticsearchTemplate.putMapping(NoteItem.class);
        Assert.assertTrue(b);

        /*------------------------------- 新增文档 */
        IndexQuery indexQuery = new IndexQueryBuilder().withObject(new NoteItem() {{
            setId("1");
            setContent("测试中文");
        }}).build();
        String resultStr = this.elasticsearchTemplate.index(indexQuery);
        Assert.assertEquals("1", resultStr);

        TimeUnit.SECONDS.sleep(1);

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
            setContent("哭否而");
        }}).build());
        indexQueryList.add(new IndexQueryBuilder().withObject(new NoteItem() {{
            setId("3");
            setContent("及偶尔uore");
        }}).build());
        this.elasticsearchTemplate.bulkIndex(indexQueryList);

        TimeUnit.SECONDS.sleep(1);

        searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery()).build();
        noteItemList = this.elasticsearchTemplate.queryForList(searchQuery, NoteItem.class);
        Assert.assertEquals(3, noteItemList.size());
        Assert.assertEquals("3", noteItemList.get(0).getId());
        Assert.assertEquals("2", noteItemList.get(1).getId());
        Assert.assertEquals("1", noteItemList.get(2).getId());

        /*------------------------------- 修改文档，实质是创建文档操作，只要id一样就会替换文档 */
        String content = "建立家乐福人";
        indexQuery = new IndexQueryBuilder().withObject(new NoteItem() {{
            setId("1");
            setContent(content);
        }}).build();
        resultStr = this.elasticsearchTemplate.index(indexQuery);
        Assert.assertEquals("1", resultStr);

        TimeUnit.SECONDS.sleep(1);

        idsQueryBuilder = QueryBuilders.idsQuery().addIds("1");
        searchQuery = new NativeSearchQueryBuilder().withQuery(idsQueryBuilder).build();
        noteItemList = this.elasticsearchTemplate.queryForList(searchQuery, NoteItem.class);
        Assert.assertEquals(1, noteItemList.size());
        Assert.assertEquals(content, noteItemList.get(0).getContent());

        /*------------------------------- 全局模糊查询，不指定列，match_all */
        searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.queryStringQuery("偶尔")).build();
        noteItemList = this.elasticsearchTemplate.queryForList(searchQuery, NoteItem.class);
        Assert.assertEquals(1, noteItemList.size());
        Assert.assertEquals("3", noteItemList.get(0).getId());

        /*------------------------------- 指定列的match搜索 */
        searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchQuery("content", "偶尔间")).build();
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
                QueryBuilders.matchQuery("content", "偶尔"),
                QueryBuilders.termQuery("id", "2")));
        searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
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
        // todo 高亮有点麻烦暂时不研究

        /*------------------------------- 根据id删除文档 */
        resultStr = this.elasticsearchTemplate.delete(NoteItem.class, "3");
        Assert.assertEquals("3", resultStr);

        TimeUnit.SECONDS.sleep(1);

        searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery()).build();
        noteItemList = this.elasticsearchTemplate.queryForList(searchQuery, NoteItem.class);
        Assert.assertEquals(2, noteItemList.size());
        Assert.assertEquals("2", noteItemList.get(0).getId());
        Assert.assertEquals("1", noteItemList.get(1).getId());

        /*------------------------------- 根据多个id批量删除多个文档 */
        idsQueryBuilder = QueryBuilders.idsQuery().addIds("1", "2");
        DeleteQuery deleteQuery = new DeleteQuery();
        deleteQuery.setQuery(idsQueryBuilder);
        this.elasticsearchTemplate.delete(deleteQuery, NoteItem.class);

        TimeUnit.SECONDS.sleep(1);

        searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery()).build();
        noteItemList = this.elasticsearchTemplate.queryForList(searchQuery, NoteItem.class);
        Assert.assertEquals(0, noteItemList.size());
    }

}
