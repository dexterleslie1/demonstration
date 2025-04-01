package com.future.demo.lucene;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QueryParserTests {
    @Test
    public void testTermQuery() throws IOException, ParseException {
        IndexWriter writer = null;
        IndexReader reader = null;
        IndexSearcher searcher = null;
        Directory directory = null;
        try {
            // 索引数据目录
            String indexDirectory = ".tmp-idx-data";
            File file = new File(indexDirectory);
            // 创建目录
            directory = FSDirectory.open(file);
            // 创建IndexWriter
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35));
            // 覆盖之前索引数据，创建新的索引数据
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            writer = new IndexWriter(directory, config);

            // 创建Document
            ObjectMapper OMInstance = new ObjectMapper();
            List<ObjectNode> documentObjectList = new ArrayList<>();
            ObjectNode documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 1);
            documentObject.put("content", "Note how QueryParser built the term query by appending the default field we’d pro- vided when instantiating it, subject, to the analyzed term, computers.");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 2);
            documentObject.put("content", "Section 3.5.11 shows how you can specify a field other than the default one. ");
            documentObjectList.add(documentObject);

            for(ObjectNode documentObjectTemporary : documentObjectList) {
                Document document = new Document();
                document.add(new Field("id", documentObjectTemporary.get("id").asText(),
                        Field.Store.YES,
                        Field.Index.NOT_ANALYZED));
                String content = documentObjectTemporary.get("content").asText();
                Field field = new Field("content", content, Field.Store.YES, Field.Index.ANALYZED);
                document.add(field);
                writer.addDocument(document);
            }

            writer.commit();

            Assert.assertEquals(documentObjectList.size(), writer.numDocs());
            Assert.assertEquals(documentObjectList.size(), writer.maxDoc());

            file = new File(indexDirectory);
            directory = FSDirectory.open(file);
            reader = IndexReader.open(directory);
            searcher = new IndexSearcher(reader);
            QueryParser parser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
            Query query = parser.parse("computers");
            TopDocs topDocs = searcher.search(query, 10);
            Assert.assertEquals(1, topDocs.totalHits);
            Document document = searcher.doc(topDocs.scoreDocs[0].doc);
            Assert.assertEquals(documentObjectList.get(0).get("id").asText(), document.get("id"));
        } catch (IOException e) {
            throw e;
        } catch (ParseException e) {
            throw e;
        } finally {
            try {
                if(writer!=null) {
                    writer.close();
                    writer = null;
                }
            } catch (IOException e) {
                // 忽略异常
            }

            try {
                if(reader!=null) {
                    reader.close();
                    reader = null;
                }
            } catch (IOException e) {
                // 忽略异常
            }

            try {
                if(searcher!=null) {
                    searcher.close();
                    searcher = null;
                }
            } catch (IOException e) {
                // 忽略异常
            }
        }
    }

    @Test
    public void testTermRangeQuery() throws IOException, ParseException {
        IndexWriter writer = null;
        IndexReader reader = null;
        IndexSearcher searcher = null;
        Directory directory = null;
        try {
            // 索引数据目录
            String indexDirectory = ".tmp-idx-data";
            File file = new File(indexDirectory);
            // 创建目录
            directory = FSDirectory.open(file);
            // 创建IndexWriter
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35));
            // 覆盖之前索引数据，创建新的索引数据
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            writer = new IndexWriter(directory, config);

            // 创建Document
            ObjectMapper OMInstance = new ObjectMapper();
            List<ObjectNode> documentObjectList = new ArrayList<>();
            ObjectNode documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 1);
            documentObject.put("title", "ukkk");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 2);
            documentObject.put("title", "yooo");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 3);
            documentObject.put("title", "abc");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 4);
            documentObject.put("title", "ewyy");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 5);
            documentObject.put("title", "dvvvvv");
            documentObjectList.add(documentObject);

            for(ObjectNode documentObjectTemporary : documentObjectList) {
                Document document = new Document();
                document.add(new Field("id", documentObjectTemporary.get("id").asText(),
                        Field.Store.YES,
                        Field.Index.NOT_ANALYZED));
                Field fieldTitle = new Field("title", documentObjectTemporary.get("title").asText(),
                        Field.Store.NO,
                        Field.Index.NOT_ANALYZED_NO_NORMS);
                document.add(fieldTitle);
                writer.addDocument(document);
            }

            writer.commit();

            Assert.assertEquals(documentObjectList.size(), writer.numDocs());
            Assert.assertEquals(documentObjectList.size(), writer.maxDoc());

            // title域设置了1.5 boost的文档排列第一
            file = new File(indexDirectory);
            directory = FSDirectory.open(file);
            reader = IndexReader.open(directory);
            searcher = new IndexSearcher(reader);
            QueryParser parser = new QueryParser(Version.LUCENE_35, "title", new StandardAnalyzer(Version.LUCENE_35));
            Query query = parser.parse("[a TO h]");
            TopDocs topDocs = searcher.search(query, 10);
            Assert.assertEquals(3, topDocs.totalHits);
            Document document = searcher.doc(topDocs.scoreDocs[0].doc);
            Assert.assertEquals(documentObjectList.get(2).get("id").asText(), document.get("id"));
            document = searcher.doc(topDocs.scoreDocs[1].doc);
            Assert.assertEquals(documentObjectList.get(3).get("id").asText(), document.get("id"));
            document = searcher.doc(topDocs.scoreDocs[2].doc);
            Assert.assertEquals(documentObjectList.get(4).get("id").asText(), document.get("id"));
        } catch (IOException e) {
            throw e;
        } catch (ParseException e) {
            throw e;
        } finally {
            try {
                if(writer!=null) {
                    writer.close();
                    writer = null;
                }
            } catch (IOException e) {
                // 忽略异常
            }

            try {
                if(reader!=null) {
                    reader.close();
                    reader = null;
                }
            } catch (IOException e) {
                // 忽略异常
            }

            try {
                if(searcher!=null) {
                    searcher.close();
                    searcher = null;
                }
            } catch (IOException e) {
                // 忽略异常
            }
        }
    }
}
