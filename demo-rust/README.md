## Rust是什么呢？

Rust 是一种**系统级编程语言**，由 Mozilla 主导开发，设计目标是在保持高性能的同时，提供内存安全和并发安全，并且支持现代编程范式。

---

### 一、Rust 的核心特点

1. **内存安全，无需垃圾回收（GC）**
   - 通过**所有权（Ownership）**、**借用检查（Borrow Checker）**、**生命周期（Lifetimes）**这套编译期机制来保证内存安全。
   - 编译器在编译阶段就能捕获空指针、悬垂指针、数据竞争等问题，运行时不需要 GC，因此性能和 C/C++ 接近。

2. **高性能**
   - 编译为本地机器码，没有虚拟机开销。
   - 零成本抽象（Zero-cost abstractions）：高级语法带来的便利不会带来额外运行时开销。

3. **并发安全**
   - 利用所有权和类型系统在编译期防止数据竞争（data race）。
   - 多线程代码如果不安全，编译器直接报错，帮助开发者写出正确的并发程序。

4. **现代语言特性**
   - 模式匹配（`match`）、代数数据类型（`enum`）、泛型、trait（类似接口但更灵活）、闭包等。
   - 支持函数式编程风格，同时保留命令式编程的可读性。

5. **跨平台**
   - 可编译到 Windows / Linux / macOS / WebAssembly 等多种目标平台。
   - 常用于操作系统、嵌入式设备、浏览器引擎、区块链、游戏引擎等对性能和安全性要求高的场景。

---

### 二、典型应用场景

- **系统编程**：操作系统内核、驱动、嵌入式开发（如物联网设备）。
- **高性能服务**：WebAssembly 运行时、数据库、搜索引擎组件、消息队列等后端服务。
- **前端工具链**：例如前端构建工具 Vite、打包工具 Rollup 的部分核心模块使用 Rust 编写。
- **区块链与安全关键软件**：智能合约平台、加密算法实现等。

---

### 三、和其他语言的简单对比

| 特性         | C/C++              | Java/Go         | Rust                       |
| ------------ | ------------------ | --------------- | -------------------------- |
| 内存管理方式 | 手动 `malloc/free` | GC              | 编译期所有权机制（无 GC）  |
| 性能         | 很高               | 较高            | 接近 C/C++                 |
| 安全性       | 依赖程序员         | GC 保证基本安全 | 编译期保障内存 & 并发安全  |
| 学习曲线     | 中等偏难           | 相对平缓        | 初期较陡（所有权概念新颖） |

## Cargo是什么呢？

Cargo 是 Rust 官方的**包管理器和构建工具**，相当于：  
- 用 Python 时的 `pip + setuptools`  
- 用 Node.js 时的 `npm`  
- 用 Go 时的 `go mod`  

它把“下载依赖、编译项目、运行测试、发布包”这些事都统一管起来，是日常写 Rust 时几乎离不开的工具。

---

### 一、Cargo 主要做什么？

1. **创建项目**
   - 一条命令生成标准项目结构：
   ```bash
   cargo new hello_rust
   ```
   会生成：
   - `Cargo.toml`：项目配置和依赖清单
   - `src/main.rs`：入口文件

2. **编译与运行**
   - 编译并运行：
   ```bash
   cargo run
   ```
   - 只编译不运行：
   ```bash
   cargo build
   ```

3. **管理依赖**
   - 在 `Cargo.toml` 中写：
   ```toml
   [dependencies]
   serde = "1.0"
   ```
   - 然后执行：
   ```bash
   cargo build
   ```
   Cargo 会自动从 https://crates.io 下载 `serde` 及其依赖，并编译好。

4. **运行测试**
   - 自动发现并运行所有 `#[test]` 函数：
   ```bash
   cargo test
   ```

5. **代码格式化与静态检查**
   - 配合工具使用：
   ```bash
   cargo fmt      # 按官方规范格式化代码
   cargo clippy   # 检查常见错误和坏味道
   ```

6. **发布库**
   - 把你的库打包并发布到 crates.io：
   ```bash
   cargo publish
   ```

---

