# JUC

## 基础概念



### 什么是 JUC？

Java JUC指的是java.util.concurrent包下的所有提供的工具类的简称，这是一个处理线程的工具包，自JDK1.5开始出现。以下是对Java JUC的详细解析：

一、JUC的主要功能和作用

JUC包含了一系列的工具类、线程池、原子变量类等，用于简化并发编程，提高性能，以及更好地处理并发和同步问题。它主要用于实现多线程通信、线程安全以及线程间高并发的场景。

二、JUC中的关键概念和组件

1. **线程状态**：
   - NEW：新建态，创建线程还未启动。
   - RUNNABLE：可运行态，包括就绪态和运行态。
   - TERMINATED：终止态，线程结束并回收线程资源。
   - BLOCKED：阻塞态，线程等待某项资源而主动放弃CPU进入阻塞态。
   - WAITING：无限等待，调用wait()方法线程会进入无限等待状态，等待其他线程唤醒。
   - TIMED_WAITING：有限等待，调用sleep()方法或带参wait(t)方法，线程进入等待状态直到设置时间才被唤醒。
2. **并发与并行**：
   - 并发：指的是多个程序可以同时运行的一种现象，但真正意义上，一个单核心CPU任一时刻都只能运行一个线程。所以此处的“同时运行”并非真的同一时刻有多个线程运行（这是并行的概念），而是提供了一种功能让用户看来多个程序同时运行起来了，但实际上这些程序中的进程不是一直霸占CPU的，而是根据CPU的调度，执行一会儿停一会儿。
   - 并行：一段时间内，多个进程或线程同时运行。并行缩短了任务队列的长度，提高了效率。但并行的效率一方面受多进程/线程编码的好坏的影响，另一方面也受硬件角度上的CPU的影响。
3. **锁机制**：
   - Synchronized：Java提供的关键字，是一种同步锁（对方法或者代码块中存在共享数据的操作），同步锁可以是任意对象，主要用于实现线程同步操作，保证线程安全。
   - Lock接口：JUC提供的一种更加灵活、功能更为强大的同步锁框架。其有多个功能强大的接口和实现类，例如ReentrantLock类（可重入锁）等。

三、JUC在实际开发中的应用

在实际开发中，JUC常用于解决多线程编程中的共享资源管理、同步和线程安全等问题。例如，使用ExecutorService、ThreadPoolExecutor等线程池类来管理和调度线程，提高程序的性能和响应速度；使用Semaphore、CountDownLatch等同步辅助类来实现线程间的协调和控制；使用ReentrantLock等锁机制来保证线程安全等。

四、JUC与多线程的关系

多线程是一种编程模型，而JUC是Java提供的一组工具和类，用于更方便、更安全地进行并发编程。多线程是JUC使用和依赖的基础，提供了通过Thread和Runnable来并行执行任务的能力。而JUC建立在多线程的基础上，利用多线程能够并发运行的特点，提供了更高级、更先进的同步组件和数据结构来更好地管理线程并发。

综上所述，Java JUC是Java并发编程中的重要组成部分，它提供了一系列工具和类来简化并发编程、提高性能以及更好地处理并发和同步问题。在实际开发中，熟练掌握JUC的使用对于提高程序的性能和稳定性具有重要意义。



### 管程

在 Java 中，管程（Monitor）是一种同步机制，它可以用来保护共享资源，防止多个线程同时访问并修改这些资源，从而避免数据不一致和竞态条件。  它不是一个具体的类或接口，而是一种概念模型，通过内置的同步机制（主要是锁）来实现。

你可以把管程想象成一个带有锁的房间：

* **房间（共享资源）：**  包含需要保护的共享数据或代码。
* **锁（互斥锁）：**  只允许一个线程同时进入房间。其他线程必须等待，直到拥有锁的线程离开房间。
* **条件变量（可选）：**  允许线程在房间里等待特定条件满足后再继续执行。  这相当于房间里的一些开关，只有当开关打开时，线程才能继续工作。


**Java 如何实现管程？**

Java 使用 synchronized 关键字和 `Object` 类的 `wait()`、`notify()`、`notifyAll()` 方法来实现管程的功能。

* **`synchronized` 关键字:**  用于修饰方法或代码块，确保同一时刻只有一个线程可以执行被 `synchronized` 修饰的代码。  这相当于房间的锁，保证了对共享资源的互斥访问。

* **`Object.wait()`:**  使当前线程进入等待状态，释放锁，直到被 `notify()` 或 `notifyAll()` 唤醒。  这相当于线程在房间里等待某个条件满足。

* **`Object.notify()`:**  唤醒等待在同一个对象锁上的一个线程。  这相当于打开一个开关，让一个等待的线程继续执行。

* **`Object.notifyAll()`:**  唤醒等待在同一个对象锁上的所有线程。 这相当于打开所有开关，让所有等待的线程继续执行，然后它们会竞争锁。


**一个简单的例子:**

```java
public class Counter {
    private int count = 0;
    private final Object lock = new Object(); // 创建锁对象

    public void increment() {
        synchronized (lock) { // 获得锁
            count++;
        }
    }

    public int getCount() {
        synchronized (lock) { // 获得锁
            return count;
        }
    }
}
```

在这个例子中，`lock` 对象充当了管程的锁。  `increment()` 和 `getCount()` 方法都使用 `synchronized` 关键字来同步访问 `count` 变量，保证了线程安全。


**管程的优势:**

* **简化同步代码:**  管程提供了一种更高级别的抽象，可以简化多线程编程的复杂性。
* **避免竞态条件:**  通过互斥访问共享资源，避免了数据不一致和竞态条件。
* **支持条件变量:**  `wait()`、`notify()`、`notifyAll()` 方法允许线程在特定条件满足时才继续执行，提供了更灵活的同步机制。


**管程与锁的区别：**

虽然 Java 使用锁来实现管程，但它们的概念有所不同。  锁只是一个低级别的同步原语，而管程是一个包含锁和条件变量的更高级别的同步机制。  管程将锁和条件变量结合在一起，提供了一种更结构化和易于理解的方式来管理共享资源的访问。


总而言之，Java 管程是一种重要的同步机制，它通过内置的同步原语来实现对共享资源的互斥访问和条件同步，从而保证了多线程程序的正确性和可靠性。



### 并发和并行

并发和并行是两个容易混淆但又截然不同的概念，它们都与处理多个任务有关，但方式不同：

**并发 (Concurrency):**

* **定义:** 并发是指在一个时间段内处理多个任务。  这些任务可能在同一时间点上并不是真正同时执行的，而是通过快速切换来实现一种“同时”执行的效果。  这就像一个厨师同时烹饪多道菜，他不是同时做每一道菜的所有步骤，而是快速地在各个菜之间切换，让它们看起来像是同时在进行。
* **实现方式:**  并发通常通过多线程、协程等机制来实现。  操作系统会根据调度算法在不同线程之间切换，给用户造成多个任务同时运行的错觉。
* **特点:**
    * 提高资源利用率：充分利用 CPU 时间片，即使只有一个 CPU 核心也能处理多个任务。
    * 响应速度更快：即使一个任务阻塞，其他任务仍然可以继续执行，提高系统的响应能力。
    * 复杂性增加：管理多个线程或协程需要额外的开销和同步机制，增加了程序的复杂性。
    * 存在竞争条件：多个线程同时访问共享资源时，可能出现竞争条件，导致数据不一致或程序错误。


**并行 (Parallelism):**

* **定义:** 并行是指多个任务真正同时执行。 这需要多个处理器核心或多个计算单元。  就像多个厨师同时烹饪不同的菜肴一样，每个厨师都独立地完成自己的工作。
* **实现方式:** 并行通常需要多核处理器或分布式计算系统。  每个任务都可以在单独的处理器核心上运行。
* **特点:**
    * 显著提高处理速度：多个任务同时执行，总的执行时间大大缩短。
    * 提高吞吐量：在相同时间内可以处理更多任务。
    * 需要更多资源：需要多个处理器核心或计算单元，成本较高。
    * 程序设计更复杂：需要考虑任务间的协调和数据共享问题。


**区别总结:**

| 特性     | 并发 (Concurrency)               | 并行 (Parallelism)                 |
| -------- | -------------------------------- | ---------------------------------- |
| 任务执行 | 交错执行，看起来同时进行         | 真正同时执行                       |
| 资源需求 | 单个 CPU 核心即可                | 多个 CPU 核心或计算单元            |
| 速度提升 | 提高资源利用率，提高响应速度     | 显著提高处理速度，提高吞吐量       |
| 复杂性   | 中等，需要考虑线程同步和竞争条件 | 高，需要考虑任务间的协调和数据共享 |


**关系:**

并行是并发的一种特例。 并行一定是并发，但并发不一定是并行。  一个系统可以是并发但非并行的 (例如，单核 CPU 上运行多线程程序)，也可以是并行且并发的 (例如，多核 CPU 上运行多线程程序)。

理解并发和并行的区别对于编写高效和可靠的程序至关重要，尤其是在处理大量数据或需要高性能的应用中。  选择合适的并发或并行模型取决于具体的应用场景和资源约束。



## 线程



### 进程和线程

Java 进程和线程是并发编程中的两个核心概念，它们之间既有联系又有区别：

**进程 (Process):**

* **定义:**  一个进程是操作系统分配资源（内存、文件句柄、网络连接等）的基本单位。  它是一个独立的执行环境，拥有自己的内存空间、上下文和资源。
* **创建:** 创建一个新的进程需要操作系统内核的参与，开销比较大。
* **内存空间:** 每个进程拥有独立的内存空间，进程之间内存空间相互隔离，保证了进程的安全性与稳定性。  进程间通信需要特定的机制（例如管道、共享内存、套接字等）。
* **资源:**  进程是资源分配的单位，操作系统为每个进程分配资源。
* **例子:**  你打开一个浏览器就是一个进程，打开一个文本编辑器也是一个进程。即使这两个程序都是同一个程序的不同实例，它们也是不同的进程。


**线程 (Thread):**

* **定义:** 线程是进程内的一个执行单元，是进程内部更轻量级的并发执行单位。它共享进程的内存空间和资源。
* **创建:** 创建一个新线程的开销比创建进程小得多。
* **内存空间:**  同一个进程内的所有线程共享同一个内存空间，这意味着它们可以方便地共享数据。  这同时也带来了数据竞争的问题，需要使用同步机制（例如锁、信号量等）来保护共享数据。
* **资源:** 线程共享进程的资源，但每个线程有自己的程序计数器、栈和局部变量等。
* **例子:**  在一个浏览器进程中，可能有多个线程负责不同的任务，例如渲染页面、处理网络请求、执行JavaScript代码等。


**进程与线程的主要区别总结：**

| 特性     | 进程                                    | 线程                                                        |
| -------- | --------------------------------------- | ----------------------------------------------------------- |
| 定义     | 操作系统分配资源的基本单位              | 进程内的执行单元                                            |
| 内存空间 | 独立的内存空间                          | 共享进程的内存空间                                          |
| 资源     | 独占资源                                | 共享进程的资源                                              |
| 创建开销 | 大                                      | 小                                                          |
| 通信方式 | 进程间通信（IPC）,例如管道，套接字等    | 直接共享内存 (需要同步机制)                                 |
| 健壮性   | 一个进程崩溃不会影响其他进程            | 一个线程崩溃可能导致整个进程崩溃                            |
| 并行性   | 真正的并行，多个进程同时运行在多核CPU上 | 伪并行，多个线程并发运行在一个进程中，多核CPU下才能真正并行 |


**Java 中的进程和线程:**

Java 程序运行在一个 Java 虚拟机 (JVM) 进程中。  JVM 负责管理内存、执行字节码等。 在 Java 中，我们使用 `Thread` 类或 `Runnable` 接口来创建和管理线程。  Java 通过多线程来实现并发编程。


你还有什么关于Java进程和线程的问题吗？  我可以尝试更详细地解释某些方面，或者提供一些代码示例。



### 线程状态

Java 线程具有多种状态，这些状态描述了线程的生命周期。  不同的 Java 版本对线程状态的描述略有差异，但核心概念基本一致。  以下是一些主要的线程状态，以及它们的含义和转换关系：

**主要状态:**

* **NEW (新建):** 线程对象已创建，但尚未启动。  此时线程还没有开始运行。

* **RUNNABLE (运行):** 线程正在运行或准备运行。  这包括线程正在 CPU 上执行，以及线程处于就绪状态等待 CPU 分配时间片两种情况。  在多核处理器中，多个线程可以同时处于 RUNNABLE 状态并真正并行执行。

* **BLOCKED (阻塞):** 线程正在等待一个锁。  当线程试图获取一个已经被其他线程持有的锁时，它会进入 BLOCKED 状态。

* **WAITING (等待):** 线程正在无限期地等待另一个线程执行特定的操作。  例如，调用 `Object.wait()` 方法会使线程进入 WAITING 状态，直到另一个线程调用 `Object.notify()` 或 `Object.notifyAll()` 方法唤醒它。  这种等待是无限期的，除非被其他线程唤醒。

* **TIMED_WAITING (超时等待):** 线程正在等待另一个线程执行特定的操作，但是有一个超时时间限制。  例如，调用 `Thread.sleep()`、`Object.wait(long timeout)` 或 `Thread.join(long timeout)` 方法会使线程进入 TIMED_WAITING 状态，如果超时时间到了，线程会自动醒来，即使没有被其他线程唤醒。

* **TERMINATED (终止):** 线程已经执行完毕，或者因为异常而结束。


**状态转换:**

线程状态之间的转换并非线性，而是根据线程的执行情况动态变化的。  一些常见的转换关系：

* **NEW -> RUNNABLE:**  调用 `Thread.start()` 方法启动线程。

* **RUNNABLE -> BLOCKED:**  线程试图获取一个被其他线程持有的锁。

* **RUNNABLE -> WAITING:**  调用 `Object.wait()` 方法。

* **RUNNABLE -> TIMED_WAITING:**  调用 `Thread.sleep()`、`Object.wait(long timeout)` 或 `Thread.join(long timeout)` 方法。

* **BLOCKED -> RUNNABLE:**  线程获取到了锁。

* **WAITING -> RUNNABLE:**  另一个线程调用 `Object.notify()` 或 `Object.notifyAll()` 方法唤醒等待的线程。

* **TIMED_WAITING -> RUNNABLE:**  超时时间到了，或者另一个线程调用 `Object.notify()` 或 `Object.notifyAll()` 方法唤醒等待的线程。

* **RUNNABLE -> TERMINATED:**  线程执行完毕或发生异常。


**获取线程状态:**

可以使用 `Thread.getState()` 方法获取线程的当前状态。  该方法返回一个 `Thread.State` 枚举值，表示线程的当前状态。


**需要注意的是:**

*  `RUNNABLE` 状态涵盖了线程实际运行和等待运行两种情况，  这使得它在某些情况下并不十分精确。
*  `WAITING` 和 `TIMED_WAITING` 之间的区别在于是否有超时时间限制。
*  理解线程状态对于调试和优化多线程程序至关重要。


希望这个解释能够帮助你理解 Java 线程的状态。  你还有什么其他的问题吗？



### 用户线程和守护线程

在Java中，线程分为两种类型：用户线程和守护线程。它们的主要区别在于JVM（Java虚拟机）的终止行为。

**用户线程 (User Thread):**

* **定义:**  这是普通的线程，当程序启动时创建，执行程序的主要任务。
* **JVM 终止行为:**  JVM 只有在所有用户线程都结束运行后才会退出。  如果还有用户线程在运行，JVM 会等待它们结束。  即使有守护线程还在运行，JVM 也会终止。
* **例子:**  你用 `new Thread()` 创建的线程，默认都是用户线程。  你的主线程也是一个用户线程。


**守护线程 (Daemon Thread):**

* **定义:**  守护线程是为其他线程服务的线程，通常用于在后台执行一些辅助任务，例如垃圾回收、监控等。  它们是为用户线程服务的。
* **JVM 终止行为:**  当 JVM 中只剩下守护线程时，JVM 会直接退出，而不会等待守护线程结束。 守护线程不会阻止 JVM 的终止。
* **设置方法:**  可以使用 `setDaemon(true)` 方法将一个线程设置为守护线程。  **必须在 `start()` 方法调用之前设置**，否则会抛出 `IllegalThreadStateException`。
* **例子:**  垃圾回收器线程就是一个守护线程。


