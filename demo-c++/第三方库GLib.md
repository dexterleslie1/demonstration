## GLib库是什么呢？

GLib 是一个功能强大、高性能且跨平台的 C 语言工具库。它最初是为 GTK+ 图形界面框架开发的底层支撑库，后来发展成为一个独立的、通用的基础库，被 GNOME 项目广泛使用。

简单来说，GLib 为 C 语言开发者提供了一套“瑞士军刀”式的工具集，封装了大量常用的数据结构和系统功能，极大地提升了 C 语言开发的效率和程序的可移植性。

### 主要特点

GLib 的核心价值在于它弥补了标准 C 语言的一些不足，提供了更高级、更安全的抽象：

*   **出色的跨平台性**：它抽象了不同操作系统（如 Linux, Windows, macOS）的底层差异，让你可以用统一的 API 进行开发，程序无需大改就能轻松移植。
*   **丰富的数据结构**：提供了链表（`GList`）、哈希表（`GHashTable`）、动态字符串（`GString`）、队列等常用数据结构的线程安全实现，开发者无需重复造轮子。
*   **面向对象编程支持**：通过其核心组件 **GObject** 系统，GLib 在 C 语言中实现了类、对象、继承、信号等面向对象编程的特性，使得代码结构更清晰、更易于维护。
*   **完善的内存管理**：提供了如 `g_malloc`、`g_free` 等函数，对标准 C 的内存管理进行了封装和增强，使内存操作更安全、更便捷。
*   **强大的事件循环**：内置了 `GMainLoop` 和 `GSource` 机制，非常适合编写事件驱动的应用程序，如图形界面程序或网络服务器中的任务调度器。
*   **多线程支持**：提供了 `GThread`、`GMutex` 等完整的线程模型，简化了多线程编程的复杂性。

### GLib 与 glibc 的区别

这是一个非常常见的混淆点。**GLib** 和 **glibc** 是两个完全不同的库，尽管名字相似。

| 特性     | **GLib**                               | **glibc**                                      |
| :------- | :------------------------------------- | :--------------------------------------------- |
| **全称** | The GLib Library                       | GNU C Library                                  |
| **定位** | 高级、通用的 C 语言**工具库**          | Linux 系统的**标准 C 运行库**                  |
| **功能** | 提供数据结构、线程、对象系统等高级功能 | 提供 `printf`, `malloc`, `fork` 等最基础的函数 |
| **关系** | 依赖于 glibc，在其之上构建             | 是操作系统的基础，几乎所有 C 程序都依赖它      |

简而言之，**glibc** 是 Linux 系统运行的基石，提供了最基础的 C 语言标准函数；而 **GLib** 则是在 glibc 之上，为开发者提供更便捷、更强大的高级功能库。

### 示例

具体用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/blob/main/demo-c++/demo-glib.c

安装GLib库依赖

```sh
sudo apt install pkg-config libglib2.0-dev
```

编译并运行

```sh
gcc demo-glib.c -o main $(pkg-config --cflags --libs glib-2.0)
./main
```

## GLib的GMainLoop、GMainContext、GSource是什么呢？

GLib 的 `GMainLoop` 是一个**主事件循环**的实现，它是 GLib 和 GTK 应用程序的核心，负责管理和分发各种事件。

你可以把它想象成一个应用程序的“心脏”或“指挥中心”，它持续运行，监听并处理来自不同来源的事件，例如：

*   **用户交互**：鼠标点击、键盘按键等。
*   **系统事件**：文件读写就绪、网络数据到达、定时器超时等。

在没有事件需要处理时，主循环会让应用程序进入休眠状态以节省资源；一旦有事件发生，它就会被唤醒并进行处理。

### 核心组件

`GMainLoop` 的工作依赖于两个关键组件：`GMainContext` 和 `GSource`。

1.  **GMainContext (主循环上下文)**
    这是 `GMainLoop` 的核心，可以看作是一个**事件源的容器**。它负责管理所有待处理的事件源，并驱动整个事件循环的流程。一个 `GMainContext` 通常只在一个线程中运行。

