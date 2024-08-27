# `ubuntu/debian`配置修改源

## `sources.list`配置解析

在Debian和基于Debian的Linux发行版（如Ubuntu）中，`/etc/apt/sources.list` 文件是用于定义APT（Advanced Package Tool）从哪些仓库（repositories）中获取软件包及其源代码的。这个文件通常包含一系列的`deb`和`deb-src`行。

1. **deb**：
   - `deb`行定义了APT从哪个仓库获取二进制软件包（即已经编译好的、可以直接安装的软件包）。
   - 例如：`deb http://archive.ubuntu.com/ubuntu/ focal main restricted`
   - 这行告诉APT从`http://archive.ubuntu.com/ubuntu/`这个URL的`focal`版本的主（main）和限制（restricted）组件中获取二进制软件包。
2. **deb-src**：
   - `deb-src`行定义了APT从哪个仓库获取源代码包（即用于编译软件包的源代码）。
   - 例如：`deb-src http://archive.ubuntu.com/ubuntu/ focal main restricted`
   - 与上面的`deb`行类似，但这行告诉APT从相同的URL和组件中获取源代码包。

何时需要`deb-src`？

- **开发或编译软件**：如果你需要编译或修改某个软件包，你可能需要该软件包的源代码。
- **构建自定义的Debian包**：如果你正在为某个特定的环境或硬件配置自定义的Debian包，源代码将非常有用。
- **信任源代码**：对于某些用户来说，查看和验证软件包的源代码可能是一个重要的安全考虑因素。

注意事项：

- 启用`deb-src`可能会增加你的磁盘使用空间，因为源代码包通常比二进制包大得多。
- 不是所有的用户都需要或想要源代码包，因此默认情况下，许多发行版在`/etc/apt/sources.list`中只包含`deb`行。
- 你可以通过运行`sudo apt-get source <package-name>`来从已启用的`deb-src`仓库中获取特定软件包的源代码。
- 如果你添加了自定义的`deb-src`仓库，但APT无法找到相应的源代码包，你可能会在尝试获取源代码时看到错误消息。确保你的仓库配置正确，并且该仓库确实提供了源代码包。

## `main`, `restricted`, `universe`, `multiverse`, `non-free`, 和 `contrib` 是什么？

`main`, `restricted`, `universe`, `multiverse`, `non-free`, 和 `contrib` 是Debian和基于Debian的Linux发行版（如Ubuntu）中软件仓库的组件分类。这些分类帮助用户和组织更好地理解软件包的来源、许可证以及支持级别。以下是每个组件的简要说明：

1. main：

   - 包含自由软件，即遵循Debian自由软件指南的软件包。
   - 这些软件包由Ubuntu团队官方支持，并经过了严格的测试和审查。
   - 主要包括了大多数流行的和稳定的开源软件。

2. restricted：

   - 包含由于版权或法律问题而不能被自由分发的软件，但它们对于某些用户来说可能是必需的。
   - 这些软件包通常包括一些专有设备的驱动程序，它们不是完全的自由软件，但仍然得到了Ubuntu团队的支持。

3. universe：

   - 包含社区维护的自由软件，这些软件可能不在Ubuntu官方支持范围内。
   - 这些软件包由各种开源项目和独立开发者提供，并通过社区进行测试和支持。
   - universe组件的软件包数量庞大，提供了许多额外的功能和工具。

4. multiverse：

   - 包含非自由软件，这些软件可能包含专有代码或具有限制性的许可证。
   - 使用multiverse中的软件需要用户注意软件的许可协议和使用条款。
   - 这些软件包通常不会得到Ubuntu官方的直接支持。

5. non-free

   （主要在Debian中使用，而不是Ubuntu）：

   - 包含明确不遵循Debian自由软件指南的非自由软件。
   - 这些软件包可能包含专有软件或具有特定使用限制的软件。

