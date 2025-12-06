## 安装

### `Windows11`

访问 [Java Downloads | Oracle](https://www.oracle.com/java/technologies/downloads/#java8-windows) 下载 `jdk-8u461-windows-x64.exe`（下载过程中需要登陆`Oracle`账号）并根据提示安装

设置当前用户的环境变量`JAVA_HOME`

```
JAVA_HOME=C:\Program Files\Java\jdk-1.8
```



## 对象的`hashcode`和`equals`

### 介绍

Java 对象的 `hashCode()` 和 `equals()` 方法是紧密相关的，它们共同决定了对象在基于哈希的集合（如 `HashMap`、`HashSet`）中的行为。  它们之间的关系可以用以下规则来总结：

**规则 1：相等对象必须具有相同的哈希码**

这是最关键的规则。如果两个对象根据 `equals()` 方法判断是相等的，那么它们的 `hashCode()` 方法**必须**返回相同的值。  这是因为基于哈希的集合使用对象的哈希码来快速查找对象。如果相等的对象具有不同的哈希码，那么集合就不能正确地工作。

**规则 2：不相等对象可以具有相同的哈希码（但最好避免）**

如果两个对象根据 `equals()` 方法判断是不相等的，那么它们的 `hashCode()` 方法可以返回相同的值。  这种情况称为“哈希冲突”。  虽然允许哈希冲突，但它会降低哈希表的性能，因为哈希表需要在冲突的情况下进行线性查找。  因此，好的 `hashCode()` 实现应该尽量减少哈希冲突，即使不相等的对象，也应该尽可能返回不同的哈希码。

**总结成一句话：**  `equals()` 返回 `true`，则 `hashCode()` **必须**返回相同的值；`equals()` 返回 `false`，则 `hashCode()` **最好**返回不同的值，但这不是强制要求。

**为什么需要遵守这个规则？**

基于哈希的集合（例如 `HashMap` 和 `HashSet`）依赖于对象的哈希码来快速查找和存储对象。 它们使用哈希码将对象存储到不同的桶中，以便快速访问。 如果相等的对象具有不同的哈希码，那么这些对象将被存储在不同的桶中，导致集合无法正确地找到这些对象。 这将导致一些问题，例如：

* **无法正确添加元素：**  `HashSet` 可能添加重复元素。
* **无法正确查找元素：**  `HashMap` 可能找不到已存在的键值对。
* **性能降低：** 哈希冲突会导致哈希表性能显著下降。


**最佳实践:**

* 始终一起重写 `equals()` 和 `hashCode()` 方法。  如果你重写了其中一个，你**必须**重写另一个，并确保它们保持一致。
* 使用 `Objects.equals()` 和 `Objects.hashCode()` 方法来简化代码并提高可靠性，特别是对于包含多个字段的对象。
* 尽量使哈希码均匀分布，以减少哈希冲突。


**例子说明不遵守规则的后果：**

如果违反了规则1，比如两个相等的 Person 对象具有不同的 hashCode，那么在 HashSet 中添加这两个对象后，HashSet 会认为它们是不同的对象，并把它们都添加进去，导致重复元素。

总之，`equals()` 和 `hashCode()` 方法的协调工作对于 Java 中基于哈希的集合的正确性和性能至关重要。  务必确保它们始终保持一致。



### 对象的 equals 和 hashCode 方法重写最佳实践是什么呢？

Java 对象的 `equals()` 和 `hashCode()` 方法的重写是容易出错但又非常重要的任务。  最佳实践应该遵循以下几个原则：

**1. 始终一起重写:**  如果重写了 `equals()` 方法，**必须**同时重写 `hashCode()` 方法，反之亦然。  这是因为 `equals()` 和 `hashCode()` 方法在基于哈希的集合（如 `HashMap`、`HashSet`）中协同工作。  如果 `equals()` 返回 `true`，那么 `hashCode()` 必须返回相同的值；否则，集合的行为将是不可预测的。

**2. 使用 `Objects` 类中的工具方法:**  Java 提供了 `java.util.Objects` 类，其中包含了方便的工具方法来简化 `equals()` 和 `hashCode()` 方法的实现：

* `Objects.equals(Object a, Object b)`:  安全地比较两个对象是否相等，即使其中一个为 `null` 也不会抛出 `NullPointerException`。
* `Objects.hash(Object... values)`:  计算多个对象的哈希码，可以方便地组合多个字段的哈希码。

**3.  考虑所有重要的字段:**  `equals()` 方法应该比较所有对对象逻辑相等性有意义的字段。  忽略重要的字段会导致不相等的两个对象被认为是相等的。

**4.  保持一致性:**  `equals()` 方法的结果必须满足自反性、对称性、传递性和一致性。  这意味着：

* **自反性:**  `x.equals(x)` 必须返回 `true`。
* **对称性:**  如果 `x.equals(y)` 返回 `true`，那么 `y.equals(x)` 也必须返回 `true`。
* **传递性:**  如果 `x.equals(y)` 返回 `true`，并且 `y.equals(z)` 返回 `true`，那么 `x.equals(z)` 也必须返回 `true`。
* **一致性:**  如果 `x` 和 `y` 对象的 `equals` 方法调用的参数没有改变，那么 `x.equals(y)` 的返回值必须保持一致。

**5.  处理 `null` 值:**  在比较字段时，要小心处理 `null` 值。  使用 `Objects.equals()` 方法可以安全地处理 `null` 值。

**6.  选择合适的哈希算法:**  `hashCode()` 方法应该返回一个均匀分布的哈希码，以尽量减少哈希冲突。 使用 `Objects.hash()` 方法是一个好的开始，它提供了一个合理的默认实现。  如果你的类包含大量的字段，或者需要更高的哈希码分布均匀性，你可能需要考虑更高级的哈希算法。

**7.  覆盖 `toString()` 方法 (建议):**  虽然这不是强制性的，但重写 `toString()` 方法通常是一个好主意，因为它有助于调试和打印对象信息。

**示例：**

假设有一个 `Person` 类：

```java
import java.util.Objects;

class Person {
    String name;
    int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return age == person.age && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
```

这个例子展示了如何使用 `Objects` 类中的工具方法来简化 `equals()` 和 `hashCode()` 方法的实现，并遵守了最佳实践。

通过遵循这些最佳实践，你可以编写出更健壮、更可靠、更易于维护的 Java 代码。  记住，正确的 `equals()` 和 `hashCode()` 实现对于基于哈希的集合的正确性和性能至关重要。



## 枚举

### 介绍

Java 枚举是一种特殊的类，它用于定义一组命名的常量。  枚举提供了一种比使用静态常量更安全、更易于维护的方式来表示一组相关的常量值。

**声明枚举:**

枚举的声明与类的声明类似，但使用 `enum` 关键字：

```java
public enum DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}
```

这声明了一个名为 `DayOfWeek` 的枚举，它包含七个常量：`MONDAY`、`TUESDAY` 等。  每个常量都是 `DayOfWeek` 类的实例。

**枚举的特性:**

* **隐式静态 final:**  枚举常量隐式地是 `static` 和 `final` 的，这意味着它们在创建后不能被修改。

* **自动生成的 toString() 方法:**  枚举自动生成一个 `toString()` 方法，返回枚举常量的名称。

* **switch 语句:**  枚举常量可以直接在 `switch` 语句中使用。

* **迭代:**  可以使用 `values()` 方法获取枚举所有常量的数组。

* **自定义方法和字段:**  可以为枚举添加自定义方法和字段。

* **构造方法:**  枚举可以拥有构造方法，但在构造方法中只能访问 `final` 的成员变量。

**示例：带自定义方法和字段的枚举:**

```java
public enum Planet {
    MERCURY (3.303e+23, 2.4397e6),
    VENUS   (4.869e+24, 6.0518e6),
    EARTH   (5.976e+24, 6.37814e6),
    MARS    (6.421e+23, 3.3972e6),
    JUPITER (1.900e+27, 7.1492e7),
    SATURN  (5.688e+26, 6.0268e7),
    URANUS  (8.686e+25, 2.5559e7),
    NEPTUNE (1.024e+26, 2.4746e7);

    private final double mass;   // in kilograms
    private final double radius; // in meters

    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
    }

    public double getMass() { return mass; }
    public double getRadius() { return radius; }

    public double surfaceGravity() {
        return 6.67300E-11 * mass / (radius * radius);
    }
    public double surfaceWeight(double otherMass) {
        return otherMass * surfaceGravity();
    }
}

public class Main {
    public static void main(String[] args) {
        System.out.println(Planet.EARTH.surfaceWeight(175)); // 输出地球上的体重
    }
}
```

**使用 `values()` 方法迭代枚举:**

```java
for (DayOfWeek day : DayOfWeek.values()) {
    System.out.println(day);
}
```

**使用 `switch` 语句:**

```java
DayOfWeek day = DayOfWeek.FRIDAY;
switch (day) {
    case FRIDAY:
        System.out.println("TGIF!");
        break;
    case SATURDAY:
    case SUNDAY:
        System.out.println("Weekend!");
        break;
    default:
        System.out.println("Weekday!");
}
```

枚举在 Java 中非常有用，可以提高代码的可读性、可维护性和安全性。  它们是表示一组有限的、命名的常量值的最佳方式。



### 操作

Java 枚举类型提供了多种操作方式，使其能够在程序中灵活运用。以下是一些常见的 Java 枚举类型操作：

**1. 获取枚举常量：**

* **直接访问:**  这是最常用的方法，直接使用枚举名和常量名即可访问。

```java
public enum Color { RED, GREEN, BLUE }

Color myColor = Color.RED;
```

* **`values()` 方法:**  该方法返回一个包含所有枚举常量的数组。

```java
Color[] colors = Color.values();
for (Color color : colors) {
    System.out.println(color);
}
```

* **`valueOf()` 方法:**  根据枚举常量的名称字符串，返回对应的枚举常量。如果不存在该名称的常量，则抛出 `IllegalArgumentException`。

```java
Color color = Color.valueOf("GREEN"); // 返回 GREEN
//Color color2 = Color.valueOf("YELLOW"); // 抛出 IllegalArgumentException
```


**2. 使用枚举常量:**

* **在 switch 语句中:**

```java
Color color = Color.RED;
switch (color) {
    case RED:
        System.out.println("Red color");
        break;
    case GREEN:
        System.out.println("Green color");
        break;
    case BLUE:
        System.out.println("Blue color");
        break;
    default:
        System.out.println("Unknown color");
}
```

* **在 if-else 语句中:**

```java
Color color = Color.GREEN;
if (color == Color.RED) {
    // ...
} else if (color == Color.GREEN) {
    // ...
} else {
    // ...
}
```

* **作为方法参数:**

```java
public void printColor(Color color) {
    System.out.println("Color: " + color);
}
```


**3. 枚举中的方法和字段:**

你可以像普通类一样，在枚举中添加方法和字段，这使得枚举更强大和灵活。

```java
public enum TrafficLight {
    RED(30), YELLOW(5), GREEN(25);

    private final int duration;

    TrafficLight(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public String getSignal() {
        switch (this) {
            case RED: return "STOP";
            case YELLOW: return "CAUTION";
            case GREEN: return "GO";
            default: return "UNKNOWN";
        }
    }
}

public class Main {
    public static void main(String[] args) {
        System.out.println(TrafficLight.RED.getDuration()); // 输出 30
        System.out.println(TrafficLight.GREEN.getSignal()); // 输出 GO
    }
}
```


**4.  从字符串创建枚举对象 (反向查找):**

除了 `valueOf()`，你也可以自己实现一个方法从字符串创建枚举对象：

```java
public enum Color {
    RED, GREEN, BLUE;

    public static Color fromString(String colorString) {
        for (Color color : Color.values()) {
            if (color.name().equalsIgnoreCase(colorString)) {
                return color;
            }
        }
        return null; // Or throw an exception
    }
}
```


**5.  ordinal() 方法:**

`ordinal()` 方法返回枚举常量在枚举定义中的索引，从 0 开始。

```java
System.out.println(Color.GREEN.ordinal()); // 输出 1
```


**6.  name() 方法:**

`name()` 方法返回枚举常量的名称 (字符串)。

记住，枚举常量是静态的，因此它们在类加载时就被创建。  理解这些操作方式，可以更好地利用 Java 枚举类型来增强你的代码的可读性和可维护性。  选择合适的操作方法，可以使你的代码更简洁高效。

**示例代码：**

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/blob/master/demo-java/demo-java-assistant/src/test/java/com/future/demo/EnumTests.java`

```java
public class EnumTests {

    @Test
    public void test() {
        // 获取 RED 对应的 duration
        Assert.assertEquals(30, TrafficLight.RED.getDuration());
        // 使用 GREEN 实例调用 getSignal 函数
        Assert.assertEquals("GO", TrafficLight.GREEN.getSignal());

        // 枚举常量数组
        Assert.assertArrayEquals(new TrafficLight[]{TrafficLight.RED, TrafficLight.YELLOW, TrafficLight.GREEN}, TrafficLight.values());

        // 从字符串创建枚举
        Assert.assertEquals(TrafficLight.RED, TrafficLight.valueOf("RED"));

        // `ordinal()` 方法返回枚举常量在枚举定义中的索引，从 0 开始。
        Assert.assertEquals(0, TrafficLight.RED.ordinal());
        Assert.assertEquals(1, TrafficLight.YELLOW.ordinal());
        Assert.assertEquals(2, TrafficLight.GREEN.ordinal());

        // `name()` 方法返回枚举常量的名称 (字符串)。
        Assert.assertEquals("RED", TrafficLight.RED.name());
    }

    public enum TrafficLight {
        RED(30), YELLOW(5), GREEN(25);

        private final int duration;

        TrafficLight(int duration) {
            this.duration = duration;
        }

        public int getDuration() {
            return duration;
        }

        public String getSignal() {
            switch (this) {
                case RED:
                    return "STOP";
                case YELLOW:
                    return "CAUTION";
                case GREEN:
                    return "GO";
                default:
                    return "UNKNOWN";
            }
        }
    }
}
```



## 内存引用类型

Java 的内存引用类型主要指的是 JVM 如何管理对象在堆内存中的引用，它们体现在垃圾回收机制中。  为了更清晰地解释，我将用更详细的例子来说明四种引用类型：强引用、软引用、弱引用和虚引用。  每个例子都包含代码片段和详细解释。


**1. 强引用 (Strong Reference):**

这是最常见的引用类型，也是默认的引用类型。只要强引用还存在，垃圾收集器就不会回收被引用的对象。  即使内存不足，JVM 也不会回收被强引用指向的对象。


```java
public class StrongReferenceExample {
    public static void main(String[] args) {
        // 创建一个对象
        Object obj = new Object();

        // obj 是一个强引用，指向 new Object() 创建的对象
        System.out.println("Object created.");

        // 即使你将 obj 赋值为 null，对象仍然可能不被立即回收，因为JVM可能没有立即执行垃圾回收。
        obj = null;
        System.out.println("obj set to null.");

        // 需要等待JVM进行垃圾回收，才能真正释放内存。手动触发GC一般不建议，除非你对GC的运行机制有非常深入的理解。
        System.gc(); // 尝试强制垃圾回收 (不保证立即回收)
        System.out.println("Garbage collection attempted.");
        //  对象仍然在内存中占空间, 直到JVM下次GC。
    }
}
```

在上面的例子中，`obj` 是一个强引用。即使将 `obj` 设置为 `null`，该对象仍然存在于内存中，直到垃圾收集器执行回收操作。


**2. 软引用 (Soft Reference):**

只有在内存不足的情况下，垃圾收集器才会回收被软引用关联的对象。软引用通常用于实现缓存功能。当内存空间足够时，软引用指向的对象仍然可以被访问；当内存不足时，JVM 会回收被软引用指向的对象来释放内存。


```java
import java.lang.ref.SoftReference;

public class SoftReferenceExample {
    public static void main(String[] args) {
        // 创建一个大的对象，模拟内存压力
        byte[] largeArray = new byte[1024 * 1024 * 10]; // 10MB

        // 创建一个软引用
        SoftReference<byte[]> softRef = new SoftReference<>(largeArray);

        largeArray = null; // 手动释放强引用


        System.out.println("Large array created, strong reference released.");

        // 尝试获取软引用指向的对象
        byte[] recoveredArray = softRef.get();

        if (recoveredArray != null) {
            System.out.println("Object recovered from soft reference.");
        } else {
            System.out.println("Object garbage collected due to memory pressure.");
        }
    }
}
```

在这个例子中，如果内存充足，`recoveredArray` 将不为 `null`；如果内存不足，JVM 会回收 `largeArray`，`recoveredArray` 将为 `null`。


**3. 弱引用 (Weak Reference):**

弱引用比软引用更弱。即使内存充足，只要垃圾收集器开始工作，就会回收被弱引用关联的对象。  弱引用通常用于实现弱缓存。


```java
import java.lang.ref.WeakReference;

public class WeakReferenceExample {
    public static void main(String[] args) {
        Object obj = new Object();
        WeakReference<Object> weakRef = new WeakReference<>(obj);
        obj = null; //释放强引用

        System.gc(); // 强制垃圾回收 (提高回收概率，但不是必然)
        System.out.println("Garbage collection attempted.");

        if (weakRef.get() == null) {
            System.out.println("Object garbage collected.");
        } else {
            System.out.println("Object is still reachable (unlikely).");
        }
    }
}
```

即使没有内存压力，`obj` 也可能在下次垃圾回收时被回收。


**4. 虚引用 (Phantom Reference):**

虚引用是最弱的一种引用，它不会阻止对象被回收。虚引用的主要作用是跟踪对象的垃圾回收状态，通常用于配合引用队列使用，例如在对象被回收之前执行一些操作（例如清理资源）。


```java
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

public class PhantomReferenceExample {
    public static void main(String[] args) throws InterruptedException {
        ReferenceQueue<Object> queue = new ReferenceQueue<>();
        Object obj = new Object();
        PhantomReference<Object> phantomRef = new PhantomReference<>(obj, queue);
        obj = null;

        System.gc(); //尝试进行垃圾回收
        Thread.sleep(1000); //等待垃圾回收完成

        if (queue.poll() != null) {
            System.out.println("Object is finalized and phantom reference is enqueued.");
        } else {
            System.out.println("Object is still alive or the queue hasn't been processed yet.");
        }
    }
}
```

当 `obj` 被回收时，它的 `PhantomReference` 会被添加到 `queue` 中。我们可以通过检查 `queue` 来得知对象已经被回收。


这些例子展示了四种引用类型的不同行为和用途。选择哪种引用类型取决于具体的应用场景和对内存管理的需求。  记住，`System.gc()` 只是建议垃圾回收器执行回收，不能保证立即回收。  实际回收时间取决于 JVM 的垃圾回收策略。



## `JDK API`

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-jdk-api)



### Base64

```java
public class Base64Tests {
    /**
     * https://stackoverflow.com/questions/41935207/base64-string-to-byte-in-java
     */
    @Test
    public void test() {
        // region 测试使用jdk base64 api转换byte数组到base64字符串
        Random random = new Random();
        byte[] datum = new byte[1024 * 1024];
        random.nextBytes(datum);

        String base64Str1 = Base64.getEncoder().encodeToString(datum);

        //endregion

        //region 测试base64字符串转换为byte

        byte[] datum1 = Base64.getDecoder().decode(base64Str1);
        Assert.assertArrayEquals(datum, datum1);

        //endregion
    }
}
```



### 文件操作

#### 在 /tmp 目录创建文件

```java
/**
 * 在 tmp 目录随机创建文件
 */
@Test
public void testCreateRandomFileInTempDirectory() throws IOException {
    String temporaryDirectory = System.getProperty("java.io.tmpdir");
    // https://stackoverflow.com/questions/6142901/how-to-create-a-file-in-a-directory-in-java
    String randomFilename = UUID.randomUUID() + ".doc";
    String path = temporaryDirectory + File.separator + randomFilename;
    File randomFile = new File(path);
    File parentFile = randomFile.getParentFile();
    // 随机文件的父路径为 /tmp 目录
    Assert.assertEquals(temporaryDirectory, parentFile.getAbsolutePath());
    // 创建文件成功返回 true，否则如果文件已经存在则返回 false
    boolean result = randomFile.createNewFile();
    Assert.assertTrue(result);
}
```



### Runtime 接口

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-java-assistant)

#### 获取系统当前 CPU 数量

```java
/**
 * 获取系统当前 CPU 数量
 */
@Test
public void testAvailableProcessors() {
    Runtime runtime = Runtime.getRuntime();
    int availableProcessors = runtime.availableProcessors();
    Assert.assertEquals(8, availableProcessors);
}
```



## `TreeSet` 底层算法

Java中的`java.util.TreeSet`**底层确实使用了二叉树算法**，但更准确地说，它基于**红黑树（Red-Black Tree）**——一种**自平衡的二叉搜索树（Self-Balancing Binary Search Tree, BST）**。


### 1. 核心底层结构：TreeMap的红黑树
`TreeSet`的实现依赖于`java.util.TreeMap`：  
`TreeSet`内部维护了一个`TreeMap`实例，将元素作为`TreeMap`的**键（Key）**存储，而值（Value）则固定为一个静态的空对象（`private static final Object PRESENT = new Object()`）。因此，`TreeSet`的元素操作（如添加、删除、查找）本质上是对`TreeMap`键集合的操作，而`TreeMap`的键正是存储在**红黑树**中的。


### 2. 红黑树：自平衡的二叉搜索树
红黑树是二叉搜索树（BST）的优化版本，通过以下规则保持**自平衡**，避免普通BST在极端情况（如插入有序数据）下退化为链表（时间复杂度从`O(log n)`退化为`O(n)`）：  
- **节点着色规则**：每个节点要么是红色，要么是黑色；  
- **根节点规则**：根节点必须是黑色；  
- **叶子节点规则**：所有叶子节点（NIL节点，空节点）视为黑色；  
- **红节点规则**：红色节点的两个子节点必须是黑色（即不存在连续红色节点）；  
- **路径长度规则**：从任一节点到其所有后代叶子节点的路径中，黑色节点的数量相同（称为“黑高”）。  

这些规则确保了红黑树的**高度始终保持在`O(log n)`级别**（最坏情况下约为`2log(n+1)`），因此`TreeSet`的**插入、删除、查找操作的时间复杂度均为`O(log n)`**，效率稳定。


### 3. TreeSet的特性与红黑树的关系
`TreeSet`的核心特性（**有序性**、**唯一性**）均由红黑树的特性保证：  
- **有序性**：红黑树是二叉搜索树，节点按**自然排序（Natural Ordering）**或**自定义`Comparator`**排列，因此`TreeSet`的元素可以按顺序遍历（如`iterator()`方法返回升序迭代器）；  
- **唯一性**：二叉搜索树中，若插入重复元素（与现有节点比较相等），则不会新增节点（`TreeSet.add(E e)`返回`false`），从而保证元素唯一。


### 4. 为什么选择红黑树而不是普通BST？
普通BST的性能高度依赖数据的插入顺序：  
- 若数据有序插入（如升序或降序），BST会退化为**链表**，此时插入、查找的时间复杂度为`O(n)`；  
- 红黑树通过自平衡机制，无论数据插入顺序如何，都能保持`O(log n)`的时间复杂度，更适合需要**高效动态排序**的场景（如`TreeSet`）。


### 总结
`TreeSet`的底层实现是**红黑树（自平衡二叉搜索树）**，属于二叉树的高级变体。它通过自平衡机制保证了稳定的性能，同时支持元素的有序存储和唯一约束，是Java中处理**有序、无重复集合**的高效数据结构。

**关键结论**：  
`TreeSet`使用**红黑树（自平衡二叉搜索树）**，而非普通二叉树，但其本质仍属于二叉树算法的应用。



## `JMX` - 概念

当然，很乐意为您解释 Java JMX。

### 核心概念：一句话概括

**Java JMX（Java Management Extensions）是一套为应用程序、设备、系统等植入管理功能的框架和一套标准API。简单来说，它就是Java的“管理面板”，让你可以远程监控和管理Java应用程序（比如JVM、Tomcat、Kafka等）的运行状态和配置。**

你可以把它想象成汽车的仪表盘和引擎盖下的各种接口。仪表盘让你能看到车速、转速、油量（这相当于**监控**），而专业的维修技师可以通过接口连接电脑，读取故障码甚至调整发动机参数（这相当于**管理**）。JMX就提供了这套“仪表盘”和“接口”的标准。

---

### 为什么需要 JMX？

在分布式或复杂的生产环境中，应用程序通常运行在远程服务器上。我们迫切需要一种标准化的方式来：
1.  **实时监控**：查看应用的性能指标，如内存使用量、线程数、CPU占用、缓存命中率等。
2.  **动态管理**：在不重启应用的情况下，动态修改配置参数。例如，动态调整日志级别、切换数据源、触发垃圾回收等。
3.  **故障排查**：当应用出现问题时，可以通过JMX获取其内部状态，帮助快速定位问题。
4.  **通知机制**：当应用发生特定事件（如超过阈值、发生错误）时，可以主动发出通知告警。

---

### JMX 的体系结构（三层架构）

JMX的架构清晰地分为三层，这是理解其如何工作的关键：

1.  **Instrumentation Level（ instrumentation 层 / 装备层）**
    *   **作用**：这是被管理的资源所在层。任何需要被管理的资源（如一个对象、一个服务）都需要被“装备”成所谓的 **MBean（Managed Bean）**。
    *   **核心**：**MBean**。它是一个Java对象，遵循特定的设计模式，对外暴露了**属性**（用于查看和设置）、**操作**（用于调用方法）和**通知**（用于发送事件）。例如，一个代表缓存的服务可以被装备成一个MBean，暴露其`cacheSize`属性和`clearCache()`操作。

2.  **Agent Level（代理层）**
    *   **作用**：这是核心的中间件层，运行在JVM中。它就像一个管理中介，管理着MBean，并为其提供各种服务。
    *   **核心**：**MBean Server**。它是代理层的核心组件，一个注册和管理MBean的容器。外部管理工具想要访问MBean，必须通过MBean Server。代理层还提供了一些基础服务，如动态加载、监控器、关系服务等。

3.  **Remote Management Level（远程管理层）**
    *   **作用**：这一层为远程管理工具提供了访问MBean Server的接口。它使得外部的客户端（如JConsole、VisualVM）能够通过网络连接到JVM内的MBean Server。
    *   **核心**：**Connectors** 和 **Adaptors**。
        *   **Connector**：使用JMX API定义的协议（如RMI），为Java客户端提供连接。
        *   **Adaptor**：将JMX协议转换成其他通用协议（如HTTP），使得即使不是Java写的工具（如Web浏览器）也能进行管理。最著名的是**HTML Adaptor**，它可以将MBean Server的内容以网页形式展示。



---

### 如何连接和使用 JMX？

通常有两种主要方式：

1.  **使用本地JVM工具（无需配置）**
    *   对于本地开发环境，可以直接使用JDK自带的 **JConsole** 或 **VisualVM**。
    *   它们会自动检测到本机运行的所有Java进程，可以直接连接，无需任何特殊配置。这是最简单的入门方式。

2.  **远程连接（生产环境常用）**
    *   要远程连接服务器上的Java应用，需要在启动JVM时添加一些参数来开启远程JMX连接并设置端口和认证信息。
    *   **示例启动参数**：
        ```bash
        java -Dcom.sun.management.jmxremote \
             -Dcom.sun.management.jmxremote.port=9999 \
             -Dcom.sun.management.jmxremote.authenticate=false \
             -Dcom.sun.management.jmxremote.ssl=false \
             -jar YourApplication.jar
        ```
        *   **注意**：上面示例为了演示关闭了认证和SSL，**在生产环境中强烈建议启用认证和SSL以保证安全**。
    *   启动后，就可以在另一台机器的JConsole中通过 `hostname:9999` 来连接了。

---

### 一个简单的例子

假设我们有一个应用程序配置类，我们想动态修改它的日志级别。

1.  **定义一个 MBean 接口**：
    ```java
    public interface AppConfigMBean {
        // 定义一个属性
        public void setLogLevel(String level);
        public String getLogLevel();
    
        // 定义一个操作
        public void reloadConfiguration();
    }
    ```

2.  **实现这个 MBean 接口**：
    ```java
    public class AppConfig implements AppConfigMBean {
        private String logLevel = "INFO";
    
        @Override
        public String getLogLevel() {
            return logLevel;
        }
    
        @Override
        public void setLogLevel(String level) {
            this.logLevel = level;
            System.out.println("LogLevel changed to: " + level);
            // 这里可以实际触发日志框架重新加载配置
        }
    
        @Override
        public void reloadConfiguration() {
            System.out.println("Configuration reloaded with logLevel: " + logLevel);
        }
    }
    ```

3.  **在应用中注册 MBean**：
    ```java
    public class Main {
        public static void main(String[] args) {
            // 获取平台默认的 MBean Server
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    
            // 为我们的 MBean 创建一个对象名（唯一标识）
            ObjectName name = new ObjectName("com.example:type=AppConfig");
    
            // 实例化我们的 MBean 并注册到 MBean Server 中
            AppConfig mbean = new AppConfig();
            mbs.registerMBean(mbean, name);
    
            // ... 你的应用主逻辑，保持运行
            Thread.sleep(Long.MAX_VALUE);
        }
    }
    ```

4.  **使用 JConsole 进行管理**：
    *   启动上述应用。
    *   打开 JConsole，连接到该Java进程。
    *   在 **MBeans** 标签页中，找到 `com.example` -> `AppConfig`。
    *   你可以**查看**当前的 `LogLevel` 属性值。
    *   你可以**写入**一个新的值（如 `DEBUG`）来动态修改它。
    *   你可以**调用** `reloadConfiguration` 操作。

---

### 总结

| 方面         | 描述                                                         |
| :----------- | :----------------------------------------------------------- |
| **是什么**   | Java 管理和监控的标准 API 和框架。                           |
| **核心思想** | 将管理功能与被管理资源分离，通过 MBean 标准化接口。          |
| **核心组件** | **MBean**（被管资源）、**MBean Server**（代理核心）、**Connector/Adaptor**（远程访问）。 |
| **主要用途** | 监控 JVM 和应用的性能指标、动态修改配置、故障排查、发送通知。 |
| **常用工具** | JConsole, VisualVM, JVisualVM, 以及专业的 APM 工具（如 Prometheus + JMX Exporter）。 |
| **安全性**   | 支持密码认证和 SSL/TLS 加密，生产环境必须启用。              |

JMX 是 Java 生态系统中至关重要的一环，是理解和掌握 Java 应用运维的必备知识。几乎所有主流的Java应用服务器（Tomcat, JBoss, WebSphere）和大型中间件（Kafka, Cassandra）都深度集成了JMX来提供管理和监控功能。



## `JMX` - 实验入门

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-java/demo-jmx-getting-started)

`AppConfigMBean`

```java
public interface AppConfigMBean {
    // 定义一个属性
    public void setLogLevel(String level);
    public String getLogLevel();

    // 定义一个操作
    public void reloadConfiguration();
}
```

`AppConfig`

```java
public class AppConfig implements AppConfigMBean {
    private String logLevel = "INFO";

    @Override
    public String getLogLevel() {
        return logLevel;
    }

    @Override
    public void setLogLevel(String level) {
        this.logLevel = level;
        System.out.println("LogLevel changed to: " + level);
        // 这里可以实际触发日志框架重新加载配置
    }

    @Override
    public void reloadConfiguration() {
        System.out.println("Configuration reloaded with logLevel: " + logLevel);
    }
}
```

`Main`

```java
public class Main {
    public static void main(String[] args) throws Exception {
        // 获取平台默认的 MBean Server
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        // 为我们的 MBean 创建一个对象名（唯一标识）
        ObjectName name = new ObjectName("com.example:type=AppConfig");

        // 实例化我们的 MBean 并注册到 MBean Server 中
        AppConfig mbean = new AppConfig();
        mbs.registerMBean(mbean, name);

        // ... 你的应用主逻辑，保持运行
        Thread.sleep(Long.MAX_VALUE);
    }
}
```



## `JMX` - 启用`SpringBoot`内嵌`Tomcat`的`MBean`

>提示：如果不启用 `SpringBoot` 的 `MBean`，使用 `jconsole` 连接 `JMX` 后不会显式关于 `Tomcat` 的 `MBean`。

`application.properties` 添加如下配置：

```properties
# 启用 Tomcat 的 MBean 注册
server.tomcat.mbeanregistry.enabled=true
```



## 内部类

当然！在 Java 中，内部类（Inner Class）是一个非常重要且强大的特性。简单来说，**内部类就是定义在另一个类内部的类**。

把它想象成一个“类中的类”，外面的类称为**外部类（Outer Class）**。

---

### 为什么要使用内部类？

使用内部类主要有以下几个优点：
1.  **逻辑分组**：如果一个类只对另一个类有用，那么将它们放在一起可以使封装性更好，代码更易于维护。
2.  **增强封装性**：内部类可以访问外部类的私有成员（包括私有字段和方法），反之亦然，实现了更紧密的耦合。
3.  **提高可读性和可维护性**：将相关的类组织在一起，使代码结构更清晰。
4.  **实现多重继承的变通**：通过多个内部类各自继承一个类，外部类可以间接获得多种行为（虽然 Java 本身不支持多重继承）。

---

### 内部类的分类

Java 中的内部主要分为以下四种类型：

#### 1. 成员内部类（Member Inner Class）
- **定义**：在一个类中直接定义的、非静态的类。
- **特点**：
    - 可以访问外部类的**所有成员**（包括 `private` 的）。
    - **不能定义静态成员**（`static` 字段或方法），除非是静态常量（`static final`）。
    - 必须先创建外部类的实例，才能创建成员内部类的实例。

```java
public class OuterClass {
    private String outerField = "I'm from Outer!";

    // 成员内部类
    class InnerClass {
        public void print() {
            // 可以直接访问外部类的私有成员
            System.out.println(outerField);
        }
    }

    public static void main(String[] args) {
        // 1. 首先创建外部类对象
        OuterClass outer = new OuterClass();
        // 2. 通过外部类对象创建内部类对象
        OuterClass.InnerClass inner = outer.new InnerClass();
        inner.print(); // 输出: I'm from Outer!
    }
}
```

#### 2. 静态内部类（Static Nested Class）
- **定义**：使用 `static` 关键字修饰的、定义在类中的内部类。
- **特点**：
    - **不能直接访问**外部类的非静态成员（需要先创建外部类实例才能访问）。
    - 可以定义静态成员。
    - 创建它的实例**不需要**先创建外部类的实例。

```java
public class OuterClass {
    private String outerField = "I'm from Outer!";
    private static String staticOuterField = "I'm static from Outer!";

    // 静态内部类
    static class StaticNestedClass {
        public void print() {
            // System.out.println(outerField); // 错误！不能访问非静态成员
            System.out.println(staticOuterField); // 可以访问静态成员
        }
    }

    public static void main(String[] args) {
        // 无需外部类实例，直接创建
        OuterClass.StaticNestedClass staticInner = new OuterClass.StaticNestedClass();
        staticInner.print(); // 输出: I'm static from Outer!
    }
}
```

#### 3. 局部内部类（Local Inner Class）
- **定义**：定义在一个方法或作用域块内的类。
- **特点**：
    - 作用域仅限于定义它的方法或块中，外界不可见。
    - 可以访问外部类的成员。
    - 可以访问**所在方法的 `final` 或 effectively final 的局部变量**（Java 8+）。

```java
public class OuterClass {
    private String outerField = "I'm from Outer!";

    public void someMethod() {
        final String localVar = "I'm local!";

        // 局部内部类
        class LocalClass {
            public void print() {
                System.out.println(outerField); // 访问外部类成员
                System.out.println(localVar);   // 访问方法的局部变量（必须是final或effectively final）
            }
        }

        LocalClass local = new LocalClass();
        local.print();
    }

    public static void main(String[] args) {
        OuterClass outer = new OuterClass();
        outer.someMethod(); // 输出: I'm from Outer! \n I'm local!
    }
}
```

#### 4. 匿名内部类（Anonymous Inner Class）
- **定义**：没有名字的局部内部类。它通常用于**一次性**实现一个接口或继承一个类，并同时创建实例。
- **特点**：
    - 代码非常简洁，无需显式定义类。
    - 语法格式为：`new 父类构造器() { /* 类体 */ }` 或 `new 接口() { /* 类体 */ }`。
    - 在 Java 8 引入 Lambda 表达式之前，它被广泛用于实现函数式接口（如 `Runnable`）。

```java
public class OuterClass {
    public static void main(String[] args) {
        // 传统方式：创建一个实现了Runnable接口的类的实例
        // 使用匿名内部类方式
        Runnable task = new Runnable() { 
            @Override
            public void run() {
                System.out.println("匿名内部类在运行！");
            }
        };

        new Thread(task).start();

        // 更常见的写法是直接作为参数传递
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("直接传递的匿名内部类！");
            }
        }).start();

        // Java 8+ 后，通常用 Lambda 表达式简化（本质不同，但效果类似）
        new Thread(() -> System.out.println("使用Lambda表达式！")).start();
    }
}
```

---

### 总结与对比

| 类型           | 位置                 | 可访问外部类成员     | 可包含静态成员 | 实例化方式                 | 常见用途                   |
| :------------- | :------------------- | :------------------- | :------------- | :------------------------- | :------------------------- |
| **成员内部类** | 类内部（非静态）     | **是**（所有）       | 否（常量除外） | `outer.new Inner()`        | 紧密关联的辅助功能         |
| **静态内部类** | 类内部（静态）       | 否（只能访问静态的） | **是**         | `new Outer.StaticNested()` | 与外部类逻辑相关但无需实例 |
| **局部内部类** | 方法或块内部         | **是**（所有）       | 否             | 在方法内部 `new`           | 方法内局部使用的辅助类     |
| **匿名内部类** | 方法或块内部（无名） | **是**（所有）       | 否             | 随定义随 `new`             | 快速实现接口/类，事件监听  |

希望这个详细的解释能帮助你彻底理解 Java 的内部类！



## `JavaFX`

当然！这是一个关于 JavaFX 的全面介绍。

### 核心定义

**JavaFX** 是一个开源的、基于 Java 的客户端应用程序平台，用于创建丰富的桌面、移动和嵌入式设备上的图形用户界面（GUI）。你可以把它看作是 Java 生态系统中用于构建现代、美观的桌面应用的**下一代 GUI 工具包**，用于取代之前比较陈旧的 Swing 和 AWT。

------

### 主要特点和优势

与它的前辈（如 Swing）相比，JavaFX 提供了许多强大的优势：

1. **现代化的外观和丰富的视觉效果**： **硬件加速**：利用 GPU 进行渲染，使得界面非常流畅，即使有复杂的动画和特效。 **CSS 样式**：可以使用熟悉的 CSS 来美化界面，将应用程序的逻辑（Java代码）和表现样式（CSS）完全分离，让设计师和开发者可以更好地协作。 **内置动画 API**：提供了强大的类库，可以轻松创建平滑的过渡、变换和动画效果。
2. **FXML 与场景构建器**： **FXML**：一种基于 XML 的标记语言，用于以声明式的方式设计用户界面，而不是完全用 Java 代码来“画”界面。这使得界面布局更清晰、更易于维护。 **Scene Builder**：一个可视化的拖放式设计工具，可以直观地设计 FXML 界面，然后直接与 Java IDE（如 IntelliJ IDEA, NetBeans）集成。
3. **强大的图形和媒体支持**： **2D/3D 图形**：支持绘制复杂的 2D 形状，并内置了 3D 图形功能。 **音频和视频**：内置支持播放多种格式的媒体文件（如 MP3, WAV, FLV, MP4）。
4. **绑定 API**： 提供了一个强大的“绑定”机制，可以自动同步 UI 控件和底层数据模型的状态。当数据改变时，UI 会自动更新，无需手动编写大量的更新代码，这大大简化了开发。
5. **跨平台**： 继承了 Java “一次编写，到处运行” 的特性。编译后的 JavaFX 应用程序可以在 Windows, macOS, Linux 等多个操作系统上运行。

------

### 架构核心：场景图

理解 JavaFX 架构的关键是理解其**场景图** 模型。它类似于一棵树状结构：

- **舞台（Stage）**：应用程序的最顶层容器，对应一个桌面窗口。
- **场景（Scene）**：填充舞台的内容区域，一个 Stage 包含一个 Scene。
- **节点（Node）**：场景图中的所有元素都是节点，例如按钮（Button）、文本框（TextField）、布局面板（Pane, VBox, HBox）等。节点可以嵌套，形成父子关系。

这种层次结构使得管理和渲染 UI 元素非常高效。

------

### JavaFX 与 Swing/AWT 的关系

- **AWT**：Java 最早的 GUI 工具包，依赖本地操作系统组件，外观和行为因平台而异。
- **Swing**：在 AWT 之上构建，提供了一套“轻量级”的纯 Java 实现的组件，外观可插拔，更加灵活。曾是多年来的主流。
- **JavaFX**：旨在取代 Swing，作为构建现代、富客户端应用的**官方首选方案**。它不依赖于本地组件，而是自己进行渲染，因此能实现更一致、更炫酷的视觉效果。

**重要历史节点**：在 Java 8 之前，JavaFX 是独立于 JDK 的。从 **Java 8 到 Java 10**，JavaFX 被捆绑在 JDK 中。从 **Java 11** 开始，Oracle 将 JavaFX 从 JDK 中剥离出来，成为一个独立的开源项目（名为 **OpenJFX**）。现在，开发者需要单独下载 JavaFX SDK 或通过 Maven/Gradle 依赖来使用它。

------

### 一个简单的 “Hello World” 示例

```
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class HelloWorld extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. 创建一个控件（节点）
        Label label = new Label("Hello, JavaFX!");

        // 2. 创建一个布局面板，并添加控件
        StackPane root = new StackPane();
        root.getChildren().add(label);

        // 3. 创建一个场景，并设置其大小和根布局
        Scene scene = new Scene(root, 300, 200);

        // 4. 设置舞台的标题和场景，并显示窗口
        primaryStage.setTitle("My First JavaFX App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // 启动应用程序
    }
}
```

### 总结

| 特性         | 描述                                                         |
| ------------ | ------------------------------------------------------------ |
| **定位**     | 构建现代、丰富、跨平台桌面客户端应用程序的官方 Java GUI 框架。 |
| **核心优势** | 硬件加速、CSS 样式、FXML、强大的动画和媒体支持、数据绑定。   |
| **当前状态** | 自 JDK 11 起作为独立项目 **OpenJFX** 发展，非常活跃。        |
| **适用场景** | 需要复杂图形、动画、媒体播放或现代化 UI 的企业级桌面应用、工具软件、Kiosk 系统、数据可视化仪表盘等。 |

简单来说，**如果你今天想用 Java 来开发一个看起来很棒、体验流畅的桌面应用程序，JavaFX 是你的不二之选。**

## Socket编程

>说明：演示socket客户端连接socket服务器并每5秒客户端和服务器分别向对端自动发送数据。
>
>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-java/demo-socket

## 整数或字节数组和进制字符串相互转换

>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-java/demo-jdk-api

```java
package com.future.demo;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

