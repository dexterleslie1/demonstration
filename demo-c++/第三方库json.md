## 主流开源json库有哪些呢？

在 C 语言生态中，由于缺乏原生的高级数据结构支持，选择一款合适的 JSON 库至关重要。根据社区活跃度、代码质量和应用场景，目前主流的开源 JSON 解析库主要分为**“极简轻量级”**和**“功能完备型”**两类。

以下是为你整理的几款最主流的 C 语言 JSON 库及其特点分析：

### 1. 极简轻量级首选：cJSON
**cJSON** 是目前 GitHub 上 Star 数最多、应用最广泛的轻量级 JSON 库。
*   **特点**：
    *   **极简**：核心代码仅由 `cJSON.c` 和 `cJSON.h` 两个文件组成，无外部依赖，直接拖入项目即可编译。
    *   **API 友好**：采用树形结构（DOM），API 设计非常符合直觉（如 `cJSON_Parse`, `cJSON_GetObjectItem`）。
    *   **内存管理**：默认使用 `malloc/free`，但支持通过 `cJSON_InitHooks` 注入自定义内存分配器，非常适合嵌入式系统（如 FreeRTOS）。
*   **适用场景**：嵌入式设备、资源受限环境、快速开发。
*   **缺点**：解析性能中规中矩，错误处理相对简单。

### 2. 极致性能与嵌入式：jsmn
**jsmn** 是一个极简主义的 JSON 解析器，它的目标不是好用，而是“小”和“快”。
*   **特点**：
    *   **单文件**：代码量极少（约 200 行），头文件实现。
    *   **零内存分配**：它不解析数据内容，只生成“令牌（Tokens）”，不占用堆内存。
    *   **速度快**：在解析速度基准测试中通常名列前茅。
*   **适用场景**：对内存极其敏感的单片机（MCU）、需要极致解析速度的场景。
*   **缺点**：API 较底层，需要自己根据 Token 去原字符串里取数据，开发效率较低。

### 3. 企业级标准：json-c
**json-c** 是 Linux 环境下非常老牌且成熟的 JSON 库，被许多系统级软件（如 NetworkManager）所采用。
*   **特点**：
    *   **引用计数**：内部实现了引用计数机制，内存管理比 cJSON 更安全，适合复杂对象共享。
    *   **功能丰富**：支持从文件流解析、深度拷贝、序列化格式化等高级功能。
    *   **稳定性**：经过长期验证，API 稳定。
*   **适用场景**：Linux 系统编程、服务端后台程序、企业级应用。
*   **缺点**：依赖较多，编译配置相对繁琐（通常使用 Autotools/CMake），体积比 cJSON 大。

### 4. 现代与易用性：Jansson
**Jansson** 是一款设计现代、文档完善的库，常用于 Web 服务端开发。
*   **特点**：
    *   **类型安全**：提供了严格的类型检查和丰富的 API。
    *   **功能全面**：支持流式解析、Unicode 处理、以及强大的 `json_pack`/`json_unpack` 函数（类似 printf 风格的格式化操作）。
    *   **内存安全**：同样采用引用计数管理内存。
*   **适用场景**：复杂的业务逻辑处理、Web 服务、需要处理大量嵌套数据的场景。

### 5. 流式处理：YAJL (Yet Another JSON Library)
**YAJL** 采用了流式解析（SAX 模式），这与上述库的 DOM 模式（一次性加载到内存）截然不同。
*   **特点**：
    *   **内存高效**：逐块读取数据，不需要将整个 JSON 文件加载到内存中。
    *   **高并发**：在处理超大文件或高并发日志分析时，性能优势巨大。
*   **适用场景**：大数据处理、日志分析、超大 JSON 文件解析。

---

### 核心参数对比

为了帮你快速决策，我总结了以下对比表：

