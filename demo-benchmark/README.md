# 基准测试

## 介绍

基准测试（Benchmark Testing）是一种用于评估软件或硬件系统性能的方法。它通常通过运行一组预定的测试任务来测量系统的响应时间、吞吐量、资源利用率等关键性能指标。基准测试广泛应用于软件开发、数据库管理、云计算、大数据分析等领域，帮助开发者、系统管理员和架构师了解系统的性能瓶颈，优化资源配置，确保系统能够满足预期的性能需求。

基准测试的基本步骤通常包括：

1. **确定测试目标**：明确测试的目的，比如评估新硬件的性能提升、比较不同数据库查询的性能、测试软件升级后的响应速度等。
2. **设计测试场景**：根据测试目标设计具体的测试场景，包括测试数据、负载模型、测试执行策略等。测试数据应尽可能接近实际应用场景的数据分布和规模。
3. **选择测试工具**：根据测试需求选择合适的基准测试工具。这些工具可以是开源的，也可以是商业的，用于生成负载、监控性能指标、分析结果等。
4. **执行测试**：在控制环境中执行测试，确保测试条件的一致性。这包括确保测试环境（硬件、操作系统、软件版本等）与生产环境尽可能相似。
5. **收集和分析数据**：收集测试过程中的性能指标数据，如响应时间、吞吐量、CPU利用率、内存使用等。然后分析这些数据，识别性能瓶颈和改进点。
6. **报告结果**：将测试结果以报告的形式呈现，包括测试目标、测试环境、测试方法、性能指标数据、问题分析、改进建议等。
7. **优化和验证**：根据测试结果对系统进行优化，如调整配置、优化代码、升级硬件等。然后重新执行基准测试，验证优化效果。

基准测试对于确保软件系统的性能和稳定性至关重要。通过持续的基准测试，组织可以及时发现并解决性能问题，提高系统的整体效率和用户体验。同时，基准测试也是评估新技术、新硬件引入前后性能变化的有效手段。



## 制作 SpringBoot 应用容器镜像作为基准测试目标

编译 SpringBoot 基准测试项目，源代码 [链接](https://gitee.com/dexterleslie/demonstration/tree/main/demo-benchmark/demo-spring-boot-benchmark)

>注意：编译此项目需要使用 JDK17。

```bash
./build.sh
```

推送容器镜像

```bash
./push.sh
```



## 部署基于容器的单个 SpringBoot 基准测试目标

步骤如下：

在开发环境实例中制作 SpringBoot 应用容器镜像作为基准测试目标：参考 <a href="/benchmark/README.html#制作-springboot-应用容器镜像作为基准测试目标" target="_blank">链接</a>

在部署环境实例中使用容器运行 SpringBoot 应用作为基准测试目标

- 创建 CentOS8 实例

- 内核参数文件描述符限制调优：参考 <a href="/linux/README.html#设置" target="_blank">链接</a>

- 参考 <a href="/docker/docker的安装.html#使用-dcli-安装" target="_blank">链接</a> 安装 Docker 环境

- 运行 SpringBoot 应用

  ```bash
  docker compose up -d
  ```

- 访问 `http://192.168.1.x` 测试基准测试目标是否正常启动。

  


## 部署基于容器单个 OpenResty 基准测试目标

步骤如下：

- 创建 CentOS8 实例

- 内核参数文件描述符限制调优：参考 <a href="/linux/README.html#设置" target="_blank">链接</a>

- 参考 <a href="/docker/docker的安装.html#使用-dcli-安装" target="_blank">链接</a> 安装 Docker 环境

- 复制本站示例用于启动基准测试目标 [链接](https://gitee.com/dexterleslie/demonstration/tree/main/demo-benchmark/demo-openresty-standalone-benchmark)

- 启动基准测试目标

  ```bash
  docker compose up -d
  ```

- 访问 `http://192.168.1.x` 测试基准测试目标是否正常启动。