public class RadixTests {
    /**
     * x进制字符串转换为字节数组
     */
    @Test
    public void testRadixStringToOrFromByteArray() throws DecoderException {
        // region 二进制字符串转换为字节数组

        String binaryString = "00000001" +
                "00000010" +
                "00000011";
        byte[] bytes = new BigInteger(binaryString, 2).toByteArray();
        Assert.assertEquals(3, bytes.length);
        Assert.assertEquals(1, bytes[0]);
        Assert.assertEquals(2, bytes[1]);
        Assert.assertEquals(3, bytes[2]);

        // 测试很大的二进制字符串
        int count = 1000;
        binaryString = "";
        for (int i = 0; i < count; i++) {
            binaryString += "00000001";
        }
        bytes = new BigInteger(binaryString, 2).toByteArray();
        Assert.assertEquals(count, bytes.length);
        for (int i = 0; i < count; i++) {
            Assert.assertEquals(1, bytes[i]);
        }

        // endregion

        // region 字节数组转换为二进制字符串

        BigInteger bigInteger = new BigInteger(bytes);
        String binaryStringFromByteArray = bigInteger.toString(2);
        Assert.assertTrue(binaryString.endsWith(binaryStringFromByteArray));

        // endregion

        // region 16进制字符串转换为字节数组

        String radixString = "FF" +
                "A0" +
                "34";
        bytes = Hex.decodeHex(radixString);
        Assert.assertEquals(3, bytes.length);
        Assert.assertEquals(-1, bytes[0]);
        Assert.assertEquals(-96, bytes[1]);
        Assert.assertEquals(52, bytes[2]);

        // 测试很大的16进制字符串
        count = 1000;
        radixString = "";
        for (int i = 0; i < count; i++) {
            radixString += "34";
        }
        bytes = Hex.decodeHex(radixString);
        Assert.assertEquals(count, bytes.length);
        for (int i = 0; i < count; i++) {
            Assert.assertEquals(52, bytes[i]);
        }

        // endregion

        // region 字节数组转换为16进制字符串

        String radixStringFromByteArray = Hex.encodeHexString(bytes);
        Assert.assertTrue(radixString.endsWith(radixStringFromByteArray));

        // endregion

        // region 整数转换为x进制字符串

        // 转换为16进制字符串
        int integer = 0x0F;
        radixString = Integer.toHexString(integer);
        radixString = StringUtils.leftPad(radixString, 2, "0");
        Assert.assertEquals("0f", radixString);

        // 转换为二进制字符串
        integer = 0b1111;
        radixString = Integer.toBinaryString(integer);
        radixString = StringUtils.leftPad(radixString, 8, "0");
        Assert.assertEquals("00001111", radixString);

        // endregion
    }
}
```

## 注解 - 概念

### 一、核心概念：注解是什么？

简单来说，**注解是一种形式化的元数据，它可以被添加到 Java 代码中**。

让我们拆解这个定义：

- **元数据**：意思是“关于数据的数据”。注解本身不是程序逻辑的一部分，而是为代码提供额外的信息或说明。
- **形式化**：注解有特定的语法和规则，不同于普通的注释（`//`或 `/* ... */`）。
- **添加到代码中**：注解可以用于修饰类、方法、变量、参数、包等。

