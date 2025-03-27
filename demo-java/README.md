# Java



## 对象的 hashcode 和 equals

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



## JDK API

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