2.  **GSource (事件源)**
    这是对具体事件的抽象。任何类型的事件（如文件描述符、超时、空闲任务）都可以被封装成一个 `GSource` 对象，然后添加到 `GMainContext` 中进行管理。每个 `GSource` 都有自己的优先级，值越小优先级越高，确保紧急事件能被优先处理。

### 工作原理

`GMainLoop` 的运行过程，本质上是 `GMainContext` 不断迭代，检查和处理所有 `GSource` 的过程。这个循环主要包含以下几个步骤：

1.  **准备 (prepare)**
    循环会调用每个事件源的 `prepare` 函数。这个函数用来判断事件源是否已经准备好被处理，并可能设置一个超时时间。

2.  **查询与轮询 (query & poll)**
    如果没有事件源立即就绪，主循环会根据计算出的最短超时时间进入休眠（通过 `poll` 系统调用），等待事件发生。当有事件发生或超时时，循环被唤醒。

3.  **检查 (check)**
    循环被唤醒后，会调用每个事件源的 `check` 函数，确认是否有事件真正发生。

4.  **分发 (dispatch)**
    对于 `check` 返回为真的事件源，主循环会调用其 `dispatch` 函数，执行用户注册的回调函数来处理具体事件。

这个过程会周而复始地进行，直到主循环被显式地停止。

### 基本用法

使用 `GMainLoop` 通常遵循以下简单步骤：

1.  **创建**：使用 `g_main_loop_new()` 创建一个新的主循环实例。
2.  **添加事件源**：将你需要监听的事件源（如定时器 `g_timeout_add`、空闲任务 `g_idle_add` 或自定义的 `GSource`）添加到主循环的上下文中。
3.  **运行**：调用 `g_main_loop_run()` 启动主循环。此后，程序将一直在此函数中运行，处理事件。
4.  **退出**：在某个事件的处理回调中，调用 `g_main_loop_quit()` 来停止主循环，`g_main_loop_run()` 函数便会返回，程序可以继续执行后续代码或退出。

### 示例

具体用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/blob/main/demo-c++/demo-glib-mainloop.c

安装GLib库依赖

```sh
sudo apt install pkg-config libglib2.0-dev
```

编译并运行

```sh
gcc demo-glib-mainloop.c -o main $(pkg-config --cflags --libs glib-2.0)
./main
```

## GLib GMutex是什么呢？

`GMutex` 是 GLib 库提供的一种**互斥锁（Mutex）**，它是多线程编程中用于同步和保护共享资源的核心工具。

简单来说，它的作用就像一把“锁”，确保在任意时刻，只有一个线程能够访问被它保护的代码段或数据，从而有效避免了因多个线程同时读写而引发的数据竞争和不一致问题。

### 基本用法

使用 `GMutex` 通常遵循“初始化 -> 加锁 -> 解锁 -> 清理”的模式。

1.  **初始化 (`g_mutex_init`)**：在使用前必须先初始化一个 `GMutex` 变量。
2.  **加锁 (`g_mutex_lock`)**：线程在进入需要保护的“临界区”代码前调用此函数。如果锁已被其他线程持有，当前线程会**阻塞**（等待），直到锁被释放。
3.  **解锁 (`g_mutex_unlock`)**：线程在完成临界区的操作后，必须调用此函数来释放锁，以便其他等待的线程可以获取它。
4.  **清理 (`g_mutex_clear`)**：当互斥锁不再需要时，应调用此函数来释放其占用的资源。

下面是一个简单的 C 语言示例：

```c
#include <glib.h>

GMutex mutex; // 声明一个全局的互斥锁
int shared_counter = 0; // 一个会被多个线程共享的变量

gpointer thread_function(gpointer data) {
    for (int i = 0; i < 1000; i++) {
        g_mutex_lock(&mutex); // 1. 加锁，进入临界区
        shared_counter++;     // 2. 安全地操作共享资源
        g_mutex_unlock(&mutex); // 3. 解锁，离开临界区
    }
    return NULL;
}

int main() {
    g_mutex_init(&mutex); // 初始化锁

    // 创建两个线程，它们都会执行 thread_function
    g_thread_new("thread1", thread_function, NULL);
    g_thread_new("thread2", thread_function, NULL);

    // 等待线程结束 (此处简化，实际应使用 g_thread_join)
    g_usleep(2000000);

    g_print("最终计数器的值是: %d\n", shared_counter); // 结果将是 2000

    g_mutex_clear(&mutex); // 清理锁资源
    return 0;
}
```