你可以把注解想象成一个“标签”。你给一段代码（比如一个方法）贴上一个标签，这个标签本身不执行任何操作，但它可以告诉编译器、开发工具或者运行时环境一些重要的信息。

**注解 vs. 普通注释**

| 特性                   | 注解（Annotation）                  | 普通注释（Comment）             |
| ---------------------- | ----------------------------------- | ------------------------------- |
| **作用对象**           | 编译器、开发工具、运行时环境（JVM） | 仅限阅读代码的开发者            |
| **格式**               | 以 `@`符号开头，如 `@Override`      | `//`, `/* ... */`, `/** ... */` |
| **是否编译到类文件中** | 是（可以通过反射读取）              | 否                              |
| **是否有功能影响**     | 是（可以被处理，从而影响程序行为）  | 否                              |

------

### 二、注解的主要用途

注解主要有三个层面的用途：

1. **给编译器提供信息**：编译器可以根据注解来检查代码，发现错误或抑制警告。 **例子**：`@Override`：告诉编译器这个方法是重写父类的方法，如果方法签名与父类不匹配，编译器会报错。 **例子**：`@SuppressWarnings`：告诉编译器忽略特定的警告，如 `@SuppressWarnings("unchecked")`。
2. **编译时处理**：在编译阶段，一些工具（如注解处理器）可以扫描代码中的注解，并自动生成额外的代码、配置文件等。这是许多流行框架（如 Lombok, MapStruct）的核心原理。
3. **运行时处理**：在程序运行期间，可以通过 Java 的反射（Reflection）机制读取类、方法上的注解，并根据注解的信息来动态改变程序的行为。这是大多数现代 Java 框架（如 Spring, JUnit, Hibernate）的基石。

