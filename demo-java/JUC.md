# JUC



## 介绍

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



## 并发和并行

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



## 用户线程和守护线程



### 介绍

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
