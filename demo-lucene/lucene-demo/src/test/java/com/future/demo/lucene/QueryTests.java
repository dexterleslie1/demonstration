package com.future.demo.lucene;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
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

public class QueryTests {
    @Test
    public void testTermQuery() throws IOException {
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

    @Test
    public void testQueryParser() throws IOException, ParseException {
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
            QueryParser parser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
            Query query = parser.parse("content:common AND title:common");
            TopDocs topDocs = searcher.search(query, 10);
            Assert.assertEquals(1, topDocs.totalHits);
            Document document = searcher.doc(topDocs.scoreDocs[0].doc);
            Assert.assertEquals(documentObjectList.get(1).get("id").asText(), document.get("id"));
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
    public void testTermRangeQuery() throws IOException {
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
            // TODO 似乎includeLower和includeUpper不起作用
            TermRangeQuery query = new TermRangeQuery("title", "a", "h", true, true);
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
    public void testNumericRangeQuery() throws IOException {
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
            documentObject.put("age", 58);
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 2);
            documentObject.put("age", 37);
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 3);
            documentObject.put("age", 45);
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 4);
            documentObject.put("age", 12);
            documentObjectList.add(documentObject);

            for(ObjectNode documentObjectTemporary : documentObjectList) {
                Document document = new Document();
                document.add(new Field("id", documentObjectTemporary.get("id").asText(),
                        Field.Store.YES,
                        Field.Index.NOT_ANALYZED));
                int age = documentObjectTemporary.get("age").asInt();
                NumericField ageField = new NumericField("age", Field.Store.YES, true);
                ageField.setIntValue(age);
                document.add(ageField);
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
            // TODO 似乎includeLower和includeUpper不起作用
            NumericRangeQuery<Integer> query = NumericRangeQuery.newIntRange("age", 5, 46, true, true);
            TopDocs topDocs = searcher.search(query, 10);
            Assert.assertEquals(3, topDocs.totalHits);
            Document document = searcher.doc(topDocs.scoreDocs[0].doc);
            Assert.assertEquals(documentObjectList.get(1).get("id").asText(), document.get("id"));
            document = searcher.doc(topDocs.scoreDocs[1].doc);
            Assert.assertEquals(documentObjectList.get(2).get("id").asText(), document.get("id"));
            document = searcher.doc(topDocs.scoreDocs[2].doc);
            Assert.assertEquals(documentObjectList.get(3).get("id").asText(), document.get("id"));
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

    @Test
    public void testPrefixQuery() throws IOException {
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
            documentObject.put("code", "1-1");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 2);
            documentObject.put("code", "1-1-1");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 3);
            documentObject.put("code", "1-1-2");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 4);
            documentObject.put("code", "1-2-1");
            documentObjectList.add(documentObject);