### 二、Cargo 项目的典型目录结构

```
hello_rust/
├── Cargo.toml        # 项目配置 + 依赖声明
├── Cargo.lock        # 精确锁定依赖版本（自动生成）
└── src/
    └── main.rs       # 主程序入口
```

- `Cargo.toml`：人写的，决定要什么依赖、什么版本。
- `Cargo.lock`：工具生成的，确保团队/CI 环境用完全一样的依赖版本，避免“在我这能跑”的问题。

---

### 三、和 rustc 的关系

- `rustc` 是 Rust 的**编译器本身**，只负责把代码编译成可执行文件或库。
- `cargo` 是**上层工具**，内部调用 `rustc` 完成实际编译，但帮你处理：
  - 依赖解析
  - 增量编译缓存
  - 多目标构建（比如同时生成 x86_64 和 wasm）

日常开发一般只用 `cargo`，很少需要直接敲 `rustc`。

## Cargo.toml文件是什么作用呢？

`Cargo.toml` 是 Rust 项目里**最核心的配置文件**，由 Cargo 工具读取，用来描述“这个项目是谁、长什么样、依赖什么”。

可以把它理解为：  
- 像 Node.js 里的 `package.json`  
- 像 Python 项目里的 `pyproject.toml` / `setup.py`  

---

### 一、Cargo.toml 主要作用

1. **声明项目基本信息**
   - 项目名称、版本、作者、描述、许可证等。
   - 示例：
   ```toml
   [package]
   name = "my_app"
   version = "0.1.0"
   edition = "2021"
   authors = ["Your Name <you@example.com>"]
   description = "一个简单的 Rust 示例项目"
   ```

2. **管理依赖（最重要的部分之一）**
   - 说明项目用了哪些第三方库（crate），以及版本约束。
   - 示例：
   ```toml
   [dependencies]
   serde = "1.0"           # 任意兼容 1.x 的最新版
   tokio = { version = "1", features = ["full"] }
   ```

3. **配置构建行为**
   - 设置 crate 类型、编译器参数、特性开关等。
   - 示例：
   ```toml
   [lib]
   name = "my_lib"
   crate-type = ["cdylib"]   # 编译成动态库
   
   [profile.release]
   opt-level = 3             # release 模式开启最高优化
   ```

4. **定义 dev-dependencies**
   - 只在开发和测试时用到的依赖，比如测试框架、mock 库。
   ```toml
   [dev-dependencies]
   proptest = "1.0"
   ```

5. **版本锁定（配合 Cargo.lock）**
   - `Cargo.toml` 写的是“版本规则”（例如 `^1.2`），  
   - 真正锁定的精确版本写在 `Cargo.lock` 里，由 Cargo 自动维护。

---

### 二、整体结构大致长这样

```toml
[package]              # 项目元数据
name = "..."
version = "..."
edition = "..."

[dependencies]         # 运行时依赖
some_crate = "1.2"

[dev-dependencies]     # 测试/开发依赖
tempfile = "3.0"

[build-dependencies]   # 构建脚本依赖
cc = "1.0"

[profile.dev]          # dev 构建配置
opt-level = 0

[profile.release]      # release 构建配置
opt-level = 3
```

---

### 三、Cargo.toml vs Cargo.lock

- **Cargo.toml**：  
  - 给人看的配置，决定“我要用什么依赖、大概什么版本”。  
  - 提交到 Git，方便团队协作和发布。

- **Cargo.lock**：  
  - 给工具看的锁定文件，记录“这次构建用的每个依赖的具体版本号”。  
  - 通常也需要提交到 Git，保证所有人、CI 环境装到的依赖完全一致。

## Cargo怎么处理依赖的呢？

Cargo 是 Rust 的包管理器和构建工具，它有一套非常系统化的方式来处理项目依赖。下面我会从几个关键点来解释 Cargo 是如何处理依赖的：

---

### 一、依赖来源

Cargo 支持多种依赖来源：

1. **crates.io（默认）**
   - 这是 Rust 官方的公共包仓库。
   - 在 `Cargo.toml` 中写：
     ```toml
     [dependencies]
     serde = "1.0"
     ```
     表示使用 crates.io 上 `serde` 的最新版（符合语义化版本规则）。

