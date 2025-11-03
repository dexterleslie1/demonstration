## 禁用自动更新功能

### `window10`禁用自动更新功能

1. 打开控制面板：
   - 点击任务栏上的“开始”按钮，然后选择“控制面板”图标。
   - 或者，你可以通过搜索栏输入“控制面板”来快速找到并打开它。
2. 进入管理工具：
   - 在控制面板中，找到并点击“管理工具”选项。
3. 打开服务管理器：
   - 在管理工具窗口中，选择“服务”选项。这将打开Windows的服务管理器。
4. 找到Windows Update服务：
   - 在服务管理器中，向下滚动找到名为“Windows Update”的服务。
5. 禁用Windows Update服务：
   - 双击“Windows Update”服务以打开其属性窗口。
   - 在属性窗口中，切换到“常规”选项卡。
   - 找到“启动类型”下拉菜单，将其设置为“禁用”。
   - （可选）点击“停止”按钮以立即停止当前正在运行的Windows Update服务。
   - 点击“应用”和“确定”按钮保存更改。



### `Windows 11`  禁用自动更新功能

停止更新服务：

- 按 Win + R，输入 services.msc，回车。
- 找到 Windows Update 服务，右键选择“停止”。

禁用服务启动：

- 双击 Windows Update，在“启动类型”中选择“禁用”。

恢复选项：

- 在“恢复”选项卡中，将所有失败操作设置为“无操作”。

应用更改：

- 点击“确定”并重启计算机。



## 启用`hyper-v`特性

>windows11 启用`hyper-v`特性：https://www.bdrsuite.com/blog/how-to-enable-hyper-v-in-windows-11-and-windows-server-2022/

启用CPU硬件虚拟化支持。

打开功能`控制面板`>`程序和功能`>`启用或关闭Windows功能`，勾选Hyper-V功能点击确认按钮进行安装，安装完毕后重启系统。



## `Cygwin` - 概念

>提醒：支持和 `Ubuntu` 一样正常使用 `find`、`grep` 等命令。

如果说 MinGW 是一个“语言老师”，那 Cygwin 就是一个功能强大的“翻译官”兼“模拟环境”。

### 一句话概括

**Cygwin 是一个巨大的兼容层，它在 Windows 上提供了一个完整的、类似于 Linux 或其它 Unix 系统的运行环境和开发环境。** 它的目标不是生成原生 Windows 程序，而是**让你能在 Windows 里像在 Linux 中一样工作和运行 Linux 程序**。

---

### 1. 核心思想：兼容层

理解 Cygwin 的关键在于理解它的核心组件：**`cygwin1.dll`** 这个动态链接库。

*   **它做了什么？** 这个 DLL 文件充当了一个“翻译官”的角色。
*   **翻译过程**：
    1.  你在 Cygwin 环境里编译一个原本为 Linux 写的程序，或者直接运行一个 Linux 程序。
    2.  当这个程序运行时，它仍然会调用 Linux 特有的 API（也就是 POSIX API），比如 `fork()`, `exec()` 等。
    3.  此时，`cygwin1.dll` 会拦截这些调用，并**实时地**将它们“翻译”成 Windows 系统能够理解的等价的 Windows API 调用。
    4.  程序因此得以在 Windows 上正常运行，但它自己并不知道它在 Windows 上，它以为自己运行在 Linux 下。

**所以，任何通过 Cygwin 编译或运行的程序，都强烈依赖于 `cygwin1.dll`**。没有这个 DLL，程序就无法启动。

---

### 2. Cygwin 的组成部分

Cygwin 不仅仅是一个库，它提供了一整套丰富的软件包和一个模拟环境：

