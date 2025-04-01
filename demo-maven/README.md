# maven



## 运行原理

Maven的运行原理主要基于其项目对象模型（Project Object Model，POM）、依赖管理系统、构建生命周期以及插件机制等核心组件和概念。以下是Maven运行原理的详细解释：

一、项目对象模型（POM）

Maven通过POM来描述项目的构建、依赖以及其他相关信息。POM是一个XML文件（通常是pom.xml），它包含了项目的groupId（组织或项目的唯一标识符）、artifactId（项目的唯一标识符，在groupId下唯一）、version（项目的版本号）等基本坐标信息，以及项目的依赖、插件、构建配置等详细信息。Maven根据POM文件中的信息来构建项目、管理依赖以及执行其他相关任务。

二、依赖管理系统

Maven的依赖管理系统是其核心功能之一。它通过坐标机制来唯一定位一个具体的依赖，并自动从远程仓库或本地仓库下载所需的依赖及其传递性依赖。Maven还提供了依赖冲突解决策略，如最短路径原则、声明优先原则和依赖排除等，以确保项目中使用的依赖版本是正确和一致的。

三、构建生命周期

Maven拥有三套相互独立的生命周期：Clean Lifecycle、Default Lifecycle和Site Lifecycle。其中，Default Lifecycle是构建的核心部分，它包括了编译、测试、打包、部署等阶段。每个生命周期都由一组阶段（Phase）组成，每个阶段都有特定的目标和任务。当运行Maven的某个阶段时，它之前的所有阶段都会被依次执行。这种预定义的默认行为简化了Maven的使用，并确保了构建过程的一致性和可重复性。

四、插件机制

Maven的插件机制是其灵活性和可扩展性的基础。Maven提供了大量的内置插件，用于执行构建过程中的各种任务，如编译代码、运行测试、打包项目等。此外，用户还可以根据需要编写自定义插件或引入第三方插件来扩展Maven的功能。插件与Maven的生命周期紧密集成，可以在特定的生命周期阶段执行特定的任务。

五、Maven的运行过程

1. **解析POM文件**：Maven首先解析项目的POM文件，获取项目的坐标、依赖、插件等信息。
2. **下载依赖**：根据POM文件中的依赖信息，Maven从远程仓库或本地仓库下载所需的依赖及其传递性依赖。
3. **执行生命周期阶段**：根据用户指定的生命周期阶段（如clean、compile、test、package、install、deploy等），Maven依次执行该阶段及其之前的所有阶段。
4. **执行插件任务**：在生命周期阶段的执行过程中，Maven会调用相应的插件来执行特定的任务。
5. **生成构建结果**：最终，Maven会生成构建结果，如编译后的类文件、打包后的JAR文件等，并将它们放置在指定的目录中。

综上所述，Maven的运行原理是基于其POM、依赖管理系统、构建生命周期以及插件机制等核心组件和概念的。通过这些组件和概念的协同工作，Maven能够自动化地构建项目、管理依赖并生成构建结果。



Maven的运行过程涉及多个环节，包括项目的清理、编译、测试、打包、安装、部署等。以下是Maven运行过程的详细解释：

一、Maven生命周期

Maven的生命周期是对所有构建过程的抽象和统一，包括几乎所有的构建步骤。Maven有三套独立的生命周期，分别是：

1. **clean生命周期**：主要用于对项目进行清理。它包含以下阶段：
   - **pre-clean**：执行清理前需要做的工作。
   - **clean**：清理上次构建生成的文件。
2. **default生命周期**：用于建立项目，是Maven最常用的生命周期。它包含以下阶段：
   - **validate**：验证工程是否正确，所有需要的资源是否可用。
   - **compile**：将项目的源代码编译成字节码文件。
   - **test**：使用合适的单元测试框架来测试已编译的源代码，这些测试不需要打包和部署。
   - **package**：把已编译的代码打包成可发布的格式，如JAR、WAR等。
   - **integration-test**：如有需要，将包处理和发布到一个能够进行集成测试的环境。
   - **verify**：运行所有检查，验证包是否有效且达到质量标准。
   - **install**：把包安装到Maven本地仓库，使其可以被其他工程作为依赖来使用。
   - **deploy**：在集成或者发布环境下执行，将最终版本的包拷贝到远程仓库，使得其他的开发者或者工程可以共享。
3. **site生命周期**：用于创建和发布项目站点。它包含以下阶段：
   - **pre-site**：生成项目站点前要做的工作。
   - **site**：生成项目网站的文档。
   - **post-site**：生成项目站点后要做的工作。

二、Maven运行过程详解

1. **依赖管理**：

   Maven会根据项目的`pom.xml`文件中配置的依赖项信息，自动下载所需的依赖库，并添加到项目的classpath中。依赖的下载顺序是首先查找本地Maven仓库，如果本地仓库中没有找到，则按照`pom.xml`文件中配置的顺序搜索远程仓库，包括私有仓库和中央仓库。

2. **编译**：

   在项目的源代码编写完成后，Maven会编译源代码。通常将源代码放在`src/main/java`目录下，并按照Maven的标准项目结构组织代码。编译过程由`maven-compiler-plugin`插件完成，可以在`pom.xml`文件中配置该插件的源代码和目标Java版本。

3. **测试**：

   编写单元测试是良好的实践，单元测试用例通常存放在`src/test/java`目录下，并使用JUnit或其他测试框架进行编写。Maven会运行这些单元测试，并生成测试报告。测试过程由`maven-surefire-plugin`等插件完成。

4. **打包**：

   Maven会将编译后的代码和资源文件打包成可部署的应用程序。打包类型可以在`pom.xml`文件中通过`<packaging>`标签指定，如JAR、WAR等。打包过程由`maven-jar-plugin`、`maven-war-plugin`等插件完成。

5. **安装和部署**：

   打包完成后，Maven可以将包安装到本地仓库，使其可以被其他Maven项目作为依赖来使用。此外，还可以将包部署到远程仓库，如私服或中央仓库，以便其他开发者或项目可以共享。安装和部署过程分别由`install`和`deploy`阶段完成。

三、Maven插件的使用

Maven插件是一组可以执行特定任务的工具，每个插件包含一个或多个目标（Goals），这些目标对应于具体的任务。要在Maven项目中使用插件，需要在`pom.xml`文件的`<build>`部分声明插件，并指定插件的`groupId`、`artifactId`、`version`以及配置参数等。

综上所述，Maven的运行过程是一个涉及多个环节和多个插件的复杂过程。通过合理配置`pom.xml`文件和选择适当的插件，可以实现对项目构建过程的精细控制。



## maven 3.8.1 之后版本 blocked http

> `https://gist.github.com/vegaasen/1d545aafeda867fcb48ae3f6cd8fd7c7`

~/.m2/settings.xml 添加如下 mirror

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.2.0 http://maven.apache.org/xsd/settings-1.2.0.xsd">
  ...
    <mirrors>
        <mirror>
            <id>maven-default-http-blocker</id>
            <mirrorOf>external:dont-match-anything-mate:*</mirrorOf>
            <name>Pseudo repository to mirror external repositories initially using HTTP.</name>
            <url>http://0.0.0.0/</url>
            <blocked>false</blocked>
        </mirror>
    </mirrors>
  ...
</settings>
```