2. **Git 仓库**
   - 可以指定一个 Git 地址和分支/标签/提交：
     ```toml
     [dependencies]
     mylib = { git = "https://github.com/user/repo.git", branch = "main" }
     ```

3. **本地路径**
   - 用于开发中的本地 crate：
     ```toml
     [dependencies]
     mylib = { path = "../mylib" }
     ```

4. **私有注册表（如公司内部 registry）**
   - 通过配置 `[registries]` 和 `cargo login` 使用。

---

### 二、依赖解析（Dependency Resolution）

当你运行 `cargo build` 或 `cargo run` 时，Cargo 会：

1. 读取 `Cargo.toml` 中的依赖声明。
2. 检查 `Cargo.lock` 文件（如果存在），其中记录了**精确版本和来源**，确保可重复构建。
3. 如果 `Cargo.lock` 不存在（如新项目），则：
   - 从 crates.io 或 Git 等源获取所有依赖的元数据。
   - 解析出满足所有版本约束的**具体版本组合**（解决冲突，比如 A 需要 `log 0.4`，B 需要 `log 0.5`）。
   - 将解析结果写入 `Cargo.lock`。

> ✅ 建议将 `Cargo.lock` 提交到版本控制（尤其是应用项目），以保证团队和部署环境的一致性。

---

### 三、依赖下载与缓存

Cargo 会将下载的依赖源码缓存在本地：

- **crates.io 依赖**：  
  存放在 `~/.cargo/registry/src/` 和 `~/.cargo/registry/index/`
- **Git 依赖**：  
  存放在 `~/.cargo/git/checkouts/` 和 `~/.cargo/git/db/`

这些缓存避免重复下载，提高构建速度。

---

### 四、构建与编译

1. Cargo 使用 `rustc` 编译每个 crate。
2. 它会：
   - 按依赖拓扑排序（谁依赖谁）进行编译。
   - 只重新编译发生变化的 crate 及其依赖（增量编译）。
3. 编译产物放在 `target/` 目录下，按 profile（`debug` / `release`）和 target triple 分类。

---

### 五、版本管理策略

- 使用 **SemVer（语义化版本）**：`^1.2.3` 表示兼容 1.x.x 的最新版。
- 通过 `cargo update` 可以更新 `Cargo.lock` 中的依赖版本（在 `Cargo.toml` 允许的范围内）。
- 使用 `cargo tree` 查看依赖树，分析传递性依赖。

---

### 六、工作空间（Workspaces）

在大型项目中，多个 crate 可以组织成 workspace：

```toml
# Cargo.toml (workspace root)
[workspace]
members = ["crate1", "crate2"]
```

- 所有成员共享一个 `Cargo.lock` 和 `target/` 目录。
- 方便统一管理和跨 crate 开发。

---

### 七、安全与审计

- 使用 `cargo audit` 检查已知漏洞。
- 使用 `cargo crev` 或 `cargo vet` 进行代码审查信任链管理。

---

### 总结：Cargo 处理依赖的核心流程

```
1. 解析 Cargo.toml → 确定依赖及版本约束
2. 读取或生成 Cargo.lock → 锁定精确版本
3. 下载依赖源码到本地缓存（~/.cargo/...）
4. 按依赖图编译所有 crate
5. 输出到 target/ 目录
6. 支持更新、审计、工作空间等高级功能
```

Cargo 的设计目标就是：**可重复构建、依赖隔离、易用性强、支持现代软件工程实践**。

## Crate是什么呢？

Rust 里的 **Crate** 可以理解为：**一个 Rust 代码的打包单元和基本编译单位**。它是 Cargo 管理的最小“积木块”，也是 Rust 生态的组织基础。

---

### 一、Crate 是什么？

- **本质**：一组 Rust 源文件的集合，会被编译成一个或多个 `.rlib` / `.so` / `.dll` / `.exe` 等二进制产物。
- **作用**：
  - 封装函数、结构体、trait、宏等代码；
  - 可被别的 crate 通过 `use` 引用；
  - 是发布到 crates.io 或在 Git 上共享的基本单位。

