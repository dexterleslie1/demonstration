# 演示spring-batch用法

## 说明

> NOTE: 未完整学习，在工作过程中实际应用再把缺失的知识点补回。

## spring-batch概念

> JobLauncher: 作业调度器，作为启动主要入口
> Job: 作业，需要执行的任务逻辑
> Step: 作业步骤，一个job作业由1个或者多个Step组成，完成所有Step操作，一个完整Job才算执行结束。
> ItemReader: Step步骤执行过程中数据输入，可以从数据源（文件系统、数据库、队列等）中读取Item（数据记录）。
> ItemWriter: Step步骤执行过程中数据输出，将Item（数据记录）写入数据源（文件系统、数据库、队列等）。
> ItemProcessor: Item数据加工逻辑（输入），比如：数据清洗、数据转换、数据过滤、数据校验等
> JobRepository: 保存Job或者检索Job的信息。SpringBatch需要持久化Job（可以选择数据库或者内存），JobRepository就是持久化的接口。

## 知识点

> 查看测试用例回忆所有知识点