**代码示例:**

```java
public class DaemonThreadExample {
    public static void main(String[] args) {
        // 创建一个用户线程
        Thread userThread = new Thread(() -> {
            try {
                System.out.println("User thread running...");
                Thread.sleep(3000); // 运行3秒
                System.out.println("User thread finished.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 创建一个守护线程
        Thread daemonThread = new Thread(() -> {
            try {
                System.out.println("Daemon thread running...");
                while (true) {
                    Thread.sleep(1000); // 每秒打印一次
                    System.out.println("Daemon thread is still alive.");
                }
            } catch (InterruptedException e) {
                System.out.println("Daemon thread interrupted.");
            }
        });
        daemonThread.setDaemon(true); // 设置为守护线程

        userThread.start();
        daemonThread.start();

        System.out.println("Main thread finished.");
    }
}
```

在这个例子中，当主线程结束时，用户线程会继续运行直到完成，而守护线程则会被 JVM 直接终止，即使它还在循环中。


**重要注意事项:**

* **不要在守护线程中执行长时间运行的任务或操作，例如网络连接、文件I/O等。** 因为守护线程随时可能被 JVM 终止，这可能会导致资源泄漏或数据丢失。
* **守护线程应该只用于后台任务，这些任务的失败不会影响程序的正确性。**

总而言之，选择用户线程还是守护线程取决于线程的任务性质。  如果线程的任务对于程序的正常运行至关重要，则应该使用用户线程；如果线程的任务是辅助性的，并且它的结束不会影响程序的正确性，则可以使用守护线程。



### 线程 join

Java 的 `Thread.join()` 方法允许一个线程等待另一个线程完成执行。  换句话说，它阻塞调用线程，直到目标线程的 `run()` 方法执行完毕。

**使用方法：**

```java
Thread thread = new Thread(() -> {
  // 线程执行的代码
  System.out.println("Thread is running...");
  try {
    Thread.sleep(2000); // 模拟一些耗时操作
  } catch (InterruptedException e) {
    e.printStackTrace();
  }
  System.out.println("Thread is finished.");
});

thread.start(); // 启动线程

try {
  thread.join(); // 等待线程 thread 完成
  System.out.println("Main thread continues after thread finishes.");
} catch (InterruptedException e) {
  e.printStackTrace();
}
```

在这个例子中，主线程在 `thread.join()` 处阻塞，直到 `thread` 线程完成其 `run()` 方法中的所有操作。只有当 `thread` 线程完成之后，主线程才会继续执行 `System.out.println("Main thread continues after thread finishes.");` 这行代码。


**`join()` 方法的重载：**

`join()` 方法有三个重载版本：

* `join()`:  等待目标线程完成执行。
* `join(long millis)`:  等待目标线程最多 `millis` 毫秒。如果目标线程在 `millis` 毫秒内完成，则返回；否则，即使目标线程尚未完成，也会返回。
* `join(long millis, int nanos)`:  等待目标线程最多 `millis` 毫秒加 `nanos` 纳秒。


**用途：**

* **确保线程执行完毕:**  在需要依赖其他线程的结果时，使用 `join()` 可以确保这些线程先完成执行，避免出现数据不一致或其他问题。

* **简化程序逻辑:**  使用 `join()` 可以简化程序逻辑，避免使用复杂的同步机制。

* **控制程序执行顺序:**  `join()` 可以用于控制线程的执行顺序，确保某些线程在其他线程之前完成。


**异常处理:**

`join()` 方法可能会抛出 `InterruptedException` 异常，这通常发生在等待过程中当前线程被中断。  因此，在调用 `join()` 方法时，需要使用 `try-catch` 块来捕获该异常。


**示例：**  计算多个文件的总大小

假设你有多个线程分别计算不同文件的 size，你可以使用 `join()` 来等待所有线程计算完毕后再汇总结果：

```java
// ... (代码省略: 获取文件列表，创建线程并计算每个文件的大小) ...

for (Thread t : threads) {
    try {
        t.join();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}

// 汇总所有文件大小
long totalSize = 0;
for (Long size : fileSizes) {
    totalSize += size;
}

System.out.println("Total size: " + totalSize);
```


总而言之，`join()` 是一个方便且有用的方法，可以简化多线程编程中的许多场景，特别是当主线程需要等待子线程完成工作时。  记得处理潜在的 `InterruptedException`。



### 线程间通信

#### 介绍

Java 线程间通信是指多个线程在运行过程中为了协同工作而进行的数据交换和同步控制。  这对于避免数据竞争和保证程序正确性至关重要。  Java 提供了多种机制来实现线程间通信，主要包括：

**1. `wait()`、`notify()`、`notifyAll()` 方法 (基于对象的锁):**

* 这三个方法是 `Object` 类的方法，必须在同步代码块或同步方法中使用，依赖于对象的锁。
* `wait()`：使当前线程进入等待状态，并释放对象的锁。直到另一个线程调用 `notify()` 或 `notifyAll()` 方法唤醒它，或者超时时间到。
* `notify()`：随机唤醒一个正在等待该对象的锁的线程。
* `notifyAll()`：唤醒所有正在等待该对象的锁的线程。

**例子：**

```java
public class ProducerConsumer {
    private int count = 0;
    private final int MAX_COUNT = 10;
    private final Object lock = new Object();

    public void produce() {
        synchronized (lock) {
            while (count == MAX_COUNT) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            count++;
            System.out.println("Produced: " + count);
            lock.notifyAll();
        }
    }

    public void consume() {
        synchronized (lock) {
            while (count == 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            count--;
            System.out.println("Consumed: " + count);
            lock.notifyAll();
        }
    }
}
```

**2. `ReentrantLock` 和 `Condition` (更灵活的锁和条件变量):**

* `ReentrantLock` 提供了比 synchronized 更强大的功能，例如可以尝试获取锁、中断等待等。
* `Condition` 对象允许在一个锁上创建多个等待条件，实现更复杂的线程协作。  一个 `ReentrantLock` 可以关联多个 `Condition` 对象。

