## 构建工具 - 概念

> `make`、`autoconf+automake+autotools`、`cmake` 关系：https://zhuanlan.zhihu.com/p/338657327

`make`、`Autotools`、`CMake` 和 `nmake` 是用于构建和管理软件项目的工具，主要用于自动化编译和链接过程。

他们本质都是产生 `makefile` 文件的工具。`cmake` 产生的晚，解决了很多 `autotools` 工具的问题。`autotools` 是一个工具集具有强大的灵活性，但是因为步骤太多，配置繁琐，产生了很多的替代方案，`cmake` 是其中最优秀的之一。`cmake` 有跨平台特性支持。

以下是它们的简要说明和区别：

---

### 1. **`make`**

- **作用**：一个经典的构建工具，通过读取 `Makefile`（包含规则和依赖关系的文本文件）来自动化编译、链接等操作。
- **特点**：
  - 需要手动编写 `Makefile`，定义如何从源代码生成目标文件或可执行文件。
  - 跨平台性较弱，`Makefile` 的语法可能因系统（如 Unix/Linux 的 `make` 和 Windows 的 `nmake`）而略有差异。
- **示例命令**：
  ```sh
  make          # 默认执行 Makefile 中的第一个目标
  make install  # 执行安装目标
  ```

---

### 2. **`Autotools`（GNU Build System）**

- **作用**：一套工具链（包括 `autoconf`、`automake`、`libtool` 等），用于生成可移植的 `configure` 脚本和 `Makefile`。
- **特点**：
  - 适用于 Unix/Linux 系统，帮助开发者处理跨平台兼容性问题（如库依赖、系统差异）。
  - 通过 `./configure && make && make install` 流程编译安装软件。
  - 需要开发者编写 `configure.ac` 和 `Makefile.am`，由工具生成最终的 `Makefile`。
- **典型流程**：
  ```sh
  ./configure   # 检测系统环境并生成 Makefile
  make          # 编译代码
  make install  # 安装到系统
  ```

---

### 3. **`CMake`**

- **作用**：跨平台的构建工具，通过 `CMakeLists.txt` 文件生成标准化的构建脚本（如 `Makefile` 或 Visual Studio 项目）。
- **特点**：
  - 更现代，支持多种编译器和平台（Windows/Linux/macOS）。
  - 生成器模式：可以生成 `Makefile`、`Ninja` 文件、Xcode 或 Visual Studio 项目等。
  - 语法更简洁，适合复杂项目（如支持模块化配置、依赖管理）。
- **示例流程**：
  ```sh
  mkdir build && cd build
  cmake ..      # 生成 Makefile
  make          # 编译
  ```

---

### 4. **`nmake`**

- **作用**：微软提供的命令行构建工具，类似于 Unix 的 `make`，但专用于 Windows 平台。
- **特点**：
  - 解析 `Makefile`（通常由 Visual Studio 或手动编写）。
  - 与 Windows 开发工具链（如 MSVC 编译器）深度集成。
- **示例命令**：
  ```sh
  nmake /f Makefile.win  # 指定 Makefile 文件编译
  ```

---

### 对比总结

| 工具          | 跨平台性       | 配置文件           | 主要用途                             |
| ------------- | -------------- | ------------------ | ------------------------------------ |
| `make`      | 弱（依赖系统） | `Makefile`       | 基础自动化编译                       |
| `Autotools` | Unix/Linux     | `configure.ac`   | 生成可移植的 `Makefile`            |
| `CMake`     | 强             | `CMakeLists.txt` | 生成多种构建系统（如 Makefile/MSVC） |
| `nmake`     | Windows        | `Makefile`       | Windows 平台的 `make` 替代         |

---

### 使用场景建议

- **简单项目**：直接手写 `Makefile`（用 `make`）。
- **跨平台复杂项目**：用 `CMake`（现代项目主流选择）。
- **传统 Unix 开源项目**：可能需要 `Autotools`（如 `./configure`）。
- **Windows 专有项目**：可能用 `nmake` 或 Visual Studio 自带的构建工具。