------

### 三、内置的常用注解

Java 自带了一些核心注解：

- **`@Override`**：表示一个方法声明旨在重写超类中的方法声明。
- **`@Deprecated`**：表示已过时，不推荐使用的代码元素。编译器会产生警告。
- **`@SuppressWarnings`**：指示编译器忽略特定的警告。
- **`@FunctionalInterface`**（Java 8 引入）：表示一个接口是函数式接口（只有一个抽象方法）。

------

### 四、如何自定义注解？

你可以创建自己的注解来满足特定需求。这通常用于框架开发。

**1. 定义注解**

使用 `@interface`关键字来定义。

```
// 定义一个简单的注解
public @interface MyCustomAnnotation {
}
```

**2. 为注解添加元注解**

元注解是用于修饰其他注解的注解，它们定义了自定义注解的行为。

| 元注解            | 作用                                                         |
| ----------------- | ------------------------------------------------------------ |
| **`@Target`**     | 指定注解可以应用在哪些地方（如类、方法、字段等）。例如：`ElementType.METHOD`, `ElementType.TYPE`。 |
| **`@Retention`**  | 指定注解的生命周期。`RetentionPolicy.SOURCE`（仅源码），`RetentionPolicy.CLASS`（编译到class文件），**`RetentionPolicy.RUNTIME`（运行时可用，可通过反射读取）**。 |
| **`@Documented`** | 表示该注解应被 javadoc 工具记录。                            |
| **`@Inherited`**  | 表示子类可以继承父类上的注解。                               |