**例子：**

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerLock {
    private int count = 0;
    private final int MAX_COUNT = 10;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public void produce() {
        lock.lock();
        try {
            while (count == MAX_COUNT) {
                notFull.await();
            }
            count++;
            System.out.println("Produced: " + count);
            notEmpty.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void consume() {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            count--;
            System.out.println("Consumed: " + count);
            notFull.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
```


**3. `BlockingQueue` (阻塞队列):**

* `BlockingQueue` 接口提供了线程安全的队列，支持阻塞的插入和获取操作。  当队列为空时，获取操作会阻塞；当队列已满时，插入操作会阻塞。  这简化了生产者-消费者问题的实现。  例如 `ArrayBlockingQueue`、`LinkedBlockingQueue`、`PriorityBlockingQueue` 等。

**例子:**

```java
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProducerConsumerBlockingQueue {
    private final BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10);

    public void produce() throws InterruptedException {
        queue.put(1); // 阻塞直到有空间
        System.out.println("Produced");
    }

    public void consume() throws InterruptedException {
        queue.take(); // 阻塞直到有元素
        System.out.println("Consumed");
    }
}
```

**4. `CountDownLatch` 和 `CyclicBarrier` (同步工具类):**

* `CountDownLatch` 用于等待多个线程完成某个操作。
* `CyclicBarrier` 用于让一组线程在某个屏障点等待，直到所有线程都到达屏障点后，再一起继续执行。


选择哪种机制取决于具体的应用场景和复杂度。  对于简单的生产者-消费者问题，`BlockingQueue` 是最简洁的解决方案。  对于更复杂的场景，可能需要使用 `ReentrantLock` 和 `Condition` 来实现更精细的控制。

希望以上信息对您有所帮助。  如果您有更具体的问题或场景，请告诉我，我会尽力提供更详细的解答。



#### synchronized+wait()、notify()、notifyAll() 线程间通信优点和缺点

Java 中 `wait()`、`notify()` 和 `notifyAll()` 方法是用于线程间通信的经典手段，它们基于 `Object` 的监视器模型。  它们在实现线程同步和协作方面扮演着关键角色，但也存在一些不足。

**优点：**

* **简洁性:**  这些方法提供了一种相对简洁的方式来实现线程间的同步和等待/唤醒机制。相比于其他更复杂的同步工具，它们更容易理解和使用（对于简单的场景）。
* **内置于 Java:** 它们是 Java 语言的内置特性，无需引入额外的库或框架。
* **灵活性:**  `notify()` 和 `notifyAll()` 提供了在不同场景下唤醒线程的灵活性。`notify()` 唤醒单个线程，而 `notifyAll()` 唤醒所有等待的线程。


**缺点：**

* **容易出错:**  `wait()`、`notify()` 和 `notifyAll()` 的使用容易出错，尤其是在复杂的并发场景中。  不正确的使用可能会导致死锁、活锁或竞争条件等问题。  例如：

    * **遗漏 `wait()` 中的异常处理:**  `wait()` 方法可能会抛出 `InterruptedException`，需要进行正确的处理，否则可能会导致程序异常终止。
    * **虚假唤醒:**  即使没有线程调用 `notify()` 或 `notifyAll()`，等待中的线程也可能被唤醒（称为虚假唤醒）。 这需要在 `wait()` 循环中添加条件检查来避免不必要的处理。
    * **线程优先级问题:**  `notify()` 唤醒哪个线程是不确定的，这可能导致优先级低的线程长期无法获得资源。
    * **`notify()` 的不确定性:**  `notify()` 只唤醒一个线程，无法控制唤醒哪个线程，这可能会导致一些意想不到的并发问题。

* **复杂性:**  在复杂的场景下，使用 `wait()`、`notify()` 和 `notifyAll()` 可能会导致代码难以理解和维护，尤其是在多个线程相互依赖的情况下。


* **缺乏清晰的语义:** 与更现代的并发工具相比，`wait()`、`notify()` 和 `notifyAll()` 的语义不够清晰，容易造成误解和错误。


**与现代并发工具的比较：**

相比于 `wait()`、`notify()` 和 `notifyAll()`，Java 提供了更高级的并发工具，例如 `CountDownLatch`、`Semaphore`、`ReentrantLock` 与 Condition、`BlockingQueue` 等，这些工具更易于使用，并能更好地处理并发问题。它们提供了更清晰的语义和更强大的功能，从而降低了出错的风险。  对于大多数新的并发编程任务，推荐使用这些更现代的工具。

总而言之，`wait()`、`notify()` 和 `notifyAll()` 方法是理解线程间通信基础的重要概念，但在实际应用中，应该优先考虑使用更高级的并发工具来提高代码的可读性、可维护性和可靠性，除非你正在处理非常简单的并发场景。

**示例代码：**

```java
/**
 * 线程间通信使用 wait、notify、notifyAll 方法
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Slf4j
public class ThreadCommunicationByUsingObjectWaitAndNotifyTests {
    @Test
    public void test() throws InterruptedException {
        int runLoop = 5;
        ShareResource shareResource = new ShareResource();

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < runLoop; i++) {
                try {
                    shareResource.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "A");
        Thread thread11 = new Thread(() -> {
            for (int i = 0; i < runLoop; i++) {
                try {
                    shareResource.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "A1");
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < runLoop; i++) {
                try {
                    shareResource.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "B");
        Thread thread21 = new Thread(() -> {
            for (int i = 0; i < runLoop; i++) {
                try {
                    shareResource.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "B1");
        thread1.start();
        thread11.start();
        thread2.start();
        thread21.start();

        thread1.join();
        thread11.join();
        thread2.join();
        thread21.join();
    }

    static class ShareResource {
        private final Object lock = new Object();
        private int value;

        void increment() throws InterruptedException {
            // 需要先给 lock 对象上锁才能够调用其 wait、notify、notifyAll 方法
            synchronized (lock) {
                // 注意：不能使用 if 判断以避免 wait() 虚假唤醒问题
                while (value == 1) {
                    lock.wait();
                }

                value++;
                log.debug("{} value++: {}", Thread.currentThread().getName(), value);
                lock.notifyAll();
            }
        }

        void decrement() throws InterruptedException {
            // 需要先给 lock 对象上锁才能够调用其 wait、notify、notifyAll 方法
            synchronized (lock) {
                // 注意：不能使用 if 判断以避免 wait() 虚假唤醒问题
                while (value == 0) {
                    lock.wait();
                }

                value--;
                log.debug("{} value--: {}", Thread.currentThread().getName(), value);
                lock.notifyAll();
            }
        }
    }
}
```



#### wait() 虚假唤醒问题

Java 中 `wait()` 方法的虚假唤醒 (Spurious Wakeup) 指的是，等待中的线程即使没有被 `notify()` 或 `notifyAll()` 方法唤醒，也可能从 `wait()` 方法返回。 这是一种由 JVM 实现细节导致的现象，并非程序中的 bug。

**为什么会出现虚假唤醒？**

Java 的 `wait()` 方法依赖于操作系统提供的底层机制来实现线程的阻塞和唤醒。 操作系统的调度策略以及硬件中断等因素都可能导致线程在没有被显式唤醒的情况下被唤醒。  这在 Java 的文档中也有明确说明，这不是一个需要被修复的 bug。

**如何避免虚假唤醒带来的问题？**

由于虚假唤醒的存在，在使用 `wait()` 方法时，必须在循环中检查等待条件，而不是直接在 `wait()` 方法之后执行依赖于等待条件的代码。  这就是所谓的 **`wait-check-notify` 模式**。

**示例:**

假设有一个线程需要等待一个标志位 `flag` 变为 `true`：

**错误的写法 (容易受虚假唤醒影响):**

```java
synchronized (lock) {
    if (!flag) {
        lock.wait();
    }
    // 处理 flag 为 true 的情况
}
```

如果线程在 `lock.wait()` 之后立即被虚假唤醒，即使 `flag` 仍然为 `false`，程序也会继续执行后续代码，从而导致错误。

**正确的写法 (使用 `wait-check-notify` 模式):**

```java
synchronized (lock) {
    while (!flag) {
        lock.wait();
    }
    // 处理 flag 为 true 的情况
}
```

在这个正确的写法中，即使发生虚假唤醒，循环条件 `!flag` 也会确保只有在 `flag` 为 `true` 的情况下，程序才会继续执行。  循环检查条件确保了只有在满足预期的条件下才会继续执行。

**总结:**

虚假唤醒是 `wait()` 方法的一个固有特性，无法避免它的发生。  唯一的解决方法是使用 `wait-check-notify` 模式，在循环中检查等待条件，确保只有在满足条件时才继续执行。  这可以有效地防止虚假唤醒导致程序出现错误。  记住，`while` 循环比 `if` 循环更重要，因为 `if` 循环只检查一次，而 `while` 循环会持续检查直到条件为真。



#### ReentrantLock+Condition 线程间通信有点和缺点

`ReentrantLock` 和 `Condition` 是 Java 并发包中提供的更高级的线程同步工具，它们提供了比 `synchronized` 和 `wait`/`notify` 更精细的控制。  但是，它们也有一些自身的优缺点。

**ReentrantLock 的优点：**

* **更灵活的锁机制:**  `ReentrantLock` 提供了比 `synchronized` 更灵活的锁机制，例如可以设置公平锁和非公平锁，以及中断等待的线程。  `synchronized` 只提供非公平锁。
* **可中断的等待:**  `ReentrantLock` 可以中断等待锁的线程，这在某些场景下非常有用，例如当一个线程需要等待一个资源，但是等待时间过长，需要放弃等待。 `synchronized` 的等待是不可中断的。
* **超时等待:**  `ReentrantLock` 的 `tryLock()` 方法允许尝试获取锁，如果获取失败，则返回 `false`，而不是阻塞线程。  这可以避免死锁。  此外，`tryLock(long time, TimeUnit unit)` 方法允许尝试获取锁，如果在指定的时间内无法获取锁，则返回 `false`。  `synchronized` 不支持超时等待。
* **多条件变量:**  `ReentrantLock` 可以与多个 `Condition` 对象配合使用，而 `synchronized` 只允许使用单个 `wait`/`notify` 机制，这在需要处理多个不同等待条件的场景下非常有用。


**ReentrantLock 的缺点：**

* **需要手动释放锁:**  `ReentrantLock` 需要手动释放锁，使用 `unlock()` 方法，忘记释放锁可能会导致死锁或资源泄漏。  `synchronized` 会自动释放锁。
* **更复杂的代码:**  使用 `ReentrantLock` 通常会比使用 `synchronized` 导致代码更复杂，因为它需要显式地获取和释放锁。


**Condition 的优点：**

* **精细的线程控制:**  `Condition` 提供了更精细的线程控制，可以分别等待和唤醒不同的线程组，而 `wait`/`notify` 只能对所有等待在同一个对象上的线程进行操作。
* **避免虚假唤醒:**  `Condition` 的 `await()` 方法可以避免 `wait()` 方法的虚假唤醒问题，因为它内部会自动处理虚假唤醒。  在使用 `Condition` 时，不需要像使用 `wait()` 方法那样在循环中检查条件。


**Condition 的缺点:**

* **需要与 `ReentrantLock` 配合使用:**  `Condition` 必须与 `ReentrantLock` 配合使用，不能单独使用。
* **更复杂的代码:**  使用 `Condition` 通常会使代码更复杂，因为它需要额外的代码来管理锁和条件变量。



**总结:**

`ReentrantLock` 和 `Condition` 提供了比 `synchronized` 和 `wait`/`notify` 更强大的功能和更灵活的控制，但是它们也增加了代码的复杂性，并且需要程序员更小心地处理锁和条件变量。  在简单的场景下，`synchronized` 通常就足够了；但在需要更高级功能（例如可中断的等待、超时等待、多个条件变量）的复杂并发场景中，`ReentrantLock` 和 `Condition` 是更好的选择。  选择哪种方法取决于具体的应用场景和程序的复杂度。  如果需要处理多个等待条件，`Condition` 是必需的。

**示例代码：**

```java
/**
 * 线程间通信使用 ReentrantLock+Condition 方法
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Slf4j
public class ThreadCommunicationByUsingReentrantLockAndConditionTests {
    @Test
    public void test() throws InterruptedException {
        int runLoop = 5;
        ShareResource shareResource = new ShareResource();

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < runLoop; i++) {
                try {
                    shareResource.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "A");
        Thread thread11 = new Thread(() -> {
            for (int i = 0; i < runLoop; i++) {
                try {
                    shareResource.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "A1");
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < runLoop; i++) {
                try {
                    shareResource.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "B");
        Thread thread21 = new Thread(() -> {
            for (int i = 0; i < runLoop; i++) {
                try {
                    shareResource.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "B1");
        thread1.start();
        thread11.start();
        thread2.start();
        thread21.start();

        thread1.join();
        thread11.join();
        thread2.join();
        thread21.join();
    }

    static class ShareResource {
        private ReentrantLock lock = new ReentrantLock();
        private Condition condition = lock.newCondition();
        private int value;

        void increment() throws InterruptedException {
            // 需要先给 lock 对象上锁才能够调用 condition await、signal、signalAll 方法
            try {
                lock.lock();
                // 注意：不能使用 if 判断以避免 wait() 虚假唤醒问题
                while (value == 1) {
                    condition.await();
                }

                value++;
                log.debug("{} value++: {}", Thread.currentThread().getName(), value);
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }

        void decrement() throws InterruptedException {
            // 需要先给 lock 对象上锁才能够调用 condition await、signal、signalAll 方法
            try {
                lock.lock();
                // 注意：不能使用 if 判断以避免 wait() 虚假唤醒问题
                while (value == 0) {
                    condition.await();
                }

                value--;
                log.debug("{} value--: {}", Thread.currentThread().getName(), value);
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }
}
```



## 集合的线程安全

### 介绍

Java集合框架中许多类在多线程环境下不是线程安全的，这意味着如果多个线程同时访问和修改同一个集合对象，可能会导致数据不一致、异常或程序崩溃等问题。  这主要是因为这些集合类的内部实现通常没有同步机制来保护对集合的并发访问。

**哪些集合类是线程不安全的？**

大部分常用的 Java 集合类，如果不加任何同步处理，都是线程不安全的。  例如：

* `ArrayList`
* `LinkedList`
* `HashMap`
* `HashSet`
* `TreeMap`
* `TreeSet`
* `Hashtable` (虽然 `Hashtable` 本身是同步的，但其性能较低)


**线程不安全问题具体表现：**

* **数据不一致:**  多个线程同时修改集合，可能会导致数据丢失、重复或损坏。
* **`ConcurrentModificationException`:**  当一个线程迭代集合时，另一个线程修改了集合，就会抛出 `ConcurrentModificationException`。  这是因为迭代器在迭代过程中会跟踪集合的修改情况，如果检测到集合被修改，就会抛出此异常。
* **死锁:**  在某些情况下，多个线程竞争同一个集合的锁，可能会导致死锁。
* **其他异常:**  可能抛出其他类型的异常，例如 `NullPointerException` 或 `IndexOutOfBoundsException`。


**解决线程不安全问题的方法：**

1. **使用线程安全的集合类:** Java 提供了一些线程安全的集合类，例如：

   * `ConcurrentHashMap`：线程安全的 `HashMap` 实现。
   * `CopyOnWriteArrayList`：写入时复制的 `ArrayList` 实现。  读操作非常快，但写操作比较慢，因为每次写操作都会复制整个集合。
   * `CopyOnWriteArraySet`：写入时复制的 `HashSet` 实现。
   * `Vector`：同步的 `ArrayList` 实现，但是性能较低，已被 `CopyOnWriteArrayList` 等替代。
   * `Stack`：同步的栈实现，性能也较低。
   * `ConcurrentSkipListMap` 和 `ConcurrentSkipListSet`：基于跳表的线程安全实现，性能通常优于 `TreeMap` 和 `TreeSet` 的同步版本。


2. **使用同步块 (`synchronized`)：**  可以使用 `synchronized` 块来同步对集合的访问：

   ```java
   public void addToList(List<String> list, String item) {
       synchronized (list) { // 同步整个list
           list.add(item);
       }
   }
   ```

   这会确保一次只有一个线程可以访问和修改集合。 但是，这种方法在高并发环境下性能会比较低，因为只有一个线程能访问。

3. **使用锁 (`ReentrantLock`)：**  可以使用 `ReentrantLock` 来实现更精细的锁控制：

   ```java
   private final ReentrantLock lock = new ReentrantLock();
   public void addToList(List<String> list, String item) {
       lock.lock();
       try {
           list.add(item);
       } finally {
           lock.unlock();
       }
   }
   ```

   `ReentrantLock` 比 `synchronized` 提供了更灵活的锁机制，例如可以设置公平锁和非公平锁，以及中断等待的线程。 但是也更复杂。


4. **使用 `Collections.synchronizedXXX()` 方法:**  Java 提供了一些工具方法，可以将非线程安全的集合包装成线程安全的集合：

   ```java
   List<String> synchronizedList = Collections.synchronizedList(new ArrayList<>());
   ```

   这种方法相对简单，但是性能仍然不如 `ConcurrentHashMap` 等专门设计的线程安全集合类。


**选择合适的方案:**

选择哪种方法取决于具体的应用场景和性能要求。  对于高并发环境，建议使用专门设计的线程安全集合类，例如 `ConcurrentHashMap` 和 `CopyOnWriteArrayList`。  对于简单的场景，可以使用同步块或 `Collections.synchronizedXXX()` 方法。  切记要根据你的具体需求和性能目标来选择最佳方案。

总而言之，理解 Java 集合的线程安全问题非常重要，在多线程编程中务必谨慎处理集合的并发访问，以避免出现难以预测的错误。



### ArrayList 线程不安全示例

以下是一些展示 `ArrayList` 线程不安全性的示例代码：

**示例 1:  `ConcurrentModificationException`**

这个例子演示了当一个线程迭代 `ArrayList` 时，另一个线程修改 `ArrayList` 会导致 `ConcurrentModificationException`。

```java
import java.util.ArrayList;
import java.util.List;

public class ArrayListUnsafeExample1 {

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }

        Thread thread1 = new Thread(() -> {
            for (Integer num : list) {
                System.out.println("Thread 1: " + num);
                try {
                    Thread.sleep(100); // 模拟一些操作
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                list.remove(0); // 在迭代过程中修改List
                System.out.println("Thread 2 removed an element.");
            }
        });

        thread1.start();
        thread2.start();
    }
}
```

运行此代码，很大概率会抛出 `ConcurrentModificationException`。


**示例 2: 数据不一致**

这个例子演示了多个线程同时向 `ArrayList` 添加元素，可能导致数据丢失或不一致。

```java
import java.util.ArrayList;
import java.util.List;

public class ArrayListUnsafeExample2 {

    static List<Integer> list = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                list.add(i);
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 1000; i < 2000; i++) {
                list.add(i);
            }
        });

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        System.out.println("List size: " + list.size());  //可能小于2000
        System.out.println("List content: " + list); // 内容可能不完整或重复
    }
}
```

由于线程的执行顺序不确定，`list.add()` 操作不是原子的，最终 `list` 的大小可能小于 2000，并且元素可能不完整或重复。

这些例子展示了在没有同步机制的情况下使用 `ArrayList` 的危险性。  为了避免这些问题，应该使用线程安全的集合类或在访问 `ArrayList` 时使用同步机制。  记住，这些例子可能需要多次运行才能重现错误，因为线程调度是不可预测的。



### ArrayList 线程不安全解决方案

Java 的 `ArrayList` 线程不安全是因为它的方法不是原子操作，多个线程同时操作 `ArrayList`  可能会导致数据损坏或不一致。  解决方法主要分为以下几类，我会更详细地解释每一类：

**1. 使用线程安全的集合类:**

这是最直接、最简单的方法，Java 提供了几个线程安全的 `List` 实现，无需自己处理同步：

* **`Vector`:**  这是最古老的线程安全 `List` 实现。它的所有方法都使用了 `synchronized` 关键字进行同步，这保证了线程安全。然而，这种同步机制是粗粒度的，所有方法都串行执行，导致性能在高并发环境下非常低。  除非你的应用并发量非常小，否则不推荐使用 `Vector`。

* **`CopyOnWriteArrayList`:**  这是更现代、更高效的线程安全 `List` 实现。它的核心思想是“写时复制”（Copy-On-Write）：当进行写入操作（例如 `add`、`remove`、`set` 等）时，它会创建一个新的数组，在新数组上进行修改，然后将新的数组赋值给原来的引用。读取操作则直接访问原数组，无需加锁，因此读取性能很高。  写入操作虽然需要复制数组，但如果读操作远多于写操作，`CopyOnWriteArrayList` 的性能优势就非常明显。  需要注意的是，读取到的数据可能不是最新的，因为写入操作是在新的数组上进行的。

**2. 使用同步机制:**

如果出于某些原因（例如，兼容性或性能微调），你仍然需要使用 `ArrayList`，那么可以使用 Java 的同步机制来确保线程安全：

* **`Collections.synchronizedList()`:**  这是最方便的方法，它可以将一个普通的 `ArrayList` 包装成一个线程安全的 `List`。  它内部使用了 `ReentrantLock` 来实现同步，性能比 `Vector` 好得多。

```java
List<String> list = Collections.synchronizedList(new ArrayList<>());
```

* **手动加锁 (使用 `synchronized` 块或 `ReentrantLock`)**:  这提供了最精细的控制，你可以根据你的具体需求，选择合适的锁粒度。  但是，这需要更小心的编程，以避免死锁或其他并发问题。  这对于更复杂的场景，或者需要对锁进行更精细控制时，才会考虑这种方法。

```java
List<String> list = new ArrayList<>();
private final Object lock = new Object(); // or ReentrantLock lock = new ReentrantLock();

// ...

synchronized (lock) { // or lock.lock(); ... lock.unlock();
    list.add("element");
}
```

**3. 使用并发编程工具:**

对于更高级的并发场景，可以考虑使用 Java 并发工具包 (java.util.concurrent) 中的其他数据结构：

* **`ConcurrentHashMap` (如果数据是键值对):**  如果你的数据可以表示为键值对，`ConcurrentHashMap` 通常是更好的选择。它提供了更高的并发性能，比手动同步 `HashMap` 更高效。

**选择哪个方案的建议：**

* **高并发，读多写少:** `CopyOnWriteArrayList` 通常是最佳选择。
* **高并发，读写比例接近，或写操作频繁:**  `Collections.synchronizedList()` 是一个不错的折中方案，性能比 `Vector` 好得多。
* **极低并发:** `Collections.synchronizedList()` 也足够。
* **非常低并发，简单场景，不需要高性能:** `Vector` 可以勉强接受，但不推荐。
* **需要更细粒度的控制，或性能要求极高，且非常了解并发编程:**  手动加锁（`synchronized` 或 `ReentrantLock`）可能需要考虑，但它需要非常谨慎的编程。
* **数据是键值对:** 使用 `ConcurrentHashMap`。

记住，选择合适的方案需要根据你的具体应用场景，权衡性能、内存占用和代码复杂度等因素。  不要盲目选择，在做出决定之前，务必仔细考虑你的应用需求和并发特性。



### ArrayList 线程不安全各个解决方案性能对比

示例代码：

```java
/**
 * 测试集合线程安全性能
 */
// https://blog.csdn.net/a23452/article/details/126680840
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark) //使用的SpringBoot容器，都是无状态单例Bean，无安全问题，可以直接使用基准作用域BenchMark
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS) //预热1s
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
// 指定并发执行线程数
// https://stackoverflow.com/questions/39644383/jmh-run-benchmark-concurrently
@Threads(-1)
public class CollectionThreadSafetyProblemBenchmarkTests {

    final static int IntStart = 0;
    final static int IntEnd = 10000;

    List<Integer> arrayList = new ArrayList<>();
    List<Integer> vector = new Vector<>();
    List<Integer> synchronizedList = Collections.synchronizedList(new ArrayList<>());
    List<Integer> copyOnWriteArrayList = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(CollectionThreadSafetyProblemBenchmarkTests.class.getSimpleName())
                // 断点调试时fork=0
                .forks(1)
                // 发生错误停止测试
                .shouldFailOnError(true)
                .jvmArgs("-Xmx2G")
                .build();
        new Runner(opt).run();
    }

    /**
     * 初始化，获取springBoot容器，run即可，同时得到相关的测试对象
     */
    @Setup(Level.Trial)
    public void setup() {
    }

    /**
     * 测试的后处理操作，关闭容器，资源清理
     */
    @TearDown(Level.Trial)
    public void teardown() {
    }

    /**
     * 测试 ArrayList 性能
     */
    @Benchmark
    public void testArrayList(Blackhole blackhole) {
        int randomInt = RandomUtils.nextInt(0, 3);
        if (randomInt == 0) {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            arrayList.add(randomInt);
        } else if (randomInt == 1) {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            try {
                arrayList.remove(randomInt);
            } catch (IndexOutOfBoundsException ignored) {

            }
        } else {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            try {
                blackhole.consume(arrayList.get(randomInt));
            } catch (IndexOutOfBoundsException ignored) {

            }
        }
    }

    /**
     * 测试 Vector 性能
     */
    @Benchmark
    public void testVector(Blackhole blackhole) {
        int randomInt = RandomUtils.nextInt(0, 3);
        if (randomInt == 0) {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            vector.add(randomInt);
        } else if (randomInt == 1) {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            try {
                vector.remove(randomInt);
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }
        } else {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            try {
                blackhole.consume(vector.get(randomInt));
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }
        }
    }

    /**
     * 测试 synchronizedList 性能
     */
    @Benchmark
    public void testSynchronizedList(Blackhole blackhole) {
        int randomInt = RandomUtils.nextInt(0, 3);
        if (randomInt == 0) {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            synchronizedList.add(randomInt);
        } else if (randomInt == 1) {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            try {
                synchronizedList.remove(randomInt);
            } catch (IndexOutOfBoundsException ignored) {

            }
        } else {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            try {
                blackhole.consume(synchronizedList.get(randomInt));
            } catch (IndexOutOfBoundsException | IllegalStateException ignored) {

            }
        }
    }

    /**
     * 测试 copyOnWriteArrayList 性能
     */
    @Benchmark
    public void testCopyOnWriteArrayList(Blackhole blackhole) {
        int randomInt = RandomUtils.nextInt(0, 3);
        if (randomInt == 0) {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            copyOnWriteArrayList.add(randomInt);
        } else if (randomInt == 1) {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            try {
                copyOnWriteArrayList.remove(randomInt);
            } catch (IndexOutOfBoundsException ignored) {

            }
        } else {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            try {
                blackhole.consume(copyOnWriteArrayList.get(randomInt));
            } catch (IndexOutOfBoundsException | IllegalStateException ignored) {

            }
        }
    }
}
```

JMH 结果：

```
Benchmark                                                              Mode  Cnt        Score         Error  Units
CollectionThreadSafetyProblemBenchmarkTests.testArrayList             thrpt    3   425181.365 ± 2411007.900  ops/s
CollectionThreadSafetyProblemBenchmarkTests.testCopyOnWriteArrayList  thrpt    3   271624.334 ±  369097.490  ops/s
CollectionThreadSafetyProblemBenchmarkTests.testSynchronizedList      thrpt    3  2821699.919 ± 4372499.634  ops/s
CollectionThreadSafetyProblemBenchmarkTests.testVector                thrpt    3  2534076.175 ± 5384026.966  ops/s
```



### HashSet 线程不安全解决方案

Java 的 `HashSet`  和 `ArrayList` 一样，不是线程安全的。多个线程同时操作同一个 `HashSet`  可能会导致数据不一致、丢失元素或其他异常情况。解决方法和 `ArrayList`  类似，主要有以下几种：

**1. 使用线程安全的集合类：**  Java 没有直接提供线程安全的 `HashSet` 实现，但我们可以利用 `Collections.synchronizedSet()` 方法来创建一个线程安全的 `Set`。

```java
Set<String> set = Collections.synchronizedSet(new HashSet<>());
```

这个方法使用一个内部锁来同步 `HashSet` 的所有方法，保证了线程安全。  但是，和 `Collections.synchronizedList()` 一样，这种同步方式是粗粒度的，所有操作都需要获得同一个锁，在高并发情况下性能会下降。


**2. 使用并发编程工具：**  `ConcurrentHashMap`  虽然是 `Map`，但它可以很方便地用作线程安全的 `Set`。  你可以只使用 `ConcurrentHashMap` 的 `keySet()` 方法来获取一个线程安全的 `Set`  视图：

```java
Map<String, Boolean> map = new ConcurrentHashMap<>();
Set<String> set = map.keySet(); // set is thread-safe
```

因为 `ConcurrentHashMap`  使用了更细粒度的锁机制，所以它的并发性能比 `Collections.synchronizedSet()`  要高得多。  这种方法尤其适用于需要高并发性能的场景。


**3. 手动加锁：**  类似于 `ArrayList`，你可以使用 `synchronized` 块或 `ReentrantLock`  来手动同步对 `HashSet` 的访问。 这种方式需要更细致的控制，但同时也增加了代码的复杂性和出错的可能性。  除非你有非常特殊的需求，否则不推荐这种方法。

```java
Set<String> set = new HashSet<>();
private final Object lock = new Object(); // or ReentrantLock lock = new ReentrantLock();