## 构建工具 - 识别项目使用哪种工具

`cmake` 项目包含 `CMakeLists.txt` 文件。

`autotools` 项目包含 `configure.scan` 或者 `configure.ac`、`aclocal.m4`、`config.h.in`、`Makefile.am`、`Makefile.in`、`configure` 等文件。

## 构建工具 - `make` - 概念

### 一句话概括

**`make` 是一个经典且强大的自动化构建工具**，它通过读取一个名为 `Makefile` 的配置文件，**自动决定一个大型项目中哪些部分需要重新编译，并以正确的顺序执行编译和链接命令**，从而将源代码高效地转换成可执行文件或库。

---

### 核心思想：为什么要用 `make`？

想象一下一个大型项目有成千上万个源文件：

* 如果你只修改了其中一个文件，重新编译所有文件将浪费大量时间。
* 文件之间可能存在复杂的依赖关系（A 文件依赖于 B 文件，B 文件又依赖于 C 文件），手动按顺序编译极易出错。

`make` 的出现解决了这两个核心问题：

1. **增量构建（Incremental Build）**：它通过比较文件的时间戳，**只重新编译那些被修改过的文件以及依赖于这些文件的其他文件**，极大缩短了编译时间。
2. **管理依赖关系**：它根据 `Makefile` 中定义的依赖关系图，以正确的顺序执行构建步骤，确保不会漏掉任何环节。

---

### 核心概念：它是如何工作的？

`make` 的工作机制围绕三个核心概念：

#### 1. 目标（Target）

* 这是 `make` 想要生成的东西。它通常是一个文件（如可执行文件 `main` 或目标文件 `main.o`）。
* 也可以是一个“伪目标”（Phony Target），代表一个要执行的动作（如 `clean`），而不是一个文件。
* 运行 `make` 时，可以指定要构建的目标（如 `make all`），如果不指定，默认构建第一个目标。

#### 2. 依赖（Prerequisites）

* 这是生成目标所需要的文件列表。
* 例如，要生成目标文件 `main.o`，需要源文件 `main.c` 和头文件 `header.h`。那么 `main.c` 和 `header.h` 就是 `main.o` 的依赖。

#### 3. 配方（Recipe）

* 这是一系列 shell 命令，定义了**如何从依赖文件生成目标文件**。
* 配方必须以 Tab 字符开头，不能用空格。

---

### 一个简单的 `Makefile` 示例

假设我们有一个项目，由 `main.c` 和 `helper.c` 两个源文件组成，最终要生成一个叫 `myapp` 的可执行文件。

```makefile
# 定义最终目标 'myapp'，它依赖于两个目标文件
myapp: main.o helper.o
	# 配方：链接目标文件生成可执行文件
	gcc main.o helper.o -o myapp

# 目标 'main.o' 依赖于 'main.c'
main.o: main.c
	# 配方：编译源文件生成目标文件
	gcc -c main.c

# 目标 'helper.o' 依赖于 'helper.c'
helper.o: helper.c
	gcc -c helper.c

# 定义一个伪目标，用于清理生成的文件
clean:
	rm -f *.o myapp

# 定义一个伪目标，表示构建所有内容（通常作为默认目标）
all: myapp
```

**如何使用这个 Makefile：**

* `make` 或 `make all`：构建 `myapp`（默认第一个目标）。
* `make clean`：删除所有 `.o` 文件和 `myapp`。
* `make main.o`：只编译 `main.o`。

**`make` 的智能之处：**
如果你只修改了 `helper.c` 然后运行 `make`，它会：

1. 检查 `myapp` 的依赖项 `main.o` 和 `helper.o`。
2. 发现 `helper.o` 的依赖项 `helper.c` 比 `helper.o` 文件新，于是重新执行 `gcc -c helper.c` 来更新 `helper.o`。
3. 发现 `main.o` 的依赖项 `main.c` 没有 `main.o` 新，所以跳过编译。
4. 最后，因为 `helper.o` 被更新了，所以重新执行链接命令 `gcc main.o helper.o -o myapp`。

