# Software Development Life Cycle (SDLC)



## SDLC 阶段

软件开发生命周期（SDLC）概述了构建软件应用程序所需的几个任务。开发过程将经历如开发人员添加新功能和修复软件漏洞等几个阶段。

SDLC 过程的细节因团队而异。不过，下面我们会概述一些常见的 SDLC 阶段。

**规划**

规划阶段通常包括成本效益分析、调度、资源评估和分配等任务。开发团队从利益相关者们（如客户、内部和外部专家以及经理）那里收集需求，以创建**软件需求规格文档**。

该文档设定了期望，并定义了有助于项目规划的共同目标。团队评估成本，制定时间表，并形成实现目标的详细计划。

**设计**

在设计阶段，软件工程师会对需求进行分析，并确定建构**软件的最佳解决方案**。例如，他们可能会考虑集成现有的模块，做出**技术选择**，并确定开发工具。他们会研究怎样才能将新软件与组织现在可能拥有的 IT 基础设施实现最佳整合。

**实现**

在实现阶段，开发团队会对产品进行编码。他们通过分析需求来确定可以逐日完成的、较小的编码任务，以实现最终结果。

**测试**

开发团队会结合**自动化测试和手动测试**，来检查软件中的漏洞。质量分析包括测试软件是否存在错误，以及检查其是否满足客户的要求。因为很多团队会立即测试编写的代码，所以测试阶段通常与开发阶段并行进行。

**部署**

团队开发软件时，在用户有权访问的软件副本之外，会在不同的软件副本上进行编码和测试。客户使用的软件称为*生产环境*，而其他副本则称为仍处于*构建环境*或测试环境。

独立的构建环境和生产环境可以确保客户即使在软件发生变更或升级时也可以继续使用软件。部署阶段包括了一系列将最新的构建副本移至生产环境的任务，例如**打包、配置环境和安装**。

**维护**

在维护阶段，除了其他任务外，团队还需要修复漏洞，解决客户问题，并管理软件变更。此外，开发团队还会**监控整个系统的性能、安全性**和用户体验，以确定改进现有软件的新方法。



## SDLC 模型

>软件开发模式参考`https://blog.csdn.net/qq_38661984/article/details/89117175`

软件开发生命周期（SDLC）模型从概念上有条理地展示了 SDLC，从而帮助组织实现 SDLC。不同的模型以不同的时间顺序排列 SDLC 阶段以优化开发周期。下面我们将介绍一些流行的 SDLC 模型。

### **瀑布**

瀑布模型按顺序排列所有阶段，这样每个新阶段都依赖于前一阶段的结果。从概念上讲，设计从一个阶段流向下一个阶段，就像瀑布一样。

优点和缺点：瀑布模型为项目管理规定纪律，并在每个阶段的最后提供有形的输出。然而，一旦某个阶段被认为已完成，就几乎没有更改的余地了，因为更改会影响软件的交付时间、成本和质量。因此，该模型最适用于任务易于安排和管理、需求可以精确定义的小型软件开发项目。



### **迭代**

迭代过程建议，团队应从需求的一个小子集着手进行软件开发。然后，随着时间的推移，迭代地增强版本，直到软件可以用于生产环境。团队在每一轮迭代结束时都会生成一个新的软件版本。

优点和缺点：识别和管理风险是很容易的，因为在迭代过程中，需求可能会发生改变。然而，重复的周期可能导致范围的变化和对资源的低估。



### **螺旋**

螺旋模型将迭代模型的小型重复循环与瀑布模型的线性顺序流相结合，以优先考虑风险分析。通过在每个阶段构建原型，您可以使用螺旋模型来确保软件的逐步发布和优化。

优点和缺点：螺旋模型适用于需要频繁更改的大型复杂项目。然而，对于范围有限的小型项目来说，它可能很昂贵。



### **敏捷**

>什么是敏捷开发？`https://blog.csdn.net/csdn15556927540/article/details/90712308`
>
>基于Jira scrum敏捷开发模式实施`https://www.atlassian.com/agile/tutorials/how-to-do-scrum-with-jira-software`

敏捷模型将 SDLC 阶段安排为几个开发周期。开发团队快速地迭代各个阶段，在每个周期中只交付小规模的、增量的软件变更。他们持续地评估需求、计划和结果，以便能够快速响应变化。敏捷模型既是迭代的，也是增量的，这使得它比其他过程模型更有效。

优点和缺点：在面临复杂项目时，快速的开发周期能够帮助团队在问题出现的早期或其恶化为重大问题之前识别和解决问题。它们还可以让客户和涉众参与进来，在整个项目生命周期中获得反馈。然而，过度依赖客户反馈可能导致过度的范围变更或项目中途终止。





## SDLC Java 实践

目标：模型设计和实现能力、模型优化和完善能力、模型调试和跟踪推理能力。

**实现**

- Java 的基础
- SSM、Spring Cloud
- MySQL
- Redis
- RabbitMQ

**测试**

- JUnit、Mockito
- JMH、JMeter

**部署**

- Linux 的使用
- Docker、Docker Compose、Docker Swarm
- Ansible

**维护**

- 监控：prometheus、skywalking、ELK
- 安全防护：openresty + lua 实现 CC 防御、naxsi
- 在线调试（Arthas、JWDP）