### 进阶用法：`GMutexLocker`

为了简化锁的管理并防止因忘记解锁而导致的死锁，GLib 提供了 `GMutexLocker`。这是一种基于作用域的锁守卫（RAII）机制。

*   **自动管理**：当创建一个 `GMutexLocker` 对象时，它会自动对指定的 `GMutex` 加锁。
*   **自动释放**：当 `GMutexLocker` 对象离开其作用域（例如，代码块结束）时，它会自动解锁。

这种方式能确保在任何情况下（包括函数提前返回或发生异常）锁都能被正确释放，极大地提高了代码的健壮性。

```c
#include <glib.h>

GMutex mutex;

void safe_operation() {
    // 在这个作用域内，锁被自动管理
    GMutexLocker locker = g_mutex_locker_new(&mutex); // 自动加锁

    // ... 执行需要保护的临界区代码 ...

} // 函数结束，locker 超出作用域，自动解锁
```

### 重要注意事项

*   **成对出现**：`g_mutex_lock` 和 `g_mutex_unlock` 必须成对出现，确保每个加锁操作都有对应的解锁操作。
*   **避免死锁**：普通的 `GMutex` 不是递归的。这意味着同一个线程不能对它已经持有的锁再次调用 `g_mutex_lock`，否则会导致死锁。如果需要这种功能，应使用递归互斥锁 `GRecMutex`。

## GLib GHashTable是什么呢？

`GHashTable` 是 GLib 库提供的一种高效的**哈希表（Hash Table）**数据结构。

它以**键值对（Key-Value Pair）**的形式存储数据，通过一个键（Key）可以快速地访问到与它关联的值（Value）。它的核心优势在于查找、插入和删除操作的效率非常高，在理想情况下，这些操作的时间复杂度都接近 O(1)，即常数时间。

### 创建哈希表

GLib 提供了两个函数来创建 `GHashTable`，主要区别在于是否自动管理内存。

1.  **`g_hash_table_new()`**
    这是基础的创建函数，你需要提供哈希函数和键比较函数。

2.  **`g_hash_table_new_full()`**
    这是更完整的版本，除了哈希和比较函数外，还允许你指定**键和值的销毁函数（Destroy Notify）**。当条目被删除或哈希表被销毁时，GLib 会自动调用这些函数来释放内存，这对于管理动态分配的内存（如 `g_strdup` 创建的字符串）非常方便，可以有效防止内存泄漏。

#### 关键参数

*   **`hash_func`**: 一个函数指针，用于根据键计算出一个哈希值。GLib 为常用类型提供了内置函数，如 `g_str_hash`（用于字符串）和 `g_int_hash`（用于整数）。
*   **`key_equal_func`**: 一个函数指针，用于比较两个键是否相等。GLib 同样提供了 `g_str_equal` 和 `g_int_equal` 等内置函数。
*   **`key_destroy_func` / `value_destroy_func`**: 用于释放键和值所占内存的函数，通常传入 `g_free`。

### 基本用法

以下代码展示了 `GHashTable` 的常见操作：

```c
#include <glib.h>
#include <stdio.h>
#include <string.h>

int main() {
    GHashTable *hash_table;
    char *value;

    // 1. 创建哈希表，使用字符串作为键，并自动管理内存
    hash_table = g_hash_table_new_full(g_str_hash,    // 字符串哈希函数
                                       g_str_equal,   // 字符串比较函数
                                       g_free,        // 键的销毁函数
                                       g_free);       // 值的销毁函数

    // 2. 插入键值对
    // 注意：因为设置了自动内存管理，这里使用 g_strdup 复制字符串
    g_hash_table_insert(hash_table, g_strdup("name"), g_strdup("Alice"));
    g_hash_table_insert(hash_table, g_strdup("city"), g_strdup("Guangzhou"));

    // 3. 查找值
    value = (char *)g_hash_table_lookup(hash_table, "name");
    if (value) {
        g_print("Name: %s\n", value); // 输出: Name: Alice
    }

    // 4. 获取哈希表大小
    g_print("Size: %u\n", g_hash_table_size(hash_table)); // 输出: Size: 2

    // 5. 删除条目
    // 调用 g_free 释放被删除条目的内存
    g_hash_table_remove(hash_table, "city");

    // 6. 销毁哈希表
    // 这会自动调用 g_free 释放所有剩余的键和值
    g_hash_table_destroy(hash_table);

    return 0;
}
```