| 库名称      | 许可证 | 核心优势                   | 内存管理         | 典型应用场景               |
| :---------- | :----- | :------------------------- | :--------------- | :------------------------- |
| **cJSON**   | MIT    | 极简、单文件、API 简单     | 手动/自定义钩子  | 嵌入式、IoT、通用开发      |
| **jsmn**    | MIT    | 极致小巧、零堆内存占用     | 无分配 (Token化) | 资源极度受限的 MCU         |
| **json-c**  | MIT    | 历史悠久、引用计数、稳定   | 引用计数         | Linux 系统工具、企业级应用 |
| **Jansson** | MIT    | 功能丰富、格式化 API、健壮 | 引用计数         | 服务器端、复杂业务逻辑     |
| **YAJL**    | ISC    | 流式解析、处理大文件       | 堆分配           | 大数据日志分析             |

### 选型建议

1.  **如果你是做嵌入式开发（STM32, ESP32 等）**：首选 **cJSON**（平衡性好）或 **jsmn**（极致省内存）。
2.  **如果你在写 Linux 后台服务或桌面应用**：推荐 **json-c** 或 **Jansson**，它们的内存管理和错误处理更适合长期运行的进程。
3.  **如果你需要处理 GB 级别的日志文件**：请务必使用 **YAJL**，否则内存会瞬间爆炸。
4.  **如果你追求开发效率和代码可读性**：**Jansson** 的 `json_pack` 风格会让你的代码非常优雅。

## cJSON库是什么呢？

**cJSON** 是目前 C 语言生态中**最流行、应用最广泛的轻量级 JSON 解析与生成库**。

它由 Dave Gamble 开发，以其**极简的设计**、**单文件结构**和**零依赖**的特性，成为了嵌入式系统、物联网设备以及底层 C 语言项目中处理 JSON 数据的**事实标准**。

简单来说，如果你需要在 C 语言项目中处理 JSON，cJSON 通常是你的第一选择，因为它既简单又可靠。

### 核心特点

1.  **极致轻量与单文件**
    *   **文件结构**：整个库的核心仅由 `cJSON.c` 和 `cJSON.h` 两个文件组成，代码量极少（通常不足 2000 行）。
    *   **零依赖**：它不依赖任何第三方库，甚至不强制依赖 C99 标准（兼容 C89），这使得它极易移植。
    *   **体积小巧**：编译后的静态库体积通常不足 20KB，非常适合资源受限的环境。

2.  **高度可移植（嵌入式首选）**
    *   **跨平台**：可以在 Windows、Linux、macOS 上运行，也能完美运行在 ARM Cortex-M、RISC-V 等微控制器上。
    *   **RTOS 支持**：广泛应用于 FreeRTOS、Zephyr、RT-Thread 等实时操作系统。
    *   **内存控制**：虽然默认使用 `malloc/free`，但它提供了钩子机制（`cJSON_InitHooks`），允许你注入自定义的内存分配函数（如 `pvPortMalloc`），这对于防止内存碎片和泄漏至关重要。

3.  **功能完备**
    *   **标准支持**：严格遵循 JSON RFC 7159/8259 标准，支持对象、数组、字符串、数字、布尔值和 null。
    *   **双向解析**：既能将 JSON 字符串解析为内存中的树状结构（反序列化），也能将内存对象转换为格式化的 JSON 字符串（序列化）。
    *   **编码处理**：支持 UTF-8 编码和 Unicode 转义（`\uXXXX`）。

### 工作原理：树形结构

cJSON 将 JSON 数据解析为一棵**树（Tree）**。
*   **节点**：每个 JSON 元素（无论是键值对、数组项还是具体数值）都是树上的一个节点，数据类型为 `cJSON` 结构体。
*   **链表**：节点之间通过双向链表（`next`/`prev`）连接，对象或数组的子元素通过 `child` 指针连接。

### 常用 API 速查

cJSON 的 API 设计非常直观，以下是开发中最常用的函数：

| 功能分类 | 函数名                                    | 说明                                           |
| :------- | :---------------------------------------- | :--------------------------------------------- |
| **解析** | `cJSON_Parse(const char *value)`          | 将 JSON 字符串解析为 `cJSON*` 对象。           |
| **创建** | `cJSON_CreateObject()` / `CreateArray()`  | 创建空的 JSON 对象或数组。                     |
| **查询** | `cJSON_GetObjectItem(obj, "key")`         | 获取对象中指定键的节点。                       |
| **添加** | `cJSON_AddItemToObject(obj, "key", item)` | 向对象中添加键值对。                           |
| **输出** | `cJSON_Print(item)`                       | 将 JSON 对象转换为带缩进的字符串（易读）。     |
| **输出** | `cJSON_PrintUnformatted(item)`            | 转换为紧凑字符串（无空格，省流量）。           |
| **释放** | `cJSON_Delete(item)`                      | **重要**：递归释放整个 JSON 树，防止内存泄漏。 |