6. contrib

   （也主要在Debian中使用）：

   - 包含依赖于non-free组件中软件的自由软件。
   - 这些软件包本身是自由的，但它们依赖于非自由软件才能正常工作。

需要注意的是，`non-free` 和 `contrib` 主要与Debian发行版相关，而在Ubuntu中，相应的分类更倾向于被整合到 `multiverse` 中。Ubuntu为了保持其对自由软件的承诺，在其官方仓库中不包括 `non-free` 分类，但用户可以通过添加第三方源或使用其他方法来安装这些软件包。

总的来说，这些分类有助于用户根据自己的需求和偏好选择合适的软件包，同时也反映了软件包的许可证状态和支持级别。

## `buster`、`buster-backports`、`buster-updates`、`buster-proposed`和`buster-security`是什么呢？

在Debian系统中，`buster`、`buster-backports`、`buster-updates`、`buster-proposed`和`buster-security`代表了不同的软件源组件，它们与Debian 10（代号Buster）版本相关。以下是这些组件的详细解释：

1. buster（Debian 10的稳定版软件仓库）：
   - 这是Debian 10的默认稳定版软件仓库，包含了该版本发布时的所有经过测试并被认为是稳定可靠的软件包。
2. buster-backports（backports存储库）：
   - backports存储库是一个额外的软件源，它包含了针对最新Debian稳定版本的软件包的更新版本。这些软件包可能包含新功能或修复了某些bug，但还没有被正式加入到稳定版中。
   - 用户可以通过这个源获取到比默认稳定版更新的软件包，而无需等待整个系统版本的升级。
3. buster-updates（更新仓库）：
   - 这个仓库包含了Debian 10的稳定版发布后，各个软件包的小版本升级和补丁。这些升级通常用于修复bug或增强功能。
   - 需要注意的是，buster-updates仓库中的软件包可能未经过充分的测试，因此不建议在生产环境中直接使用。
4. buster-proposed（提议的更新仓库）：
   - **buster-proposed** 并不是一个官方定义的仓库名称，但在Debian的开发过程中，有时会使用类似`-proposed-updates`的仓库来测试新的软件包或更新。这些软件包可能还没有经过充分的测试，但已经被提议加入到稳定版中。
   - Debian的某些特定项目或团队可能会使用类似的仓库来测试他们开发的软件包，但这并不是Debian官方提供的标准仓库之一。
5. buster-security（安全更新仓库）：
   - 这个仓库提供了对Debian稳定版本的安全更新。当软件包中发现安全漏洞时，修复这些漏洞的更新将被放置在此仓库中。
   - 对于运行Debian稳定版本的用户来说，启用这个仓库是非常重要的，因为它可以确保系统得到及时的安全更新。

**归纳**：

- `buster`：Debian 10的稳定版软件仓库。
- `buster-backports`：包含针对稳定版本的更新软件包的额外软件源。
- `buster-updates`：包含稳定版发布后软件包的小版本升级和补丁的仓库，但可能包含未经充分测试的软件包。
- `buster-proposed`（非官方标准仓库）：可能用于测试新的软件包或更新的提议仓库。
- `buster-security`：提供安全更新的仓库，确保系统安全。

在配置`/etc/apt/sources.list`文件时，用户可以根据需要启用或禁用这些仓库。对于大多数用户来说，启用`buster`和`buster-security`就足够了。对于需要最新功能或修复的用户，可以考虑启用`buster-backports`。而`buster-updates`通常不建议在生产环境中启用。`buster-proposed`（如果存在）通常是针对特定团队或项目的，普通用户不需要启用。

## `sudo apt install <package-name>`会自动从`deb-src`中下载源代码吗？

默认情况下，当你使用 `apt-get install <package-name>` 安装软件包时，你只会下载并安装编译好的二进制文件，而不是源代码。

`sudo apt-get source <package-name>` 命令用于从 Ubuntu 的软件仓库（或 Debian 的，因为两者在很多方面是相似的）中下载指定软件包的源代码。