---

### `make` 的优势和劣势

| 优势                                                        | 劣势                                                                                               |
| :---------------------------------------------------------- | :------------------------------------------------------------------------------------------------- |
| **简单直接**：概念清晰，易于理解小型项目的构建过程。  | **跨平台性差**：不同平台（Unix, Windows）的 `make` 实现可能有差异，`Makefile` 需要调整。 |
| **极其高效**：增量构建节省大量时间。                  | **语法古老**：基于 Tab 的语法容易出错，功能扩展复杂。                                        |
| **高度灵活**：可以通过 shell 命令完成任何操作。       | **管理大型项目复杂**：手动编写和维护大型项目的依赖关系非常繁琐且容易出错。                   |
| **无处不在**：几乎所有类 Unix 系统都预装了 `make`。 | **依赖管理弱**：需要手动指定所有依赖关系，容易遗漏。                                         |

---

### `make` 与现代构建系统（如 CMake）的关系

很多人会混淆 `make` 和 `CMake`，它们的关系是这样的：

* **`make`**：是**底层执行者**。它负责读取 `Makefile` 并执行里面的一条条命令。它很“笨”，只知道按规则办事。
* **`CMake`**：是**高级生成器**。它负责根据一份更抽象、更高级的配置文件（`CMakeLists.txt`），**为不同的平台生成对应的 `Makefile`**（或 Visual Studio 项目文件、Ninja 文件等）。

**简单比喻：**

* **`CMake`** 像是一个**建筑师**，他画出了一份标准的设计蓝图（`CMakeLists.txt`）。
* **`make`** 像是一个**施工队**，它拿到针对当地建筑规范的具体施工图纸（`Makefile`）后，开始砌砖、盖房（编译、链接）。

所以，你经常看到的流程是：

```bash
mkdir build && cd build
cmake ..     # 建筑师根据蓝图生成施工图纸（Makefile）
make         # 施工队根据图纸开始建房
```

### 总结

**`make` 是一个自动化构建流程的基石工具**。它通过一个定义依赖关系和构建规则的 `Makefile` 文件，智能地决定需要重新编译的内容，从而高效地管理软件项目的构建过程。

虽然现代大型项目更倾向于使用 **CMake** 或 **Meson** 这类高级工具来生成 `Makefile`，但理解 `make` 的工作原理对于深入掌握软件构建过程至关重要。几乎所有 Linux 上的软件安装指南中的 `./configure && make && sudo make install` 三步曲，其中的 `make` 指的就是这个工具。

## 构建工具 - `make` - 入门

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-c++/demo-make/demo-makefile)

## 构建工具 - `autotools` - 概念

工具集合：

* `autoscan`：扫描源代码以搜寻普通的可移植性问题，比如检查编译器、库、头文件等，生成文件 `configure.scan`，它是 `configure.ac` 的一个雏形。参考：https://blog.csdn.net/chupaokan7404/article/details/100905223
* `aclocal`：根据已经安装的宏，用户定义宏和 `acinclude.m4` 文件中的宏将 `configure.ac` 文件所需要的宏集中定义到文件 `aclocal.m4` 中。`aclocal` 是一个 `perl` 脚本程序，它的定义是：“aclocal - create aclocal.m4 by scanning configure.ac”。参考：https://blog.csdn.net/chupaokan7404/article/details/100905223
* `autoheader`：根据 `configure.ac` 中的某些宏，比如 `cpp` 宏定义，运行 `m4`，声称 `config.h.in`。参考：https://blog.csdn.net/chupaokan7404/article/details/100905223
* `autoconf`：将 `configure.ac` 中的宏展开，生成 `configure` 脚本。这个过程可能要用到 `aclocal.m4` 中定义的宏。参考：https://blog.csdn.net/chupaokan7404/article/details/100905223
* `automake`：`automake` 将 `Makefile.am` 中定义的结构建立 `Makefile.in`，然后 `configure` 脚本将生成的 `Makefile.in` 文件转换为 `Makefile`。如果在 `configure.ac` 中定义了一些特殊的宏，比如 `AC_PROG_LIBTOOL`，它会调用 `libtoolize`，否则它会自己产生 `config.guess` 和 `config.sub`。参考：https://blog.csdn.net/chupaokan7404/article/details/100905223