// ...

synchronized (lock) { // or lock.lock(); ... lock.unlock();
    set.add("element");
}
```


**选择哪个方案的建议：**

* **大多数情况，并发量适中：**  `Collections.synchronizedSet(new HashSet<>())`  是一个简单易用的选择，除非你的并发量非常高。

* **高并发，需要高性能：**  使用 `ConcurrentHashMap` 的 `keySet()` 方法创建一个线程安全的 `Set`  是更好的选择。  `ConcurrentHashMap` 的分段锁机制使其在高并发场景下性能更佳。

* **极低并发，对性能要求不高：**  `Collections.synchronizedSet(new HashSet<>())` 也足够。

* **需要非常细粒度的控制，且非常了解并发编程：** 手动加锁（`synchronized` 或 `ReentrantLock`）是最后的选择，但需要非常小心地处理，以避免死锁等问题。

总的来说，除非你的并发量极高或者对性能有非常严格的要求，否则 `Collections.synchronizedSet()` 通常是一个足够好且简单的解决方案。  对于高并发场景，`ConcurrentHashMap.keySet()` 提供了更好的性能。  手动加锁的方法应该仅在有非常特殊的需求且你对并发编程非常熟悉的情况下使用。  记住，在选择之前，要仔细考虑你的应用场景和并发特性。



### HashMap 线程不安全解决方案

Java 的 `HashMap` 不是线程安全的，在多线程环境下并发访问可能会导致数据损坏、死循环甚至程序崩溃。  解决方法主要有以下几种：

**1. 使用线程安全的 Map 实现:**

这是最直接、最推荐的解决方法。Java 提供了几个线程安全的 `Map` 实现，无需自己处理同步：

* **`ConcurrentHashMap`:** 这是最常用的线程安全 `Map` 实现。它采用了一种细粒度的锁机制，将 `HashMap` 分成多个段（segment），每个段拥有自己的锁。这样，多个线程可以同时访问不同的段，从而提高并发性能。  在大多数情况下，`ConcurrentHashMap` 是处理多线程 `HashMap` 问题的最佳选择。  它提供了与 `HashMap` 类似的 API，迁移成本较低。

* **`Collections.synchronizedMap()`:**  类似于 `ArrayList` 和 `HashSet` 的情况，你可以使用 `Collections.synchronizedMap()` 方法将一个普通的 `HashMap` 包装成一个线程安全的 `Map`。  但是，这个方法使用了单一的锁来同步所有操作，在高并发情况下性能会严重下降，因此不推荐在高并发环境下使用。


**2. 手动加锁 (使用 `synchronized` 块或 `ReentrantLock`)**

这是最底层的解决方案，需要开发者自己管理锁，控制对 `HashMap` 的访问。  这需要非常谨慎的编程，以避免死锁或其他并发问题。  除非你对并发编程非常熟悉，并且需要对锁进行非常精细的控制，否则不推荐使用这种方法。  这对于学习并发编程原理很有帮助，但在实际应用中，通常应该优先使用 `ConcurrentHashMap`。

```java
HashMap<String, Integer> map = new HashMap<>();
private final Object lock = new Object(); // or ReentrantLock lock = new ReentrantLock();

// ...

synchronized (lock) { // or lock.lock(); ... lock.unlock();
    map.put("key", 1);
}
```

**3. 使用其他并发数据结构 (根据具体需求)**

在一些特殊场景下，其他并发数据结构可能更合适：

* **如果你的操作主要集中在读取上，且写入操作相对较少:** 考虑 `ConcurrentHashMap` 的 `computeIfAbsent()` 方法，这样能最大限度地减少对共享资源的修改操作，提高性能。

* **如果你的数据是只读的（immutable）:** 你可以创建多个 `HashMap` 实例，每个线程使用一个实例。这可以避免锁竞争，但要注意数据的一致性问题。  这种方法只适用于读取操作，写入操作则需要引入更复杂的同步策略。


**选择哪个方案的建议：**

* **绝大多数情况：** `ConcurrentHashMap` 是最佳选择，因为它提供了高性能的并发访问能力，并且 API 与 `HashMap` 相似，易于使用。

* **低并发场景，简单代码：** `Collections.synchronizedMap()` 可用，但性能不如 `ConcurrentHashMap`。

* **高并发场景，需要精确控制锁:** 手动加锁的方法只在你有非常特殊的需求且你对并发编程非常熟悉的情况下使用。


**总结：**

对于线程安全的 `HashMap`，`ConcurrentHashMap` 通常是首选。  它兼顾了性能和易用性。  避免使用 `Collections.synchronizedMap()` 和手动加锁的方式，除非你对并发编程非常了解，并且有充分的理由选择它们。  在选择任何解决方案之前，务必仔细评估你的应用程序的并发特性和性能需求。



## 锁



### 悲观锁和乐观锁

乐观锁和悲观锁是两种不同的并发控制策略，它们在处理数据并发访问时采取不同的方法：

**悲观锁 (Pessimistic Locking):**

* **核心思想:**  悲观锁总是假设并发冲突总是会发生，因此它在访问数据时会先获取锁，确保在整个操作过程中不会有其他线程修改数据。  只有获取到锁的线程才能访问数据，其他线程必须等待。
* **实现方式:**  通常使用数据库锁机制或代码锁机制（例如 Java 中的 `synchronized` 关键字或 `ReentrantLock`）。
* **优点:**  简单易懂，能够保证数据的完整性和一致性。
* **缺点:**  性能较低，因为锁的竞争会导致线程阻塞，降低并发效率。  如果锁竞争激烈，程序性能会严重下降。  容易产生死锁问题。


**乐观锁 (Optimistic Locking):**

* **核心思想:**  乐观锁总是假设并发冲突很少发生，因此它在访问数据时不加锁。  它会在更新数据之前检查数据是否被修改过。如果数据没有被修改，则更新数据；如果数据已经被修改，则通常会回滚事务或抛出异常，让程序员处理冲突。
* **实现方式:**  通常使用版本号或时间戳机制。  在数据库中，可以通过添加版本号列来实现乐观锁。  在代码中，可以使用 CAS (Compare And Swap) 指令或原子操作。
* **优点:**  性能较高，因为没有锁的竞争，并发效率高。  减少了死锁的可能性。
* **缺点:**  需要额外的机制来检测并发冲突，增加程序复杂度。  如果并发冲突频繁发生，则乐观锁的效率反而会降低。  可能出现ABA问题。


**版本号实现乐观锁示例 (数据库层面):**

假设数据库表 `users` 中有 `id` 和 `version` 两个字段，`version` 用于表示版本号。

更新语句：

```sql
UPDATE users SET name = 'newName', version = version + 1 WHERE id = 1 AND version = 1;
```

这条语句只会在 `id` 为 1 且 `version` 为 1 的情况下更新数据，并把 `version` 加 1。如果另一个线程已经修改了该记录，`version` 值就不再是 1，更新语句将不会执行，从而避免了数据冲突。


**CAS 实现乐观锁示例 (代码层面):**

Java 中的 `AtomicInteger` 类就使用了 CAS 机制来实现乐观锁。

```java
import java.util.concurrent.atomic.AtomicInteger;

public class OptimisticLockExample {
    private AtomicInteger count = new AtomicInteger(0);

    public int increment() {
        int expectedValue = count.get();
        while (!count.compareAndSet(expectedValue, expectedValue + 1)) {
            expectedValue = count.get(); // 重新获取当前值
        }
        return count.get();
    }
}
```

`compareAndSet` 方法会比较当前值和期望值是否相等，如果相等则更新为新值，并返回 `true`；否则返回 `false`，需要重新获取当前值并再次尝试。


**选择策略:**

选择乐观锁还是悲观锁取决于具体的应用场景：

* **并发冲突频繁:**  选择悲观锁，以保证数据的完整性。
* **并发冲突较少:**  选择乐观锁，以提高性能。
* **对一致性要求高:**  选择悲观锁。
* **对性能要求高:**  选择乐观锁。


总而言之，乐观锁和悲观锁各有优缺点，需要根据实际情况选择合适的并发控制策略。  在实际应用中，也可能结合两种锁的优势，例如在某些场景下使用悲观锁，在其他场景下使用乐观锁。  此外，还要注意乐观锁的ABA问题，以及在高并发场景下可能存在的性能瓶颈。



### synchronized

#### 介绍

Java 的 `synchronized` 关键字是用于实现同步的机制，它可以保证在同一时刻只有一个线程可以访问被 `synchronized` 修饰的代码块或方法。这主要用于解决多线程环境下的并发问题，例如避免数据竞争和保证线程安全。

`synchronized` 可以用在以下两种地方：

**1. synchronized 方法:**

将 `synchronized` 关键字放在方法声明之前，可以使整个方法成为同步方法。  这意味着当一个线程正在执行该同步方法时，其他线程将无法执行该方法，直到当前线程执行完毕释放锁。

```java
public class Counter {
    private int count = 0;

    public synchronized void increment() { // synchronized 方法
        count++;
    }

    public int getCount() {
        return count;
    }
}
```

在这个例子中，`increment()` 方法是同步方法。  每个线程在调用 `increment()` 方法时都会获取同一把锁（锁是 `Counter` 对象本身），从而保证了 `count` 变量的原子性操作。


**2. synchronized 代码块:**

将 `synchronized` 关键字与代码块一起使用，可以更精细地控制同步的范围。  `synchronized` 代码块需要指定一个锁对象，只有获取到该锁对象的线程才能执行该代码块。

```java
public class Counter {
    private int count = 0;
    private final Object lock = new Object(); // 锁对象

    public void increment() {
        synchronized (lock) { // synchronized 代码块
            count++;
        }
    }

    public int getCount() {
        return count;
    }
}
```

在这个例子中，`lock` 对象充当锁。  所有线程在进入 `synchronized` 代码块之前都必须获取 `lock` 对象的锁。  这比 `synchronized` 方法更灵活，因为它允许你只同步需要同步的部分代码，而不是整个方法。


**锁的机制:**

`synchronized` 关键字底层依赖于 Java 对象头中的锁标记。  当一个线程获取锁时，JVM 会修改对象头中的锁标记，表示该对象已经被锁住。  其他线程试图获取该锁时，会阻塞直到锁被释放。  释放锁发生在 `synchronized` 代码块或方法执行完毕时。


**隐式锁和显式锁:**

* **隐式锁 (Intrinsic Lock):**  当使用 `synchronized` 方法时，锁对象是该方法所属的对象实例。  也称为“对象锁”或“监视器锁”。
* **显式锁 (Explicit Lock):**  当使用 `synchronized` 代码块时，锁对象可以是任何对象，由开发者指定。


**潜在问题:**

* **性能损耗:**  `synchronized` 会带来一定的性能损耗，因为它涉及到线程的阻塞和上下文切换。  过度使用 `synchronized` 可能会导致程序性能下降。
* **死锁:**  如果多个线程相互持有对方需要的锁，则可能发生死锁，导致程序无法继续执行。
* **活锁:**  线程不断尝试获取锁但始终无法获取，导致程序无法继续执行。


**替代方案:**

对于高并发场景，可以考虑使用更高效的同步机制，例如：

* **ReentrantLock:**  一个可重入的互斥锁，提供比 `synchronized` 更丰富的功能，例如公平锁、超时获取锁等。
* **ConcurrentHashMap:**  一个线程安全的 HashMap 实现，用于高效地处理并发访问。
* **AtomicInteger, AtomicLong 等原子类:**  提供原子性的操作，避免数据竞争。

选择哪种同步机制取决于具体的应用场景和性能要求。  对于简单的同步需求，`synchronized` 足够使用；对于更复杂的场景，可能需要考虑使用更高级的同步机制。  在高并发场景下，应优先考虑使用无锁数据结构或其他更高效的并发编程技术，以最大限度地提高性能。



#### 对象锁和类锁

Java 中的 `synchronized` 关键字可以用于实现对象锁和类锁，它们在作用范围和粒度上有所不同：

**1. 对象锁 (Instance Lock):**

* **作用范围:**  `synchronized` 修饰实例方法 (非静态方法) 或同步代码块，其中 `this` 作为锁对象。  这意味着只有持有该对象锁的线程才能执行该方法或代码块。  不同的对象实例拥有独立的锁。

* **代码示例:**

```java
public class MyObject {
    public synchronized void synchronizedMethod() {
        // ... synchronized code ...
    }

    public void otherMethod() {
        synchronized (this) {
            // ... synchronized code ...
        }
    }
}
```

在这个例子中，`synchronizedMethod()` 使用隐式对象锁 `this`，而 `otherMethod()` 使用显式对象锁 `this`。  如果创建了两个 `MyObject` 实例，`obj1` 和 `obj2`，那么 `obj1.synchronizedMethod()` 和 `obj2.synchronizedMethod()` 可以同时执行，因为它们持有的是不同的对象锁。


**2. 类锁 (Class Lock):**

* **作用范围:** `synchronized` 修饰静态方法 (static 方法) 或同步代码块，其中 `MyObject.class` (或其他类名.class) 作为锁对象。这意味着所有线程在访问该静态方法或代码块时，都需要竞争同一个锁，只有一个线程可以执行。

* **代码示例:**

```java
public class MyObject {
    public static synchronized void synchronizedStaticMethod() {
        // ... synchronized code ...
    }