**示例：定义一个更复杂的注解**

```
import java.lang.annotation.*;

@Target(ElementType.METHOD) // 这个注解只能用在方法上
@Retention(RetentionPolicy.RUNTIME) // 这个注解在运行时存在，可以通过反射读取
public @interface MyCustomAnnotation {
    String value() default "default value"; // 定义一个属性，默认值为 "default value"
    int priority() default 0; // 定义另一个属性
}
```

**3. 使用自定义注解**

```
public class MyClass {
    @MyCustomAnnotation(value = "这是一个重要方法", priority = 1)
    public void myMethod() {
        // 方法实现
    }
}
```

**4. 在运行时通过反射处理注解**

这是让注解“活”起来的关键一步。

```
import java.lang.reflect.Method;

public class AnnotationTest {
    public static void main(String[] args) throws Exception {
        // 1. 获取类的Class对象
        Class<MyClass> clazz = MyClass.class;

        // 2. 获取指定方法
        Method method = clazz.getMethod("myMethod");

        // 3. 判断方法上是否存在指定的注解
        if (method.isAnnotationPresent(MyCustomAnnotation.class)) {
            // 4. 获取注解实例
            MyCustomAnnotation annotation = method.getAnnotation(MyCustomAnnotation.class);

            // 5. 从注解实例中获取属性值
            String value = annotation.value();
            int priority = annotation.priority();

            // 6. 根据注解信息执行逻辑
            System.out.println("Value: " + value);
            System.out.println("Priority: " + priority);

            if (priority > 0) {
                System.out.println("这是一个高优先级方法，需要特殊处理！");
            }
        }
    }
}
```