## 构建工具 - `autotools` - 构建 `curl`

步骤如下：

* 下载源代码

  ```shell
  git clone https://github.com/curl/curl.git
  ```
* 安装 `libtool`

  ```shell
  yum install libtool
  ```
* 使用autotools自动配置项目

  ```shell
  ./buildconf
  ```
* 使用configure生成Makefile

  ```shell
  ./configure
  ```
* 编译

  ```shell
  make
  ```
* 运行curl程序

  ```shell
  cd src
  ./curl --help
  ```

## 构建工具 - `cmake` - 概念

### 一句话概括

**CMake 是一个开源、跨平台的构建系统生成器**。它的核心工作不是直接编译代码，而是根据你的配置（`CMakeLists.txt` 文件）**生成**你所选用的原生构建系统（如 Makefile 或 Visual Studio 项目文件）所需的文件，然后再由这些原生构建系统来执行实际的编译和链接工作。

可以把 CMake 想象成一个**项目架构师**或**总承包商**：

* **它不亲自砌砖（编译代码）**。
* **它负责画蓝图（读取 CMakeLists.txt）**。
* **然后根据蓝图，为不同的施工队（不同的编译环境）生成具体的施工图纸（Makefile / .sln 文件等）**。
* 最后，由**施工队**（make, msbuild, ninja 等工具）拿着图纸去真正盖房子（编译生成可执行文件）。

---

### 为什么需要 CMake？它解决了什么问题？

在 CMake 出现之前，开发者面临很多痛点：

1. **跨平台兼容性噩梦**：

   * 在 Linux 上，你要写 `Makefile`。
   * 在 Windows 上，你要写 `Visual Studio` 的 `.sln` 和 `.vcxproj` 文件。
   * 在 macOS 上，你可能要写 `Xcode` 的 `.xcodeproj` 文件。
   * 维护这么多套不同平台的构建文件几乎是不可能的任务。
2. **复杂的依赖管理**：

   * 项目依赖一个第三方库（如 OpenCV 或 Boost）时，如何告诉编译器去找到它的头文件和库文件？
   * 在不同系统上，这些库的安装路径可能完全不同。

**CMake 的出现完美地解决了这些问题：**

* **你只需要写一份配置文件（`CMakeLists.txt`）**。
* CMake 会根据当前的目标平台，**自动生成**对应平台所需的构建文件。
* 它提供了强大的命令来**查找依赖库**，屏蔽了不同平台的路径差异。

---

### CMake 的核心工作流程

使用 CMake 构建一个项目通常遵循以下流程，通常在一个新建的 `build` 目录中进行（**out-of-source build，分离构建**），以保持源码目录的清洁：

```bash
# 1. 进入项目根目录并创建构建目录
mkdir build && cd build

# 2. 运行 cmake，指定CMakeLists.txt所在路径（..表示上一级目录）
#    这会生成当前平台所需的构建文件（如Makefile）
cmake ..

# 3. 运行生成的构建文件来实际编译项目
#    Linux/macOS: 使用生成的Makefile，执行make
make
#    Windows: 如果生成的是Visual Studio项目，可以用msbuild，或者直接打开.sln文件编译
#    也可以使用CMake生成的“cmake --build”命令，它是跨平台的
cmake --build . --config Release

# 4. (可选)安装生成的目标文件
sudo make install # 类Unix系统
```

---

### CMake 的核心概念和组成部分