    public static void otherStaticMethod() {
        synchronized (MyObject.class) {
            // ... synchronized code ...
        }
    }
}
```

在这个例子中，`synchronizedStaticMethod()` 和 `otherStaticMethod()` 都使用了 `MyObject.class` 作为锁对象。  任何线程想要执行这些静态方法，都必须获得这个类锁。


**对象锁和类锁的区别总结:**

| 特性     | 对象锁 (Instance Lock)                  | 类锁 (Class Lock)                    |
| -------- | --------------------------------------- | ------------------------------------ |
| 锁对象   | `this` (或在同步代码块中指定的任意对象) | `ClassName.class` (或其他类名.class) |
| 作用范围 | 实例方法或同步代码块                    | 静态方法或同步代码块                 |
| 锁粒度   | 细粒度，每个对象实例拥有独立的锁        | 粗粒度，所有对象实例共享同一个锁     |
| 竞争强度 | 相对较低                                | 相对较高，容易成为瓶颈               |


**选择对象锁还是类锁:**

* 使用对象锁来保护实例变量或与特定对象实例相关的数据。
* 使用类锁来保护静态变量或与类本身相关的数据。

选择哪种类型的锁取决于你要保护的数据范围和并发控制的粒度。  如果需要更高的并发性，通常建议使用对象锁。  如果需要保证对共享静态资源的独占访问，则应该使用类锁。  但是，要小心类锁的竞争，因为它可能成为性能瓶颈。

记住，不当的锁使用可能会导致死锁或性能问题。  需要仔细设计和测试你的代码，以确保正确和高效地使用 `synchronized`。



#### 示例

```java
// region synchronized 锁测试

SynchronizedTestingAssistantObject synchronizedTestingAssistantObject1 = new SynchronizedTestingAssistantObject();
SynchronizedTestingAssistantObject synchronizedTestingAssistantObject2 = new SynchronizedTestingAssistantObject();

// 两个 synchronized 并且同一个实例的方法，锁互斥
CompletableFuture<Void> completableFuture1 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject1.instanceMethod1WithSynchronized("c1"), this.executor);
CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject1.instanceMethod2WithSynchronized("c2"), this.executor);
CompletableFuture.allOf(completableFuture1, completableFuture2).join();
Assert.assertTrue(MyCheckPoint.isBefore("c1", "c2"));
MyCheckPoint.clear();

// 一个 synchronized 实例方法，一个普通实例方法，同一个实例，锁不互斥
completableFuture1 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject1.instanceMethod1WithSynchronized("c1"), this.executor);
completableFuture2 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject1.instanceMethod3WithoutSynchronized("c2"), this.executor);
CompletableFuture.allOf(completableFuture1, completableFuture2).join();
Assert.assertTrue(MyCheckPoint.isBefore("c2", "c1"));
MyCheckPoint.clear();

// 两个 synchronized 并且不是同一个实例的方法，锁不互斥
completableFuture1 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject1.instanceMethod1WithSynchronized("c1"), this.executor);
completableFuture2 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject2.instanceMethod3WithoutSynchronized("c2"), this.executor);
CompletableFuture.allOf(completableFuture1, completableFuture2).join();
Assert.assertTrue(MyCheckPoint.isBefore("c2", "c1"));
MyCheckPoint.clear();

// 两个 synchronized 静态方法并且是同一个实例，锁互斥
completableFuture1 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject1.staticMethod1WithSynchronized("c1"), this.executor);
completableFuture2 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject1.staticMethod2WithSynchronized("c2"), this.executor);
CompletableFuture.allOf(completableFuture1, completableFuture2).join();
Assert.assertTrue(MyCheckPoint.isBefore("c1", "c2"));
MyCheckPoint.clear();

// 两个 synchronized 静态方法并且不是同一个实例，锁互斥
completableFuture1 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject1.staticMethod1WithSynchronized("c1"), this.executor);
completableFuture2 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject2.staticMethod2WithSynchronized("c2"), this.executor);
CompletableFuture.allOf(completableFuture1, completableFuture2).join();
Assert.assertTrue(MyCheckPoint.isBefore("c1", "c2"));
MyCheckPoint.clear();

// 一个 synchronized 静态方法，一个 synchronized 实例方法，同一个实例，锁不互斥
completableFuture1 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject1.staticMethod1WithSynchronized("c1"), this.executor);
completableFuture2 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject1.instanceMethod2WithSynchronized("c2"), this.executor);
CompletableFuture.allOf(completableFuture1, completableFuture2).join();
Assert.assertTrue(MyCheckPoint.isBefore("c2", "c1"));
MyCheckPoint.clear();

// 一个 synchronized 静态方法，一个 synchronized 实例方法，不是同一个实例，锁不互斥
completableFuture1 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject1.staticMethod1WithSynchronized("c1"), this.executor);
completableFuture2 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject2.instanceMethod2WithSynchronized("c2"), this.executor);
CompletableFuture.allOf(completableFuture1, completableFuture2).join();
Assert.assertTrue(MyCheckPoint.isBefore("c2", "c1"));
MyCheckPoint.clear();

// endregion
```



#### 性能

```java
public static class MyObject {
    public synchronized void methodSimulateLongRunTaskWithSynchronized() {
        try {
            TimeUnit.MILLISECONDS.sleep(5);
        } catch (InterruptedException ignored) {

        }
    }

    public void methodSimulateLongRunTaskWithoutSynchronized() {
        try {
            TimeUnit.MILLISECONDS.sleep(5);
        } catch (InterruptedException ignored) {

        }
    }

    final static int AmountVar = 320;
    final static double DoubleVar = 12.328383984984;

    public synchronized double methodWithSynchronized() {
        return AmountVar * (DoubleVar - 1);
    }

    public double methodWithoutSynchronized() {
        return AmountVar * (DoubleVar - 1);
    }
}
```

```java
/**
 * 测试模拟耗时逻辑并且使用 synchronized
 *
 * @param blackhole
 */
@Benchmark
public void testMethodSimulateLongRunTaskWithSynchronized(Blackhole blackhole) {
    myObject.methodSimulateLongRunTaskWithSynchronized();
}

/**
 * 测试模拟耗时逻辑并且不使用 synchronized
 *
 * @param blackhole
 */
@Benchmark
public void testMethodSimulateLongRunTaskWithoutSynchronized(Blackhole blackhole) {
    myObject.methodSimulateLongRunTaskWithoutSynchronized();
}

/**
 * 测试方法 synchronized 性能
 *
 * @param blackhole
 */
@Benchmark
public void testMethodWithSynchronized(Blackhole blackhole) {
    blackhole.consume(myObject.methodWithSynchronized());
}

/**
 * 测试方法没有 synchronized 性能
 *
 * @param blackhole
 */
@Benchmark
public void testMethodWithoutSynchronized(Blackhole blackhole) {
    blackhole.consume(myObject.methodWithoutSynchronized());
}
```

```
Benchmark                                                             Mode  Cnt          Score           Error  Units
LockBenchmarkTests.testMethodSimulateLongRunTaskWithSynchronized     thrpt    3        186.583 ±       515.280  ops/s
LockBenchmarkTests.testMethodSimulateLongRunTaskWithoutSynchronized  thrpt    3       1453.983 ±        80.062  ops/s
LockBenchmarkTests.testMethodWithSynchronized                        thrpt    3   41255924.770 ±  12131495.283  ops/s
LockBenchmarkTests.testMethodWithoutSynchronized                     thrpt    3  919558366.757 ± 730791882.673  ops/s
```

- synchronized 会导致并发线程竞争锁资源导致排队处理并发性能下降。



### ReentrantLock - 公平锁和非公平锁

#### 介绍

Java中的公平锁和非公平锁是`ReentrantLock`类提供的两种不同的锁获取策略。它们的区别在于线程获取锁的顺序：

**1. 非公平锁 (Non-Fair Lock):**

* **特性:**  线程尝试获取锁时，不考虑等待队列中的顺序。  如果锁空闲，则直接尝试获取，即使队列中有其他线程等待更长时间。这类似于现实生活中“先到先得”的场景。

* **性能:**  通常比公平锁性能更高，因为避免了队列管理的开销。  减少了线程上下文切换的次数，因为线程可以抢占锁，而不需要一直等待。

* **适用场景:**  在大多数情况下，非公平锁是首选，因为它具有更高的吞吐量和性能。 尤其适用于锁竞争不激烈的情况。


**2. 公平锁 (Fair Lock):**

* **特性:**  线程获取锁时，严格按照等待队列中的顺序进行。  先到达的线程先获得锁。  这确保了所有线程都有公平的机会获取锁。

* **性能:**  通常比非公平锁性能略低，因为需要维护等待队列和管理线程的等待顺序，增加了开销。 也会增加线程上下文切换的次数。

* **适用场景:**  公平锁适用于需要保证所有线程都能得到公平对待的场景，例如避免饥饿现象（某些线程长期无法获取锁）。  但需要权衡性能。



**ReentrantLock 的构造函数:**

`ReentrantLock` 类有两个构造函数：

* `ReentrantLock():` 创建一个非公平锁 (默认)。
* `ReentrantLock(boolean fair):` 创建一个公平锁或非公平锁，根据 `fair` 参数的值决定 (true 为公平锁，false 为非公平锁)。


**代码示例:**

```java
import java.util.concurrent.locks.ReentrantLock;

public class FairAndUnfairLocks {
    public static void main(String[] args) {
        // 非公平锁
        ReentrantLock unfairLock = new ReentrantLock();

        // 公平锁
        ReentrantLock fairLock = new ReentrantLock(true);

        // ... 使用 unfairLock 和 fairLock ...
    }
}
```

**总结:**

| 特性       | 非公平锁 (Non-Fair)                              | 公平锁 (Fair)                                     |
| ---------- | ------------------------------------------------ | ------------------------------------------------- |
| 获取锁顺序 | 不考虑等待队列顺序，直接尝试获取                 | 严格按照等待队列顺序获取                          |
| 性能       | 通常更高                                         | 通常较低                                          |
| 适用场景   | 锁竞争不激烈，追求高吞吐量                       | 需要保证所有线程公平，避免饥饿现象                |
| 默认情况   | `ReentrantLock()` 构造函数创建的锁是**非公平锁** | 需要显式指定 `new ReentrantLock(true)` 才为公平锁 |


在大多数情况下，除非你有非常严格的公平性要求，否则非公平锁是更好的选择，因为它具有更高的性能。  公平锁虽然保证公平性，但性能损失可能很大，在高并发场景下可能会导致吞吐量下降。  选择哪种类型的锁取决于你的应用程序的特定需求和性能要求。



#### 示例

```java
// region 公平锁和非公平锁

// 非公平锁测试
ReentrantLock reentrantLock = new ReentrantLock();
AtomicLong counter = new AtomicLong(1000);
AtomicLong lockCounter1 = new AtomicLong();
AtomicLong lockCounter2 = new AtomicLong();
AtomicLong lockCounter3 = new AtomicLong();
ReentrantLock finalReentrantLock3 = reentrantLock;
AtomicLong finalCounter3 = counter;
AtomicLong finalLockCounter3 = lockCounter1;
CompletableFuture<Void> completableFuture1 = CompletableFuture.runAsync(() -> {
    while (true) {
        try {
            finalReentrantLock3.lock();

            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException ignored) {

            }

            if (finalCounter3.get() <= 0) {
                break;
            }
            finalCounter3.decrementAndGet();
            finalLockCounter3.incrementAndGet();
        } finally {
            finalReentrantLock3.unlock();
        }
    }
}, this.executor);
ReentrantLock finalReentrantLock4 = reentrantLock;
AtomicLong finalCounter4 = counter;
AtomicLong finalLockCounter4 = lockCounter2;
CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(() -> {
    while (true) {
        try {
            finalReentrantLock4.lock();

            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException ignored) {

            }

            if (finalCounter4.get() <= 0) {
                break;
            }
            finalCounter4.decrementAndGet();
            finalLockCounter4.incrementAndGet();
        } finally {
            finalReentrantLock4.unlock();
        }
    }
}, this.executor);
ReentrantLock finalReentrantLock5 = reentrantLock;
AtomicLong finalCounter5 = counter;
AtomicLong finalLockCounter5 = lockCounter3;
CompletableFuture<Void> completableFuture3 = CompletableFuture.runAsync(() -> {
    while (true) {
        try {
            finalReentrantLock5.lock();

            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException ignored) {

            }

            if (finalCounter5.get() <= 0) {
                break;
            }
            finalCounter5.decrementAndGet();
            finalLockCounter5.incrementAndGet();
        } finally {
            finalReentrantLock5.unlock();
        }
    }
}, this.executor);
CompletableFuture.allOf(completableFuture1, completableFuture2, completableFuture3).join();
// 从输出结果中可以看出各个线程获取到的锁的次数不平均
log.debug("lockCounter1=" + lockCounter1.get() + ",lockCounter2=" + lockCounter2.get() + ",lockCounter3=" + lockCounter3.get());

// 公平锁测试
reentrantLock = new ReentrantLock(true);
counter = new AtomicLong(1000);
lockCounter1 = new AtomicLong();
lockCounter2 = new AtomicLong();
lockCounter3 = new AtomicLong();
ReentrantLock finalReentrantLock = reentrantLock;
AtomicLong finalCounter = counter;
AtomicLong finalLockCounter = lockCounter1;
completableFuture1 = CompletableFuture.runAsync(() -> {
    while (true) {
        try {
            finalReentrantLock.lock();

            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException ignored) {

            }

            if (finalCounter.get() <= 0) {
                break;
            }
            finalCounter.decrementAndGet();
            finalLockCounter.incrementAndGet();
        } finally {
            finalReentrantLock.unlock();
        }
    }
}, this.executor);
ReentrantLock finalReentrantLock1 = reentrantLock;
AtomicLong finalCounter1 = counter;
AtomicLong finalLockCounter1 = lockCounter2;
completableFuture2 = CompletableFuture.runAsync(() -> {
    while (true) {
        try {
            finalReentrantLock1.lock();

            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException ignored) {

            }

            if (finalCounter1.get() <= 0) {
                break;
            }
            finalCounter1.decrementAndGet();
            finalLockCounter1.incrementAndGet();
        } finally {
            finalReentrantLock1.unlock();
        }
    }
}, this.executor);
ReentrantLock finalReentrantLock2 = reentrantLock;
AtomicLong finalCounter2 = counter;
AtomicLong finalLockCounter2 = lockCounter3;
completableFuture3 = CompletableFuture.runAsync(() -> {
    while (true) {
        try {
            finalReentrantLock2.lock();

            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException ignored) {

            }

            if (finalCounter2.get() <= 0) {
                break;
            }
            finalCounter2.decrementAndGet();
            finalLockCounter2.incrementAndGet();
        } finally {
            finalReentrantLock2.unlock();
        }
    }
}, this.executor);
CompletableFuture.allOf(completableFuture1, completableFuture2, completableFuture3).join();
// 从输出结果中可以看出各个线程获取到的锁的次数平均
log.debug("lockCounter1=" + lockCounter1.get() + ",lockCounter2=" + lockCounter2.get() + ",lockCounter3=" + lockCounter3.get());

// endregion
```



#### 性能

```java
ReentrantLock reentrantLockUnfair = new ReentrantLock();
ReentrantLock reentrantLockFair = new ReentrantLock(true);
```

```java
/**
 * 测试非公平锁性能
 */
@Benchmark
public void testReentrantLockUnfair() {
    try {
        reentrantLockUnfair.lock();
    } finally {
        reentrantLockUnfair.unlock();
    }
}

/**
 * 测试公平锁性能
 */