            for(ObjectNode documentObjectTemporary : documentObjectList) {
                Document document = new Document();
                document.add(new Field("id", documentObjectTemporary.get("id").asText(),
                        Field.Store.YES,
                        Field.Index.NOT_ANALYZED));
                String code = documentObjectTemporary.get("code").asText();
                Field field = new Field("code", code, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
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
            PrefixQuery query = new PrefixQuery(new Term("code", "1-1"));
            TopDocs topDocs = searcher.search(query, 10);
            Assert.assertEquals(3, topDocs.totalHits);
            Document document = searcher.doc(topDocs.scoreDocs[0].doc);
            Assert.assertEquals(documentObjectList.get(0).get("id").asText(), document.get("id"));
            document = searcher.doc(topDocs.scoreDocs[1].doc);
            Assert.assertEquals(documentObjectList.get(1).get("id").asText(), document.get("id"));
            document = searcher.doc(topDocs.scoreDocs[2].doc);
            Assert.assertEquals(documentObjectList.get(2).get("id").asText(), document.get("id"));
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

    @Test
    public void testBooleanQuery() throws IOException {
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
            documentObject.put("email", "1@gmail.com");
            documentObject.put("name", "user1");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 2);
            documentObject.put("email", "2@gmail.com");
            documentObject.put("name", "user2");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 3);
            documentObject.put("email", "3@gmail.com");
            documentObject.put("name", "user3");
            documentObjectList.add(documentObject);

            for(ObjectNode documentObjectTemporary : documentObjectList) {
                Document document = new Document();
                document.add(new Field("id", documentObjectTemporary.get("id").asText(),
                        Field.Store.YES,
                        Field.Index.NOT_ANALYZED));
                String email = documentObjectTemporary.get("email").asText();
                document.add(new Field("email", email, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
                String name = documentObjectTemporary.get("name").asText();
                Field field = new Field("name", name, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
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
            BooleanQuery query = new BooleanQuery();
            query.add(new TermQuery(new Term("email", "2@gmail.com")), BooleanClause.Occur.MUST);
            query.add(new TermQuery(new Term("name", "user2")), BooleanClause.Occur.MUST);
            TopDocs topDocs = searcher.search(query, 10);
            Assert.assertEquals(1, topDocs.totalHits);
            Document document = searcher.doc(topDocs.scoreDocs[0].doc);
            Assert.assertEquals(documentObjectList.get(1).get("id").asText(), document.get("id"));

            query = new BooleanQuery();
            query.add(new TermQuery(new Term("email", "2@gmail.com")), BooleanClause.Occur.SHOULD);
            query.add(new TermQuery(new Term("name", "user3")), BooleanClause.Occur.SHOULD);
            topDocs = searcher.search(query, 10);
            Assert.assertEquals(2, topDocs.totalHits);
            document = searcher.doc(topDocs.scoreDocs[0].doc);
            Assert.assertEquals(documentObjectList.get(1).get("id").asText(), document.get("id"));
            document = searcher.doc(topDocs.scoreDocs[1].doc);
            Assert.assertEquals(documentObjectList.get(2).get("id").asText(), document.get("id"));
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

    @Test
    public void testWildcardQuery() throws IOException {
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
            documentObject.put("content", "Wildcard queries let you query for terms with missing pieces but still find matches.");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 2);
            documentObject.put("content", "Two standard wildcard characters are used: * for zero or more characters, and ?");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 3);
            documentObject.put("content", "You can think of WildcardQuery as a more general PrefixQuery because the wildcard doesn’t have to be at the end.");
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
            WildcardQuery query = new WildcardQuery(new Term("content", "gene*"));
            TopDocs topDocs = searcher.search(query, 10);
            Assert.assertEquals(1, topDocs.totalHits);
            Document document = searcher.doc(topDocs.scoreDocs[0].doc);
            Assert.assertEquals(documentObjectList.get(2).get("id").asText(), document.get("id"));

            query = new WildcardQuery(new Term("content", "gen?r?l"));
            topDocs = searcher.search(query, 10);
            Assert.assertEquals(1, topDocs.totalHits);
            document = searcher.doc(topDocs.scoreDocs[0].doc);
            Assert.assertEquals(documentObjectList.get(2).get("id").asText(), document.get("id"));
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

    @Test
    public void testFuzzyQuery() throws IOException {
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
            documentObject.put("content", "fuzzy");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 2);
            documentObject.put("content", "wuzzy");
            documentObjectList.add(documentObject);

            for(ObjectNode documentObjectTemporary : documentObjectList) {
                Document document = new Document();
                document.add(new Field("id", documentObjectTemporary.get("id").asText(),
                        Field.Store.YES,
                        Field.Index.NOT_ANALYZED));
                String content = documentObjectTemporary.get("content").asText();
                Field field = new Field("content", content, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
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
            FuzzyQuery query = new FuzzyQuery(new Term("content", "wuzza"));
            TopDocs topDocs = searcher.search(query, 10);
            Assert.assertEquals(2, topDocs.totalHits);
            Assert.assertTrue(topDocs.scoreDocs[0].score>topDocs.scoreDocs[1].score);
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

    @Test
    public void testMatchAllDocsQuery() throws IOException {
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
            documentObject.put("content", "fuzzy");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 2);
            documentObject.put("content", "wuzzy");
            documentObjectList.add(documentObject);

            for(ObjectNode documentObjectTemporary : documentObjectList) {
                Document document = new Document();
                document.add(new Field("id", documentObjectTemporary.get("id").asText(),
                        Field.Store.YES,
                        Field.Index.NOT_ANALYZED));
                String content = documentObjectTemporary.get("content").asText();
                Field field = new Field("content", content, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
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
            MatchAllDocsQuery query = new MatchAllDocsQuery("content");
            TopDocs topDocs = searcher.search(query, 10);
            Assert.assertEquals(2, topDocs.totalHits);
            Document document = searcher.doc(topDocs.scoreDocs[0].doc);
            Assert.assertEquals(documentObjectList.get(0).get("id").asText(), document.get("id"));
            document = searcher.doc(topDocs.scoreDocs[1].doc);
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
}
