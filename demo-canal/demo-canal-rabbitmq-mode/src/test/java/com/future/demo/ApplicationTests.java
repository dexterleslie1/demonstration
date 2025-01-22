package com.future.demo;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.future.demo.mapper.TestMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ApplicationTests {

    @Resource
    Receiver receiver;
    @Resource
    TestMapper testMapper;

    @Test
    public void test() throws InterruptedException {
        this.testMapper.delete(Wrappers.query());

        TimeUnit.SECONDS.sleep(2);

        this.clearCounter();

        int concurrent = 10;
        int loopInsert = 500;
        int loopDelete = 10000;
        int loopUpdate = 10000;

        // 测试 insert
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (int j = 0; j < concurrent; j++) {
            executorService.submit(() -> {
                for (int i = 0; i < loopInsert; i++) {
                    this.testMapper.insert(new TestModel() {{
                        setCreateTime(new Date());
                    }});
                }
            });
        }

        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

        // 测试 update
        TimeUnit.SECONDS.sleep(1);
        executorService = Executors.newCachedThreadPool();

        // 查询所有id
        List<TestModel> modelList = this.testMapper.selectList(Wrappers.query());
        int countUpdate = modelList.size();
        modelList = Collections.synchronizedList(modelList);
        Random random = new Random();

        List<TestModel> finalModelList = modelList;
        for (int j = 0; j < concurrent; j++) {
            executorService.submit(() -> {
                for (int i = 0; i < loopUpdate; i++) {
                    TestModel testModel = null;
                    synchronized (this) {
                        if (finalModelList.size() <= 0) {
                            return;
                        }

                        int randomInt = random.nextInt(finalModelList.size());
                        testModel = finalModelList.get(randomInt);
                        finalModelList.remove(testModel);
                    }

                    testModel.setCreateTime(new Date());
                    this.testMapper.updateById(testModel);
                }
            });
        }

        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

        // 测试 delete
        executorService = Executors.newCachedThreadPool();

        // 查询所有id
        List<Long> idList = this.testMapper.selectList(Wrappers.query()).stream().map(TestModel::getId).collect(Collectors.toList());
        int countDelete = idList.size();
        idList = Collections.synchronizedList(idList);

        List<Long> finalIdList = idList;
        for (int j = 0; j < concurrent; j++) {
            executorService.submit(() -> {
                for (int i = 0; i < loopDelete; i++) {
                    Long id = -1L;
                    synchronized (this) {
                        if (finalIdList.size() <= 0) {
                            return;
                        }

                        int randomInt = random.nextInt(finalIdList.size());
                        id = finalIdList.get(randomInt);
                        finalIdList.remove(id);
                    }

                    this.testMapper.deleteById(id);
                }
            });
        }

        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

        TimeUnit.SECONDS.sleep(3);

        Assert.assertEquals(concurrent * loopInsert, this.receiver.getCounterInsert().get());
        Assert.assertEquals(countUpdate, this.receiver.getCounterUpdate().get());
        Assert.assertEquals(countDelete, this.receiver.getCounterDelete().get());

        // 测试一条update语句修改多条记录情况
        executorService = Executors.newCachedThreadPool();

        for (int j = 0; j < concurrent; j++) {
            executorService.submit(() -> {
                for (int i = 0; i < loopInsert; i++) {
                    this.testMapper.insert(new TestModel() {{
                        setCreateTime(new Date());
                    }});
                }
            });
        }

        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;
        TimeUnit.SECONDS.sleep(2);

        this.clearCounter();

        int count = this.testMapper.selectCount(Wrappers.query());

        UpdateWrapper<TestModel> updateWrapper = Wrappers.update();
        updateWrapper.set("createTime", new Date());
        this.testMapper.update(null, updateWrapper);

        TimeUnit.SECONDS.sleep(2);

        Assert.assertEquals(count, this.receiver.getCounterUpdate().get());

        // 测试一条delete语句删除多条记录情况
        this.clearCounter();

        count = this.testMapper.selectCount(Wrappers.query());

        this.testMapper.delete(Wrappers.query());

        TimeUnit.SECONDS.sleep(2);

        Assert.assertEquals(count, this.receiver.getCounterDelete().get());
    }

    void clearCounter() {
        this.receiver.getCounterInsert().set(0);
        this.receiver.getCounterDelete().set(0);
        this.receiver.getCounterUpdate().set(0);
    }

}
