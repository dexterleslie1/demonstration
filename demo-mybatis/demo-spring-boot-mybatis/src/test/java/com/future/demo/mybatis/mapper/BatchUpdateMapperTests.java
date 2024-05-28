package com.future.demo.mybatis.mapper;

import com.future.demo.mybatis.Application;
import com.future.demo.mybatis.entity.BatchInsertModel;
import com.future.demo.mybatis.entity.BatchUpdateModel;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class BatchUpdateMapperTests {
    @Resource
    BatchUpdateMapper batchUpdateMapper;

    Random random = new Random();
    List<Long> idList = new ArrayList<>();

    @Before
    public void setup() throws InterruptedException {
        this.batchUpdateMapper.truncate();

        int count = 50000;
        int concurrentThreads = 20;
        int looperInner = count / concurrentThreads;
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0; i<concurrentThreads; i++) {
            int finalI = i;
            executorService.submit(()-> {
                try {
                    int start = finalI * looperInner;
                    List<BatchUpdateModel> modelList = new ArrayList<>();
                    for(int j=0; j<looperInner; j++) {
                        int index = start + j;
                        BatchUpdateModel model = new BatchUpdateModel();
                        model.setField1(String.valueOf(index));
                        model.setField2(String.valueOf(index));
                        modelList.add(model);

                        if(modelList.size() >= 100 || j+1 >= looperInner) {
                            this.batchUpdateMapper.insert(modelList);
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

        Assert.assertEquals(count, this.batchUpdateMapper.count());

        idList = this.batchUpdateMapper.findAllId();
        Assert.assertEquals(count, idList.size());
    }

    // 普通update语句一条条更新
    @Test
    public void testUpdate1() throws InterruptedException {
        Date startTime = new Date();
        int count = 50000;
        int concurrentThreads = 20;
        int looperInner = count / concurrentThreads;
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0; i<concurrentThreads; i++) {
            executorService.submit(()-> {
                try {
                    int size = idList.size();
                    for(int j=0; j<looperInner; j++) {
                        int randomInt = random.nextInt(size);
                        BatchUpdateModel model = new BatchUpdateModel();
                        model.setId(idList.get(randomInt));
                        model.setField1(UUID.randomUUID().toString());
                        model.setField2(UUID.randomUUID().toString());
                        this.batchUpdateMapper.update1(model);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }

        executorService.shutdown();
        while(!executorService.awaitTermination(1, TimeUnit.SECONDS));

        Date endTime = new Date();
        System.out.println("耗时：" + (endTime.getTime() - startTime.getTime()) + "毫秒");
    }

    // 批量update set xxx=case when ... then ... when then ... where in(...)
    @Test
    public void testUpdate2() throws InterruptedException {
        Date startTime = new Date();
        int count = 50000;
        int concurrentThreads = 20;
        int looperInner = count / concurrentThreads;
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0; i<concurrentThreads; i++) {
            executorService.submit(()-> {
                try {
                    List<BatchUpdateModel> modelList = new ArrayList<>();
                    int size = idList.size();
                    for(int j=0; j<looperInner; j++) {
                        int randomInt = random.nextInt(size);
                        BatchUpdateModel model = new BatchUpdateModel();
                        model.setId(idList.get(randomInt));
                        model.setField1(UUID.randomUUID().toString());
                        model.setField2(UUID.randomUUID().toString());
                        modelList.add(model);

                        if(modelList.size() >= 100 || j+1 >= looperInner) {
                            this.batchUpdateMapper.update2(modelList);
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
        System.out.println("耗时：" + (endTime.getTime() - startTime.getTime()) + "毫秒");
    }
}