### 代码实战示例

下面是一个简单的示例，演示如何解析一个 JSON 字符串并读取其中的字段：

```c
#include <stdio.h>
#include "cJSON.h"

int main() {
    // 1. 准备 JSON 字符串
    const char *json_string = "{\"name\":\"Alice\", \"age\":25, \"city\":\"Beijing\"}";

    // 2. 解析字符串
    cJSON *root = cJSON_Parse(json_string);
    if (root == NULL) {
        // 解析失败，获取错误信息
        const char *error_ptr = cJSON_GetErrorPtr();
        printf("解析错误: %s\n", error_ptr);
        return 1;
    }

    // 3. 获取数据
    cJSON *name_item = cJSON_GetObjectItemCaseSensitive(root, "name");
    if (cJSON_IsString(name_item) && (name_item->valuestring != NULL)) {
        printf("Name: %s\n", name_item->valuestring);
    }

    cJSON *age_item = cJSON_GetObjectItemCaseSensitive(root, "age");
    if (cJSON_IsNumber(age_item)) {
        printf("Age: %d\n", age_item->valueint);
    }

    // 4. 释放内存 (非常重要！)
    cJSON_Delete(root);

    return 0;
}
```

### 总结
cJSON 就像是 C 语言世界的“瑞士军刀”——虽然它可能不是解析速度最快（如 `jsmn`）或功能最复杂（如 `json-c`）的库，但它在**易用性、体积和功能**之间取得了完美的平衡，是 C 语言开发者处理 JSON 数据的首选工具。

### 示例

详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/blob/main/demo-c++/demo-lib-json-cjson.c

安装cJSON依赖

>提示：cJSON库没有使用cjson.pc文件配置pkg-config，所以不能通过命令pkg-config --cflags --libs libcjson获取cJSON库路径。

```sh
sudo apt install libcjson-dev
```

编译并运行

```sh
# 编译
gcc -o main demo-lib-json-cjson.c -I/usr/include/cjson -lcjson

# 运行
./main
```

## json-c库是什么呢？

**json-c** 是 C 语言生态中另一款极具影响力的 JSON 解析与生成库。

如果说 `cJSON` 是“极简主义”的代表，那么 `json-c` 就是**“工程化与稳健性”**的典范。它广泛应用于 Linux 系统编程、网络服务（如 RESTful API）以及企业级嵌入式开发中。

以下是 `json-c` 的核心特点、与 `cJSON` 的区别以及使用示例：

### 核心特点

1.  **引用计数内存管理**
    这是 `json-c` 最显著的特征。与 `cJSON` 需要手动递归删除节点不同，`json-c` 使用**引用计数**来管理对象的生命周期。
    *   **机制**：当你获取一个对象或将其传递给其他函数时，引用计数会自动增加（`json_object_get`）。
    *   **释放**：当你不再需要该对象时，调用 `json_object_put`。只有当引用计数降为 0 时，内存才会被真正释放。
    *   **优势**：这在复杂的程序中非常安全，避免了“悬空指针”或重复释放内存的问题。

2.  **深度集成 Linux/POSIX 环境**
    `json-c` 是基于 GNU Autotools 构建的，深度集成 POSIX 标准。
    *   **构建**：通常使用 `./configure && make && make install` 安装。
    *   **依赖**：虽然它尽量保持轻量，但相比 `cJSON`，它对外部构建环境（如 `pkg-config`）有一定依赖。
    *   **应用**：它是许多 Linux 发行版和开源项目（如 NetworkManager, Lighttpd, OpenWrt）的标准依赖库。

3.  **功能丰富的 API**
    它提供了非常细致的操作接口，支持：
    *   **文件流操作**：可以直接从文件读取 JSON (`json_object_from_file`) 或将 JSON 保存到文件。
    *   **类型安全**：提供宏来检查类型（如 `json_object_is_type`）。
    *   **遍历**：提供了便捷的宏（如 `json_object_object_foreach`）来遍历对象键值对。