1. **`CMakeLists.txt`**：

   * 这是项目的**核心配置文件**，你需要在里面用 CMake 的语法声明如何构建你的项目。
   * 通常项目根目录有一个主 `CMakeLists.txt`，每个子目录也可以有各自的 `CMakeLists.txt`。
2. **生成器（Generators）**：

   * 这是 CMake 跨平台能力的体现。你可以告诉 CMake 你想要生成哪种构建系统的文件。
   * 常用生成器：
     * `Unix Makefiles`: 生成 `Makefile`（用于 Linux/macOS）。
     * `Visual Studio 17 2022`: 生成 Visual Studio 2022 的解决方案和项目文件。
     * `Xcode`: 生成 Xcode 项目文件。
     * `Ninja`: 生成 `build.ninja` 文件（一种比 make 更快的构建系统）。
3. **变量和缓存（Variables & Cache）**：

   * CMake 使用变量（如 `CMAKE_CXX_COMPILER`）来存储信息和控制构建过程。
   * 一些变量会被缓存到 `CMakeCache.txt` 文件中，下次配置时可以直接使用。
4. **命令（Commands）**：

   * CMake 提供了丰富的命令来定义构建规则，例如：
     * `project()`: 定义项目名称。
     * `add_executable()`: 告诉 CMake 要从哪些源文件生成一个可执行文件。
     * `add_library()`: 告诉 CMake 要生成一个库（静态库或动态库）。
     * `target_link_libraries()`: 将可执行文件与所需的库链接起来。
     * `find_package()`: 自动查找系统上安装的第三方库。

---

### 一个简单的 `CMakeLists.txt` 示例

假设你有一个简单的项目，只有一个 `main.cpp` 源文件。

```cmake
# CMake 最低版本要求
cmake_minimum_required(VERSION 3.10)

# 项目名称和使用的语言（CXX 代表 C++）
project(MyAwesomeProject VERSION 1.0 LANGUAGES CXX)

# 设置 C++ 标准
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED True)

# 添加一个可执行目标，名为 HelloWorld，由源文件 main.cpp 构建而来
add_executable(HelloWorld main.cpp)

# 如果这个可执行文件需要链接一个叫 SomeLibrary 的库
# target_link_libraries(HelloWorld PRIVATE SomeLibrary)
```

---

### CMake 相比 Autotools、纯 Makefile 的优势

| 特性               | CMake                                                                           | Autotools / Makefile                                  |
| :----------------- | :------------------------------------------------------------------------------ | :---------------------------------------------------- |
| **跨平台**   | **极佳**。一份配置，多处生成。                                            | **较差**。主要针对类Unix系统，Windows支持很弱。 |
| **语法**     | 相对现代、清晰。                                                                | 较为复杂、古老，学习曲线陡峭。                        |
| **依赖查找** | 内置强大的 `find_package()` 等机制。                                          | 需要手动编写宏或使用 `pkg-config`。                 |
| **IDE 支持** | **极佳**。可生成主流 IDE 项目文件，本身也被众多 IDE（如 CLion）直接支持。 | 几乎无直接支持。                                      |
| **生态系统** | **庞大且活跃**。是当今 C/C++ 项目的事实标准。                             | 传统且稳定，但新项目较少采用。                        |

### 总结

**CMake 是现代 C/C++ 项目的构建系统标准解决方案。** 它通过抽象底层编译工具的复杂性，让开发者能够用一份统一的配置来管理在不同平台和编译器下的构建过程，极大地提高了项目的可移植性和维护效率。

当你看到一个新项目时，如果它的构建说明是 `mkdir build && cd build && cmake .. && make`，那么你就知道它是一个使用 CMake 管理的项目。

## 构建工具 - `cmake` - 安装

### `CentOS`

```
yum install libarchive
yum install cmake
```

### `Ubuntu`

```shell
sudo apt install cmake
```

## 构建工具 - `cmake` - 构建动态或静态链接库

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-c++/demo-cmake-lib)