运行上述 `AnnotationTest`类，输出结果将是：

```
Value: 这是一个重要方法
Priority: 1
这是一个高优先级方法，需要特殊处理！
```

------

### 五、总结

| 特性         | 描述                                                         |
| ------------ | ------------------------------------------------------------ |
| **本质**     | 附加在代码上的元数据（标签）。                               |
| **目的**     | 为编译器、开发工具或运行时环境提供指示。                     |
| **核心机制** | **反射**是实现运行时注解功能的核心。                         |
| **应用场景** | 代码检查、依赖注入（如 Spring 的 `@Autowired`）、单元测试（JUnit）、ORM 映射（Hibernate）、自动生成代码等。 |

总而言之，注解是 Java 中一种强大的元编程工具，它将配置信息和代码本身紧密地结合在一起，极大地提高了开发效率和代码的声明性，是现代 Java 生态系统的支柱之一。

## 注解 - @Target

### 核心作用

`@Target`注解的**核心作用是指定自定义注解可以应用在 Java 程序的哪些元素上**。换句话说，它定义了注解的使用目标，限制了注解的使用范围，防止注解被错误地用在不当的地方。

------

### 基本语法

`@Target`接收一个 `ElementType[]`作为参数值，即可以指定一个或多个目标类型。

```
@Target({ElementType.TYPE, ElementType.METHOD}) // 可以同时指定多个目标
public @interface MyCustomAnnotation {
    // 注解的定义
}
```