---

### 二、两种类型的 Crate

| 类型             | 说明                                                         |
| ---------------- | ------------------------------------------------------------ |
| **二进制 crate** | 包含一个 `main` 函数，编译后生成可执行程序（`.exe`、`bin`）。<br>通常用于 CLI 工具、服务端程序等。 |
| **库 crate**     | 没有 `main` 函数，编译后生成库文件（`.rlib`、`.so` 等）。<br>提供 API 给其它 crate 调用，如 `serde`、`tokio`。 |

一个项目里**通常只有一个二进制 crate**，但可以包含多个库 crate（通过 workspace 或子 crate 组织）。

---

### 三、Crate 和 Cargo 的关系

- 一个 **Cargo 项目** 至少对应一个 crate：
  - 有 `Cargo.toml` 描述这个 crate 的元信息（名称、版本、依赖等）；
  - 有 `src/main.rs`（二进制）或 `src/lib.rs`（库）作为入口。
- 在 `Cargo.toml` 中写：
  ```toml
  [dependencies]
  serde = "1.0"
  ```
  表示：当前 crate 要依赖名为 `serde` 的另一个库 crate。

---

### 四、Crate 的来源

- **crates.io**：官方公共仓库，绝大多数开源 crate 都在这里发布。
- **Git 仓库**：可以直接依赖某个 GitHub 等项目里的 crate。
- **本地路径**：指向你自己写的本地 crate（常用于拆分大型项目）。

---

### 五、Crate Root 与模块体系

- **Crate Root**：编译这个 crate 的起点文件：
  - 二进制 crate：`src/main.rs`
  - 库 crate：`src/lib.rs`
- 在 crate root 中用 `mod xxx;` 可以把其它文件纳入同一个 crate，形成模块的层级结构。

---

### 六、举个例子

假设你有一个项目叫 `hello_cargo`：

```
hello_cargo/
├── Cargo.toml          # 声明 crate 名称和依赖
└── src/
    └── main.rs         # crate root（二进制 crate）
```

这里的整个项目就是一个 **二进制 crate**，它可以依赖很多 **库 crate**（比如 `regex`、`reqwest` 等），组合在一起完成更复杂的功能。

---

一句话记住：  
**Crate = Rust 的“模块包”，既是代码组织单位，也是编译和分发的最小单元。**  

## ~~Cargo 命令 Ubuntu 安装~~

~~在 Ubuntu 上安装 Cargo 需要先安装 Rust 工具链（Cargo 随 Rust 一起提供），推荐使用官方 **rustup** 安装：~~

1. ~~**使用 rustup 安装（推荐）**~~

   ```bash
   curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh
   ```

   ~~按提示选择默认安装即可。安装完成后，执行以下命令使环境变量生效，或重新打开终端：~~

   ```bash
   source "$HOME/.cargo/env"
   ```

2. ~~**验证安装**~~

   ```bash
   cargo --version
   rustc --version
   ```

3. ~~**可选：使用系统包管理器**~~

   ~~若不想用 rustup，也可用 Ubuntu 自带仓库安装（版本可能较旧）：~~

   ```bash
   sudo apt update
   sudo apt install cargo
   ```

   ~~建议优先使用 rustup，便于后续升级和切换 Rust 版本。~~

---

## Rust Ubuntu 安装

在 Ubuntu 上安装 Rust 推荐使用官方 **rustup**（会同时安装 `rustc` 和 `cargo`）：

1. **使用 rustup 安装（推荐）**

   ```bash
   curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh
   ```

   按提示选择默认安装即可。安装完成后执行以下命令使环境变量生效，或重新打开终端：

   ```bash
   source "$HOME/.cargo/env"
   ```

2. **验证安装**

   ```bash
   rustc --version
   cargo --version
   ```

3. **可选：使用系统包管理器**

   若不想用 rustup，也可用 Ubuntu 自带仓库安装（版本可能较旧）：

   ```bash
   sudo apt update
   sudo apt install rustc cargo
   ```

   建议优先使用 rustup，便于后续升级和切换 Rust 版本（如 stable / nightly）。