1.  **Cygwin DLL (`cygwin1.dll`)**: 核心的兼容层库。
2.  **Cygwin 终端**： 一个模拟的 shell 环境（默认是 `bash`），让你可以使用熟悉的 Linux 命令。
3.  **大量的 POSIX 工具软件包**： 通过 Cygwin 的安装工具（`setup-x86_64.exe` 等），你可以选择安装成千上万个 Linux 下常见的工具，比如：
    *   **Shell**: `bash`, `zsh`, `tcsh`
    *   **文件工具**: `ls`, `cp`, `rm`, `grep`, `find`, `sed`, `awk`
    *   **开发工具**: `gcc`, `g++`, `gdb`, `make`, `vim`, `emacs`
    *   **网络工具**: `ssh`, `curl`, `wget`, `rsync`
    *   **甚至包括** `X Window Server`： 这意味着你可以在 Cygwin 下运行图形界面的 Linux 程序！

---

### 3. 与 MinGW 的再次对比（强化理解）

让我们回顾并深化一下与 MinGW 的对比，这能让你更清楚地看到两者的本质区别。

| 特性         | MinGW                                                     | Cygwin                                                       |
| :----------- | :-------------------------------------------------------- | :----------------------------------------------------------- |
| **哲学目标** | **让 Windows 拥有 GCC 编译器**，生成纯净的 Windows 程序。 | **让 Windows 模拟一个 Linux 系统**，提供一个 Linux 兼容环境。 |
| **实现方式** | 直接使用 Windows API。程序“母语”就是 Windows 语。         | 通过 `cygwin1.dll` 翻译 POSIX API。程序说 Linux 语，由 DLL 翻译。 |
| **程序依赖** | **无额外依赖**，生成的原生 .exe 可独立分发运行。          | **必须依赖 `cygwin1.dll`**。分发程序时需要带上这个 DLL。     |
| **使用体验** | 像一个 Windows 下的开发工具。你还是在用 Windows。         | 像一个运行在 Windows 内部的“Linux 子系统”。你感觉像是在用 Linux。 |
| **性能**     | 程序直接运行，性能最优。                                  | 有 API 翻译的开销，性能略有损失（通常可忽略不计）。          |
| **典型输出** | 一个可以双击运行的普通 Windows 应用程序。                 | 一个只能在安装了 Cygwin 环境的电脑上运行的、行为像 Linux 的程序。 |

---

### 4. 谁会用 Cygwin？常见用途

1.  **需要同时在 Windows 和 Linux 工作的开发者**： 比如，公司服务器是 Linux，但你的办公电脑是 Windows。你可以在 Cygwin 里用熟悉的 Linux 命令和脚本处理文件、连接服务器，而无需切换系统。
2.  **将 Linux 软件移植到 Windows**： 对于大量使用 Linux 特有 API 的复杂软件，用 MinGW 移植几乎需要重写，而用 Cygwin 重新编译则可能只需少量修改就能运行。
3.  **Linux 用户暂时不得不使用 Windows**： 对于习惯 Linux 命令行强大功能的人来说，Windows 自带的 `cmd` 或老版本 PowerShell 可能很难用。Cygwin 提供了几乎原汁原味的 `bash` shell 和工具链，极大提升了工作效率。
4.  **学习和教学**： 可以在 Windows 电脑上学习 Unix/Linux 系统管理和 shell 编程。

---

### 5. 与现代替代品的比较

*   **WSL (Windows Subsystem for Linux)**： 这是微软官方推出的更强大的解决方案。**WSL 不是一个模拟层，而是一个真正的 Linux 兼容内核**。它允许你在 Windows 上直接运行原生的 Linux 二进制文件，性能和支持度远超 Cygwin。对于大多数新用户，**WSL 是比 Cygwin 更推荐的选择**。
*   **Cygwin 现在的定位**： 在无法使用 WSL 的旧版 Windows 系统上，或者需要更轻量级、与 Windows 文件系统深度交互的场景下，Cygwin 依然是一个非常优秀和成熟的选择。

### 总结

**Cygwin 是一个在 Windows 上模拟 POSIX（如 Linux）环境的强大工具**。它通过一个核心的 DLL 文件翻译系统调用，让你能够**在不离开 Windows 的情况下，获得一个功能极其丰富的 Linux 命令行体验**，并直接运行大量的 Linux 程序。它与旨在生成纯净 Windows 程序的 MinGW 有着根本性的不同。

