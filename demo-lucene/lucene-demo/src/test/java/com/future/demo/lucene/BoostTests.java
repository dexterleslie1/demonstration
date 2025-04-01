package com.future.demo.lucene;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
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

public class BoostTests {
    @Test
    public void testBoost() throws IOException, ParseException, InterruptedException {
        // 测试文档默认boost
        this.testBoostDocumentWithDefaultSetting();
        // 测试文档手动设置boost
        this.testBoostDocumentWithManualSetting();
        // 测试域手动设置boost
        this.testBoostFieldWithManualSetting();
    }

    void testBoostDocumentWithDefaultSetting() throws IOException, ParseException {
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
            documentObject.put("email", "a1@gmail.com");
            documentObject.put("content", "This is a content contains common common word.");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 2);
            documentObject.put("email", "b@future.com");
            documentObject.put("content", "This is a content contains common word.");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 3);
            documentObject.put("email", "c1@gmail.com");
            documentObject.put("content", "This is a content contains common common common word.");
            documentObjectList.add(documentObject);

            for(ObjectNode documentObjectTemporary : documentObjectList) {
                Document document = new Document();
                document.add(new Field("id", documentObjectTemporary.get("id").asText(),
                        Field.Store.YES,
                        Field.Index.NOT_ANALYZED));
                document.add(new Field("email", documentObjectTemporary.get("email").asText(),
                        Field.Store.YES,
                        Field.Index.NOT_ANALYZED));
                document.add(new Field("content", documentObjectTemporary.get("content").asText(),
                        Field.Store.NO,
                        Field.Index.ANALYZED));
                writer.addDocument(document);
            }

            writer.commit();

            Assert.assertEquals(documentObjectList.size(), writer.numDocs());
            Assert.assertEquals(documentObjectList.size(), writer.maxDoc());

            // 没有设置文档boost值时，默认以词语出现频率评分
            file = new File(indexDirectory);
            directory = FSDirectory.open(file);
            reader = IndexReader.open(directory);
            searcher = new IndexSearcher(reader);
            TopDocs topDocs = searcher.search(new TermQuery(new Term("content", "common")), 10);
            Assert.assertEquals(documentObjectList.size(), topDocs.totalHits);
            Document document = searcher.doc(topDocs.scoreDocs[0].doc);
            Assert.assertEquals(documentObjectList.get(2).get("id").asText(), document.get("id"));
            document = searcher.doc(topDocs.scoreDocs[1].doc);
            Assert.assertEquals(documentObjectList.get(0).get("id").asText(), document.get("id"));
            document = searcher.doc(topDocs.scoreDocs[2].doc);
            Assert.assertEquals(documentObjectList.get(1).get("id").asText(), document.get("id"));
        } catch (IOException e) {
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

    void testBoostDocumentWithManualSetting() throws IOException, ParseException {
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
            documentObject.put("email", "a1@gmail.com");
            documentObject.put("content", "This is a content contains common common word.");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 2);
            documentObject.put("email", "b@future.com");
            documentObject.put("content", "This is a content contains common word.");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 3);
            documentObject.put("email", "c1@gmail.com");
            documentObject.put("content", "This is a content contains common common common word.");
            documentObjectList.add(documentObject);

            for(ObjectNode documentObjectTemporary : documentObjectList) {
                Document document = new Document();
                document.add(new Field("id", documentObjectTemporary.get("id").asText(),
                        Field.Store.YES,
                        Field.Index.NOT_ANALYZED));
                String email = documentObjectTemporary.get("email").asText();
                document.add(new Field("email", email,
                        Field.Store.YES,
                        Field.Index.NOT_ANALYZED));
                document.add(new Field("content", documentObjectTemporary.get("content").asText(),
                        Field.Store.NO,
                        Field.Index.ANALYZED));

                if(email.contains("@future.com")) {
                    document.setBoost(1.5f);
                }

                writer.addDocument(document);
            }

            writer.commit();

            Assert.assertEquals(documentObjectList.size(), writer.numDocs());
            Assert.assertEquals(documentObjectList.size(), writer.maxDoc());

            // 设置了1.5 boost的文档排列第一
            file = new File(indexDirectory);
            directory = FSDirectory.open(file);
            reader = IndexReader.open(directory);
            searcher = new IndexSearcher(reader);
            TopDocs topDocs = searcher.search(new TermQuery(new Term("content", "common")), 10);
            Assert.assertEquals(documentObjectList.size(), topDocs.totalHits);
            Document document = searcher.doc(topDocs.scoreDocs[0].doc);
            Assert.assertEquals(documentObjectList.get(1).get("id").asText(), document.get("id"));
            document = searcher.doc(topDocs.scoreDocs[1].doc);
            Assert.assertEquals(documentObjectList.get(2).get("id").asText(), document.get("id"));
            document = searcher.doc(topDocs.scoreDocs[2].doc);
            Assert.assertEquals(documentObjectList.get(0).get("id").asText(), document.get("id"));
        } catch (IOException e) {
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

    void testBoostFieldWithManualSetting() throws IOException, ParseException {
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
            documentObject.put("email", "a1@gmail.com");
            documentObject.put("title", "This is a title 1");
            documentObject.put("content", "This is a content contains common common common word.");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 2);
            documentObject.put("email", "b@future.com");
            documentObject.put("title", "This is a title common");
            documentObject.put("content", "This is a content contains common word.");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 3);
            documentObject.put("email", "c1@gmail.com");
            documentObject.put("title", "This is a title 2");
            documentObject.put("content", "This is a content contains common common common common common word.");
            documentObjectList.add(documentObject);

            for(ObjectNode documentObjectTemporary : documentObjectList) {
                Document document = new Document();
                document.add(new Field("id", documentObjectTemporary.get("id").asText(),
                        Field.Store.YES,
                        Field.Index.NOT_ANALYZED));
                String email = documentObjectTemporary.get("email").asText();
                document.add(new Field("email", email,
                        Field.Store.YES,
                        Field.Index.NOT_ANALYZED));
                Field fieldTitle = new Field("title", documentObjectTemporary.get("title").asText(),
                        Field.Store.NO,
                        Field.Index.ANALYZED);
                fieldTitle.setBoost(1.5f);
                document.add(fieldTitle);
                document.add(new Field("content", documentObjectTemporary.get("content").asText(),
                        Field.Store.NO,
                        Field.Index.ANALYZED));

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
            BooleanQuery query = new BooleanQuery();
            query.add(new TermQuery(new Term("content", "common")), BooleanClause.Occur.MUST);
            query.add(new TermQuery(new Term("title", "common")), BooleanClause.Occur.SHOULD);
            TopDocs topDocs = searcher.search(query, 10);
            Assert.assertEquals(documentObjectList.size(), topDocs.totalHits);
            Document document = searcher.doc(topDocs.scoreDocs[0].doc);
            Assert.assertEquals(documentObjectList.get(1).get("id").asText(), document.get("id"));
            document = searcher.doc(topDocs.scoreDocs[1].doc);
            Assert.assertEquals(documentObjectList.get(2).get("id").asText(), document.get("id"));
            document = searcher.doc(topDocs.scoreDocs[2].doc);
            Assert.assertEquals(documentObjectList.get(0).get("id").asText(), document.get("id"));
        } catch (IOException e) {
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