## 编译与发布

使用本站示例辅助测试：https://gitee.com/dexterleslie/demonstration/tree/main/demo-rust/demo-getting-started

### 运行

```bash
cargo run
```

输出：`Hello, world!`

### 编译

#### 开发/调试构建（默认）

```bash
cargo build
```

生成可执行文件：`target/debug/hello-world`（或 Windows 下 `hello-world.exe`）。

#### 发布构建（优化版本）

```bash
cargo build --release
```

生成可执行文件：`target/release/hello-world`。  
Release 会开启优化，体积更小、运行更快，适合实际部署和分发。

### 发布到 crates.io（可选）

若要将库发布到 [crates.io](https://crates.io)：

1. 在 crates.io 登录并获取 API Token。
2. 执行：`cargo login <your-token>`
3. 在 `Cargo.toml` 中补全 `description`、`license` 等。
4. 执行：`cargo publish`

本示例为二进制项目（`src/main.rs`），通常用于本地编译与发布构建；库项目才需要发布到 crates.io。

## vcpkg是什么呢？

**vcpkg** 是一个由微软开源的 **C/C++ 包管理器**，主要用于简化第三方库的下载、编译和集成流程。它的目标是让开发者可以更方便地管理项目依赖，尤其是在 Windows、Linux 和 macOS 平台上使用 Visual Studio 或其他编译器时。

---

### 一、主要特点

1. **跨平台支持**
   - 原生支持 Windows（Visual Studio）、Linux、macOS。
   - 可在命令行中使用，不局限于某个 IDE。

2. **自动化编译**
   - 自动从源码编译第三方库，避免手动配置复杂的编译选项。
   - 支持多种编译配置（Debug/Release、静态库/动态库）。

3. **依赖管理**
   - 类似 npm（Node.js）、pip（Python）的方式管理 C/C++ 库。
   - 支持版本控制和依赖解析。

4. **与 Visual Studio 深度集成**
   - 安装后可直接在 Visual Studio 的项目中使用已安装的库。
   - 支持 CMake 项目。

---

### 二、基本使用流程

1. **安装 vcpkg**
   ```bash
   git clone https://github.com/microsoft/vcpkg.git
   cd vcpkg
   ./bootstrap-vcpkg.sh    # Linux/macOS
   bootstrap-vcpkg.bat     # Windows
   ```

2. **安装库**
   ```bash
   ./vcpkg install fmt
   ./vcpkg install boost:x64-windows
   ```

3. **集成到项目**
   - 在 Visual Studio 中运行：
     ```bash
     ./vcpkg integrate install
     ```
   - 或在 CMake 项目中通过 `CMAKE_TOOLCHAIN_FILE` 指定 vcpkg 工具链文件。

---

### 三、优势

- **省时**：避免手动下载、解压、配置库。
- **可复现**：通过清单文件（`vcpkg.json`）记录依赖，方便团队协作。
- **社区支持**：有大量预编译的流行库（如 OpenCV、Boost、SDL 等）。

---

### 四、适用场景

- 需要快速引入多个第三方 C/C++ 库的项目。
- 希望在不同平台保持一致的构建方式。
- 使用 Visual Studio 或 CMake 进行开发。

## VCPKG_ROOT是什么呢？

`VCPKG_ROOT` 是一个**环境变量**，用来告诉系统（以及 Visual Studio、CMake、命令行工具等） **vcpkg 的安装根目录在哪里**。

---

### 一、vcpkg 是什么？

- vcpkg 是微软推出的 **C/C++ 包管理器**。
- 用来方便地安装第三方库，比如：
  - Boost
  - OpenCV
  - fmt
  - spdlog
- 安装完的库，会自动配置好头文件、库文件路径，方便项目直接链接使用。

---

### 二、`VCPKG_ROOT` 的作用

当你把 vcpkg 安装到某个目录，比如：

```
D:\dev\vcpkg
```

那么：

- 这个目录就是 **vcpkg 的根目录**；
- 你需要让工具知道它的位置，方式就是设置环境变量：

```bash
VCPKG_ROOT=D:\dev\vcpkg
```

常见用途：

1. **在命令行里使用 vcpkg 命令时**  
   不一定要进到 `D:\dev\vcpkg` 目录，只要 `VCPKG_ROOT` 设好，就可以在任意路径执行：
   ```bash
   %VCPKG_ROOT%\vcpkg install fmt:x64-windows
   ```

2. **给 CMake 用**  
   在 `CMakePresets.json` 或 `CMakeSettings.json` 中，可以写：
   ```json
   "CMAKE_TOOLCHAIN_FILE": "$env{VCPKG_ROOT}/scripts/buildsystems/vcpkg.cmake"
   ```
   CMake 会根据 `VCPKG_ROOT` 找到 vcpkg 的工具链文件，从而自动引入已安装的库。

3. **给 Visual Studio 用**  
   装了 vcpkg 的 VS 集成后，VS 也会参考 `VCPKG_ROOT` 去定位库和头文件。

---

### 三、如何设置 `VCPKG_ROOT`

- **Windows（CMD）**
  ```cmd
  setx VCPKG_ROOT "D:\dev\vcpkg"
  ```
- **Windows（PowerShell）**
  ```powershell
  [Environment]::SetEnvironmentVariable("VCPKG_ROOT", "D:\dev\vcpkg", "User")
  ```
- **Linux / macOS（bash）**
  ```bash
  export VCPKG_ROOT=$HOME/dev/vcpkg
  # 可写入 ~/.bashrc 或 ~/.zshrc 使其永久生效
  ```

设置完成后，重新开一个终端/IDE，让新的环境变量生效。

---

### 四、小结

- `VCPKG_ROOT` 不是系统自带的，而是 **vcpkg 的使用者自己定义的环境变量**；
- 它的值 = vcpkg 所在目录；
- 有了它，各种工具（命令行、CMake、VS）才能找到 vcpkg 和它管理的库。

## Ubuntu安装vcpkg

>提示：vcpkg 没有官方的 Ubuntu apt 软件包。vcpkg 是微软开发的 C/C++ 包管理器，需要克隆其 Git 仓库并运行引导脚本才能安装。

步骤如下：

1. 自动设置VCPKG_ROOT环境变量

   ```sh
   sudo vim /etc/profile.d/vcpkg.sh
   
   # 内容如下：
   #!/bin/bash
   
   export VCPKG_ROOT=/home/dexterleslie/workspace-git/vcpkg
   
   ```

   重启系统。

2. 配置git克隆仓库使用代理

   ```sh
   sudo apt install git
   git config --global http.proxy socks5h://192.168.1.55:1080
   git config --global https.proxy socks5h://192.168.1.55:1080
   ```

3. 克隆仓库

   ```sh
   mkdir ~/workspace-git
   cd ~/workspace-git
   git clone https://github.com/microsoft/vcpkg.git
   ```

4. 更新您的系统并安装所需的依赖项：Vcpkg 及其管理的库需要构建工具、Git 和其他实用程序。

   ```sh
   sudo apt update
   sudo apt install -y build-essential tar git zip unzip curl pkg-config nasm
   ```

5. 运行引导脚本：导航到新克隆的目录并运行相应的脚本来构建 vcpkg 可执行文件。

   ```sh
   cd ~/workspace-git/vcpkg
   sudo ./bootstrap-vcpkg.sh
   ```

6. 创建符号链接（可选，用于系统范围访问）：要从任何目录使用 vcpkg 命令，可以在系统 PATH 中的目录中创建符号链接，例如 /usr/local/bin。

   ```sh
   sudo ln -s ~/workspace-git/vcpkg/vcpkg /usr/local/bin/vcpkg
   ```

7. 验证安装

   ```sh
   vcpkg version
   ```

8. 安装完成后，您可以使用 vcpkg 安装 C/C++ 库。例如，要安装 fmt 库。

   >提示：首次运行需要等待比较长时间，因为需要下载CMake源码并编译。

   ```sh
   vcpkg install fmt
   ```

   