简单来说：
*   你想**为 Windows 写一个 C++ 程序**，生成一个谁都能直接运行的 `.exe` 文件？用 **MinGW**。
*   你想**在 Windows 系统里，像在 Linux 终端下一样工作**，运行 `grep`, `ssh`, `vim` 等？用 **Cygwin**（或者更现代的 **WSL**）。



## `Cygwin` - 安装

`Windows 11` 安装 `Cygwin`

通过 [链接](https://www.cygwin.com/) 下载最新版本 `Cygwin`，例如：`setup-x86_64.exe`，运行安装程序并选择通过互联网在线安装，选择阿里云镜像网站和所有组件安装 `Cygwin`。



## `Cygwin` - 使用

### 切换到 `c:` 盘

```bash
cd /cygdrive/c
pwd
```



### 在当前目录打开文件管理器

```bash
cygstart .
```



## `MinGW` - 概念

MinGW 的全称是 Minimalist GNU for Windows。

- GNU： 这是一个著名的开源操作系统项目，提供了大量的软件开发工具，其中最核心的就是 GCC（GNU Compiler Collection，GNU 编译器套件）。
- for Windows： 意味着它将这些工具移植到了 Windows 平台。
- Minimalist： 表明它只包含了在 Windows 上编译代码所必需的最核心组件，不包含其他庞大的东西（这点下面会对比说明）。

所以，MinGW 的本质就是 GCC 编译器在 Windows 上的一个移植版本。

MinGW 的核心作用可以概括为：**为 Windows 平台提供一个强大、免费、开源的 GCC 编译环境，用于生成高性能、无依赖的原生 Windows 应用程序。**

下面是其具体作用的详细分解：

### 1. 核心作用：编译生成原生 Windows 程序

这是 MinGW 最根本、最重要的作用。

*   **“原生”是什么意思？** 意味着使用 MinGW 的 GCC 编译器编译出的可执行文件（.exe）是纯粹的 Windows 程序。它直接调用 Windows 操作系统提供的 API（如 Win32 API 或 C 运行时库），**不需要任何额外的中间层或兼容库**（比如 Cygwin 所需的 `cygwin1.dll`）。
*   **带来的好处**：
    *   **程序体积小**： 因为不需要打包庞大的兼容库。
    *   **运行效率高**： 没有中间层的翻译开销，程序直接与操作系统交互。
    *   **分发简单**： 编译出的 .exe 文件通常可以单独拷贝到任何同版本的 Windows 系统上直接运行，用户无需安装任何额外环境。

### 2. 为 Windows 带来成熟的 GNU 开发工具链

MinGW 将 Linux/Unix 世界久经考验的 GNU 工具链完整地移植到了 Windows。

*   **GCC (GNU Compiler Collection)**： 支持 C, C++, Fortran, Ada 等多种语言的工业级编译器，以代码优化能力强、符合标准而闻名。
*   **GDB (GNU Debugger)**： 功能强大的调试器，可以用于跟踪和修复代码中的错误。
*   **binutils**： 包含汇编器（as）、链接器（ld）、库管理工具（ar）等一系列二进制工具。
*   **make**： 用于实现项目的自动化构建。

这使得习惯 Linux 下开发的程序员可以在 Windows 上使用几乎相同的命令（如 `gcc`, `g++`, `gdb`, `make`）进行开发，降低了跨平台开发的成本。

### 3. 具体应用场景（谁在用？用来做什么？）

**1. 学习和教学 C/C++**
*   **作用**： 对于初学者来说，Visual Studio 过于庞大复杂。MinGW 提供了一个轻量级、纯粹的编译环境。学生可以专注于学习语言本身和命令行操作，更好地理解编译、链接等底层过程。
*   **典型工具**： 常与轻量级代码编辑器（如 VS Code, Notepad++）或 IDE（如 Code::Blocks, Dev-C++）搭配使用。

**2. 开发和移植开源软件**
*   **作用**： 大量著名的开源项目（如 Python 解释器本身、FFmpeg 音视频处理工具、GTK+ 图形界面库等）都使用 GCC 作为标准编译器。MinGW 允许这些项目在 Windows 平台上被顺利地编译和构建。
*   **例子**： 很多 Linux 下的开源软件，通过 MinGW 移植，才有了对应的 Windows 版本。

**3. 构建跨平台项目**
*   **作用**： 如果一个项目需要在 Linux、macOS 和 Windows 上运行，使用 GCC（在 Windows 上就是 MinGW）作为统一的编译器可以最大程度地保证代码行为一致，减少因编译器差异带来的问题。CMake 等构建工具可以很方便地配置 MinGW 作为生成器。

**4. 创建轻量级的命令行工具**
*   **作用**： 如果你需要编写一个在 Windows 下运行的命令行小工具（例如日志分析、文件批量处理等），用 MinGW 编译出的程序非常小巧，易于分发和集成到脚本中。

**5. 替代商业编译器**
*   **作用**： MinGW 是完全免费开源的，对于个人开发者、初创公司或预算有限的团队，它是一个功能不输于商业编译器（如旧版 Visual Studio 的部分功能）的绝佳选择。

### 与 Visual Studio 编译器的对比

为了更好地理解 MinGW 的作用，可以将其与 Windows 上最主要的编译器——Microsoft 的 Visual C++ (MSVC) 进行对比：

| 特性         | MinGW (GCC)                                              | Microsoft Visual C++ (MSVC)                                  |
| :----------- | :------------------------------------------------------- | :----------------------------------------------------------- |
| **核心**     | GNU 开源工具链在 Windows 的移植                          | 微软官方开发的商业/社区版编译器                              |
| **许可证**   | 自由软件（GPL 等）                                       | 微软专属许可证                                               |
| **生成代码** | 使用 GNU 风格的运行时库，链接到 `libgcc`, `libstdc++`    | 使用微软的运行时库（MSVCRT），链接到 `msvcrt.dll`, `vcruntimexxx.dll` |
| **兼容性**   | 生成的程序通常不依赖特定版本的 Visual C++ 可再发行组件包 | 程序运行可能需要相应版本的 **Visual C++ Redistributable**    |
| **开发体验** | 更接近 Linux/Unix 风格，常用命令行和 Makefile            | 深度集成在 Visual Studio IDE 中，提供强大的图形化调试和开发体验 |

### 总结

MinGW 的核心作用就像一个 **“桥梁”** 和 **“工具提供商”**：

*   **对 Windows 平台**：它架起了一座桥，将 Linux 世界强大的 GNU 编译工具引入进来，丰富了 Windows 的开发生态。
*   **对开发者**：它提供了一套免费、强大、标准的工具，让你能：
    *   编译出**高效、纯净的 Windows 程序**。
    *   在 Windows 上延续 **Linux 的开发习惯**。
    *   轻松完成**跨平台项目的编译**。

因此，只要你需要在 Windows 上使用 GCC 编译器来生成真正的本地 Windows 程序，MinGW（特别是其现代版本 MinGW-w64）就是你不可或缺的工具。

## MSYS2概念

### 一句话概括

**MSYS2** 是一个在 Windows 上提供类 Linux 开发环境和命令行工具集的技术集合。你可以把它理解为一个“Windows 上的迷你 Linux 终端环境”。

---

### 核心概念分解

为了更好地理解，我们把 MSYS2 拆解成几个关键部分：

1.  **一个软件发行版（Software Distribution）**
    *   它基于 **Arch Linux** 的包管理系统 **Pacman**。这意味着你可以使用简单的命令（如 `pacman -S 软件包名`）来安装、更新和卸载成千上万的开发工具和库（如 GCC、Python、Vim、Git 等），而无需手动去网上到处下载。

2.  **一个运行时环境（Runtime Environment）**
    *   它提供了一个名为 **Bash** 的 Unix-style 命令行 shell（就像你在 Linux 或 macOS 上看到的那种终端），以及一系列核心的 Unix 工具（如 `ls`, `grep`, `find`, `sed`, `awk` 等）。这使得在 Windows 上运行为 Linux 编写的脚本和工具成为可能。

3.  **一个兼容层（Compatibility Layer）**
    *   它的核心是 **Cygwin** 的一个衍生版本。Cygwin 是一个强大的工具，它通过在 Windows 和类 Unix 软件之间提供一个 POSIX 兼容性层，让这些软件无需修改就能在 Windows 上编译和运行。MSYS2 优化了 Cygwin，特别专注于**支持原生 Windows 程序的开发**。

### MSYS2 的主要特点和优势

*   **强大的包管理（Pacman）**：这是 MSYS2 最大的亮点。安装编译器、库、工具只需一行命令，并且能自动解决依赖关系。
*   **用于 Windows 的原生开发**：这是 MSYS2 与 Cygwin 的一个关键区别。MSYS2 的主要目标之一是帮助您构建**原生 Windows 程序（.exe）**。它特别适合与 **MinGW-w64** 工具链配合使用，后者是一个编译器套件（GCC），可以生成不依赖 Cygwin 的原生 Windows 可执行文件。
*   **出色的开源生态系统支持**：许多流行的开源项目（尤其是在 Linux 上开发的）可以很容易地在 MSYS2 环境中进行编译，因为它提供了类似 Linux 的构建环境和熟悉的工具。
*   **与 Windows 的良好集成**：它能够很好地访问你的 Windows 文件系统（如 `C:\` 被映射为 `/c/`），并且生成的原生程序可以和任何其他 Windows 程序一样运行。

---

### MSYS2 的三个主要启动环境（Shell）

安装 MSYS2 后，你会看到三个不同的快捷方式，它们针对不同的用途：

1.  **MSYS2 MSYS**（黑色图标）：
    *   **目的**：用于**运行和维护 MSYS2 环境本身**。
    *   **特点**：使用纯粹的 MSYS2 环境，优先使用 MSYS2 自身的工具。适合进行 `pacman` 包管理操作。
    *   **路径示例**：`/usr/local/bin:/usr/bin:/bin`

2.  **MSYS2 MinGW 64-bit**（蓝色图标）：
    *   **目的**：用于**编译 64 位原生 Windows 程序**。这是**最常用**的开发环境。
    *   **特点**：优先使用 MinGW-w64 的 64 位工具链（GCC），编译出的程序是原生的 64 位 Windows 程序。
    *   **路径示例**：`/mingw64/bin:/usr/local/bin:/usr/bin:/bin`

3.  **MSYS2 MinGW 32-bit**（绿色图标）：
    *   **目的**：用于**编译 32 位原生 Windows 程序**。
    *   **特点**：优先使用 MinGW-w64 的 32 位工具链，编译出的程序是原生的 32 位 Windows 程序。

**简单选择指南**：如果你是初学者，或者要编译 64 位程序，请始终使用 **MSYS2 MinGW 64-bit**。

---

### 典型工作流程示例

假设你想用 GCC 编译一个 C 程序：

1.  安装 MSYS2。
2.  打开 **MSYS2 MinGW 64-bit** 终端。
3.  安装 GCC 编译器：`pacman -S mingw-w64-x86_64-gcc`
4.  编写你的代码，例如 `hello.c`。
5.  编译：`gcc hello.c -o hello.exe`（这会生成一个不依赖 MSYS2 的原生 Windows 程序 `hello.exe`）
6.  运行：`./hello.exe`

---

### 总结：MSYS2 是什么？

| 方面         | 描述                                                         |
| :----------- | :----------------------------------------------------------- |
| **本质**     | 一个集成了包管理器、Bash Shell 和编译工具链的 Windows 软件开发生态系统。 |
| **核心组件** | Pacman（包管理器）、Bash Shell、MinGW-w64（编译器工具链）。  |
| **主要用途** | 1. 在 Windows 上获得一个强大的类 Linux 命令行环境。<br>2. **编译原生 Windows 程序**（特别是来自开源世界的程序）。<br>3. 提供与 Linux 兼容的脚本和工具运行环境。 |
| **替代品**   | **Cygwin**（更注重“在 Windows 上模拟 Linux”，其程序依赖 Cygwin 运行时库）、**WSL**（Windows Subsystem for Linux，在 Windows 内运行一个真正的 Linux 内核，更接近完整的 Linux 体验）。 |

**总而言之，如果你需要在 Windows 上进行软件开发，尤其是涉及 GNU 工具链或开源库的编译，MSYS2 是一个极其强大和方便的选择。**

## MSYS2和MinGW的关系

简单来说，它们不是竞争关系，而是**互补和协作关系**。你可以把它们想象成一个厨房：

*   **MinGW-w64** 是**灶具和锅具**（编译器工具链）：它们是核心生产工具，用来把原材料（源代码）煮熟（编译）成食物（可执行的 Windows 程序）。
*   **MSYS2** 是**整个厨房环境**（开发环境）：它提供了空间、水电、橱柜（Bash Shell、Unix 工具），并且管理着所有厨具和食材的仓库（Pacman 包管理器），让厨师（开发者）能够高效地工作。

---

### 详细分解

#### 1. MinGW-w64 是什么？（核心工具）

*   **全称**：Minimalist GNU for Windows 64-bit (也支持 32-bit)。
*   **核心职责**：**编译代码，生成原生 Windows 可执行文件（.exe/.dll）**。
*   **关键组件**：
    *   **GCC**： GNU 编译器集合，可以编译 C, C++, Fortran 等语言。
    *   **GDB**： GNU 调试器。
    *   **Binutils**： 包含链接器（ld）、汇编器等二进制工具。
*   **重要特性**：它编译出的程序是**原生 Windows 程序**，不依赖任何额外的运行时库（比如 Cygwin 的 `cygwin1.dll`）。这意味着你编译出的程序可以在任何 Windows 电脑上运行。

**MinGW-w64 是 MinGW 项目的现代演进版，提供了更好的标准兼容性和对 64 位的支持，现在基本上已经取代了原来的 MinGW。**

#### 2. MSYS2 是什么？（环境和管家）

*   **核心职责**：提供一个**类 Unix 的构建环境和强大的软件包管理系统**。
*   **关键组件**：
    *   **Pacman**： 包管理器，来自 Arch Linux。你可以通过命令（如 `pacman -S mingw-w64-x86_64-gcc`）轻松安装 MinGW-w64 工具链以及成千上万的其他开发库。
    *   **Bash Shell**： 一个强大的命令行界面，让你可以运行自动化脚本（如 `configure`、`make`）。
    *   **Coreutils**： 一系列基本的 Unix 工具（如 `ls`, `cp`, `mkdir`, `grep`, `sed`）。许多开源项目的构建脚本依赖这些工具。

---

### 它们是如何协同工作的？

MSYS2 的核心价值在于它**完美地封装和集成了 MinGW-w64**。

**没有 MSYS2 的时代：**
你需要手动下载 MinGW-w64，手动设置复杂的系统环境变量（如 `PATH`），手动处理依赖库，整个过程非常繁琐且容易出错。

**有 MSYS2 的时代：**
1.  你安装 MSYS2。
2.  打开 MSYS2 终端，只需一行命令即可安装完整的 MinGW-w64 工具链：
    ```bash
    # 安装 64 位工具链
    pacman -S mingw-w64-x86_64-gcc
    # 安装 32 位工具链
    pacman -S mingw-w64-i686-gcc
    ```
3.  MSYS2 的 Pacman 会自动下载 GCC、GDB、Binutils 以及所有必需的依赖库，并正确配置好环境。
4.  你现在拥有了一个“开箱即用”的完整开发环境。你可以在 MSYS2 的 Bash Shell 中直接使用 `gcc` 命令来编译你的代码，生成的将是原生 Windows 程序。

### 关键区别与总结（表格）

| 特性             | MinGW-w64（工具链）                       | MSYS2（环境与包管理器）                                 |
| :--------------- | :---------------------------------------- | :------------------------------------------------------ |
| **主要目的**     | **编译**生成原生 Windows 程序。           | 提供**构建环境**和**软件管理**。                        |
| **核心产出**     | 编译器 (gcc)、调试器 (gdb)、链接器 (ld)。 | Bash Shell、Pacman 包管理器、Unix 工具。                |
| **依赖关系**     | 编译出的程序**不依赖**MSYS2或Cygwin。     | 其自身的环境**基于**Cygwin 的运行时库。                 |
| **可否独立存在** | 可以，但配置困难。                        | **不能**，它的主要价值就是管理像 MinGW-w64 这样的工具。 |

### 一个生动的比喻

*   **MinGW-w64** 就像是一把**电锯**，是砍树的终极工具。
*   **MSYS2** 就像是一个**设备齐全的林场**，它不仅给你提供电锯，还管理燃油、维护工具、提供地图和道路（环境），让你能高效安全地砍树（开发）。

**结论：**
你今天在 Windows 上进行 C/C++ 开发，**最佳实践就是通过 MSYS2 来获取和使用 MinGW-w64**。MSYS2 是 MinGW-w64 的“官方推荐”的部署和管理平台，它极大地简化了在 Windows 上搭建类 Linux 开发环境的流程。

## `Microsoft Windows Desktop Runtime - 8.0.15(x64)`

`Windows Desktop` 运行时，`.Net Windows Desktop` 运行时用于在你的计算机上运行 `Windows` 窗体和 `WPF` 应用程序。`.Net` 是开源、跨平台的，且由 `Microsoft` 提供支持。



## `Java`开发环境配置

>在 `Windows11` 配置开发环境。

### 安装`IDEA`

参考本站 [链接](/idea/README.html#windows11) 安装

### 安装`Firefox`

>说明：用于协助谷歌搜索资料等。

参考本站 [链接](/firefox/README.html#windows11) 安装

### 安装谷歌浏览器

>说明：用于开发调试或者清除`Cookie`测试场景使用。

参考本站 [链接](/chrome/README.html#windows11安装google-chrome浏览器) 安装。

### 安装`GitExtensions`

安装 `GitExtensions-x64-5.2.1.18061-0d74cfdc3.msi`、`Git-2.49.0-64-bit.exe`、`windowsdesktop-runtime-8.0.15-win-x64.exe`。

### 安装`Typora`

参考本站 [链接](/typora/README.html#安装-windows11) 安装

### 安装`Cygwin`

> 说明：使用`find`、`grep`等工具查找文件或者文件内容。

参考本站 [链接](/windows/README.html#cygwin-安装) 安装

### 安装`Docker`

参考本站 [链接](/docker/docker的安装.html#windows11-上安装-docker) 安装

### 安装`Gost`服务

参考本站 [链接](/gost/README.html#windows11) 安装

### 安装`NodeJS`

>说明：用于运行`VitePress`。

参考本站 [链接](/nodejs/README.html#windows) 安装

### 安装`VSCode`

参考本站 [链接](/vscode/README.html#windows11) 安装

### 安装`MySQL`客户端`SQLyog`

参考本站 [链接](/mysql-n-mariadb/mysql客户端工具.html#window11安装) 安装

### 安装`JDK1.8`

参考本站 [链接](/java/README.html#windows11) 安装



## 命令 - `netstat`

查看特定端口是否监听状态（如 1080）

```cmd
netstat -ano | findstr :1080
```

