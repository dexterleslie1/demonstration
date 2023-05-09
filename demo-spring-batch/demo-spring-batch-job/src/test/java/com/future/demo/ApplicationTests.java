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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ApplicationTests {

    @Resource
    JobLauncher jobLauncher;

    @Resource(name =  "job")
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

}