@Benchmark
public void testReentrantLockFair() {
    try {
        reentrantLockFair.lock();
    } finally {
        reentrantLockFair.unlock();
    }
}
```

```
Benchmark                                    Mode  Cnt         Score         Error  Units
LockBenchmarkTests.testReentrantLockFair    thrpt    3     20858.606 ±   16254.369  ops/s
LockBenchmarkTests.testReentrantLockUnfair  thrpt    3  48043004.839 ± 4146679.284  ops/s
```

- 非公平锁性能明显高于公平锁性能



### 死锁和排查

#### 什么是死锁呢？

Java 多线程死锁指的是两个或多个线程因为互相持有对方需要的资源，而永久地阻塞，无法继续执行的情况。  这就像一个僵局，每个线程都在等待其他线程释放它需要的资源，但这些资源永远不会被释放。

**死锁产生的四个必要条件:**

为了发生死锁，必须同时满足以下四个条件：

1. **互斥 (Mutual Exclusion):**  资源只能被一个线程独占访问。  这意味着多个线程不能同时访问同一个资源。

2. **持有并等待 (Hold and Wait):**  一个线程持有至少一个资源，并等待获取其他线程持有的资源。

3. **不可抢占 (No Preemption):**  资源不能被抢占。  一个线程已经获得的资源，在未主动释放之前，不能被其他线程强行夺走。

4. **循环等待 (Circular Wait):**  存在一个循环等待的资源链，例如线程 A 等待线程 B 释放资源，线程 B 等待线程 C 释放资源，而线程 C 又等待线程 A 释放资源，形成一个闭环。


**死锁的示例:**

假设有两个线程 `Thread1` 和 `Thread2`，它们需要访问两个资源 `ResourceA` 和 `ResourceB`。

* `Thread1` 获取了 `ResourceA`，然后试图获取 `ResourceB`。
* `Thread2` 获取了 `ResourceB`，然后试图获取 `ResourceA`。

现在，`Thread1` 和 `Thread2` 都被阻塞了：`Thread1` 等待 `Thread2` 释放 `ResourceB`，`Thread2` 等待 `Thread1` 释放 `ResourceA`。  这就是死锁。


**死锁的代码示例:**

```java
public class DeadlockExample {
    private static Object resourceA = new Object();
    private static Object resourceB = new Object();

    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            synchronized (resourceA) {
                System.out.println("Thread 1: Holding resource A");
                try {
                    Thread.sleep(1000); // Simulate some work
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (resourceB) {
                    System.out.println("Thread 1: Holding resource B");
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (resourceB) {
                System.out.println("Thread 2: Holding resource B");
                try {
                    Thread.sleep(1000); // Simulate some work
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (resourceA) {
                    System.out.println("Thread 2: Holding resource A");
                }
            }
        });

        thread1.start();
        thread2.start();
    }
}
```

在这个例子中，`thread1` 获取 `resourceA` 后尝试获取 `resourceB`，而 `thread2` 获取 `resourceB` 后尝试获取 `resourceA`，从而导致死锁。


**避免死锁的方法:**

* **避免循环依赖:**  仔细设计代码，避免形成循环依赖的锁获取顺序。
* **使用一致的锁获取顺序:**  如果多个线程需要访问多个锁，确保所有线程都以相同的顺序获取这些锁。
* **使用超时机制:**  在尝试获取锁时设置超时时间，如果在超时时间内无法获取锁，则进行适当的处理，例如重试或放弃。
* **使用更高级的锁机制:**  例如 `ReentrantReadWriteLock`，可以提高并发性。
* **死锁检测工具:**  使用一些工具来检测和诊断死锁问题。

死锁是一个复杂的问题，需要仔细分析代码和线程的执行流程才能有效避免。  良好的代码设计和测试至关重要。  仔细考虑资源的访问顺序，并采取预防措施，可以最大限度地减少死锁的发生。



#### 死锁有哪些情况呢？

Java 多线程死锁发生在两个或多个线程互相持有对方需要的资源，并且都在等待对方释放资源的情况。  这会导致所有参与死锁的线程都永久阻塞，无法继续执行。

以下是几种常见的 Java 多线程死锁情况，并附带代码示例：

**1. 互相持有锁的情况:**

这是最常见的一种死锁情况。两个线程分别持有不同的锁，并且都需要获取对方持有的锁才能继续执行，最终导致互相等待，形成死锁。

```java
public class DeadlockExample1 {
    private static Object lock1 = new Object();
    private static Object lock2 = new Object();

    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            synchronized (lock1) {
                System.out.println("Thread 1: Holding lock 1...");
                try {
                    Thread.sleep(1000); // 模拟一些工作
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock2) {
                    System.out.println("Thread 1: Holding lock 2...");
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (lock2) {
                System.out.println("Thread 2: Holding lock 2...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock1) {
                    System.out.println("Thread 2: Holding lock 1...");
                }
            }
        });

        thread1.start();
        thread2.start();
    }
}
```

在这个例子中，`thread1` 先获得 `lock1`，然后尝试获取 `lock2`，而 `thread2` 先获得 `lock2`，然后尝试获取 `lock1`。  如果 `thread1` 和 `thread2` 几乎同时运行，就可能发生死锁。

**2.  循环依赖的情况:**

多个线程之间形成循环依赖，每个线程都持有某个锁，并等待另一个线程释放它所持有的锁，形成一个闭环。

```java
public class DeadlockExample2 {
    private static Object lock1 = new Object();
    private static Object lock2 = new Object();
    private static Object lock3 = new Object();

    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            synchronized (lock1) {
                synchronized (lock2) {
                    synchronized (lock3) {
                        // ...
                    }
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (lock3) {
                synchronized (lock1) {
                    synchronized (lock2) {
                        // ...
                    }
                }
            }
        });
        // ... more threads with different lock acquisition order forming a cycle ...

        thread1.start();
        thread2.start();
    }
}
```

这个例子中，如果有多个线程以不同的顺序获取这三个锁，就可能形成循环依赖并导致死锁。

**3.  与 I/O 操作结合的情况:**

线程在等待 I/O 操作完成的同时持有锁，而其他线程需要获取这个锁，导致死锁。  这种情况常常发生在网络编程或者数据库操作中。

**避免死锁的方法:**

- **避免循环依赖:**  仔细设计代码，避免多个线程之间形成循环依赖的锁获取顺序。
- **使用一致的锁获取顺序:**  如果多个线程需要访问多个锁，确保所有线程都以相同的顺序获取这些锁。
- **使用超时机制:**  在尝试获取锁时设置超时时间，如果在超时时间内无法获取锁，则进行适当的处理，例如重试或放弃。
- **避免在持有锁时进行 I/O 操作:**  将 I/O 操作与锁获取分开，避免阻塞其他线程。
- **使用更高级的锁机制:**  例如 `ReentrantReadWriteLock`，可以提高并发性。
- **死锁检测工具:**  使用一些工具来检测和诊断死锁问题。

记住，死锁是一个复杂的问题，需要仔细分析代码和线程的执行流程才能有效避免。  上面只是一些常见的死锁情况，实际情况可能更复杂。  良好的代码设计和测试至关重要。



#### 死锁示例和排查

示例代码：

```java
public class DeadLockTests {
    /**
     * 演示三条线程死锁情况
     */
    @Test
    public void test() {
        ExecutorService executor = Executors.newCachedThreadPool();

        Object object1 = new Object();
        Object object2 = new Object();
        Object object3 = new Object();

        CompletableFuture<Void> completableFuture1 = CompletableFuture.runAsync(() -> {
            synchronized (object1) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ignored) {
                }
                synchronized (object2) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException ignored) {
                    }
                    synchronized (object3) {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            }
        }, executor);
        CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(() -> {
            synchronized (object2) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ignored) {
                }
                synchronized (object1) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException ignored) {
                    }
                    synchronized (object3) {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            }
        }, executor);
        CompletableFuture<Void> completableFuture3 = CompletableFuture.runAsync(() -> {
            synchronized (object3) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ignored) {
                }
                synchronized (object2) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException ignored) {
                    }
                    synchronized (object1) {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            }
        }, executor);
        CompletableFuture.allOf(completableFuture1, completableFuture2, completableFuture3).join();

        executor.shutdown();
    }
}
```

排查：

- 获取 java 进程 id

  ```bash
  jps -l
  ```

- 查看线程死锁信息，其中 xxx 为 jps -l 查到的进程 id

  ```bash
  jstack xxx
  ```

- jstack 线程死锁信息如下：

  ```
  Found one Java-level deadlock:
  =============================
  "pool-1-thread-1":
    waiting to lock monitor 0x00007ff804000fd0 (object 0x0000000715d7e6b8, a java.lang.Object),
    which is held by "pool-1-thread-2"
  
  "pool-1-thread-2":
    waiting to lock monitor 0x00007ff7f8000e70 (object 0x0000000715d7e6a8, a java.lang.Object),
    which is held by "pool-1-thread-1"
  
  Java stack information for the threads listed above:
  ===================================================
  "pool-1-thread-1":
  	at com.future.demo.DeadLockTests.lambda$test$0(DeadLockTests.java:30)
  	- waiting to lock <0x0000000715d7e6b8> (a java.lang.Object)
  	- locked <0x0000000715d7e6a8> (a java.lang.Object)
  	at com.future.demo.DeadLockTests$$Lambda$40/0x00007ff840015000.run(Unknown Source)
  	at java.util.concurrent.CompletableFuture$AsyncRun.run(java.base@17.0.11/CompletableFuture.java:1804)
  	at java.util.concurrent.ThreadPoolExecutor.runWorker(java.base@17.0.11/ThreadPoolExecutor.java:1136)
  	at java.util.concurrent.ThreadPoolExecutor$Worker.run(java.base@17.0.11/ThreadPoolExecutor.java:635)
  	at java.lang.Thread.run(java.base@17.0.11/Thread.java:842)
  "pool-1-thread-2":
  	at com.future.demo.DeadLockTests.lambda$test$1(DeadLockTests.java:50)
  	- waiting to lock <0x0000000715d7e6a8> (a java.lang.Object)
  	- locked <0x0000000715d7e6b8> (a java.lang.Object)
  	at com.future.demo.DeadLockTests$$Lambda$41/0x00007ff840015228.run(Unknown Source)
  	at java.util.concurrent.CompletableFuture$AsyncRun.run(java.base@17.0.11/CompletableFuture.java:1804)
  	at java.util.concurrent.ThreadPoolExecutor.runWorker(java.base@17.0.11/ThreadPoolExecutor.java:1136)
  	at java.util.concurrent.ThreadPoolExecutor$Worker.run(java.base@17.0.11/ThreadPoolExecutor.java:635)
  	at java.lang.Thread.run(java.base@17.0.11/Thread.java:842)
  
  Found 1 deadlock.
  
  ```

  - 显示线程 pool-1-thread-1 和线程 pool-1-thread-2 相互等待。



## Future

### 介绍

Java `Future` 代表一个异步计算的结果。它允许你启动一个异步操作，然后稍后获取其结果，而无需阻塞当前线程。  这对于执行耗时操作（例如网络请求或复杂的计算）非常有用，可以提高应用程序的响应能力和吞吐量。

以下是关于 Java `Future` 的一些关键方面：

**核心概念:**

* **异步计算:**  `Future` 的核心在于它允许你启动一个任务并在后台运行，而无需等待其完成。
* **结果获取:**  一旦异步计算完成，你可以使用 `Future` 的方法获取结果。
* **取消操作:**  你可以尝试取消一个正在进行的异步操作。
* **异常处理:**  你可以通过 `Future` 获取异步操作中发生的异常。


**主要方法:**

* `boolean cancel(boolean mayInterruptIfRunning)`:  尝试取消任务。`mayInterruptIfRunning` 参数指示是否允许中断正在运行的任务。
* `boolean isCancelled()`:  检查任务是否已被取消。
* `boolean isDone()`:  检查任务是否已完成（成功完成、取消或异常终止）。
* `V get()`  和 `V get(long timeout, TimeUnit unit)`:  获取任务的结果。`get()` 会阻塞直到结果可用，而 `get(long timeout, TimeUnit unit)` 会在指定超时时间后抛出 `TimeoutException`。
* `void addListener(Runnable listener, Executor executor)`:  在任务完成时执行一个监听器。


**常见的实现:**

* `java.util.concurrent.Future` 接口:  定义了 `Future` 的核心方法。
* `java.util.concurrent.FutureTask`:  `Future` 的一个具体实现，它可以包装一个 `Callable` 或 `Runnable` 任务。


**如何使用:**

最常见的用法是结合 `ExecutorService` 使用：

```java
ExecutorService executor = Executors.newCachedThreadPool(); // 创建线程池

Future<Integer> future = executor.submit(new Callable<Integer>() {
    @Override
    public Integer call() throws Exception {
        // 执行耗时操作
        Thread.sleep(2000);
        return 10;
    }
});

try {
    // 获取结果，最多等待 5 秒
    Integer result = future.get(5, TimeUnit.SECONDS);
    System.out.println("Result: " + result);
} catch (InterruptedException e) {
    System.out.println("Interrupted: " + e.getMessage());
} catch (ExecutionException e) {
    System.out.println("Execution Exception: " + e.getCause().getMessage());
} catch (TimeoutException e) {
    System.out.println("Timeout: " + e.getMessage());
} finally {
    executor.shutdown(); // 关闭线程池
}
```

这段代码提交了一个耗时 2 秒的任务到线程池，然后尝试获取结果，最多等待 5 秒。  它还处理了 `InterruptedException`, `ExecutionException` 和 `TimeoutException` 异常。


**总结:**

`Future` 是 Java 并发编程中一个强大的工具，它允许你编写更有效率、更响应迅速的应用程序。  理解其核心概念和使用方法对于构建高性能的并发应用至关重要。  记住要妥善处理异常并关闭 `ExecutorService` 以避免资源泄漏。



### 引入 Future 特性的演变过程

让我们通过一个例子说明 Java 在引入 `Future` 特性前后的演变过程，以一个需要下载多个文件的场景为例：

**方法一：同步下载（无 Future，阻塞主线程）**

在 `Future` 出现之前，如果需要下载多个文件，通常的做法是顺序下载，每个文件下载完成后再下载下一个。 这会导致主线程被阻塞，直到所有文件下载完成。

```java
public class SynchronousDownloader {

    public static void downloadFile(String url) throws IOException {
        // 模拟下载过程，耗时操作
        System.out.println("Downloading: " + url);
        Thread.sleep(2000); // 模拟下载耗时 2 秒
        System.out.println("Downloaded: " + url);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String[] urls = {"url1", "url2", "url3"};
        for (String url : urls) {
            downloadFile(url);
        }
        System.out.println("All files downloaded.");
    }
}
```

这段代码会顺序下载三个文件，每个文件下载需要 2 秒，总共需要 6 秒才能完成。  在此期间，程序无法响应任何其他请求。


**方法二：使用线程，但缺乏集中管理（改进，但仍复杂）**

为了避免阻塞主线程，我们可以使用多个线程来并发下载文件。  但是，我们需要自己管理线程，并等待所有线程完成。

```java
public class MultithreadedDownloader {

    public static void downloadFile(String url) throws IOException, InterruptedException {
        // 模拟下载过程
        System.out.println("Downloading: " + url + " on thread " + Thread.currentThread().getName());
        Thread.sleep(2000);
        System.out.println("Downloaded: " + url + " on thread " + Thread.currentThread().getName());
    }