------

### `ElementType`的枚举值

`ElementType`是一个枚举类，包含了所有可以指定的目标类型。以下是常用的值：

| `ElementType`值       | 含义                           | 示例                                       |
| --------------------- | ------------------------------ | ------------------------------------------ |
| **`TYPE`**            | 类、接口（包括注解类型）、枚举 | `class`, `interface`, `enum`               |
| **`FIELD`**           | 字段（包括枚举常量）           | 类的成员变量                               |
| **`METHOD`**          | 方法                           | 类的方法                                   |
| **`PARAMETER`**       | 方法的形参                     | `myMethod(@MyAnnotation String param)`     |
| **`CONSTRUCTOR`**     | 构造函数                       | 类的构造方法                               |
| **`LOCAL_VARIABLE`**  | 局部变量                       | 方法内部定义的变量                         |
| **`ANNOTATION_TYPE`** | 注解类型                       | 用于定义元注解（注解的注解）               |
| **`PACKAGE`**         | 包                             | 包信息（通常在 `package-info.java`中使用） |
| **`TYPE_PARAMETER`**  | 类型参数（Java 8+）            | 泛型参数：`class MyClass<T>`               |
| **`TYPE_USE`**        | 类型使用（Java 8+）            | 几乎任何使用类型的地方，功能非常强大       |

------

### 代码示例

#### 示例1：定义一个只能用在方法上的注解

这个注解用于标记需要进行性能测试的方法。

```
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 指定该注解只能用于方法
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时可用
public @interface PerformanceTest {
    // 可以添加一些属性，比如超时时间（毫秒）
    long timeout() default 1000L;
}
```

**使用该注解：**

```
public class Calculator {

    @PerformanceTest(timeout = 500) // 正确：用在方法上
    public void calculate() {
        // ... 复杂的计算
    }

    // @PerformanceTest // 错误！如果写在这里，编译会报错，因为@PerformanceTest不能用于字段
    // private String name;
}
```

#### 示例2：定义一个可以用在类和方法上的注解

这个注解用于标记一个功能模块，包括模块本身（类）和模块的核心方法。

```
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD}) // 可以同时用于类和方法
public @interface FeatureModule {
    String moduleName();
    String version() default "1.0";
}
```

**使用该注解：**

```
@FeatureModule(moduleName = "用户管理", version = "2.0") // 正确：用在类上
public class UserService {

    @FeatureModule(moduleName = "用户登录") // 正确：用在方法上
    public void login() {
        // ... 登录逻辑
    }
}
```

------

### 如果不使用 `@Target`会怎样？

如果一个自定义注解没有使用 `@Target`进行修饰，那么该注解可以用于**除了类型参数（`TYPE_PARAMETER`）之外**的任何元素上。

**但是，强烈建议始终使用 `@Target`**，原因如下：

1. **明确意图**：让阅读代码的人立刻明白这个注解的用途。
2. **编译时检查**：编译器会帮你检查注解是否被用错了地方，提前发现错误，避免运行时出现意外行为。
3. **提高代码质量**：限制注解的使用范围是良好API设计的一部分。

------

### 总结

| 特性         | 描述                                                         |
| ------------ | ------------------------------------------------------------ |
| **目的**     | 限制自定义注解可以标注的程序元素（如类、方法、字段等）。     |
| **参数**     | 一个或多个 `ElementType`枚举值。                             |
| **重要性**   | 提供编译时安全检查，明确注解的用途，是定义注解时的**最佳实践**。 |
| **默认行为** | 如果省略 `@Target`，注解可以用于几乎任何地方（除 `TYPE_PARAMETER`外），但这不推荐。 |

简单来说，**`@Target`就是注解的“使用说明书”，它告诉编译器和开发者这个注解应该贴在哪儿。**

## 注解 - @Retention

### 核心作用

`@Retention`注解的**核心作用是定义自定义注解的生命周期**，即指定注解信息在哪个级别可用：仅在源代码中、在编译后的类文件中，还是在运行时通过反射可读取。

------

### 基本语法

`@Retention`接收一个 `RetentionPolicy`枚举值作为参数。

```
@Retention(RetentionPolicy.RUNTIME) // 最常见的使用方式
public @interface MyCustomAnnotation {
    // 注解的定义
}
```

------

### `RetentionPolicy`的枚举值

`RetentionPolicy`有三个枚举值，代表了三种不同的保留策略：

| `RetentionPolicy`值 | 生命周期       | 说明                                                         | 常见用途                                                     |
| ------------------- | -------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **`SOURCE`**        | **源码级别**   | 注解只在源代码中保留，**编译器编译后就会丢弃**。编译后的 `.class`文件中不包含该注解信息。 | 1. **编译期检查**：如 `@Override`、`@SuppressWarnings`，编译器利用它们进行检查或抑制警告，检查完后就没用了。 2. **代码生成工具**：如 Lombok，在编译时根据注解生成代码，生成完后注解的使命就结束了。 |
| **`CLASS`**         | **类文件级别** | 注解会被编译器保留在编译后的 `.class`文件中，但**不会被加载到 JVM 内存中**。因此在**运行时不可通过反射获取**。这是**默认策略**。 | 1. **字节码处理工具**：在程序运行时之外的工具（如 ASM、CGLib 等）可以读取 `.class`文件中的注解信息进行一些后处理。 2. 实际开发中直接使用较少。 |
| **`RUNTIME`**       | **运行时级别** | 注解会被编译器保留在 `.class`文件中，并且在程序**运行时会被 JVM 保留，可以通过反射机制读取**。 | 1. **运行时框架**：这是最常用的策略。如 Spring 框架中的 `@Controller`、`@Autowired`，JUnit 中的 `@Test`，JPA 中的 `@Entity`等。框架在运行时通过反射读取这些注解来配置行为、依赖注入、执行测试等。 |

------

### 代码示例与对比

#### 示例1：`SOURCE`级别（如 `@Override`）

`@Override`注解本身就是 `@Retention(RetentionPolicy.SOURCE)`的典型例子。

```
// 模拟 @Override 的定义（实际定义更复杂）
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE) // 源码级别保留
public @interface MyOverride {
}

public class Father {
    public void sayHello() {
        System.out.println("Hello from Father");
    }
}

public class Son extends Father {
    @MyOverride // 作用：提示编译器检查此方法是否真的重写了父类方法
    public void sayHello() {
        System.out.println("Hello from Son");
    }
}
```

- **作用**：编译器看到 `@MyOverride`会检查 `Son`类是否真的重写了 `Father`的方法。如果 `Father`没有 `sayHello`方法，编译器会报错。
- **生命周期**：检查完成后，这个注解信息就不会被写入 `.class`文件。你在运行时通过反射是获取不到 `@MyOverride`注解的。

#### 示例2：`RUNTIME`级别（Spring/ JUnit 风格）

