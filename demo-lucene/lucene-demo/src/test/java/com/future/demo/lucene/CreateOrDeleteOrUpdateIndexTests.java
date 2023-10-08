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
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateOrDeleteOrUpdateIndexTests {
    @Test
    public void testCreateAndSearchIndex() throws IOException, ParseException {
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
            documentObject.put("content", "The static main method parses B, C the incoming arguments, cre- ates an Indexer instance");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 2);
            documentObject.put("content", "This example intentionally focuses on plain text files with .txt extensions to keep things simple");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 3);
            documentObject.put("content", "Lucene will also emulate bugs present in that release and fixed in later releases, if the Lucene developers felt that fixing the bug would break backward compatibility of existing indexes.");
            documentObjectList.add(documentObject);

            for(ObjectNode documentObjectTemporary : documentObjectList) {
                Document document = new Document();
                document.add(new Field("id", documentObjectTemporary.get("id").asText(),
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

            // 搜索数据
            file = new File(indexDirectory);
            directory = FSDirectory.open(file);
            reader = IndexReader.open(directory);
            searcher = new IndexSearcher(reader);

            // 创建QueryParser
            String queryStr = "present";
            QueryParser parser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
            Query query = parser.parse(queryStr);

            // 执行搜索
            TopDocs topDocs = searcher.search(query, 10);
            Assert.assertEquals(1, topDocs.totalHits);
            Document document = searcher.doc(topDocs.scoreDocs[0].doc);
            Assert.assertEquals(documentObjectList.get(2).get("id").asText(), document.get("id"));
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
    public void testDeleteIndex() throws IOException, ParseException {
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
            documentObject.put("content", "The static main method parses B, C the incoming arguments, cre- ates an Indexer instance");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 2);
            documentObject.put("content", "This example intentionally focuses on plain text files with .txt extensions to keep things simple");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 3);
            documentObject.put("content", "Lucene will also emulate bugs present in that release and fixed in later releases, if the Lucene developers felt that fixing the bug would break backward compatibility of existing indexes.");
            documentObjectList.add(documentObject);

            for(ObjectNode documentObjectTemporary : documentObjectList) {
                Document document = new Document();
                document.add(new Field("id", documentObjectTemporary.get("id").asText(),
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

            // 删除索引并且未真正删除
            writer.deleteDocuments(new Term("id", "2"));
            writer.commit();
            Assert.assertTrue(writer.hasDeletions());
            Assert.assertEquals(documentObjectList.size(), writer.maxDoc());
            Assert.assertEquals(documentObjectList.size()-1, writer.numDocs());

            // 删除索引后使用optimize触发真正删除
            writer.optimize(true);
            Assert.assertEquals(documentObjectList.size()-1, writer.maxDoc());
            Assert.assertEquals(documentObjectList.size()-1, writer.numDocs());
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
    public void testUpdateIndex() throws IOException, ParseException {
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
            documentObject.put("content", "The static main method parses B, C the incoming arguments, cre- ates an Indexer instance");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 2);
            documentObject.put("content", "This example intentionally focuses on plain text files with .txt extensions to keep things simple");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 3);
            documentObject.put("content", "Lucene will also emulate bugs present in that release and fixed in later releases, if the Lucene developers felt that fixing the bug would break backward compatibility of existing indexes.");
            documentObjectList.add(documentObject);

            for(ObjectNode documentObjectTemporary : documentObjectList) {
                Document document = new Document();
                document.add(new Field("id", documentObjectTemporary.get("id").asText(),
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

            // 测试更新文档
            documentObjectList.get(0).put("content", documentObjectList.get(0).get("content") + " present");
            Document document = new Document();
            document.add(new Field("id", documentObjectList.get(0).get("id").asText(),
                    Field.Store.YES,
                    Field.Index.NOT_ANALYZED));
            document.add(new Field("content", documentObjectList.get(0).get("content").asText(),
                    Field.Store.NO,
                    Field.Index.ANALYZED));
            writer.updateDocument(new Term("id", "1"), document);
            writer.commit();

            // 验证是否成功更新文档
            file = new File(indexDirectory);
            directory = FSDirectory.open(file);
            reader = IndexReader.open(directory);
            searcher = new IndexSearcher(reader);
            String queryStr = "present";
            QueryParser parser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
            Query query = parser.parse(queryStr);
            TopDocs topDocs = searcher.search(query, 10);
            Assert.assertEquals(2, topDocs.totalHits);
            document = searcher.doc(topDocs.scoreDocs[0].doc);
            Assert.assertEquals(documentObjectList.get(0).get("id").asText(), document.get("id"));
            document = searcher.doc(topDocs.scoreDocs[1].doc);
            Assert.assertEquals(documentObjectList.get(2).get("id").asText(), document.get("id"));
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
    public void testMultivalued() throws IOException, ParseException {
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
            documentObject.put("author", "abc,def,Dexter.Chan");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 2);
            documentObject.put("author", "abc,ghi");
            documentObjectList.add(documentObject);

            documentObject = OMInstance.createObjectNode();
            documentObject.put("id", 3);
            documentObject.put("author", "jkl,mno,Dexter.Chan,pqr");
            documentObjectList.add(documentObject);

            for(ObjectNode documentObjectTemporary : documentObjectList) {
                Document document = new Document();
                document.add(new Field("id", documentObjectTemporary.get("id").asText(),
                        Field.Store.YES,
                        Field.Index.NOT_ANALYZED));
                String [] authorArray = documentObjectTemporary.get("author").asText().split(",");
                for(String author : authorArray) {
                    document.add(new Field("author", author,
                            Field.Store.YES,
                            Field.Index.NOT_ANALYZED));
                }
                writer.addDocument(document);
            }

            writer.commit();

            Assert.assertEquals(documentObjectList.size(), writer.numDocs());
            Assert.assertEquals(documentObjectList.size(), writer.maxDoc());

            // 搜索数据
            file = new File(indexDirectory);
            directory = FSDirectory.open(file);
            reader = IndexReader.open(directory);
            searcher = new IndexSearcher(reader);

            // 执行搜索
            TopDocs topDocs = searcher.search(new TermQuery(new Term("author", "Dexter.Chan")), 10);
            Assert.assertEquals(2, topDocs.totalHits);
            Document document = searcher.doc(topDocs.scoreDocs[0].doc);
            Assert.assertEquals(documentObjectList.get(0).get("id").asText(), document.get("id"));
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
}