    public static void main(String[] args) throws InterruptedException {
        String[] urls = {"url1", "url2", "url3"};
        Thread[] threads = new Thread[urls.length];
        for (int i = 0; i < urls.length; i++) {
            int finalI = i; // for lambda expression
            threads[i] = new Thread(() -> {
                try {
                    downloadFile(urls[finalI]);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
            threads[i].start();
        }

        // 等待所有线程完成 (比较笨拙的方法)
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("All files downloaded.");
    }
}
```

虽然并发下载，但是`join()`方法需要显式等待每个线程完成，不够优雅，且错误处理也较为繁琐。


**方法三：使用 ExecutorService 和 Future（推荐，利用 Future 特性）**

使用 `ExecutorService` 和 `Future` 可以更优雅地处理异步下载。

```java
import java.util.concurrent.*;

public class FutureDownloader {

    public static Integer downloadFile(String url) throws IOException, InterruptedException {
        // 模拟下载过程
        System.out.println("Downloading: " + url + " on thread " + Thread.currentThread().getName());
        Thread.sleep(2000);
        System.out.println("Downloaded: " + url + " on thread " + Thread.currentThread().getName());
        return 1; // 返回一个简单的结果
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String[] urls = {"url1", "url2", "url3"};
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<Integer>> futures = new ArrayList<>();

        for (String url : urls) {
            Future<Integer> future = executor.submit(() -> {
                try {
                    return downloadFile(url);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    return 0; // indicate failure
                }
            });
            futures.add(future);
        }

        for (Future<Integer> future : futures) {
            //  优雅地处理结果和异常
            try {
                Integer result = future.get(); // 阻塞直到完成
                System.out.println("Download result: " + result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        System.out.println("All files downloaded.");
    }
}

```

这段代码使用 `ExecutorService` 来管理线程，使用 `Future` 来获取每个下载任务的结果。 主线程不会被阻塞，可以继续执行其他任务。  错误处理也更加集中和优雅。  这体现了 `Future`  带来的优势：简化了异步编程，提高了代码的可读性和可维护性。  总耗时依然约为2秒（因为是并发下载），但主线程不会等待6秒。

这个例子展示了 Java 在并发编程方面的演变，从简单的同步阻塞到使用线程，最终利用 `Future` 和 `ExecutorService` 实现更高效、更优雅的异步编程方式。  `Future` 简化了异步操作的管理和结果获取，显著提升了程序的性能和可维护性。



### FutureTask

FutureTask 本质是 Future+Callable+Thread 有返回值



#### 基本用法

```java
// region Future+Callable+Thread 有返回值

FutureTask<String> futureTask = new FutureTask<>(new Callable<String>() {
    @Override
    public String call() throws Exception {
        TimeUnit.SECONDS.sleep(1);
        return "Hello world!";
    }
});
Thread thread = new Thread(futureTask);
thread.start();

// 主线程继续执行其他任务

String result = futureTask.get();
Assert.assertEquals("Hello world!", result);

// endregion
```



#### 结合线程池提升性能

```java
// region 结合线程池提升性能

ExecutorService executorService = Executors.newCachedThreadPool();

StopWatch stopWatch = new StopWatch();
stopWatch.start();

FutureTask<Void> futureTask1 = new FutureTask<>(() -> {
    TimeUnit.SECONDS.sleep(1);
    return null;
});
List<Future<Void>> futureList = new ArrayList<>();
for (int i = 0; i < 10; i++) {
    Future future = executorService.submit(futureTask1);
    futureList.add(future);
}

for (Future<Void> future : futureList) {
    future.get();
}

executorService.shutdown();

stopWatch.stop();
Assert.assertTrue(String.valueOf(stopWatch.getTotalTimeMillis()), stopWatch.getTotalTimeMillis() <= 1200);

// endregion
```



### CompletableFuture

提示：

- CompletableFuture 可以替代 Future 使用。



#### CompletableFuture 解决了 Future 的什么问题呢？

Java `CompletableFuture` 在 `Future` 接口的基础上做了很大的改进，主要解决了以下 `Future` 的不足：

1. **缺乏组合操作:**  `Future` 接口本身只提供获取结果 (`get()`) 和检查是否完成 (`isDone()`) 的方法，缺乏组合多个 `Future` 的机制。  这意味着如果你需要对多个异步操作的结果进行组合处理，需要编写大量的样板代码来处理回调和线程同步。

2. **简单的错误处理:** `Future` 的错误处理比较简单，通常只能通过 `get()` 方法抛出异常来处理。  这使得处理多个异步操作的异常变得非常复杂和困难。

3. **阻塞等待结果:**  `Future.get()` 方法是阻塞式的，这意味着如果需要等待一个 `Future` 完成，当前线程会被阻塞，直到 `Future` 完成或者超时。这在需要处理多个异步操作时，容易导致性能问题。


让我们用例子来说明：

**使用 Future 的方式（问题多多）：**

假设我们需要从两个不同的数据库获取数据，然后将结果合并：

```java
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureExample {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<String> future1 = executor.submit(() -> {
            // 模拟从数据库1获取数据
            Thread.sleep(1000);
            return "Data from DB1";
        });

        Future<String> future2 = executor.submit(() -> {
            // 模拟从数据库2获取数据
            Thread.sleep(1500);
            return "Data from DB2";
        });

        String result1 = future1.get();
        String result2 = future2.get(); // 这里会阻塞等待future2完成

        System.out.println("Combined result: " + result1 + " and " + result2);

        executor.shutdown();
    }
}
```

这段代码存在以下问题：

* **阻塞:** `future1.get()` 和 `future2.get()` 都是阻塞式的，程序必须等待每个 `Future` 完成才能继续执行。如果其中一个 `Future` 执行时间较长，整个程序都会被阻塞。
* **组合困难:**  合并结果 `result1` 和 `result2` 的逻辑非常简单，但如果需要进行更复杂的组合操作，代码会变得非常复杂。
* **错误处理不足:**  这段代码没有处理异常。如果其中一个 `Future` 抛出异常，`get()` 方法会直接抛出 `ExecutionException`，需要用 `try-catch` 块来捕获。


**使用 CompletableFuture 的方式（优雅高效）：**

使用 `CompletableFuture` 可以优雅地解决以上问题：

```java
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletableFutureExample {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
                return "Data from DB1";
            } catch (InterruptedException e) {
                throw new RuntimeException(e); // 使用 CompletableFuture 可以更好地处理异常
            }
        }, executor);

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1500);
                return "Data from DB2";
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, executor);

        // 使用 thenCombine 组合两个 CompletableFuture
        String combinedResult = future1.thenCombine(future2, (r1, r2) -> r1 + " and " + r2).get();

        System.out.println("Combined result: " + combinedResult);

        executor.shutdown();
    }
}

```

这段代码使用 `thenCombine` 方法优雅地组合了两个 `CompletableFuture`，避免了阻塞等待和复杂的错误处理逻辑。  `CompletableFuture` 提供了非阻塞的方式处理异步结果，并提供了更好的错误处理机制，使得异步编程更加简单、高效和健壮。  它不仅仅是 `Future` 的一个改进，更是一个功能强大的异步编程框架。



#### 用法 - 使用 CompletableFuture.runAsync 和 CompletableFuture.supplyAsync 创建 CompletableFuture

```java
// region 使用 CompletableFuture.runAsync 和 CompletableFuture.supplyAsync 创建 CompletableFuture

ExecutorService executor = Executors.newCachedThreadPool();

// 没有返回值的 CompletableFuture
AtomicInteger counter = new AtomicInteger();
CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
    try {
        TimeUnit.SECONDS.sleep(1);
        counter.incrementAndGet();
    } catch (InterruptedException ignored) {

    }
}, executor);
Assert.assertNull(completableFuture.get());
Assert.assertEquals(1, counter.get());

// 有返回值 CompletableFuture
AtomicInteger counter1 = new AtomicInteger();
CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(() -> {
    try {
        TimeUnit.SECONDS.sleep(1);
        counter1.incrementAndGet();
    } catch (InterruptedException ignored) {

    }
    return "Hello world!";
}, executor);
Assert.assertEquals("Hello world!", completableFuture1.get());
Assert.assertEquals(1, counter1.get());

executor.shutdown();

// endregion
```



#### 用法 - CompletableFuture whenComplete 和 exceptionally 回调

whenComplete 业务方法处理完毕回调、exceptionally 异常处理回调

```java
// region CompletableFuture whenComplete 和 exceptionally 回调

executor = Executors.newCachedThreadPool();

// CompletableFuture whenComplete 业务方法处理完毕回调
AtomicInteger counter2 = new AtomicInteger();
CompletableFuture<String> completableFuture2 = CompletableFuture.supplyAsync(() -> {
    try {
        TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException ignored) {

    }
    return "Hello world!";
}, executor).whenComplete((data, exception) -> {
    counter2.incrementAndGet();
});
Assert.assertEquals("Hello world!", completableFuture2.get());
Assert.assertEquals(1, counter2.get());

// CompletableFuture exceptionally 异常处理回调
AtomicInteger counter3 = new AtomicInteger();
AtomicInteger counter4 = new AtomicInteger();
CompletableFuture<String> completableFuture3 = CompletableFuture.supplyAsync(() -> {
    try {
        TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException ignored) {

    }

    boolean b = true;
    if (b) {
        throw new RuntimeException(new Exception("testing"));
    }

    return "Hello world!";
}, executor).whenComplete((data, exception) -> {
    counter3.incrementAndGet();
}).exceptionally(exception -> {
    counter4.incrementAndGet();
    return null;
});
Assert.assertNull(completableFuture3.get());
Assert.assertEquals(1, counter3.get());
Assert.assertEquals(1, counter4.get());

executor.shutdown();

// endregion
```



#### 电商比价案例

结论：使用 CompletableFuture 并发比不使用并发性能好。

```java
// region 电商比价案例

List<NetMall> netMallList = Arrays.asList(
        NetMall.builder().name("jd").price(BigDecimal.valueOf(101.11)).build(),
        NetMall.builder().name("tmall").price(BigDecimal.valueOf(91.21)).build(),
        NetMall.builder().name("taobao").price(BigDecimal.valueOf(121.00)).build(),
        NetMall.builder().name("pingduoduo").price(BigDecimal.valueOf(87.32)).build()

);

// 不并发
StopWatch stopWatch = new StopWatch();
stopWatch.start();

List<String> stringList = netMallList.stream().map(o -> String.format("%s 价格为 %f", o.getName(), o.getPrice())).collect(Collectors.toList());
log.debug(String.join(",", stringList));

stopWatch.stop();
// 不使用并发耗时较长
Assert.assertTrue(String.valueOf(stopWatch.getTotalTimeMillis()), stopWatch.getTotalTimeMillis() >= 4000 && stopWatch.getTotalTimeMillis() <= 4100);

// 使用 CompletableFuture 并发
executor = Executors.newCachedThreadPool();

stopWatch = new StopWatch();
stopWatch.start();

ExecutorService finalExecutor = executor;
stringList = netMallList.stream().map(o -> CompletableFuture.supplyAsync(() -> String.format("%s 价格为 %f", o.getName(), o.getPrice()), finalExecutor)).collect(Collectors.toList())
        .stream().map(CompletableFuture::join).collect(Collectors.toList());
log.debug(String.join(",", stringList));

stopWatch.stop();
// 使用 CompletableFuture 并发耗时为最长的任务时间
Assert.assertTrue(String.valueOf(stopWatch.getTotalTimeMillis()), stopWatch.getTotalTimeMillis() >= 1000 && stopWatch.getTotalTimeMillis() <= 1100);

executor.shutdown();

// endregion
```



#### 用法 - 获取结果

让我们详细比较 `CompletableFuture` 的四个获取结果的方法：`get()`、`get(long timeout, TimeUnit unit)`、`join()` 和 `getNow(T valueIfAbsent)`。 它们的主要区别在于阻塞行为、超时机制和异常处理方式。

**1. `get()`:**

* **阻塞行为:**  阻塞调用线程，直到 `CompletableFuture` 完成或被中断。
* **超时:**  没有超时机制。  如果 `CompletableFuture` 永远不会完成，`get()` 方法将无限期阻塞。
* **异常处理:**  抛出 `ExecutionException`（如果异步计算抛出异常）和 `InterruptedException`（如果当前线程在等待时被中断）。  `ExecutionException` 包装了原始异常，可以使用 `.getCause()` 方法访问原始异常。
* **适用场景:**  需要获取结果，并且可以接受阻塞，并且知道异步操作一定会完成（或有其他的机制来处理长时间不完成的情况）。

```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");
try {
    String result = future.get();
    System.out.println(result);
} catch (InterruptedException | ExecutionException e) {
    e.printStackTrace();
}
```

**2. `get(long timeout, TimeUnit unit)`:**

* **阻塞行为:**  阻塞调用线程，但最多阻塞指定的时间。
* **超时:**  拥有超时机制，允许指定等待的时间和时间单位。  如果在超时前 `CompletableFuture` 完成，则返回结果；如果超时，则抛出 `TimeoutException`。
* **异常处理:**  抛出 `ExecutionException`、`InterruptedException` 和 `TimeoutException`。
* **适用场景:**  需要获取结果，但不能接受无限期阻塞，需要设置超时时间，例如在处理用户请求时设置超时来避免长时间等待。

```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    try {
        Thread.sleep(2000); // 模拟耗时操作
        return "Hello, Timeout!";
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
});

try {
    String result = future.get(1, TimeUnit.SECONDS); // 等待 1 秒
    System.out.println(result);
} catch (InterruptedException | ExecutionException | TimeoutException e) {
    System.err.println("Error or timeout: " + e.getMessage());
}
```


**3. `join()`:**

* **阻塞行为:**  阻塞调用线程，直到 `CompletableFuture` 完成。
* **超时:**  没有超时机制。  与 `get()` 方法类似，如果 `CompletableFuture` 永远不会完成，`join()` 方法将无限期阻塞。
* **异常处理:**  将异步计算抛出的任何异常重新抛出。  这通常是未经检查的异常（unchecked exceptions）。
* **适用场景:**  与 `get()` 类似，但更简洁，因为它直接抛出原始异常，而不是包装在 `ExecutionException` 中。


```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");
String result = future.join();
System.out.println(result); // 直接抛出原始异常
```

**4. `getNow(T valueIfAbsent)`:**

* **阻塞行为:**  **不阻塞**。
* **超时:**  没有超时机制，因为根本不等待。
* **异常处理:**  不抛出异常。  如果 `CompletableFuture` 尚未完成，则返回提供的 `valueIfAbsent`。
* **适用场景:**  不需要等待结果，可以接受使用默认值。  如果异步操作可能需要很长时间才能完成，这对于快速响应或避免阻塞非常有用。

```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    try {
        Thread.sleep(2000);
        return "Hello, getNow!";
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
});

String result = future.getNow("Default Value"); // 立即返回，不阻塞
System.out.println(result); // 可能输出 "Default Value" 或 "Hello, getNow!"
```


**总结:**

| 方法            | 阻塞 | 超时 | 异常处理                                                     | 适用场景                                     |
| --------------- | ---- | ---- | ------------------------------------------------------------ | -------------------------------------------- |
| `get()`         | 是   | 否   | `ExecutionException`, `InterruptedException`                 | 需要结果，可阻塞，异步操作一定会完成         |
| `get(timeout)`  | 是   | 是   | `ExecutionException`, `InterruptedException`, `TimeoutException` | 需要结果，不可无限阻塞，异步操作可能不完成   |
| `join()`        | 是   | 否   | 直接抛出原始异常                                             | 需要结果，可阻塞，异步操作一定会完成，更简洁 |
| `getNow(value)` | 否   | 否   | 不抛出异常，返回默认值                                       | 不需要立即结果，可以接受默认值，避免阻塞     |

选择哪种方法取决于你的应用场景和对阻塞、超时以及异常处理的需求。  在高性能应用中，应该尽量避免阻塞线程，除非必要。  `getNow()` 非常适合于那些不需要立即获取结果的场景。  `get(timeout)` 提供了很好的平衡，允许在必要时设置超时以防止无限期等待。

**示例：**

```java
// region 获取结果

executor = Executors.newCachedThreadPool();

// get() 方法：等待直到有返回值
CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
    try {
        TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException ignored) {
    }
    return "Hello world!";
}, executor);
Assert.assertEquals("Hello world!", stringCompletableFuture.get());

// get(long timeout, TimeUnit unit) 方法：等待超时抛出 TimeoutException
stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
    try {
        TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException ignored) {
    }
    return "Hello world!";
}, executor);
String result = null;
try {
    result = stringCompletableFuture.get(500, TimeUnit.MILLISECONDS);
    Assert.fail();
} catch (TimeoutException ignored) {
    Assert.assertNull(result);
}

// join() 方法：与 `get()` 类似，但更简洁，因为它直接抛出原始异常，而不是包装在 `ExecutionException` 中。
stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
    try {
        TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException ignored) {
    }
    return "Hello world!";
}, executor);
Assert.assertEquals("Hello world!", stringCompletableFuture.join());

// getNow(T valueIfAbsent) 方法：不需要等待结果，可以接受使用默认值。  如果异步操作可能需要很长时间才能完成，这对于快速响应或避免阻塞非常有用。
stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
    try {
        TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException ignored) {

    }
    return "Hello world!";
}, executor);
Assert.assertEquals("Absent", stringCompletableFuture.getNow("Absent"));
TimeUnit.MILLISECONDS.sleep(1050);
Assert.assertEquals("Hello world!", stringCompletableFuture.getNow("Absent"));

