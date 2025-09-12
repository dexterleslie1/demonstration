## 开发`IDE`安装和配置

> 使用`VSCode`作为`go`开发`IDE`，安装`VSCode`之后直接创建`go`文件编写`go`代码即可从源代码执行程序，根据`VSCode`弹出提示安装`golang`相关插件以便`debug`使用。



## 安装



### `Ubuntu`

参考 <a href="/dcli/README.html#安装" target="_blank">链接</a> 安装 `dcli` 程序

使用 `dcli` 程序安装 `Go`

```bash
sudo dcli golang install
```

安装成功后重启操作系统以加载最新的环境变量



### `macOS`

通过 [链接](https://studygolang.com/dl) 下载 `pkg` 安装包并根据提示安装 `Go`

 编辑 `~/.bash_profile` 添加如下内容：

```bash
export GOPROXY=https://goproxy.io
export GO111MODULE=on
PATH=$PATH:~/go/bin
export PATH
```

验证 `Go` 是否成功安装

```bash
go version
```



### `centOS8`

>[参考链接](https://blog.csdn.net/shuaihj/article/details/123018041)

通过 [链接](https://studygolang.com/dl) 下载 `Go Linux` 发行包，例如：`go1.20.12.linux-amd64.tar.gz`

编辑 `~/.bash_profile` 添加如下内容：

```bash
export GOPROXY=https://goproxy.io
export GO111MODULE=on
PATH=$PATH:~/go/bin
export PATH
```

验证 `Go` 是否成功安装

```bash
go version
```



### `Windows Server 2016 Datacenter`

> [参考链接](https://silentinstallhq.com/go-programming-language-silent-install-how-to-guide/)

通过 [链接](https://go.dev/dl/) 下载 `Go MSI` 安装程序并根据 `MSI` 提示安装 `Go`

设置环境变量

```properties
GOPROXY=https://goproxy.io
GO111MODULE=on
```

打开 `CMD` 验证 `Go `是否成功安装

```bash
go version
```



## 语法基础

### 静态方法

> 注意：参考golang源代码/usr/local/software/go/src/os/exec/exec.go#func Command(name string, arg ...string) *Cmd方法总结golang静态方法的标准写法。
>
> 参考 demo-static-method

### 派生和继承

> 参考 demo-inheritance

## 编译发布golang程序

**https://freshman.tech/snippets/go/cross-compile-go-programs/**

```shell
# 发布windows 64位
GOOS=windows GOARCH=amd64 go build -o test-windows-x86_64.exe test.go

# 发布windows 32位
GOOS=windows GOARCH=386 go build -o test-windows-i386.exe test.go

# 发布macOS 64位
GOOS=darwin GOARCH=amd64 go build -o test-darwin-x86_64 test.go

# 发布macOS 32位
GOOS=darwin GOARCH=386 go build -o bin/test-darwin-i386 test.go

# 发布linux 64位
GOOS=linux GOARCH=amd64 go build -o test-linux-x86_64 test.go
```

## 使用go run从源代码运行golang程序

```shell
go run xxx.go
```



## `go mod` - 概念

`go mod` 是 Go 语言（Golang）的官方依赖管理工具，全称为 **Go Modules**。它从 Go 1.11 版本开始引入，旨在解决 Go 项目中的依赖管理问题（如版本控制、依赖隔离等），取代了旧的依赖管理方式（如 `GOPATH` 和 `vendor` 目录）。

---

### **核心概念**
1. **模块（Module）**  
   - 一个模块是**一组相关的 Go 包的集合**，由项目根目录的 `go.mod` 文件定义。
   - 模块可以发布到代码仓库（如 GitHub），并指定版本号（如 `v1.2.3`）。

2. **`go.mod` 文件**  
   - 记录模块的**名称（路径）**、**Go 版本**和**依赖项**（包括版本）。
   - 示例：
     ```go
     module github.com/yourname/project
     go 1.21
     require (
         github.com/gin-gonic/gin v1.9.1
         golang.org/x/sys v0.8.0
     )
     ```

3. **`go.sum` 文件**  
   - 记录依赖项的**加密哈希值**，用于校验依赖的完整性和版本一致性。

---

### **常用命令**
| 命令                   | 作用                                                   |
| ---------------------- | ------------------------------------------------------ |
| `go mod init <模块名>` | 初始化模块（生成 `go.mod` 文件）。                     |
| `go mod tidy`          | 添加缺失的依赖，移除未使用的依赖。                     |
| `go get <包名>@<版本>` | 下载或更新依赖（如 `go get example.com/pkg@v1.2.3`）。 |
| `go mod download`      | 下载依赖到本地缓存（默认在 `$GOPATH/pkg/mod`）。       |
| `go mod vendor`        | 将依赖复制到项目的 `vendor` 目录（可选）。             |

---

### **优势**
1. **版本控制**  
   - 支持语义化版本（如 `v1.2.3`）和伪版本（如 `v0.0.0-20230912080111-8d6d44`）。
2. **依赖隔离**  
   - 每个项目独立管理依赖，不再依赖全局 `GOPATH`。
3. **可重复构建**  
   - 通过 `go.mod` 和 `go.sum` 确保依赖的一致性。

---

### **示例流程**
1. 初始化模块：
   ```bash
   go mod init github.com/yourname/project
   ```
2. 编写代码并自动添加依赖：
   ```bash
   go run main.go  # 工具会自动识别并更新 `go.mod`
   ```
3. 手动整理依赖：
   ```bash
   go mod tidy
   ```

---

### **常见问题**
- **国内下载慢？**  
  设置 GOPROXY（如 `go env -w GOPROXY=https://goproxy.cn,direct`）。
- **依赖冲突？**  
  使用 `go mod why` 或 `go mod graph` 分析依赖关系。

Go Modules 是现代化 Go 开发的基石，熟练掌握它能显著提升项目管理效率！



## `go mod` - `init`

`go mod init` 是 Go Modules 的初始化命令，用于**创建一个新的 Go 模块（Module）**，并在项目根目录生成 `go.mod` 文件。这是使用 Go Modules 管理依赖的第一步。

---

### **核心作用**

1. **定义模块路径**  
   - 为项目指定一个唯一的模块名称（通常是代码仓库路径，如 `github.com/username/project`）。
   - 该路径会成为其他项目引用你的模块时的导入路径。

2. **生成 `go.mod` 文件**  
   - 创建一个初始的 `go.mod` 文件，内容包含模块名称和 Go 版本（后续通过 `go mod tidy` 自动添加依赖）。

3. **启用 Go Modules 功能**  
   - 标记当前目录为一个 Go 模块的根目录，后续所有依赖管理（如 `go get`、`go mod tidy`）均基于此模块。

---

### **基本语法**

```bash
go mod init <模块路径>
```

- **模块路径**：通常是代码托管平台的仓库地址（如 GitHub/GitLab），遵循“域名/作者/项目名”格式。  
  （若项目暂不上传，也可用自定义路径如 `example.com/myproject`，但需确保唯一性。）

---

### **使用示例**

#### 1. 初始化一个准备发布的项目

```bash
# 在项目根目录执行（目录名不强制要求与模块名相同）
go mod init github.com/yourname/myapp
```

生成的 `go.mod` 文件：

```go
module github.com/yourname/myapp
go 1.21  # 当前使用的 Go 版本
```

#### 2. 初始化一个本地测试项目（无需发布）

```bash
go mod init my-local-project
```

生成的 `go.mod`：

```go
module my-local-project
go 1.21
```

---

### **注意事项**

1. **必须位于项目根目录**  
   - 执行命令前需 `cd` 到包含 `.go` 源代码的目录（通常是项目顶层目录）。

2. **避免重复初始化**  
   - 如果目录下已存在 `go.mod` 文件，再次运行 `go mod init` 会报错。需删除旧文件或修改现有配置。

3. **模块路径的规范性**  
   - 若计划公开代码，模块路径应与仓库 URL 一致，否则他人无法通过 `go get` 下载你的模块。

4. **Go 版本兼容性**  
   - 生成的 `go.mod` 中会记录当前 Go 工具链的版本，但不会影响代码的兼容性（仅作为提示）。

---

### **常见问题**

#### Q：如果模块路径写错了怎么办？

- 直接编辑 `go.mod` 文件中的 `module` 行，或删除 `go.mod` 后重新运行 `go mod init`。

#### Q：为什么初始化后 `go.mod` 中没有依赖？

- `go mod init` 仅初始化模块，依赖需通过 `go get` 或代码中的 `import` 触发自动添加（或手动运行 `go mod tidy`）。

---

### **后续操作**

1. 添加依赖：

   ```bash
   go get github.com/gin-gonic/gin@v1.9.1
   ```

2. 整理依赖：

   ```bash
   go mod tidy
   ```

---

通过 `go mod init`，你可以快速为项目建立规范的依赖管理基础，告别传统的 `GOPATH` 模式，实现更灵活的代码组织和版本控制。



## `go mod` - `tidy`

`go mod tidy` 是 Go Modules 中的一个重要命令，主要用于**整理和维护项目的依赖关系**，确保 `go.mod` 和 `go.sum` 文件的准确性和完整性。它的核心作用如下：

---

### **主要功能**
1. **添加缺失的依赖**  
   - 自动扫描项目中的 `import` 语句，将代码中实际使用但未在 `go.mod` 中声明的依赖添加到文件中。

2. **移除未使用的依赖**  
   - 删除 `go.mod` 中已声明但代码中未引用的依赖（例如之前手动添加或临时测试的包）。

3. **同步依赖版本**  
   - 根据代码中的实际使用情况，更新依赖的版本号（例如间接依赖的版本）。

4. **更新 `go.sum` 文件**  
   - 重新计算所有依赖的哈希值，确保 `go.sum` 文件与当前依赖完全匹配。

---

### **使用场景**
- **初始化项目后**：首次引入依赖后运行，确保依赖完整。
- **代码重构后**：删除或添加了 `import` 语句时，同步依赖。
- **协作开发时**：拉取他人代码后，确保本地依赖与项目一致。
- **发布前**：清理无用依赖，减少项目体积。

---

### **示例**
假设项目中有以下代码：
```go
import (
    "github.com/gin-gonic/gin" // 实际使用的依赖
    "github.com/old/unused-pkg" // 未使用的依赖（残留）
)
```

执行命令：
```bash
go mod tidy
```
- **结果**：
  - 如果 `github.com/gin-gonic/gin` 未在 `go.mod` 中，会自动添加。
  - 如果 `github.com/old/unused-pkg` 在 `go.mod` 中但未使用，会被移除。
  - `go.sum` 文件会同步更新。

---

### **注意事项**
1. **网络依赖**  
   - 执行时会从网络下载缺失的模块（需确保网络畅通或配置了正确的 `GOPROXY`）。

2. **间接依赖**  
   - 如果直接依赖的库自身依赖其他包（间接依赖），`go mod tidy` 会确保它们的版本正确。

3. **副作用**  
   - 该命令可能改变 `go.mod` 和 `go.sum` 文件的内容，建议在版本控制（如 Git）中检查变更后再提交。

---

### **常用组合命令**
```bash
go mod tidy && go mod verify  # 整理依赖后验证哈希值
go mod tidy -v                # 显示详细操作日志（verbose）
```

---

通过定期运行 `go mod tidy`，可以保持项目的依赖干净、准确，避免“依赖幽灵”问题（存在未声明的依赖或残留无用依赖）。



## `go mod` - `go.mod`文件

`go.mod` 文件是 **Go Modules 的核心配置文件**，用于定义和管理项目的模块信息、依赖关系及版本约束。它的作用类似于其他语言中的 `package.json`（Node.js）或 `requirements.txt`（Python），但设计更简洁且与 Go 工具链深度集成。

---

### **核心作用**
#### 1. **定义模块标识**
   - **`module` 指令**：声明模块的全局唯一路径（通常是代码仓库地址），其他项目通过该路径引用此模块。
     ```go
     module github.com/yourname/project  // 其他项目通过此路径导入你的包
     ```

#### 2. **指定 Go 版本**
   - **`go` 指令**：标记项目所需的 Go 最低版本（工具链会检查版本兼容性）。
     ```go
     go 1.21  // 要求使用 Go 1.21 或更高版本
     ```

#### 3. **管理依赖项**
   - **`require` 指令**：显式声明项目依赖的其他模块及版本。
     ```go
     require (
         github.com/gin-gonic/gin v1.9.1  // 直接依赖
         golang.org/x/sys v0.8.0          // 另一个依赖
     )
     ```

#### 4. **控制依赖版本**
   - 支持语义化版本（如 `v1.2.3`）、分支/提交哈希（如 `master` 或 `@a1b2c3d`）和版本排除（`exclude`）。
   - 示例：禁止使用有问题的版本
     ```go
     exclude github.com/old/lib v1.2.0  // 排除特定版本
     ```

#### 5. **间接依赖标记**
   - 自动标记未被项目直接引用的间接依赖（`// indirect`）。
     ```go
     require (
         github.com/indirect/lib v2.0.0 // indirect
     )
     ```

---

### **文件结构示例**
```go
module github.com/yourname/project  // 模块路径

go 1.21  // Go 版本

require (
    github.com/gin-gonic/gin v1.9.1
    golang.org/x/sys v0.8.0
)

replace github.com/old/mod => ./local/mod  // 替换依赖为本地路径
exclude github.com/buggy/lib v1.0.0       // 排除特定版本
```

---

### **关键特性**
1. **自动维护**  
   - 通过 `go get`、`go mod tidy` 等命令自动更新依赖版本，无需手动编辑。

2. **版本精确性**  
   - 依赖版本锁定在 `go.mod` 中，结合 `go.sum`（哈希校验）确保构建一致性。

3. **灵活覆盖**  
   - 使用 `replace` 指令临时替换依赖（如调试本地分支或 fork 的库）：
     ```go
     replace github.com/remote/lib => /path/to/local/lib
     ```

4. **最小版本选择（MVS）**  
   - Go 的依赖解析算法会选择能满足所有约束的**最低兼容版本**，避免依赖膨胀。

---

### **与 `go.sum` 的关系**
- `go.mod` 定义依赖的版本，`go.sum` 记录依赖的**加密哈希值**，用于验证下载的模块是否被篡改。
- 二者需一并提交到版本控制（如 Git）以确保团队协作和构建可复现性。

---

### **常见操作场景**
1. **新增依赖**  
   - 运行 `go get example.com/pkg@v1.2.3`，工具会自动更新 `go.mod`。

2. **降级/升级依赖**  
   - 指定版本号重新获取：`go get example.com/pkg@v1.1.0`。

3. **本地调试依赖**  
   - 在 `go.mod` 中添加 `replace` 指令指向本地路径。

4. **清理无用依赖**  
   - 运行 `go mod tidy` 移除未使用的条目。

---

### **注意事项**
- **不要手动删除 `require` 条目**：应通过 `go mod tidy` 自动清理。
- **谨慎使用 `replace`**：仅限临时调试，提交代码时需确认是否需保留。
- **模块路径一旦发布不可更改**：否则会导致依赖者无法升级。

---

通过 `go.mod`，Go 项目实现了声明式、可追溯的依赖管理，显著提升了开发效率和构建可靠性。



## `go mod` - `go.sum`文件

`go.sum` 文件是 Go Modules 中用于**确保依赖项完整性和安全性**的关键文件，它与 `go.mod` 配合工作，记录所有依赖模块的**加密哈希值**。以下是它的核心作用和工作原理：

---

### **核心作用**
1. **完整性校验**  
   - 存储每个依赖模块的特定版本的**哈希值**（SHA-256）。当再次下载或构建项目时，Go 工具链会比对哈希值，若不一致则报错，防止依赖被篡改或损坏。

2. **安全防护**  
   - 避免“依赖劫持”或“中间人攻击”。即使依赖仓库被入侵，哈希不匹配会触发警告。

3. **构建可重现性**  
   - 确保不同环境（开发/生产）或不同时间下载的同一版本依赖内容完全一致。

---

### **文件结构示例**
每行记录一个依赖版本的哈希值，格式为：
```text
<模块路径> <版本> <哈希算法>/<哈希值>
<模块路径> <版本> go.mod <哈希算法>/<哈希值>  # 记录go.mod文件的哈希
```
示例：
```text
github.com/gin-gonic/gin v1.9.1 h256:4d8a4e...
github.com/gin-gonic/gin v1.9.1/go.mod h256:8f3bb2...
```

---

### **关键特性**
1. **自动生成与更新**  
   - 通过 `go get`、`go mod tidy` 等命令自动维护，无需手动编辑。

2. **多哈希记录**  
   - 每个依赖版本会记录两个哈希：
     - 模块压缩包（.zip）的哈希。
     - 该模块自身的 `go.mod` 文件的哈希（防止依赖的依赖被篡改）。

3. **严格校验**  
   - 若 `go.sum` 中不存在某个依赖的哈希记录，Go 会拒绝构建（除非通过 `-mod=mod` 强制下载）。

---

### **与 `go.mod` 的关系**
| 文件     | 作用                     | 是否需提交版本控制 |
| -------- | ------------------------ | ------------------ |
| `go.mod` | 声明依赖及其版本约束     | **是**             |
| `go.sum` | 记录依赖内容的加密哈希值 | **是**             |

- **协作开发时**：必须同时提交 `go.mod` 和 `go.sum`，否则他人可能下载到被篡改的依赖。
- **离线构建时**：`go.sum` 可确保本地缓存的依赖未被意外修改。

---

### **常见问题**
#### Q1: 为什么有时 `go.sum` 中有重复条目？
- 同一依赖的不同版本会分别记录（如 `v1.0.0` 和 `v1.1.0`）。
- 间接依赖（被其他依赖引入但未直接使用）也会被记录。

#### Q2: 能否删除 `go.sum` 重新生成？
- 可以，但需谨慎：
  ```bash
  rm go.sum && go mod tidy  # 重新下载并生成哈希
  ```
  但会触发所有依赖的重新下载和校验，可能因网络问题失败。

#### Q3: `go.sum` 文件冲突怎么解决？
- 通常是版本不一致导致，运行 `go mod tidy` 自动同步即可。

---

### **注意事项**
1. **必须提交到 Git**  
   - 若忽略 `go.sum`，团队其他成员可能因哈希不匹配导致构建失败。

2. **不要手动编辑**  
   - 哈希值由 Go 工具链自动计算，手动修改会导致校验失败。

3. **依赖更新时同步更新**  
   - 升级依赖版本后，旧的哈希记录可能保留（无害），但 `go mod tidy` 会清理无用条目。

---

### **示例场景**
1. **首次添加依赖**  
   ```bash
   go get github.com/gin-gonic/gin@v1.9.1
   ```
   - 工具会自动在 `go.mod` 中添加依赖，并在 `go.sum` 中记录哈希。

2. **校验依赖完整性**  
   ```bash
   go mod verify
   ```
   输出类似：
   ```text
   all modules verified  # 所有哈希匹配
   ```

---

通过 `go.sum`，Go Modules 实现了“信任但验证”的依赖管理机制，显著提升了供应链安全性。它是现代 Go 开发中不可或缺的一部分。



## `go mod` - 管理项目

步骤如下：

>参考链接：https://blog.csdn.net/Fly_as_tadpole/article/details/109441310
>
>使用本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-golang/demo-lib-sysinfo) 协助测试。

- 初始化`go.mod`文件

  ```sh
  $ go mod init github.com/dexterleslie1/demo-lib-sysinfo
  
  # 生成的 go.mod 文件内容如下：
  
  // 模块的名称
  module github.com/dexterleslie1/demo-lib-sysinfo
  
  // 最低的 go 版本
  go 1.20
  
  ```

- 自动推导依赖并更新到`go.mod`文件中，生产`go.sum`文件锁定当前第三方库依赖版本。

  ```sh
  # 生成 go.sum 文件
  # 提示：自动推导依赖并更新到 go.mod 文件中，生产 go.sum 文件锁定当前第三方库依赖版本。
  # 提示：会自动把项目所有依赖源代码下载到 ~/go/pkg/mod 目录中
  # 提示：会自动升级依赖的版本，例如：k8s.io/apiextensions-apiserver 依赖于 k8s.io/api。当前 k8s.io/apiextensions-apiserver 版本为v0.29.0，如果 k8s.io/api 版本小于 v0.29.0 则会被自动升级为此版本
  go mod tidy
  ```

- 如果生成的`go.mod`文件中自动推导的依赖版本不符合预期，可以手动编辑`go.mod`文件中的依赖版本

  ```
  module github.com/dexterleslie1/demo-lib-sysinfo
  
  go 1.20
  
  // 自动推导生成的依赖版本为 v1.1.3
  // require github.com/zcalusic/sysinfo v1.1.3
  
  // 手动修改版本为 v1.0.2
  require github.com/zcalusic/sysinfo v1.0.2
  
  require github.com/google/uuid v1.6.0 // indirect
  
  ```

- 从源代码运行示例

  ```sh
  sudo -E go run main.go
  ```

  

## 包(package)

### 包作用

> go语言如何调用不同文件，package的作用是什么？[链接](https://www.qycn.com/xzx/article/8412.html)
>
> go语言package是golang基本的管理单元，在同一个package中可以有多个不同文件，只要每个文件的头部都有“package xxx”的相同name，就可以在主方法中使用“xxx.Method()”调用不同文件中的方法。
>
> go 依赖管理的演进经历了以下 3 个阶段：GOPATH(存在缺陷被抛弃)、go vendor(存在缺陷被抛弃)、go module(现在使用这个方法模块化) [链接](https://blog.csdn.net/Sihang_Xie/article/details/124851399)

## import、init()、main()

### 基础解析

> [链接](https://blog.csdn.net/ribavnu/article/details/51646608)
>
> main() ,init()方法是go中默认的两个方法，两个保留的关键字。
> init()方法 是在任何package中都可以出现，但是建议 每个package中只包含一个init()函数比较好，容易理解。
> 但是main() 方法只能用在package main 中。
> Go程序会自动调用init()和main()，所以你不需要在任何地方调用这两个函数。每个
> package中的init函数都是可选的，但package main就必须包含一个main函数。
> 程序的初始化和执行都起始于main包。如果main包还导入了其它的包，那么就会在编译时
> 将它们依次导入。有时一个包会被多个包同时导入，那么它只会被导入一次（例如很多包可
> 能都会用到fmt包，但它只会被导入一次，因为没有必要导入多次）。当一个包被导入时，
> 如果该包还导入了其它的包，那么会先将其它包导入进来，然后再对这些包中的包级常量
> 和变量进行初始化，接着执行init函数（如果有的话），依次类推。等所有被导入的包都加
> 载完毕了，就会开始对main包中的包级常量和变量进行初始化，然后执行main包中的
> init函数(如果存在的话)，最后执行main函数

### import用法

## 变量

### 变量赋值=和:=区别

> =是变量的单纯赋值，:=是变量的定义和赋值
>
> https://baijiahao.baidu.com/s?id=1709571399429039540&wfr=spider&for=pc

## 第三方库使用

### progressbar使用

> 参考 demo-lib-progressbar
>
> https://github.com/schollz/progressbar

### termenv使用

> 参考 demo-lib-termenv
>
> https://github.com/muesli/termenv

### reflow使用

> 参考 demo-lib-reflow
>
> https://github.com/muesli/reflow

### glamour使用

> 参考 demo-lib-glamour
>
> https://github.com/charmbracelet/glamour

### bubbletea使用

> 参考 demo-lib-bubbletea
>
> https://github.com/charmbracelet/bubbletea

### lipgloss使用

> 参考 demo-lib-lipgloss
>
> https://github.com/charmbracelet/lipgloss

### sysinfo使用

> 参考 demo-lib-sysinfo
>
> https://github.com/zcalusic/sysinfo

### emoji使用

> 参考 demo-lib-emoji
>
> https://github.com/enescakir/emoji/tree/master

### xterm使用

> 参考 demo-lib-xterm
>
> golang.org/x/term

### promptui使用

> 参考 demo-lib-promptui
>
> https://github.com/manifoldco/promptui

### properties使用

> 参考 demo-lib-properties
>
> https://github.com/magiconair/properties



## 数据类型

> 参考 demo-data-type





## go mod vendor用法

> https://blog.csdn.net/test1280/article/details/120855865



## go help 用法

### 用途

用于查看 go xxx 相关命令的帮助文档

### 用法

查看`go list`命令帮助文档

```shell
go help list
```



## go get

> https://gosamples.dev/go-get/
> The `go get` command handles package management - adding, updating, or removing dependencies in the `go.mod` file. The `go get` does not build packages.

## go install

> https://gosamples.dev/go-get/
> The [`go install`](https://go.dev/ref/mod#go-install) command builds the package and installs the executable file in the directory defined by the `GOBIN` environment variable, which defaults to the `$GOPATH/bin` path. Installing the executable file allows you to call it directly  from your system terminal by simply typing the command name, e.g. `mytool` in the command line.



## go list 用法

### 参考资料

https://dave.cheney.net/2014/09/14/go-list-your-swiss-army-knife

### 用法

返回当前目录在使用 `import` 时的路径，`-f '\{\{ .ImportPath \}\}'` 和不添加 `-f` 一致是默认格式

```shell
go list
或者
go list -f '{{ .ImportPath }}'
```

查看指定 `package import` 路径

```shell
go list github.com/juju/juju
```

查询测试文件列表

```shell
go list -f '{{ .TestGoFiles }}'
```

查询外部测试文件列表

```shell
go list -f '{{ .XTestGoFiles }}'
```

查询直接依赖列表

```shell
go list -f '{{ .Imports }}'
```

查询直接和间接依赖列表

```shell
go list -f '{{ .Deps }}'
```



## golang内置api



### packagestest相关api用法

#### 参考资料

> https://pkg.go.dev/golang.org/x/tools/go/packages/packagestest
>
> 参考 demo-package-packagestest demo





## golang marker comment(marker注释)

> https://pkg.go.dev/sigs.k8s.io/controller-tools/pkg/markers