这是自定义注解最常见的用法。

```
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// 定义一个运行时注解，模拟JUnit的@Test
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME) // 关键：运行时保留
public @interface MyTest {
    // 可以添加属性，比如是否启用测试
    boolean enabled() default true;
}
```

**使用注解并利用反射在运行时读取：**

```
public class TestCase {

    @MyTest(enabled = true)
    public void testUserLogin() {
        System.out.println("执行用户登录测试...");
        // 测试逻辑
    }

    @MyTest(enabled = false) // 这个测试被禁用了
    public void testUserLogout() {
        System.out.println("执行用户登出测试...");
    }

    public void commonMethod() { // 这个不是测试方法
        System.out.println("这是一个普通方法");
    }
}
```

**编写一个简单的“测试运行器”来读取运行时注解：**

```
import java.lang.reflect.Method;

public class SimpleTestRunner {
    public static void main(String[] args) throws Exception {
        Class<TestCase> testClass = TestCase.class;
        Object testInstance = testClass.getDeclaredConstructor().newInstance();

        // 获取测试类的所有方法
        Method[] methods = testClass.getDeclaredMethods();

        for (Method method : methods) {
            // 检查方法上是否有 @MyTest 注解
            if (method.isAnnotationPresent(MyTest.class)) {
                MyTest myTest = method.getAnnotation(MyTest.class);
                // 读取注解的属性值
                if (myTest.enabled()) {
                    System.out.println("运行测试: " + method.getName());
                    method.invoke(testInstance); // 执行测试方法
                } else {
                    System.out.println("跳过禁用的测试: " + method.getName());
                }
            }
        }
    }
}
```

**输出结果：**

```
运行测试: testUserLogin
执行用户登录测试...
跳过禁用的测试: testUserLogout
```

从这个例子可以清晰地看到，**正是因为 `@MyTest`的保留策略是 `RUNTIME`，我们的 `SimpleTestRunner`才能在程序运行的时候，通过反射（`method.getAnnotation`）发现哪些方法被标记了，并根据注解的属性值决定是否执行测试。**

------

### 总结与对比

| 特性                   | `SOURCE`             | `CLASS`（默认）              | `RUNTIME`                           |
| ---------------------- | -------------------- | ---------------------------- | ----------------------------------- |
| **生命周期**           | 源码 -> 编译前       | 源码 -> 编译 -> `.class`文件 | 源码 -> 编译 -> `.class`文件 -> JVM |
| **是否在.class文件中** | 否                   | 是                           | 是                                  |
| **是否在JVM中**        | 否                   | 否                           | 是                                  |
| **反射是否可用**       | 否                   | 否                           | **是**                              |
| **主要用途**           | 编译器检查、代码生成 | 字节码处理工具               | **运行时框架（Spring, JUnit等）**   |

**简单来说：**

- **`@Retention(RetentionPolicy.SOURCE)`**：给**编译器**看的，用完就扔。
- **`@Retention(RetentionPolicy.CLASS)`**：给**编译后处理工具**看的，大部分Java程序员不直接使用。
- **`@Retention(RetentionPolicy.RUNTIME)`**：给**JVM和你的程序在运行时**看的，这是实现框架和复杂功能的关键。

在开发中，如果你希望注解在运行时起作用，**必须**将其声明为 `@Retention(RetentionPolicy.RUNTIME)`。

## 注解 - @Documented

### 核心作用

`@Documented`注解的**核心作用是标记一个注解，使其注解信息能够被包含在生成的 JavaDoc 文档中**。它是一个元注解，本身不包含任何属性。

------

### 问题背景：默认行为

默认情况下，**自定义注解的信息不会出现在 JavaDoc 文档中**。

让我们看一个例子：

#### 1. 定义一个没有 `@Documented`的注解

```
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 注意：这里没有使用 @Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UndocumentedAnnotation {
    String author() default "unknown";
    String version() default "1.0";
}
```

#### 2. 在一个类上使用这个注解

```
/**
 * 这是一个用户服务类，用于处理用户相关的业务逻辑。
 */
@UndocumentedAnnotation(author = "Alice", version = "2.0")
public class UserService {
    
    /**
     * 用户登录方法
     */
    public void login() {
        // 登录逻辑
    }
}
```

#### 3. 生成 JavaDoc 后的效果

当你使用 `javadoc`命令为 `UserService`类生成文档时，在生成的 HTML 页面中，你**不会看到** `@UndocumentedAnnotation`的任何信息。文档只会显示类的描述和方法，就像这个注解不存在一样。

------

### 解决方案：使用 `@Documented`

#### 1. 定义一个使用 `@Documented`的注解

```
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 关键：添加了 @Documented 元注解
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentedAnnotation {
    String author() default "unknown";
    String version() default "1.0";
    String description() default "";
}
```

#### 2. 在另一个类上使用这个注解

```
/**
 * 这是一个订单服务类，用于处理订单相关的业务逻辑。
 */
@DocumentedAnnotation(
    author = "Bob",
    version = "1.5",
    description = "这是订单模块的核心服务类"
)
public class OrderService {
    
    /**
     * 创建订单方法
     */
    public void createOrder() {
        // 创建订单逻辑
    }
}
```

#### 3. 生成 JavaDoc 后的效果

现在，当你为 `OrderService`类生成 JavaDoc 文档时，在类的详细说明部分，**你会看到** `@DocumentedAnnotation`的信息显示在文档中：

```
类 OrderService
java.lang.Object
  └── OrderService

@DocumentedAnnotation(author="Bob", version="1.5", description="这是订单模块的核心服务类")
public class OrderService
extends Object

这是一个订单服务类，用于处理订单相关的业务逻辑。
```

------

### 完整示例对比

让我们创建一个更完整的示例来展示区别：

#### 代码实现

```
import java.lang.annotation.*;

// 不使用 @Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface InvisibleInDoc {
    String value();
}

// 使用 @Documented
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface VisibleInDoc {
    String value();
}

/**
 * 这个类展示了不使用 @Documented 的注解效果。
 */
@InvisibleInDoc("这个注解信息不会出现在JavaDoc中")
class ClassA {
    // 类A的实现
}

/**
 * 这个类展示了使用 @Documented 的注解效果。
 */
@VisibleInDoc("这个注解信息会出现在JavaDoc中")
class ClassB {
    // 类B的实现
}
```

#### 生成 JavaDoc 的步骤和结果

1. 

   **生成文档**：

   ```
   javadoc -d doc Example.java
   ```

2. **查看结果**： 打开 `doc/ClassA.html`：你看不到 `@InvisibleInDoc`注解的任何痕迹 打开 `doc/ClassB.html`：你会清楚地看到 `@VisibleInDoc("这个注解信息会出现在JavaDoc中")`

------

### 与其他元注解的关系

`@Documented`通常与其他元注解一起使用：

```
@Documented // 包含在JavaDoc中
@Target(ElementType.METHOD) // 只能用于方法
@Retention(RetentionPolicy.RUNTIME) // 运行时可用
public @interface ApiOperation {
    String value();
    String notes() default "";
    boolean deprecated() default false;
}
```

这种组合在创建API文档工具（如Swagger）的注解时非常常见。

------

### 重要注意事项

1. **只影响 JavaDoc**：`@Documented`只影响 JavaDoc 工具生成的文档，**不影响注解的运行时行为**。
2. **需要合适的 `@Retention`**：为了让注解信息能出现在 JavaDoc 中，注解至少需要保留到 `CLASS`级别（因为 `javadoc`工具是读取 `.class`文件生成文档的）。通常与 `RUNTIME`一起使用。
3. **不影响代码功能**：无论是否使用 `@Documented`，注解在代码中的功能是完全一样的。
4. **系统注解的例子**：JDK 中的 `@Deprecated`注解就使用了 `@Documented`，所以当你在代码中使用 `@Deprecated`标记一个方法时，该方法在 JavaDoc 中也会显示为已弃用。

------

### 总结

| 方面         | 说明                                                |
| ------------ | --------------------------------------------------- |
| **主要作用** | 让自定义注解的信息出现在 JavaDoc 文档中             |
| **默认行为** | 不加 `@Documented`时，注解信息不会出现在 JavaDoc 中 |
| **使用场景** | 当你希望注解作为API契约的一部分被文档化时           |
| **影响范围** | 只影响文档生成，不影响注解的运行时功能              |
| **常见组合** | 与 `@Target`、`@Retention`等元注解一起使用          |

**简单来说：`@Documented`就是一个开关，决定你的注解是否要在项目的技术文档中"露个脸"。** 对于需要生成详细API文档的库或框架（如Spring、Swagger等），这个注解非常重要。
