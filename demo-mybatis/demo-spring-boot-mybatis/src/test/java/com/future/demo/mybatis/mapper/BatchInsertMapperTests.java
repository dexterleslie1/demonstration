package com.future.demo.mybatis.mapper;

import com.future.demo.mybatis.Application;
import com.future.demo.mybatis.entity.BatchInsertModel;
import com.future.demo.mybatis.entity.UserModel;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class BatchInsertMapperTests {
    @Resource
    BatchInsertMapper batchInsertMapper;
    @Resource
    SqlSessionFactory sqlSessionFactory;

    @Before
    public void setup() {
        this.batchInsertMapper.truncate();
    }

    // 普通insert语句一条条插入
    @Test
    public void testInsert1() throws InterruptedException {
        Date startTime = new Date();
        int count = 50000;
        int concurrentThreads = 20;
        int looperInner = count / concurrentThreads;
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0; i<concurrentThreads; i++) {
            int finalI = i;
            executorService.submit(()-> {
                try {
                    int start = finalI * looperInner;
                    for(int j=0; j<looperInner; j++) {
                        int index = start + j;
                        BatchInsertModel model = new BatchInsertModel();
                        model.setField1(String.valueOf(index));
                        model.setField2(String.valueOf(index));
                        this.batchInsertMapper.insert1(model);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }

        executorService.shutdown();
        while(!executorService.awaitTermination(1, TimeUnit.SECONDS));

        Date endTime = new Date();

        Assert.assertEquals(count, this.batchInsertMapper.count());
        System.out.println("耗时：" + (endTime.getTime() - startTime.getTime()) + "毫秒");
    }

    // foreach values insert语句插入
    @Test
    public void testInsert2() throws InterruptedException {
        Date startTime = new Date();
        int count = 50000;
        int concurrentThreads = 20;
        int looperInner = count / concurrentThreads;
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0; i<concurrentThreads; i++) {
            int finalI = i;
            executorService.submit(()-> {
                try {
                    int start = finalI * looperInner;
                    List<BatchInsertModel> modelList = new ArrayList<>();
                    for(int j=0; j<looperInner; j++) {
                        int index = start + j;
                        BatchInsertModel model = new BatchInsertModel();
                        model.setField1(String.valueOf(index));
                        model.setField2(String.valueOf(index));
                        modelList.add(model);

                        if(modelList.size() >= 100 || j+1 >= looperInner) {
                            this.batchInsertMapper.insert2(modelList);
                            modelList = new ArrayList<>();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }

        executorService.shutdown();
        while(!executorService.awaitTermination(1, TimeUnit.SECONDS));

        Date endTime = new Date();

        Assert.assertEquals(count, this.batchInsertMapper.count());
        System.out.println("耗时：" + (endTime.getTime() - startTime.getTime()) + "毫秒");
    }

    // SqlSession BATCH插入
    @Test
    public void testInsert3() throws InterruptedException {
        Date startTime = new Date();
        int count = 50000;
        int concurrentThreads = 20;
        int looperInner = count / concurrentThreads;
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0; i<concurrentThreads; i++) {
            int finalI = i;
            executorService.submit(()-> {
                try {
                    int start = finalI * looperInner;
                    SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
                    int counter = 0;
                    for(int j=0; j<looperInner; j++) {
                        int index = start + j;
                        BatchInsertModel model = new BatchInsertModel();
                        model.setField1(String.valueOf(index));
                        model.setField2(String.valueOf(index));
                        this.batchInsertMapper.insert1(model);
                        counter++;

                        if(counter >= 100 || j+1 >= looperInner) {
                            sqlSession.commit();
                            counter = 0;
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }

        executorService.shutdown();
        while(!executorService.awaitTermination(1, TimeUnit.SECONDS));

        Date endTime = new Date();

        Assert.assertEquals(count, this.batchInsertMapper.count());
        System.out.println("耗时：" + (endTime.getTime() - startTime.getTime()) + "毫秒");
    }
}