```shell
# 所有编译的中间文件存放在build目录中以避免被git版本管理
mkdir build && cd build
cmake ..
make
sudo make install
```

## 构建工具 - `cmake` - 构建可执行文件

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-c++/demo-cmake-executable)

```shell
# 所有编译的中间文件存放在build目录中以避免被git版本管理
mkdir build && cd build
cmake ..
make
# 安装helloworld程序到/usr/local/bin目录
sudo make install
helloworld
```

## `GCC`编译器工具集 - 概念

简单来说，**GCC 是一个核心的编译器工具集，它是编译、链接和管理代码（尤其是 C 和 C++ 代码）的瑞士军刀。**

它的全称是 **GNU Compiler Collection**（GNU 编译器集合）。这个名字中的 **“Collection”** 是关键，它意味着 GCC 不仅仅是一个单一的编译器，而是**一系列编译器、工具和相关组件的集合**。

---

### 1. 核心组成部分

GCC 工具集主要包括以下几个核心组件：

| 工具名称               | 功能描述                                                                                                                                                                                                                     |
| :--------------------- | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`gcc`**      | **C 编译器驱动程序**。这是最常用的命令。虽然我们常说“用 GCC 编译”，但 `gcc` 命令本身并不完全负责编译，它更像一个“总指挥”，根据命令参数调用后面的各个工具（预处理器、编译器、汇编器、链接器）来完成整个工作流程。 |
| **`g++`**      | **C++ 编译器驱动程序**。功能与 `gcc` 相同，但专门用于 C++ 程序。它默认会链接 C++ 标准库。                                                                                                                            |
| **`cpp`**      | **C 预处理器**。负责处理源代码中的 `#include`, `#define`, `#ifdef` 等宏指令，展开头文件，进行宏替换，生成一个“纯净”的代码文件给编译器。                                                                        |
| **`as`**       | **汇编器**。它将编译器生成的**汇编代码**（`.s` 文件）翻译成**机器代码**，输出为目标文件（`.o` 或 `.obj` 文件）。                                                                                     |
| **`ld`**       | **链接器**。它将一个或多个目标文件（`.o`）以及所需的库文件（如 `libc.a`）合并在一起，解析它们之间的相互引用，最终生成一个可执行文件或共享库。                                                                      |
| **`collect2`** | 一个链接器的封装器，用于处理 C++ 这样的语言中复杂的启动代码和构造函数调用。用户通常不会直接调用它。                                                                                                                          |

此外，GCC 还支持其他语言的编译器，如 `gfortran` (Fortran), `gnat` (Ada), `gobjc` (Objective-C) 等。

---

### 2. 重要的辅助工具

除了核心的编译工具，GCC 工具集还包含或经常与之配合使用以下工具：

| 工具名称                | 功能描述                                                                                                                                  |
| :---------------------- | :---------------------------------------------------------------------------------------------------------------------------------------- |
| **`ar`**        | **静态库打包器**。用于创建和管理静态库（`.a` 文件）。它将多个目标文件（`.o`）打包成一个单一的库文件。                           |
| **`nm`**        | **符号列表工具**。用于列出目标文件或库文件中的符号（如函数名、变量名），查看哪些符号已定义、未定义等。                              |
| **`objdump`**   | **目标文件分析工具**。用于显示目标文件的详细信息，如反汇编代码、节区头、符号表等。                                                  |
| **`objcopy`**   | **目标文件转换工具**。用于复制和转换目标文件，例如将可执行文件中的代码段提取出来生成纯二进制镜像，用于嵌入式开发。                  |
| **`strip`**     | **符号剔除工具**。用于从可执行文件中删除符号表和调试信息，从而显著减小文件体积，常用于发布版本。                                    |
| **`readelf`**   | **ELF 文件分析工具**。类似于 `objdump`，但专门用于显示 ELF (Executable and Linkable Format) 格式文件的信息，在 Linux 上非常常用。 |
| **`addr2line`** | **地址转换工具**。给定一个可执行文件中的地址，它能帮你找到对应的源代码文件名和行号，对调试崩溃问题非常有用。                        |
| **`strings`**   | **字符串提取工具**。用于打印二进制文件中所有可打印的字符串。                                                                        |

