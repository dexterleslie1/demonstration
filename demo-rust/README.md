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