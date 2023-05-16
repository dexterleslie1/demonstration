package com.future.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ApplicationTests {

    @Resource
    JobLauncher jobLauncher;

    @Resource(name = "job")
    Job job;

    @Resource(name = ConfigRunIdIncrementer.Prefix + "Job")
    Job job1;
    @Resource
    ConfigRunIdIncrementer configRunIdIncrementer;
    @Resource
    JobExplorer jobExplorer;

    @Resource(name = ConfigTimestampIncrementer.Prefix + "Job")
    Job job2;
    @Resource
    ConfigTimestampIncrementer configTimestampIncrementer;

    @Resource(name = ConfigJobExecutionListener.Prefix + "Job")
    Job job3;
    @Resource
    MyJobExecutionListener jobExecutionListener;
    @Resource
    ConfigJobExecutionListener configJobExecutionListener;

    @Resource(name = ConfigJobContext.Prefix + "Job")
    Job job5;
    @Resource
    ConfigJobContext configJobContext;

    @Resource(name = ConfigChunkTaskletContext.Prefix + "Job")
    Job job6;
    @Resource
    ConfigChunkTaskletContext configChunkTaskletContext;

    @Resource(name = ConfigStepExecutionListener.Prefix + "Job")
    Job jobStepExecutionListener;
    @Resource
    ConfigStepExecutionListener configStepExecutionListener;

    @Resource(name = ConfigChunkExecutionListener.Prefix + "Job")
    Job jobChunkExecutionListener;
    @Resource
    ConfigChunkExecutionListener configChunkExecutionListener;

    /**
     * 测试设置作业参数
     *
     * @throws Exception
     */
    @Test
    public void testSettingJobParameters() throws Exception {
        // 每次随机生成param1才能够使job每次都执行，否则job只执行一次，因为一个 job+参数 表示一个唯一的job
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("param1", UUID.randomUUID().toString())
                .addString("param2", UUID.randomUUID().toString())
                .toJobParameters();
        this.jobLauncher.run(job, jobParameters);
    }

    /**
     * 测试获取作业参数
     *
     * @throws Exception
     */
    @Test
    public void testGettingJobParameters() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("param1", UUID.randomUUID().toString())
                .addString("param2", UUID.randomUUID().toString())
                .toJobParameters();
        this.jobLauncher.run(job, jobParameters);
    }

    /**
     * 测试作业参数校验器
     *
     * @throws Exception
     */
    @Test
    public void testJobParametersValidator() throws Exception {
        // 场景: 没有提供param1参数
        JobParameters jobParameters = new JobParametersBuilder()
                .toJobParameters();
        try {
            this.jobLauncher.run(job, jobParameters);
            Assert.fail("没有抛出预期异常");
        } catch (JobParametersInvalidException ex) {
            Assert.assertEquals("没有提供param1参数", ex.getMessage());
        }

        // 场景: 没有提供param2参数
        jobParameters = new JobParametersBuilder()
                .addString("param1", UUID.randomUUID().toString())
                .toJobParameters();
        try {
            this.jobLauncher.run(job, jobParameters);
            Assert.fail("没有抛出预期异常");
        } catch (JobParametersInvalidException ex) {
            Assert.assertEquals("没有提供param2参数", ex.getMessage());
        }

        // 场景: 提供param1参数
        jobParameters = new JobParametersBuilder()
                .addString("param1", UUID.randomUUID().toString())
                .addString("param2", UUID.randomUUID().toString())
                .toJobParameters();
        this.jobLauncher.run(job, jobParameters);
    }

    /**
     * 测试作业自增参数
     *
     * @throws Exception
     */
    @Test
    public void testRunIdIncrementer() throws Exception {
        // 获取之前的run.id，否则run.id参数为null
        JobParameters jobParameters = new JobParametersBuilder(this.jobExplorer)
                .getNextJobParameters(job1).toJobParameters();
        this.jobLauncher.run(job1, jobParameters);

        // todo 似乎用法不科学
        // 获取之前的run.id，否则run.id参数为null
        jobParameters = new JobParametersBuilder(this.jobExplorer)
                .getNextJobParameters(job1).toJobParameters();
        this.jobLauncher.run(job1, jobParameters);

        Assert.assertEquals(2, this.configRunIdIncrementer.Counter);
    }

    /**
     * 演示自定义时间戳增量参数
     * todo 似乎增量参数设计上没有什么意义，因为可以使用普通的JobParameters实现同样的逻辑
     *
     * @throws Exception
     */
    @Test
    public void testTimestampIncrementer() throws Exception {
        // 获取之前的run.id，否则run.id参数为null
        JobParameters jobParameters = new JobParametersBuilder(this.jobExplorer)
                .getNextJobParameters(job2).toJobParameters();
        this.jobLauncher.run(job2, jobParameters);

        // 获取之前的run.id，否则run.id参数为null
        jobParameters = new JobParametersBuilder(this.jobExplorer)
                .getNextJobParameters(job2).toJobParameters();
        this.jobLauncher.run(job2, jobParameters);

        Assert.assertEquals(2, this.configTimestampIncrementer.Counter);
    }

    /**
     * 测试作业执行状态监听器
     *
     * @throws Exception
     */
    @Test
    public void testJobExecutionListener() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("random", UUID.randomUUID().toString())
                .toJobParameters();
        this.jobLauncher.run(job3, jobParameters);

        Assert.assertEquals(1, this.jobExecutionListener.CounterBeforeJob);
        Assert.assertEquals(1, this.jobExecutionListener.CounterAfterJob);
        Assert.assertEquals(1, this.configJobExecutionListener.Counter);
    }

    /**
     * 测试使用job或者step上下文共享数据
     */
    @Test
    public void testJobOrStepContextSharingDatum() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("random", UUID.randomUUID().toString())
                .toJobParameters();
        this.jobLauncher.run(job5, jobParameters);

        Assert.assertFalse(this.configJobContext.isStepContextValueShared);
        Assert.assertTrue(this.configJobContext.isJobContextValueShared);
    }

    /**
     * 测试tasklet返回值RepeatStatus
     */
    @Test
    public void testTaskletRepeatStatus() throws Exception {
        // 测试RepeatStatus.FINISHED执行一次tasklet就停止step
        this.configJobExecutionListener.Counter = 0;
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("random", UUID.randomUUID().toString())
                .toJobParameters();
        this.jobLauncher.run(job3, jobParameters);
        Assert.assertEquals(1, this.configJobExecutionListener.Counter);

        // 测试RepeatStatus.CONTINUABLE执行两次tasklet才停止step
        this.configJobExecutionListener.Counter = 0;
        jobParameters = new JobParametersBuilder()
                .addString("random", UUID.randomUUID().toString())
                .addString("ifTestRepeatStatus", "true")
                .toJobParameters();
        this.jobLauncher.run(job3, jobParameters);
        Assert.assertEquals(2, this.configJobExecutionListener.Counter);
    }

    /**
     * 测试验证结论: 内部启动3个chunk分别执行2次itemReader->itemProcessor流程(2个iteration共处理6个item)，2个iteration分别一次执行2次itemWriter
     *
     * @throws Exception
     */
    @Test
    public void testChunkTasklet() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("random", UUID.randomUUID().toString())
                .toJobParameters();
        this.jobLauncher.run(job6, jobParameters);
        Assert.assertEquals(6, this.configChunkTaskletContext.itemReaderList.size());
        Assert.assertArrayEquals(Stream.of(1, 2, 3, 4, 5, 6).map(o -> "item" + o).toArray(), this.configChunkTaskletContext.itemReaderList.stream().sorted().toArray());
        Assert.assertEquals(6, this.configChunkTaskletContext.itemProcessorList.size());
        Assert.assertArrayEquals(Stream.of(1, 2, 3, 4, 5, 6).map(o -> "item" + o + "-processed").toArray(), this.configChunkTaskletContext.itemProcessorList.stream().sorted().toArray());
        Assert.assertEquals(2, this.configChunkTaskletContext.itemWriterList.size());
        Assert.assertArrayEquals(new String[]{"item1-processed", "item2-processed", "item3-processed"}, this.configChunkTaskletContext.itemWriterList.get(1).stream().sorted().toArray());
        Assert.assertArrayEquals(new String[]{"item4-processed", "item5-processed", "item6-processed"}, this.configChunkTaskletContext.itemWriterList.get(0).stream().sorted().toArray());
    }

    /**
     * 测试 StepExecutionListener
     *
     * @throws Exception
     */
    @Test
    public void testStepExecutionListener() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("random", UUID.randomUUID().toString())
                .toJobParameters();
        this.jobLauncher.run(this.jobStepExecutionListener, jobParameters);
        Assert.assertEquals(1, this.configStepExecutionListener.CounterBeforeStep.get());
        Assert.assertEquals(1, this.configStepExecutionListener.CounterAfterStep.get());
    }

    /**
     * 测试 ChunkExecutionListener
     *
     * @throws Exception
     */
    @Test
    public void testChunkExecutionListener() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("random", UUID.randomUUID().toString())
                .toJobParameters();
        this.jobLauncher.run(this.jobChunkExecutionListener, jobParameters);
        Assert.assertEquals(1, this.configChunkExecutionListener.CounterBeforeChunk.get());
        Assert.assertEquals(0, this.configChunkExecutionListener.CounterAfterChunk.get());
        Assert.assertEquals(1, this.configChunkExecutionListener.CounterAfterChunkError.get());
    }

    @Resource(name = ConfigJobExecutionDecider.Prefix + "Job")
    Job jobExecutionDecider;
    @Resource
    ConfigJobExecutionDecider configJobExecutionDecider;
    /**
     * 测试JobExecutionDecider
     * @throws Exception
     */
    @Test
    public void testJobExecutionDecider() throws Exception {
        // 测试stepType参数为A
        this.configJobExecutionDecider.Counter.clear();
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("random", UUID.randomUUID().toString())
                .addString("stepType", "A")
                .toJobParameters();
        this.jobLauncher.run(this.jobExecutionDecider, jobParameters);
        Assert.assertEquals(1, this.configJobExecutionDecider.Counter.get("First"));
        Assert.assertEquals(1, this.configJobExecutionDecider.Counter.get("A"));
        Assert.assertFalse(this.configJobExecutionDecider.Counter.containsKey("B"));
        Assert.assertFalse(this.configJobExecutionDecider.Counter.containsKey("C"));

        // 测试stepType参数为B
        this.configJobExecutionDecider.Counter.clear();
        jobParameters = new JobParametersBuilder()
                .addString("random", UUID.randomUUID().toString())
                .addString("stepType", "B")
                .toJobParameters();
        this.jobLauncher.run(this.jobExecutionDecider, jobParameters);
        Assert.assertEquals(1, this.configJobExecutionDecider.Counter.get("First"));
        Assert.assertFalse(this.configJobExecutionDecider.Counter.containsKey("A"));
        Assert.assertEquals(1, this.configJobExecutionDecider.Counter.get("B"));
        Assert.assertFalse(this.configJobExecutionDecider.Counter.containsKey("C"));
    }

    @Resource(name = ConfigFlowStep.Prefix + "Job")
    Job jobFlowStep;
    @Resource
    ConfigFlowStep configFlowStep;
    /**
     * 测试FlowStep
     * @throws Exception
     */
    @Test
    public void testFlowStep() throws Exception {
        // 测试stepType参数为A
        this.configFlowStep.Counter.clear();
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("random", UUID.randomUUID().toString())
                .toJobParameters();
        this.jobLauncher.run(this.jobFlowStep, jobParameters);
        Assert.assertEquals(1, this.configFlowStep.Counter.get("TaskletA"));
        Assert.assertEquals(1, this.configFlowStep.Counter.get("TaskletB1"));
        Assert.assertEquals(1, this.configFlowStep.Counter.get("TaskletB2"));
        Assert.assertEquals(1, this.configFlowStep.Counter.get("TaskletB3"));
        Assert.assertEquals(1, this.configFlowStep.Counter.get("TaskletC"));
    }

}