### 进阶用法

#### 遍历哈希表

你可以使用 `g_hash_table_foreach()` 函数来遍历哈希表中的每一个键值对。你需要提供一个回调函数，该函数会被哈希表中的每个条目调用。

```c
// 定义一个回调函数
void print_entry(gpointer key, gpointer value, gpointer user_data) {
    g_print("%s = %s\n", (char *)key, (char *)value);
}

// 在主程序中使用
// ... 创建并填充 hash_table ...
g_hash_table_foreach(hash_table, print_entry, NULL);
// ...
```

#### 查找特定条目

除了按键查找，你还可以使用 `g_hash_table_find()` 来查找符合特定条件的第一个条目。这同样需要一个回调函数（谓词函数）来判断每个条目是否符合条件。

### 重要注意事项

*   **键的唯一性**：哈希表中的键必须是唯一的。如果使用 `g_hash_table_insert` 插入一个已存在的键，它会更新该键对应的值。
*   **内存管理**：务必根据键和值的数据类型选择合适的创建函数。如果存储的是动态分配的内存，强烈建议使用 `g_hash_table_new_full` 并设置销毁函数，这是避免内存泄漏的最佳实践。
*   **性能考量**：虽然哈希表在数据量大时性能优越，但对于非常小的数据集（例如少于十几个条目），一个简单的有序列表可能因为避免了哈希计算的开销而更快。

## g_printerr是什么呢？

`g_printerr()` 是 GLib 库提供的一个用于输出错误信息的函数。

你可以把它理解为一个更智能、更适合库开发的 `fprintf(stderr, ...)`。

### 主要功能

*   **默认行为**：默认情况下，`g_printerr()` 会将格式化后的字符串输出到标准错误流（`stderr`）。
*   **可定制性**：它的输出行为不是固定的。你可以通过 `g_set_printerr_handler()` 函数来设置一个自定义的日志处理函数（handler）。一旦设置了自定义 handler，所有通过 `g_printerr()` 输出的信息都会交给这个 handler 来处理，而不是直接打印到 `stderr`。

### 为什么使用它？

这种可定制的特性使得 `g_printerr()` 在开发库或大型应用程序时非常有用。它允许你在不修改大量调用代码的情况下，统一改变整个程序的错误日志输出方式，例如将错误信息重定向到日志文件、图形用户界面（GUI）的弹窗或远程日志服务器。

### 使用示例

```c
#include <glib.h>

int main() {
    // 默认输出到 stderr
    g_printerr("这是一条错误信息\n");

    // 可以像 printf 一样使用格式化字符串
    int error_code = 404;
    g_printerr("发生错误，错误代码: %d\n", error_code);

    return 0;
}
```

### 与其他 GLib 日志函数的区别

GLib 提供了一系列日志函数，用于不同级别的日志记录，`g_printerr()` 是其中之一：

| 函数               | 用途                                                        | 默认输出     |
| :----------------- | :---------------------------------------------------------- | :----------- |
| `g_print()`        | 输出普通信息，常用于调试。                                  | `stdout`     |
| **`g_printerr()`** | **输出错误信息。**                                          | **`stderr`** |
| `g_message()`      | 输出一般性消息，会添加 "Message:" 前缀。                    | `stderr`     |
| `g_warning()`      | 输出警告信息，会添加 "** WARNING **" 前缀。                 | `stderr`     |
| `g_error()`        | 输出致命错误信息并**终止程序**，会添加 "** ERROR **" 前缀。 | `stderr`     |

### 示例

具体用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/blob/main/demo-c++/demo-glib-g_printerr.c

安装GLib库依赖

```sh
sudo apt install pkg-config libglib2.0-dev
```

编译

```c
gcc -o main demo-glib-g_printerr.c $(pkg-config --cflags --libs glib-2.0)
```

运行

```c
./main
```