如果你想下载源代码，你需要执行 `sudo apt-get source <package-name>`。这将会下载 `.dsc`（源代码描述文件）、`.diff.gz`（补丁文件）和 `.orig.tar.gz`（原始源代码压缩包）等文件。这些文件通常会被放置在 `/usr/src/<package-name>-<version>` 目录下（除非你指定了不同的目录）。

此外，你还需要确保你的 `/etc/apt/sources.list` 文件或 `/etc/apt/sources.list.d/` 目录下的文件中包含了源代码仓库（通常是 `deb-src` 行）。如果没有，`apt-get source` 命令将无法找到源代码。

例如，如果你的 `/etc/apt/sources.list` 文件包含以下行：

```bash
deb http://archive.ubuntu.com/ubuntu/ focal main restricted  
# deb-src http://archive.ubuntu.com/ubuntu/ focal main restricted
```

你需要取消 `# deb-src` 那一行的注释（即删除前面的 `#`），以便 `apt-get source` 能够工作。修改后的文件应该像这样：

```bash
deb http://archive.ubuntu.com/ubuntu/ focal main restricted  
deb-src http://archive.ubuntu.com/ubuntu/ focal main restricted
```

然后，你可以运行 `sudo apt-get update` 来更新你的软件包列表，包括源代码仓库。之后，你就可以使用 `sudo apt-get source <package-name>` 来下载源代码了。

## `/etc/apt/sources.list`配置国内加速源

> [ubuntu 把软件源修改为国内源](https://www.cnblogs.com/Jimc/p/10214081.html)

备份原始源文件，当然需要系统管理员权限操作

```bash
sudo cp /etc/apt/sources.list /etc/apt/sources.list.backup
```

获取`ubuntu codename`，把`codename`替换下面的`bionic`，[参考](https://www.ngui.cc/el/1326641.html?action=onClick)

>注意：如果`lsb_release`命令不存在，则查看原来的`sources.list`文件获取`codename`

```bash
lsb_release -a
```

`debian`或主流开源`docker`容器镜像中主要使用`main`、`non-free`、`contrib`组件分类，`/etc/apt/sources.list`配置如下：

非生产环境配置：

```properties
deb http://mirrors.aliyun.com/debian/ buster main non-free contrib
deb http://mirrors.aliyun.com/debian-security buster/updates main
deb http://mirrors.aliyun.com/debian/ buster-updates main non-free contrib
deb http://mirrors.aliyun.com/debian/ buster-backports main non-free contrib
deb-src http://mirrors.aliyun.com/debian/ buster main non-free contrib
deb-src http://mirrors.aliyun.com/debian-security buster/updates main
deb-src http://mirrors.aliyun.com/debian/ buster-updates main non-free contrib
deb-src http://mirrors.aliyun.com/debian/ buster-backports main non-free contrib
```

生产环境配置（稳定版软件仓库和提供安全更新的仓库就足够了）：

```properties
deb http://mirrors.aliyun.com/debian/ buster main non-free contrib
deb http://mirrors.aliyun.com/debian-security buster/updates main
deb-src http://mirrors.aliyun.com/debian/ buster main non-free contrib
deb-src http://mirrors.aliyun.com/debian-security buster/updates main
```

`ubuntu`中国内阿里源`/etc/apt/sources.list`配置如下：

非生产环境配置：

```properties
deb http://mirrors.aliyun.com/ubuntu/ bionic main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ bionic-security main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ bionic-updates main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ bionic-proposed main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ bionic-backports main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ bionic main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ bionic-security main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ bionic-updates main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ bionic-proposed main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ bionic-backports main restricted universe multiverse
```

生产环境配置（稳定版软件仓库和提供安全更新的仓库就足够了）：

```properties
deb http://mirrors.aliyun.com/ubuntu/ bionic main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ bionic-security main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ bionic main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ bionic-security main restricted universe multiverse
```

更新源

```bash
sudo apt-get update
```

