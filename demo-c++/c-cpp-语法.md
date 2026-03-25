## 宏定义

带参数宏定义示例：

```c
#define	__SOCKADDR_COMMON(sa_prefix) \
  sa_family_t sa_prefix##family
```

这段代码是 **C 语言宏定义**，主要用于 **Linux/Unix 系统编程** 中定义网络套接字地址结构（如 `struct sockaddr_in`, `struct sockaddr_in6` 等）的公共部分。

它出自 GNU C 库（glibc）的头文件（通常是 `<bits/socket.h>` 或 `<sys/socket.h>` 的内部实现）。

### 1. 代码逐行解析

```c
#define	__SOCKADDR_COMMON(sa_prefix) \
  sa_family_t sa_prefix##family
```

*   **`#define`**: 定义一个宏。
*   **`__SOCKADDR_COMMON(sa_prefix)`**: 宏的名字是 `__SOCKADDR_COMMON`，它接受一个参数 `sa_prefix`。
    *   双下划线开头（`__`）表示这是**内部实现细节**，供系统头文件内部使用，普通用户代码不应直接调用或依赖它，因为不同版本的 glibc 可能会改变它。
*   **`\`**: 反斜杠表示宏定义延续到下一行。
*   **`sa_family_t`**: 这是一个数据类型，通常定义为 `unsigned short int`，用于存储**地址族**（Address Family），例如 `AF_INET` (IPv4), `AF_INET6` (IPv6), `AF_UNIX` (本地套接字) 等。
*   **`sa_prefix##family`**: 这是 C 预处理器的**令牌粘贴运算符**（Token Pasting Operator，`##`）。
    *   它的作用是将参数 `sa_prefix` 的值和字符串 `family` 拼接成一个新的标识符。
    *   例如：如果传入 `sa_`，结果就是 `sa_family`；如果传入 `__sockaddr_`，结果就是 `__sockaddr_family`。

### 2. 它的作用是什么？

在 Linux 系统中，所有的套接字地址结构（无论是 IPv4、IPv6 还是 Unix Domain Socket）的第一个成员必须是一个表示地址族的字段，以便内核函数（如 `bind`, `connect`, `sendto`）能够识别传入的结构体具体是哪种类型。

为了避免在每个结构体中重复写这一行代码，glibc 使用这个宏来统一生成这个公共字段。

### 3. 实际使用示例

这个宏通常这样被使用（简化版示意）：

```c
/* 定义 IPv4 地址结构体 */
struct sockaddr_in {
    __SOCKADDR_COMMON(sin_);  // 展开后变成：sa_family_t sin_family;
    
    in_port_t sin_port;       // 端口号
    struct in_addr sin_addr;  // IPv4 地址
    char sin_zero[8];         // 填充字节，为了对齐到与 sockaddr 相同的大小
};

/* 定义 IPv6 地址结构体 */
struct sockaddr_in6 {
    __SOCKADDR_COMMON(sin6_); // 展开后变成：sa_family_t sin6_family;
    
    in_port_t sin6_port;      // 端口号
    uint32_t sin6_flowinfo;   // IPv6 流信息
    struct in6_addr sin6_addr;// IPv6 地址
    uint32_t sin6_scope_id;   // 作用域 ID
};

/* 定义通用地址结构体 */
struct sockaddr {
    __SOCKADDR_COMMON(sa_);   // 展开后变成：sa_family_t sa_family;
    char sa_data[14];         // 协议特定的地址数据
};
```

### 4. 为什么要这样设计？

1.  **代码复用**：确保所有地址结构体的第一个字段名称虽然不同（`sin_family`, `sin6_family`, `sa_family`），但类型和语义完全一致。
2.  **多态性基础**：由于所有结构体的第一个字段都是 `sa_family_t` 类型且位于偏移量为 0 的位置，C 语言允许将 `struct sockaddr_in*` 强制转换为 `struct sockaddr*`。内核函数接收 `struct sockaddr*` 指针后，首先读取前几个字节（即 `sa_family`），就能知道实际传入的是哪种结构体，从而进行正确的处理。
3.  **维护性**：如果未来 `sa_family` 的类型或定义方式需要改变，只需修改这一个宏，所有使用该宏的结构体都会自动更新。

### 总结

`__SOCKADDR_COMMON(sa_prefix)` 是一个**内部宏**，利用 C 语言的**令牌粘贴**功能，在各种套接字地址结构体中自动生成名为 `<前缀>family` 的 `sa_family_t` 类型成员变量。这是 Linux 网络编程底层实现中保证结构体兼容性和多态性的关键技巧。