---

### 3. GCC 的完整工作流程

当你执行 `gcc main.c -o hello` 时，背后发生了一系列事情，完美展示了这些工具如何协作：

1. **预处理 (Preprocessing)**：
   `gcc` 调用 `cpp`，处理 `main.c` 中的 `#include` 和宏，生成一个临时的 `.i` 文件（预处理后的代码）。
2. **编译 (Compilation)**：
   `gcc` 调用真正的**编译器**（C 编译器组件），将预处理后的 `.i` 文件**编译**成**汇编代码**（`.s` 文件）。
3. **汇编 (Assembly)**：
   `gcc` 调用 `as`，将**汇编代码**（`.s` 文件）**汇编**成**目标文件**（`.o` 文件）。
4. **链接 (Linking)**：
   `gcc` 调用 `ld`，将上一步生成的**目标文件**（`.o`）与所需的**库文件**（如 C 标准库 `libc.so`）**链接**在一起，生成最终的**可执行文件** `hello`。

`gcc` 命令自动管理了这一切，通常我们看不到中间文件。但你可以通过添加 `-save-temps` 参数来保留它们，观察整个过程的输出。

```bash
gcc -save-temps main.c -o hello
# 这会生成 main.i, main.s, main.o, 最后是 hello
```

---

### 总结

* **GCC** 是一个**庞大的工具集生态系统**，核心功能是将人类编写的高级语言源代码转换为计算机可以执行的机器代码。
* 我们日常使用的 `gcc` 或 `g++` 命令是**驱动程序**，它负责调度整个流程。
* 真正的脏活累活是由**预处理器 (`cpp`)、编译器 (C/C++ 核心)、汇编器 (`as`)、链接器 (`ld`)** 等工具完成的。
* 辅助工具如 `ar`, `nm`, `objdump` 等，则在代码调试、分析和库管理方面发挥着重要作用。

因此，当人们提到“GCC 工具”时，他们指的通常是这一整套用于构建和剖析程序的强大命令行工具的集合。它是 Linux 和其他类 Unix 系统上软件开发的基础，也是跨平台（包括 Windows 上的 MinGW-w64）和嵌入式开发的重要工具。

## `GCC`编译器工具集 - `gcc`命令

### 编译步骤

步骤如下：

* 预处理

  > 展开宏、头文件、替换条件编译、删除注释、空行、空白，输出hello.i
  >

  ```shell
  gcc -E helloworld.c -o helloworld.i
  ```
* 编译

  > 检查语法规范，消耗时间，系统资源最多，生产汇编代码
  >

  ```shell
  gcc -S helloworld.c -o helloworld.s
  ```
* 汇编

  > 将汇编指令翻译为机器指令
  >

  ```shell
  gcc -c helloworld.c -o helloworld.o
  ```
* 链接

  > 将汇编二进制文件链接生成可执行文件
  >

  ```shell
  gcc helloworld.c -o helloworld && chmod +x helloworld && ./helloworld
  ```

### 常用参数

`-I`：指定头文件目录

`-c`：只编译，生成 `.o` 文件，不进行链接

`-g`：包含调试信息，用于 `gdb` 调试

`-O n=0~3`：编译优化，`n` 越大优化级别越高，`n=0` 表示不优化

`-Wall`：提示更多警告信息

`-D<DEF>`：编译是定义宏，注意 `-D` 和 ``<DEF>``之间没有空格

`-E`：生成预处理文件

`-M`：生成 `.c` 文件与头文件依赖关系以用于 `Makefile`，包括系统库的头文件

`-MM`：生成 `.c` 文件与头文件依赖关系以用于 `Makefile`，不包括系统的头文件