### json-c vs. cJSON：该怎么选？

这是开发者最常问的问题。为了帮你决策，我做了一个对比表：

| 特性         | **json-c**                         | **cJSON**                       |
| :----------- | :--------------------------------- | :------------------------------ |
| **内存管理** | **引用计数** (`put`/`get`)，更安全 | 手动递归删除 (`Delete`)，需小心 |
| **代码结构** | 模块化，多个源文件                 | **单文件** (`cJSON.c` + `.h`)   |
| **集成难度** | 中等 (需编译安装 `.so`/`.a` 库)    | **极低** (直接拖入源码即可编译) |
| **主要场景** | Linux 系统编程、服务端、复杂应用   | 嵌入式 MCU、快速原型、简单脚本  |
| **API 风格** | 严谨，类似 GObject 风格            | 简单直接，类似 C 风格           |

**一句话建议**：如果你在做**Linux 应用开发**或需要处理复杂的对象共享，选 `json-c`；如果你在做**单片机开发**或只是想快速把 JSON 跑通，选 `cJSON`。

### 常用 API 速查

`json-c` 的 API 命名非常规范，通常以 `json_object_` 开头：

| 功能           | 函数/宏                                      | 说明                            |
| :------------- | :------------------------------------------- | :------------------------------ |
| **解析字符串** | `json_tokener_parse(str)`                    | 将字符串解析为 `json_object*`。 |
| **解析文件**   | `json_object_from_file(path)`                | 直接从文件加载 JSON。           |
| **创建对象**   | `json_object_new_object()`                   | 创建一个新的 JSON 对象。        |
| **添加键值**   | `json_object_object_add(obj, key, val)`      | 向对象中添加键值对。            |
| **获取值**     | `json_object_object_get(obj, key)`           | 获取指定键的子对象。            |
| **转字符串**   | `json_object_to_json_string(obj)`            | 序列化为字符串（紧凑格式）。    |
| **格式化**     | `json_object_to_json_string_ext(obj, flags)` | 支持带缩进的格式化输出。        |
| **释放内存**   | `json_object_put(obj)`                       | **减少引用计数**，为 0 时释放。 |

### 代码实战示例

下面是一个演示如何使用 `json-c` 解析并遍历对象的示例：

```c
#include <stdio.h>
#include <json-c/json.h>

int main() {
    // 1. 准备 JSON 字符串
    const char *json_str = "{\"name": "Alice", "age": 25, "skills": ["C", "Linux"]}";

    // 2. 解析
    json_object *root = json_tokener_parse(json_str);
    if (!root) {
        printf("解析失败\n");
        return 1;
    }

    // 3. 获取基本类型
    json_object *name_obj, *age_obj;
    if (json_object_object_get_ex(root, "name", &name_obj)) {
        printf("Name: %s\n", json_object_get_string(name_obj));
    }
    if (json_object_object_get_ex(root, "age", &age_obj)) {
        printf("Age: %d\n", json_object_get_int(age_obj));
    }

    // 4. 遍历数组
    json_object *skills;
    if (json_object_object_get_ex(root, "skills", &skills)) {
        int array_len = json_object_array_length(skills);
        printf("Skills: ");
        for(int i=0; i<array_len; i++) {
            json_object *skill = json_object_array_get_idx(skills, i);
            printf("%s ", json_object_get_string(skill));
        }
        printf("\n");
    }

    // 5. 释放内存 (引用计数减 1)
    json_object_put(root);

    return 0;
}
```

### 总结
**json-c** 是一款**工业级**的 JSON 库。它的学习曲线比 `cJSON` 稍高（主要是引用计数的概念），但它提供的内存安全性和文件操作便利性，使其成为 Linux 系统开发和复杂 C 语言项目中的首选工具。

### 示例

详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/blob/main/demo-c++/demo-lib-json-jsonc.c

安装json-c依赖

```sh
sudo apt install libjson-c-dev
```

编译并运行

```sh
# 编译
gcc -std=c11 -Wall -Wextra -O2 demo-lib-json-jsonc.c $(pkg-config --cflags --libs json-c) -o main

# 运行
./main
```