// get()+complete(T value) 方法：complete(T value) 用于手动完成 CompletableFuture，并将结果传递给它。让 get() 等待中断
stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
    try {
        TimeUnit.SECONDS.sleep(2);
    } catch (InterruptedException ignored) {

    }
    return "Hello world!";
}, executor);
CompletableFuture<String> finalStringCompletableFuture = stringCompletableFuture;
executor.submit(() -> {
    // 提前中断 CompletableFuture.get() 等待
    try {
        TimeUnit.MILLISECONDS.sleep(200);
    } catch (InterruptedException ignored) {

    }
    finalStringCompletableFuture.complete("complete signal");
});
Assert.assertEquals("complete signal", stringCompletableFuture.get());

executor.shutdown();

// endregion
```



#### 用法 - 对结果处理方式

在 Java 中，`CompletableFuture` 提供了一种强大而灵活的方式来处理异步编程。它允许你通过不同的回调方式来处理异步计算的结果或异常。以下是几种常见的 `CompletableFuture` 回调处理方式：

1. **thenAccept**：当 `CompletableFuture` 完成时，执行一个接受结果的消费者（Consumer）。如果 `CompletableFuture` 异常完成，则回调不会被调用。

   ```java
   CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
       // 模拟异步计算
       return "Result";
   }).thenAccept(result -> {
       System.out.println("Result: " + result);
   });
   ```

2. **thenApply**：当 `CompletableFuture` 完成时，应用一个函数（Function）到结果上，并返回一个新的 `CompletableFuture`。

   ```java
   CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Result")
       .thenApply(result -> result + " Processed");
   future.thenAccept(processedResult -> {
       System.out.println("Processed Result: " + processedResult);
   });
   ```

3. **thenCompose**：当 `CompletableFuture` 完成时，应用一个函数（Function）到结果上，该函数返回另一个 `CompletableFuture`，并返回这个新的 `CompletableFuture`。

   ```java
   CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Result")
       .thenCompose(result -> CompletableFuture.supplyAsync(() -> result + " Further Processed"));
   future.thenAccept(furtherProcessedResult -> {
       System.out.println("Further Processed Result: " + furtherProcessedResult);
   });
   ```

4. **thenRun**：当 `CompletableFuture` 完成时，执行一个无参数的 Runnable。它不关心结果。

   ```java
   CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> "Result")
       .thenRun(() -> System.out.println("Computation completed"));
   ```

5. **exceptionally**：当 `CompletableFuture` 异常完成时，执行一个函数（Function）来处理异常，并返回一个新的结果。

   ```java
   CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
       throw new RuntimeException("Error");
   }).exceptionally(ex -> {
       return "Fallback Result";
   });
   future.thenAccept(result -> {
       System.out.println("Result: " + result);
   });
   ```

6. **handle**：当 `CompletableFuture` 完成时，无论正常还是异常，都执行一个双函数（BiFunction），它接受结果和异常（如果有的话），并返回一个新的结果。

   ```java
   CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
       throw new RuntimeException("Error");
   }).handle((result, ex) -> {
       if (ex != null) {
           return "Handled Error: " + ex.getMessage();
       } else {
           return "Result: " + result;
       }
   });
   future.thenAccept(handledResult -> {
       System.out.println("Handled Result: " + handledResult);
   });
   ```

7. **whenComplete**：当 `CompletableFuture` 完成时，无论正常还是异常，都执行一个双消费者（BiConsumer），它接受结果和异常（如果有的话）。

   ```java
   CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> "Result")
       .whenComplete((result, ex) -> {
           if (ex != null) {
               System.out.println("Error: " + ex.getMessage());
           } else {
               System.out.println("Result: " + result);
           }
       });
   ```

8. **join** 和 **get**：阻塞当前线程直到 `CompletableFuture` 完成，并获取结果。通常不推荐在异步代码中直接使用这些方法，因为它们会破坏异步性。

   ```java
   try {
       CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Result");
       String result = future.join(); // 或者 future.get();
       System.out.println("Result: " + result);
   } catch (InterruptedException | ExecutionException e) {
       e.printStackTrace();
   }
   ```

这些回调方法提供了丰富的组合能力，使得你可以灵活地处理异步计算的结果和异常。根据具体的需求，你可以选择最适合的方式来处理 `CompletableFuture`。

**示例：**

```java
// region 对结果处理方式

executor = Executors.newCachedThreadPool();

// thenApply() 系列方法: 用于对完成的结果进行转换。它们接受一个函数作为参数，该函数将 CompletableFuture 的结果作为输入，并返回一个新的 CompletionStage，其结果是应用了该函数后的结果。thenApplyAsync() 会异步执行转换函数。
stringCompletableFuture = CompletableFuture.supplyAsync(() -> "s1", executor).thenApply(v -> v + ":s2").thenApply(v -> v + ":s3");
Assert.assertEquals("s1:s2:s3", stringCompletableFuture.get());
// 异常发生时立刻终止执行后续的 thenApply
stringCompletableFuture = CompletableFuture.supplyAsync(() -> "s1", executor).thenApply(v -> {
    boolean b = true;
    if (b) {
        throw new RuntimeException(new Exception("testing"));
    }
    return v + ":s2";
}).thenApply(v -> v + ":s3").exceptionally(e -> null);
Assert.assertNull(stringCompletableFuture.get());

// handle() 方法：和 thenApply() 方法类似，但当 `CompletableFuture` 完成时，无论正常还是异常，都执行一个双函数（BiFunction），它接受结果和异常（如果有的话），并返回一个新的结果。
stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
    boolean b = true;
    if (b) {
        throw new RuntimeException(new Exception("testing"));
    }
    return "s1";
}, executor).handle((v, e) -> v + ":s2").handle((v, e) -> v + ":s3").exceptionally(e -> null);
Assert.assertEquals("null:s2:s3", stringCompletableFuture.get());

// **thenAccept**：当 `CompletableFuture` 完成时，执行一个接受结果的消费者（Consumer）。如果 `CompletableFuture` 异常完成，则回调不会被调用。
List<String> signalList = new ArrayList<>();
CompletableFuture<Void> voidCompletableFuture = CompletableFuture.supplyAsync(() -> "s1", executor).thenAccept(signalList::add);
voidCompletableFuture.get();
Assert.assertEquals(1, signalList.size());
Assert.assertEquals("s1", signalList.get(0));

// **thenRun**：当 `CompletableFuture` 完成时，执行一个无参数的 Runnable。它不关心结果。
signalList = new ArrayList<>();
List<String> finalSignalList = signalList;
voidCompletableFuture = CompletableFuture.supplyAsync(() -> "s1", executor).thenRun(() -> {
    finalSignalList.add("step1");
});
voidCompletableFuture.get();
Assert.assertEquals(1, signalList.size());
Assert.assertEquals("step1", signalList.get(0));

executor.shutdown();

// endregion
```



#### 用法 - 对结果处理方式 - applyToEither

`applyToEither()` 方法用于两个 CompletableFuture 中，任何一个先完成就执行指定的函数，并返回一个新的 CompletableFuture。它不关心哪个 CompletableFuture 先完成，只关心第一个完成的结果。

`applyToEither()` 有两个版本：

* **`applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn)`:**  同步版本。当 `this` CompletableFuture 或 `other` CompletableFuture 中任意一个首先完成时，将完成的结果作为参数传递给 `fn` 函数执行。返回一个新的 CompletableFuture，其结果是 `fn` 函数的返回值。

* **`applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn)`:** 异步版本。与同步版本类似，但是 `fn` 函数会在另一个线程中执行。返回一个新的 CompletableFuture，其结果是 `fn` 函数的返回值。还可以选择指定一个 Executor 来执行 `fn` 函数。 `applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn, Executor executor)`


**示例：**

```java
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ApplyToEitherExample {

    public static void main(String[] args) {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                return "Future 1 Result";
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                return "Future 2 Result";
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<String> combinedFuture = future1.applyToEither(future2, result -> {
            System.out.println("First completed future result: " + result);
            return result.toUpperCase();
        });

        System.out.println("Result of combined future: " + combinedFuture.join()); // 输出 Future 2 Result 的大写形式
    }
}

```

在这个例子中，`future2` 比 `future1` 先完成，所以 `applyToEither` 使用 `future2` 的结果 `"Future 2 Result"` 调用了提供的函数，并将结果转换为大写。


**关键点:**

* `applyToEither()` 只会执行一次，使用第一个完成的 CompletableFuture 的结果。
*  如果两个 CompletableFuture 都抛出异常，则新的 CompletableFuture 将以第一个抛出的异常完成 exceptionally。

希望这个解释能够帮助你理解 `applyToEither()` 的用法。 如果你还有其他问题，请随时提出。

**示例：**

```java
// region applyToEither `applyToEither()` 方法用于两个 CompletableFuture 中，任何一个先完成就执行指定的函数，并返回一个新的 CompletableFuture。它不关心哪个 CompletableFuture 先完成，只关心第一个完成的结果。

executor = Executors.newCachedThreadPool();

CompletableFuture<String> player1 = CompletableFuture.supplyAsync(() -> {
    try {
        TimeUnit.SECONDS.sleep(2);
    } catch (InterruptedException ignored) {

    }
    return "Player1";
}, executor);

CompletableFuture<String> player2 = CompletableFuture.supplyAsync(() -> {
    try {
        TimeUnit.SECONDS.sleep(3);
    } catch (InterruptedException ignored) {

    }
    return "Player2";
}, executor);

CompletableFuture<String> completableFuture4 = player1.applyToEither(player2, v -> v + " 胜出");
// Player1 比 Player2 快，所以 Player1 胜出
Assert.assertEquals("Player1 胜出", completableFuture4.get());

executor.shutdown();

// endregion
```



#### 用法 - 对结果处理方式 - thenCombine

`thenCombine()` 方法用于将两个 CompletableFuture 的结果合并成一个新的 CompletableFuture。它会在两个 CompletableFuture 都完成后，将它们的结果作为参数传递给指定的 BiFunction，并将 BiFunction 的返回值作为新的 CompletableFuture 的结果。

`thenCombine()` 有两个版本：

* **`thenCombine(CompletionStage<? extends U> other, BiFunction<? super T,? super U,? extends V> fn)`:** 同步版本。当 `this` CompletableFuture 和 `other` CompletableFuture 都完成后，将它们的结果作为参数传递给 `fn` 函数执行。返回一个新的 CompletableFuture，其结果是 `fn` 函数的返回值。

* **`thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T,? super U,? extends V> fn)`:** 异步版本。与同步版本类似，但是 `fn` 函数会在另一个线程中执行。返回一个新的 CompletableFuture，其结果是 `fn` 函数的返回值。还可以选择指定一个 Executor 来执行 `fn` 函数。`thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T,? super U,? extends V> fn, Executor executor)`


**示例：**

```java
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ThenCombineExample {

    public static void main(String[] args) {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                return "Hello";
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                return "World";
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<String> combinedFuture = future1.thenCombine(future2, (result1, result2) -> {
            return result1 + " " + result2;
        });

        System.out.println("Result of combined future: " + combinedFuture.join()); // 输出 "Hello World"
    }
}
```

在这个例子中，`future1` 返回 "Hello"，`future2` 返回 "World"。`thenCombine` 将这两个结果传递给 BiFunction，BiFunction 将它们拼接成 "Hello World"，并作为新的 CompletableFuture 的结果返回。


**关键点:**

* `thenCombine()` 要求两个 CompletableFuture 都成功完成才能执行。如果任何一个 CompletableFuture 完成 exceptionally，则组合的 CompletableFuture 也会完成 exceptionally。
* BiFunction 的参数类型必须与两个 CompletableFuture 的结果类型兼容。


希望这个解释能够帮助你理解 `thenCombine()` 的用法。如果你还有其他问题，请随时提出。

**示例：**

```java
// region thenCombine：`thenCombine()` 方法用于将两个 CompletableFuture 的结果合并成一个新的 CompletableFuture。它会在两个 CompletableFuture 都完成后，将它们的结果作为参数传递给指定的 BiFunction，并将 BiFunction 的返回值作为新的 CompletableFuture 的结果。

executor = Executors.newCachedThreadPool();

CompletableFuture<List<Integer>> completableFuture51 = CompletableFuture.supplyAsync(() -> {
    try {
        TimeUnit.SECONDS.sleep(2);
    } catch (InterruptedException ignored) {

    }
    return new ArrayList<Integer>() {{
        this.add(1);
        this.add(2);
    }};
}, executor);
CompletableFuture<List<Integer>> completableFuture52 = CompletableFuture.supplyAsync(() -> {
    try {
        TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException ignored) {

    }
    return new ArrayList<Integer>() {{
        this.add(3);
        this.add(4);
    }};
}, executor);
CompletableFuture<List<Integer>> completableFuture53 = completableFuture51.thenCombine(completableFuture52, (a, b) -> {
    a.addAll(b);
    return a;
});
Assert.assertEquals(Arrays.asList(1, 2, 3, 4), completableFuture53.get());

executor.shutdown();

// endregion
```



#### 用法 - CompletableFuture allOf 和 anyOf

注意：参考下面示例即可，没有自己编写示例实验。

`CompletableFuture` 的 `allOf` 和 `anyOf` 方法都用于组合多个 `CompletableFuture`，但它们的行为有所不同：

**`allOf`:**

* **作用:**  等待所有输入的 `CompletableFuture` 都完成。  只有当所有 future 都完成（无论成功或失败）后，`allOf` 返回的 `CompletableFuture` 才会完成。
* **返回值:** 返回一个新的 `CompletableFuture`，其结果是一个 `void`，表示所有输入的 `CompletableFuture` 都已完成。你可以用 `thenRun`、`thenAccept` 或 `thenApply` 等方法在所有 future 完成后执行后续操作。
* **异常处理:** 如果任何一个输入的 `CompletableFuture` 抛出异常，该异常会被收集到返回的 `CompletableFuture` 中，你可以在 `exceptionally` 或 `handle` 方法中进行处理。  但 `allOf` 不会阻止其他 future 的执行。
* **用法示例:**

```java
CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
    // 模拟耗时操作1
    Thread.sleep(1000);
    return "Future 1";
});

CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
    // 模拟耗时操作2
    Thread.sleep(1500);
    return "Future 2";
});

CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
    // 模拟耗时操作3  故意抛出异常
    Thread.sleep(500);
    throw new RuntimeException("Future 3 failed!");
});

CompletableFuture<Void> allFutures = CompletableFuture.allOf(future1, future2, future3);

allFutures.join(); // 等待所有 future 完成

System.out.println("All futures completed.");

allFutures.handle((v,e)->{
    if(e!=null) System.out.println("Exception occured: " + e.getMessage());
    return null;
}).join();


```


**`anyOf`:**

* **作用:** 等待任意一个输入的 `CompletableFuture` 完成。只要任何一个 future 完成（无论是成功还是失败），`anyOf` 返回的 `CompletableFuture` 就会完成，并返回第一个完成的 future 的结果。
* **返回值:** 返回一个新的 `CompletableFuture`，其结果是第一个完成的 `CompletableFuture` 的结果。
* **异常处理:** 如果第一个完成的 `CompletableFuture` 抛出异常，该异常会被传播到返回的 `CompletableFuture`。
* **用法示例:**

```java
CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
    Thread.sleep(1000);
    return "Future 1";
});

CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
    Thread.sleep(1500);
    return "Future 2";
});

CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
    Thread.sleep(500);
    throw new RuntimeException("Future 3 failed!");
});

CompletableFuture<Object> anyFuture = CompletableFuture.anyOf(future1, future2, future3);

try{
    System.out.println("First completed future result: " + anyFuture.join());
}catch(Exception e){
    System.out.println("Exception occured: " + e.getMessage());
}
```

**总结:**

选择 `allOf` 还是 `anyOf` 取决于你的需求：

* 需要所有任务都完成才能继续执行后续操作，使用 `allOf`。
* 只需要任意一个任务完成即可继续执行后续操作，使用 `anyOf`。

记住处理异常，`join()` 方法会抛出异常，需要用 `try-catch` 块捕获或者使用 `handle` 方法优雅处理。  在实际应用中，根据你的需求选择合适的异常处理策略。

