# JDK8 新特性



## OpenJDK 和 Oracle JDK

OpenJDK和Oracle JDK都是Java开发套件（JDK），用于开发和运行Java应用程序。以下是两者的详细对比：

一、开源性与授权方式

1. **OpenJDK**
   - 完全开源的项目，基于GPL v2 with Classpath Exception许可证。
   - 任何人都可以自由使用、修改和分发OpenJDK，没有使用限制。
2. **Oracle JDK**
   - 由Oracle公司开发，其源代码与OpenJDK相同，但发行版可能包含专有组件（如高级监控、诊断工具等）。
   - 2019年之后采用商业许可证，免费使用仅限于开发、测试和个人用途，生产环境的使用可能需要购买商业许可证。

二、更新与支持

1. **OpenJDK**
   - 更新频率较高，一般每六个月发布一个新版本。
   - OpenJDK社区会提供长期支持版本（LTS），但维护时间较短，通常由社区或第三方机构提供额外支持。
2. **Oracle JDK**
   - Oracle提供LTS版本的长期支持（通常8年或更长时间），并提供安全补丁和性能优化等企业级支持。
   - 用户可以通过购买许可证获得这些服务。

三、功能与特性

1. **核心功能**
   - OpenJDK和Oracle JDK的核心代码几乎相同，运行时性能差异较小。
   - 两者都能满足Java应用程序的开发和运行需求。
2. **额外功能**
   - Oracle JDK可能会包含一些额外的商业功能和工具，这些在OpenJDK中可能不可用或需要额外安装插件。
   - Oracle JDK通常会包含一些Oracle特有的性能优化和附加功能，特别是针对企业应用的需求。

四、兼容性

1. **OpenJDK**
   - 在大多数情况下，与Oracle JDK具有良好的兼容性。
   - 开发者可以在两者之间切换而不需要修改代码。
   - 但在某些边缘情况下，特定的Oracle JDK专有功能可能会导致兼容性问题。
2. **Oracle JDK**
   - 通常会完全兼容OpenJDK。
   - 同时也会包含一些专有的特性或工具。

五、下载与安装

1. **OpenJDK**
   - 可以通过其官方网站（https://openjdk.org/）下载。
   - 提供多种版本的JDK供用户选择。
   - 下载后需要手动解压缩到指定目录，并配置环境变量。
2. **Oracle JDK**
   - 可以通过Oracle官方网站（https://www.oracle.com/）下载。
   - 在“Resources”或“Java Downloads”页面可以找到最新和历史版本的JDK文件。
   - 下载后通常包含安装程序，可以一键安装并配置环境变量。

六、适用场景

1. **OpenJDK**
   - 适用于大多数开发者，特别是那些对长期支持或企业级功能没有特别需求的场景。
   - 是免费的、开源的，并且由Java社区提供支持和更新。
2. **Oracle JDK**
   - 适用于需要Oracle提供的专有功能或企业级支持的场景，如大型企业级应用、对性能有极高要求的场景等。
   - 用户可以通过购买Oracle的商业许可证来获得长期支持、安全补丁和性能优化等服务。

综上所述，OpenJDK和Oracle JDK各有优势，开发者在选择时应根据自己的实际需求和项目特点进行权衡。



## Lambda 表达式

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/blob/master/demo-java/jdk8-new-features/src/test/java/com/future/demo/jdk8/lambda/LambdaTests.java`



### 介绍

Java Lambda 表达式是Java 8引入的一项重大特性，它提供了一种简洁而强大的方式来表示匿名函数（即没有名称的函数）。Lambda 表达式允许你将一个函数作为方法的参数，或者将代码作为数据对待。

**Lambda 表达式的基本语法**

Lambda 表达式的基本语法如下：

```java
(parameters) -> expression
```

或者

```java
(parameters) -> { statements; }
```

- `parameters`：参数列表。参数类型可以省略，因为编译器可以从上下文推断出来。
- `->`：Lambda 运算符，用于分隔参数列表和 Lambda 体。
- `expression` 或 `{ statements; }`：Lambda 体。如果 Lambda 体包含单个表达式，则表达式的计算结果就是 Lambda 表达式的返回值；如果 Lambda 体包含多个语句，则必须使用花括号 `{}` 将这些语句括起来，并且需要使用 `return` 语句（如果需要返回值的话）来指定返回值。

**Lambda 表达式的示例**

1. **无参数、无返回值**

```java
Runnable r1 = () -> System.out.println("Hello World!");
```

1. **一个参数、无返回值**

```java
Consumer<String> consumer = (x) -> System.out.println(x);
```

注意：单个参数时，小括号可以省略。

```java
Consumer<String> consumer = x -> System.out.println(x);
```

1. **两个参数、有返回值**

```java
Comparator<Integer> comparator = (x, y) -> {
    return Integer.compare(x, y);
};
```

注意：当 Lambda 体包含多个语句时，必须使用花括号 `{}`。

1. **使用已有的方法或构造函数**

```java
Function<String, Integer> strlen = String::length;
```

这里使用了方法引用，它是 Lambda 表达式的一种简洁写法，用于引用已有方法或构造函数。

**Lambda 表达式与函数式接口**

Lambda 表达式通常与函数式接口一起使用。函数式接口是只包含一个抽象方法的接口，这样的接口可以被隐式地转换为 Lambda 表达式。Java 8 提供了一些常用的函数式接口，如 `Function`、`Consumer`、`Supplier`、`Predicate` 等，它们位于 `java.util.function` 包中。

**使用场景**

Lambda 表达式在 Java 中有广泛的应用场景，包括但不限于：

- **集合操作**：如 `filter`、`map`、`reduce` 等操作，可以使用 Lambda 表达式来简化代码。
- **事件处理**：在图形用户界面（GUI）编程中，可以使用 Lambda 表达式来处理事件。
- **线程**：可以使用 Lambda 表达式来简化线程的创建和启动。

**注意事项**

- Lambda 表达式中的局部变量必须是 `final` 或等效于 `final` 的（即在初始化后不再被修改）。
- Lambda 表达式不能访问外部类的非静态成员变量，除非这些变量被声明为 `final` 或等效于 `final` 的。
- Lambda 表达式不能抛出受检异常（checked exception），但可以抛出非受检异常（unchecked exception）。

总之，Lambda 表达式为 Java 语言带来了更加简洁和灵活的表达方式，使得开发者能够编写出更加清晰和易于维护的代码。



### 使用匿名内部类存在的问题

使用匿名内部类语法是很冗余的。

LambdaWithArgsInterface 类

```java
@FunctionalInterface
public interface LambdaWithArgsInterface {
    int add(int a, int b);

    // 因为接口使用@FunctionalInterface标记，只支持一个接口有且只有一个抽象方法
    // int sub(int a, int b);

    default String defaultMethod(String str) {
        return "echo:" + str;
    }
}
```

匿名内部类存在的问题

```java
@Test
public void testWhyLambda() {
    // 为何需要lambda表达式？下面演示使用匿名类实例时，代码不够精简，使用精简的lambda表达式代替

    int a = 1;
    int b = 2;
    // 使用匿名内部类语法是很冗余的
    int intReturn = new LambdaWithArgsInterface() {
        @Override
        public int add(int a, int b) {
            return a + b;
        }
    }.add(a, b);
    Assert.assertEquals(a + b, intReturn);

    // Lambda表达式的好处：可以简化匿名内部类，让代码更加精简
    intReturn = ((LambdaWithArgsInterface) (a1, b1) -> a1 + b1).add(a, b);
    Assert.assertEquals(a + b, intReturn);
}
```



### 无参数有返回值的 Lambda 表达式

LambdaWithoutArgsInterface 接口

```java
/**
 * 演示无参数lambda用法
 */
@FunctionalInterface
public interface LambdaWithoutArgsInterface {
    String echo();
}
```

`() -> str`为无参数有返回值的 Lambda 表达式

```java
// 测试无参数有返回值的 Lambda 表达式
@Test
public void testLambdaWithoutArgumentAndWithReturnValue() {
    String str = "echo:";
    LambdaWithoutArgsInterface withoutArgsInterface = () -> str;
    Assert.assertEquals(str, withoutArgsInterface.echo());

    Assert.assertEquals(str, ((LambdaWithoutArgsInterface)()->str).echo());
}
```



### 有参数和有返回值的 Lambda 表达式

LambdaWithArgsInterface 接口

```java
/**
 * 演示lambda用法和有参数lambda用法
 */
// 函数接口约束注解，表示这个接口有且只有一个抽象方法
@FunctionalInterface
public interface LambdaWithArgsInterface {
    int add(int a, int b);

    // 因为接口使用@FunctionalInterface标记，只支持一个接口有且只有一个抽象方法
    // int sub(int a, int b);
    default String defaultMethod(String str) {
        return "echo:" + str;
    }
}
```

`(a, b) -> a + b`为有参数有返回值 Lambda 表达式

```java
// 测试有参数有返回值的 Lambda 表达式
@Test
public void testLambdaWithArgumentAndReturnValue() {
    LambdaWithArgsInterface lambdaWithArgsInterface = (a, b) -> a + b;
    Assert.assertEquals(3, lambdaWithArgsInterface.add(1, 2));
}
```



### 使用 Lambda 表达式对集合排序

```java
// 使用 Lambda 表达式对集合排序
@Test
public void testListSortedByUsingLambda() {
    // 使用lambda遍历集合
    List<String> stringList = new ArrayList<>();
    stringList.add("01");
    stringList.add("02");
    stringList.add("03");

    List<String> stringList2 = new ArrayList<>();
    stringList.forEach(str -> stringList2.add(str));
    Assert.assertArrayEquals(stringList.toArray(), stringList2.toArray());

    List<ListEntry> listEntryList = new ArrayList<>();
    listEntryList.add(new ListEntry(2));
    listEntryList.add(new ListEntry(3));
    listEntryList.add(new ListEntry(9));
    listEntryList.add(new ListEntry(7));
    //  使用 Lambda 表达式对集合排序
    listEntryList.sort((o1, o2) -> o1.getNumber() - o2.getNumber());
    Assert.assertEquals(2, listEntryList.get(0).getNumber());
    Assert.assertEquals(3, listEntryList.get(1).getNumber());
    Assert.assertEquals(7, listEntryList.get(2).getNumber());
    Assert.assertEquals(9, listEntryList.get(3).getNumber());
}
```



### 强制类型转换 Lambda 表达式

LambdaWithoutArgsInterface 接口

```java
@FunctionalInterface
public interface LambdaWithoutArgsInterface {
    String echo();
}
```

LambdaWithArgsInterface 接口

```java
@FunctionalInterface
public interface LambdaWithArgsInterface {
    int add(int a, int b);

    // 因为接口使用@FunctionalInterface标记，只支持一个接口有且只有一个抽象方法
    // int sub(int a, int b);
    default String defaultMethod(String str) {
        return "echo:" + str;
    }
}
```

```java
@Test
public void test() {
    String str = "echo:";
    Assert.assertEquals(str, ((LambdaWithoutArgsInterface) () -> str).echo());
    
    int a = 1;
    int b = 2;
    int intReturn = ((LambdaWithArgsInterface) (a1, b1) -> a1 + b1).add(a, b);
    Assert.assertEquals(a + b, intReturn);
}
```



### Lambda 表达式编译原理

以下是从反编译角度对Java Lambda原理进行的一个实例分析：

**原始Java代码**

```java
import java.util.Arrays;
import java.util.List;
 
public class LambdaExample {
    public static void main(String[] args) {
        List<String> items = Arrays.asList("Apple", "Banana", "Cherry");
        items.forEach(item -> System.out.println(item));
    }
}
```

这段代码使用Lambda表达式来遍历并打印一个字符串列表。

**反编译过程**

1. **使用反编译工具**：
   使用如CFR（Class File Reader）这样的反编译工具，可以将Java的.class文件反编译回接近原始的Java源代码。

2. **反编译结果**

   反编译后的代码可能类似于以下形式（具体输出可能因反编译工具而异）：

```java
import java.lang.invoke.LambdaMetafactory;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
 
public class LambdaExample {
    public static void main(String[] stringArray) {
        List<String> list = Arrays.asList("Apple", "Banana", "Cherry");
        list.forEach((Consumer<String>) LambdaMetafactory.metafactory(
            null, 
            null, 
            null, 
            (Ljava/lang/Object;)V, 
            LambdaExample.lambda$main$0(java.lang.String), 
            (Ljava/lang/String;)V
        ).invokeExact(LambdaExample::lambda$main$0));
    }
 
    private static /* synthetic */ void lambda$main$0(String string) {
        System.out.println(string);
    }
}
```

**分析**

1. **LambdaMetafactory的使用**：
   在反编译后的代码中，Lambda表达式被转换成了对`LambdaMetafactory.metafactory`方法的调用。这个方法在运行时动态生成了一个实现了`Consumer`接口的类的实例。`forEach`方法的入参就是一个函数式接口`Consumer<? super T>`，因此最终返回的是`Consumer`实例对象。
2. **隐藏方法和类**：
   编译器生成了一个名为`lambda$main$0`的私有静态方法，这个方法封装了Lambda表达式的逻辑（即打印字符串）。这个方法在运行时由`LambdaMetafactory`动态调用。
3. **invokedynamic指令**：
   虽然反编译后的代码没有直接显示invokedynamic指令，但`LambdaMetafactory.metafactory`方法的调用在底层是通过invokedynamic指令实现的。这个指令告诉JVM在运行时动态链接到`lambda$main$0`方法。
4. **类型检查和转换**：
   `LambdaMetafactory`在创建方法句柄时，会进行动态类型检查与转换，以确保Lambda表达式的类型与`Consumer`接口的类型相匹配。在这个例子中，Lambda表达式的类型是`(String) -> void`，这与`Consumer<String>`接口的类型相匹配。
5. **捕获外部变量**：
   在这个例子中，Lambda表达式没有捕获任何外部变量。但在其他情况下，如果Lambda表达式捕获了外部变量，这些变量会被封装在一个合成的类中，并通过引用来访问。

**结论**

从反编译的角度来看，Java Lambda表达式的原理涉及编译器的特殊处理（生成隐藏方法和类）、`LambdaMetafactory`的动态生成机制以及invokedynamic指令的使用。这些机制共同使得Lambda表达式能够在Java中以一种简洁、灵活的方式实现函数式编程。通过反编译工具和分析字节码，我们可以更深入地理解Lambda表达式的底层实现和工作原理。



### Lambda 表达式的省略格式

在 Lambda 标准格式的基础上，使用省略写法的规则为：

- 小括号内参数的类型可以省略
- 如果小括号内有且仅有一个参数，则小括号可以省略
- 如果大括号内有且仅有一个语句，可以同时省略大括号、return 关键字及语句分号

```java
// 测试 Lambda 表达式省略写法
@Test
public void testLambdaAbbreviation() {
    List<Integer> list = new ArrayList<>();
    list.add(1);
    list.add(2);
    list.add(3);

    // 非省略写法
    list.sort(new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    });
    // 省略写法
    // 小括号内参数的类型可以省略
    // 如果大括号内有且仅有一个语句，可以同时省略大括号、return 关键字及语句分号
    list.sort((o1, o2) -> o1 - o2);

    // 非省略写法
    list.forEach((e) -> {
        System.out.println(e);
    });
    // 省略写法
    // 如果小括号内有且仅有一个参数，则小括号可以省略
    // 如果大括号内有且仅有一个语句，可以同时省略大括号、return 关键字及语句分号
    list.forEach(e -> System.out.println(e));
}
```



### @FunctionalInterface 注解

`@FunctionalInterface` 是 Java 8 引入的一个注解，用于指示某个接口是函数式接口。函数式接口是指仅包含一个抽象方法（不包括 Object 类中的方法，如 `toString`、`hashCode` 和 `equals` 等）的接口。由于这个特性，函数式接口可以被隐式地转换为 lambda 表达式或方法引用。

**函数式接口的特点**

1. **单一抽象方法**：除了 `Object` 类中的方法外，函数式接口只能有一个抽象方法。如果接口中定义了多个抽象方法，则编译器会报错，提示该接口不是有效的函数式接口。
2. **Lambda 表达式支持**：函数式接口可以被 lambda 表达式实现。这使得编写简洁的匿名函数成为可能，尤其是在集合操作、线程处理等场景中。
3. **方法引用**：方法引用是 lambda 表达式的一种简洁形式，可以直接引用已经存在的方法或构造函数。函数式接口使得方法引用成为可能。

**使用 `@FunctionalInterface` 注解**

- **强制约束**：虽然 Java 编译器会自动检查一个接口是否为函数式接口，但使用 `@FunctionalInterface` 注解可以显式地声明这一点，增加代码的可读性和可维护性。如果接口不符合函数式接口的定义（即包含多个抽象方法），编译器会报错。
- **文档化**：注解也为接口的使用者提供了明确的文档说明，表明该接口是设计为函数式接口，应该使用 lambda 表达式或方法引用来实现。

**示例**

下面是一个简单的函数式接口示例，用于表示一个操作，该操作接受两个整数并返回一个整数：

```java
@FunctionalInterface
public interface BinaryOperator<T> {
    T apply(T a, T b);
}
```

使用 lambda 表达式实现这个接口：

```java
BinaryOperator<Integer> addition = (a, b) -> a + b;
System.out.println(addition.apply(2, 3)); // 输出 5
```

或者使用方法引用：

```java
BinaryOperator<String> concatenation = String::concat;
System.out.println(concatenation.apply("Hello, ", "World!")); // 输出 Hello, World!
```

**总结**

`@FunctionalInterface` 注解在 Java 中用于显式声明一个接口为函数式接口，确保接口只包含一个抽象方法。这有助于利用 lambda 表达式和方法引用的特性，编写更加简洁和易读的代码。



### 支持使用 Lambda 表达式的条件

条件如下：

- 方法的参数或者局部变量类型必须为接口才能使用 Lambda 表达式
- 接口中有且仅有一个抽象方法

```java
// 支持使用 Lambda 表达式的条件
@Test
public void testSupportUsingLambdaSituation() {
    // region 方法的参数或者局部变量类型必须为接口才能使用 Lambda 表达式

    testMethod1(() -> System.out.println("Hello world!"));

    Interface1 interface1 = () -> System.out.println("Hello world2!");
    interface1.method1();

    // endregion
}

void testMethod1(Interface1 interface1) {
    interface1.method1();
}

@FunctionalInterface
interface Interface1 {
    void method1();
    
    // 接口中有且仅有一个抽象方法
    // void method2();
}
```



## 接口的默认方法



### 介绍

在 Java 8 中，接口得到了一个重要的增强，即引入了**默认方法**（default methods）。这一特性允许在接口中定义具有具体实现的方法，从而解决了之前接口无法包含实现代码的局限性。默认方法的引入主要是为了帮助平滑地过渡旧代码，尤其是在需要向现有接口添加新方法时，而不破坏实现这些接口的现有类。

**语法**

默认方法的声明语法如下：

```java
public interface MyInterface {
    // 这是一个默认方法
    default void myDefaultMethod() {
        System.out.println("This is a default method.");
    }
 
    // 其他抽象方法
    void myAbstractMethod();
}
```

**特点**

1. **默认方法使用 `default` 关键字**：在方法声明之前添加 `default` 关键字。
2. **可以有具体实现**：默认方法可以有自己的实现代码。
3. **可以被覆盖**：实现接口的类可以覆盖默认方法，提供自己的实现。
4. **解决二义性问题**：当一个类实现了多个接口，而这些接口中包含有默认方法的签名相同时，该类必须覆盖这些默认方法，以消除歧义。

**示例**

下面是一个完整的示例，展示了如何定义和使用接口默认方法：

```java
// 定义一个接口
public interface MyInterface {
    // 抽象方法
    void myAbstractMethod();
 
    // 默认方法
    default void myDefaultMethod() {
        System.out.println("This is a default method in MyInterface.");
    }
}
 
// 实现接口的类
public class MyClass implements MyInterface {
    // 实现抽象方法
    @Override
    public void myAbstractMethod() {
        System.out.println("This is the implementation of myAbstractMethod.");
    }
 
    // （可选）覆盖默认方法
    @Override
    public void myDefaultMethod() {
        System.out.println("This is the overridden default method in MyClass.");
    }
 
    public static void main(String[] args) {
        MyClass myClass = new MyClass();
        myClass.myAbstractMethod();
        myClass.myDefaultMethod();
    }
}
```

**输出**

```
This is the implementation of myAbstractMethod.
This is the overridden default method in MyClass.
```

**注意事项**

1. **不要滥用默认方法**：默认方法虽然强大，但不应滥用。它们应该主要用于向后兼容和提供默认行为，而不是作为常规的方法定义。
2. **多接口冲突**：如果一个类实现了多个接口，而这些接口中有相同的默认方法签名，那么这个类必须覆盖这个方法，以避免编译错误。
3. **抽象类 vs 接口**：尽管默认方法使接口更强大，但在某些情况下，使用抽象类可能仍然是更好的选择，特别是当你需要更复杂的继承层次和状态保持时。

通过引入默认方法，Java 8 提供了更灵活的接口设计，使得在不破坏现有代码的情况下向接口添加新方法成为可能。



### 用法

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/jdk8-new-features/src/main/java/com/future/demo/jdk8/interfaceu`

Jdk8Interface 接口

```java
/**
 * 演示jdk8支持接口默认方法和静态方法
 *
 * Oracle官方默认方法和静态方法详解
 * https://docs.oracle.com/javase/tutorial/java/IandI/defaultmethods.html
 */
public interface Jdk8Interface {
    int add(int a, int b);

    default String defaultMethod(String str) {
        return "echo:" + str;
    }
}
```

Jdk8InterfaceImpl 实现类

```java
public class Jdk8InterfaceImpl implements Jdk8Interface{
    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
```

Jdk8InterfaceImplTests 测试

```java
public class Jdk8InterfaceImplTests {
    @Test
    public void test() {
        Jdk8Interface jdk8Interface = new Jdk8InterfaceImpl();

        int a = 1;
        int b = 2;
        int intReturn = jdk8Interface.add(a, b);
        Assert.assertEquals(a + b, intReturn);

        String str = "8888";
        String strReturn = jdk8Interface.defaultMethod(str);
        Assert.assertEquals(String.format("echo:%s", str), strReturn);
    }
}

```



## 接口的静态方法

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/jdk8-new-features/src/main/java/com/future/demo/jdk8/interfaceu`

Jdk8Interface 接口

```java
/**
 * 演示jdk8支持接口默认方法和静态方法
 *
 * Oracle官方默认方法和静态方法详解
 * https://docs.oracle.com/javase/tutorial/java/IandI/defaultmethods.html
 */
public interface Jdk8Interface {
    static String staticMethod(String str) {
        return "echo:" + str;
    }
}
```

Jdk8InterfaceImplTests 测试

```java
public class Jdk8InterfaceImplTests {
    @Test
    public void test() {
        String str = "8888";

        strReturn = Jdk8Interface.staticMethod(str);
        Assert.assertEquals(String.format("echo:%s", str), strReturn);
    }
}

```



## 内置的函数式接口



### 介绍

Java 的内置函数式接口是 Java 8 引入的一个关键特性，用于支持 Lambda 表达式和方法引用。这些接口定义在 `java.util.function` 包中，每个接口都代表了一种特定的函数签名。以下是 Java 内置的主要函数式接口：

1. `java.util.function.Function<T,R>`
   - 代表一个接受一个参数并产生一个结果的函数。
   - 主要方法：`R apply(T t)`
2. `java.util.function.Consumer<T>`
   - 代表一个接受单个输入参数并且不返回结果的操作。
   - 主要方法：`void accept(T t)`
3. `java.util.function.Supplier<T>`
   - 代表一个无参数且不返回结果的操作，但会返回一个值。
   - 主要方法：`T get()`
4. `java.util.function.Predicate<T>`
   - 代表一个参数的布尔值函数。
   - 主要方法：`boolean test(T t)`
5. `java.util.function.UnaryOperator<T>`
   - 代表一个操作，它接受一个类型的参数并返回相同类型的结果。是 `Function<T,T>` 的一个特化。
   - 主要方法：`T apply(T t)`
6. `java.util.function.BinaryOperator<T>`
   - 代表一个操作，它接受两个相同类型的参数并返回一个相同类型的结果。是 `BiFunction<T,T,T>` 的一个特化。
   - 主要方法：`T apply(T t1, T t2)`
7. `java.util.function.BiFunction<T,U,R>`
   - 代表一个接受两个参数并产生一个结果的函数。
   - 主要方法：`R apply(T t, U u)`
8. `java.util.function.BiConsumer<T,U>`
   - 代表一个接受两个输入参数并且不返回结果的操作。
   - 主要方法：`void accept(T t, U u)`
9. `java.util.function.ToDoubleFunction<T>`
   - 代表一个接受一个参数并产生一个 `double` 结果的函数。
   - 主要方法：`double applyAsDouble(T value)`
10. `java.util.function.ToIntFunction<T>`
    - 代表一个接受一个参数并产生一个 `int` 结果的函数。
    - 主要方法：`int applyAsInt(T value)`
11. `java.util.function.ToLongFunction<T>`
    - 代表一个接受一个参数并产生一个 `long` 结果的函数。
    - 主要方法：`long applyAsLong(T value)`
12. `java.util.function.ToDoubleBiFunction<T,U>`
    - 代表一个接受两个参数并产生一个 `double` 结果的函数。
    - 主要方法：`double applyAsDouble(T t, U u)`
13. `java.util.function.ToIntBiFunction<T,U>`
    - 代表一个接受两个参数并产生一个 `int` 结果的函数。
    - 主要方法：`int applyAsInt(T t, U u)`
14. `java.util.function.ToLongBiFunction<T,U>`
    - 代表一个接受两个参数并产生一个 `long` 结果的函数。
    - 主要方法：`long applyAsLong(T t, U u)`
15. `java.util.function.ObjIntConsumer<T>`
    - 代表一个接受一个对象和一个 `int` 参数并且不返回结果的操作。
    - 主要方法：`void accept(T t, int value)`
16. `java.util.function.ObjLongConsumer<T>`
    - 代表一个接受一个对象和一个 `long` 参数并且不返回结果的操作。
    - 主要方法：`void accept(T t, long value)`
17. `java.util.function.ObjDoubleConsumer<T>`
    - 代表一个接受一个对象和一个 `double` 参数并且不返回结果的操作。
    - 主要方法：`void accept(T t, double value)`

这些接口极大地简化了使用 Lambda 表达式和方法引用的场景，使代码更加简洁和易读。例如，使用 `Function<T,R>` 接口可以方便地表示一个转换操作，而 `Consumer<T>` 接口则可以用于表示一个无返回值的操作。



### 用法

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/jdk8-new-features/src/main/java/com/future/demo/jdk8/builtin/function`

```java
@Slf4j
public class BuiltinFunctionTests {
    @Test
    public void test() {
        // region 内置函数式接口 Supplier

        Supplier<String> supplier = () -> "Hello world!";
        String str = supplier.get();
        Assert.assertEquals("Hello world!", str);

        // endregion

        // region 内置函数式接口 Consumer

        Consumer<String> consumer = param1 -> log.debug(param1.toUpperCase());
        consumer.accept("Hello world!");

        // endregion

        // region 内置函数式接口 Function

        // 使用 Function 接口实现 (a+b)*c
        Function<BigDecimal[], BigDecimal[]> function1 = param1 -> new BigDecimal[]{param1[2], param1[0].add(param1[1])};
        Function<BigDecimal[], BigDecimal> function2 = param1 -> param1[0].multiply(param1[1]);
        BigDecimal result = function1.andThen(function2).apply(new BigDecimal[]{new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3")});
        Assert.assertEquals(new BigDecimal("9"), result);

        // endregion

        // region 内置函数式接口 Predicate

        Predicate<String> predicate = param1 -> param1.length() > 3;
        boolean b = predicate.test("Hello");
        Assert.assertTrue(b);
        b = predicate.test("He");
        Assert.assertFalse(b);

        // endregion
    }
}
```



## 方法引用



### 介绍

在 Java 中，方法引用（Method References）是 Java 8 引入的一项特性，它提供了一种简洁的方式来引用方法或构造函数。方法引用是 Lambda 表达式的一种简洁写法，主要用于与函数式接口（Functional Interface）一起使用。函数式接口是只包含一个抽象方法的接口。

方法引用主要有四种形式：

1. **静态方法引用** - 使用类名来引用静态方法。

   ```java
   Integer::parseInt
   ```
   
2. **特定对象的实例方法引用** - 使用特定对象来引用实例方法。

   ```java
   myString::length
   ```
   
3. **特定类型的任意对象的实例方法引用** - 使用类名来引用该类型的任意对象的实例方法。

   ```java
   String::length
   ```
   
4. **构造函数引用** - 使用类名来引用构造函数。

   ```java
   ArrayList::new
   ```

**示例代码**

静态方法引用

```java
import java.util.function.Function;
 
public class MethodReferenceExample {
    public static void main(String[] args) {
        Function<String, Integer> parseIntFunction = Integer::parseInt;
        Integer result = parseIntFunction("123");
        System.out.println(result); // 输出: 123
    }
}
```

特定对象的实例方法引用

```java
import java.util.function.Consumer;
 
public class MethodReferenceExample {
    public static void main(String[] args) {
        String myString = "Hello, World!";
        Consumer<Void> printLengthConsumer = myString::length; // 这里需要适配一下，因为 length 返回 int，Consumer 接受 void
        int length = printLengthConsumer.accept(null); // 传入 null，因为我们实际上不依赖 Consumer 的输入参数
        System.out.println(length); // 输出: 13
    }
}
```

注意：上面的例子只是为了展示语法，实际应用中通常不会这样使用。更常见的做法是：

```java
Consumer<String> printLengthConsumer = s -> System.out.println(s.length());
printLengthConsumer.accept("Hello, World!"); // 输出: 13
```

特定类型的任意对象的实例方法引用

```java
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
 
public class MethodReferenceExample {
    public static void main(String[] args) {
        List<String> strings = Arrays.asList("apple", "banana", "cherry");
        Function<String, Integer> stringLengthFunction = String::length;
        List<Integer> lengths = strings.stream().map(stringLengthFunction).collect(Collectors.toList());
        System.out.println(lengths); // 输出: [5, 6, 6]
    }
}
```

构造函数引用

```java
import java.util.function.Supplier;
import java.util.ArrayList;
 
public class MethodReferenceExample {
    public static void main(String[] args) {
        Supplier<ArrayList<String>> arrayListSupplier = ArrayList::new;
        ArrayList<String> list = arrayListSupplier.get();
        list.add("Hello");
        System.out.println(list); // 输出: [Hello]
    }
}
```

**总结**

方法引用是 Java 8 中引入的一种简洁的语法糖，主要用于替代简单的 Lambda 表达式。通过使用方法引用，可以使代码更加简洁、易读。它主要有四种形式：静态方法引用、特定对象的实例方法引用、特定类型的任意对象的实例方法引用和构造函数引用。



### 为何 JDK8 引入方法引用特性

```java
// 测试为何JDK8引入方法引用特性
@Test
public void testWhyIntroduceMethodReferencesFeature() {
    // 不使用方法引用的函数式接口，代码冗长
    Supplier<Integer> supplierMaximumInteger1 = () -> {
        Integer[] integerArr = new Integer[]{23, 2, 54, 19};
        Integer maximumValue = null;
        for (Integer i : integerArr) {
            if (maximumValue == null) {
                maximumValue = i;
            }

            if (i > maximumValue) {
                maximumValue = i;
            }
        }

        return maximumValue;
    };
    Assert.assertEquals(Integer.valueOf(54), supplierMaximumInteger1.get());

    // 使用方法引用的函数式接口，代码简洁
    Supplier<Integer> supplierMaximumInteger2 = MethodReferencesTests::getMaximumInteger;
    Assert.assertEquals(Integer.valueOf(54), supplierMaximumInteger2.get());
}
```



### 实例方法引用

```java
/**
 * 实例方法引用
 */
@Test
public void test_instance_method_reference() {
    TestClassInstanceMethodReference instance = new TestClassInstanceMethodReference(5);
    Jdk8Interface jdk8Interface = instance::testAdd;
    int result = jdk8Interface.add(1, 2);
    Assert.assertEquals(3 + instance.getAdditional(), result);

    Date now = new Date();
    Supplier<Long> supplier = now::getTime;
    Long milliseconds = supplier.get();
    log.debug("milliseconds=" + milliseconds);
}

static class TestClassInstanceMethodReference {
    private final int additional;

    public TestClassInstanceMethodReference(int additional) {
        this.additional = additional;
    }

    public int getAdditional() {
        return this.additional;
    }

    int testAdd(int a, int b) {
        return a + b + additional;
    }
}
```



### 静态方法引用

```java
/**
 * 静态方法引用
 */
@Test
public void test_static_method_reference() {
    Jdk8Interface jdk8Interface = MethodReferencesTests::testAdd;
    int result = jdk8Interface.add(1, 2);
    Assert.assertEquals(3, result);

    Supplier<Long> supplier = System::currentTimeMillis;
    Long milliseconds = supplier.get();
    log.debug("milliseconds=" + milliseconds);
}

static int testAdd(int a, int b) {
    return a + b;
}
```



### 类名引用实例方法

```java
/**
 * 类名引用实例方法
 * 注意：实际上是将方法调用第一个参数作为实例引用方法的调用者，例如：将 function1.apply("Hello") 方法调用第一个参数 Hello 作为 String 实例引用方法的调用者 "Hello".length()
 */
@Test
public void test_instance_method_reference_of_particular_type() {
    InstanceMethodReferenceOfParticularTypeInterface referenceInterface = InstanceMethodReferenceOfParticularTypeClass::getStr;
    // 相当于调用 new InstanceMethodReferenceOfParticularTypeClass("测试") 实例的 getStr 方法
    Assert.assertEquals("测试", referenceInterface.get(new InstanceMethodReferenceOfParticularTypeClass("测试")));

    Function<String, Integer> function1 = String::length;
    // 相当于调用 "Hello".length() 方法
    Integer length = function1.apply("Hello");
    Assert.assertEquals(Integer.valueOf(5), length);

    BiFunction<String, Integer, String> biFunction = String::substring;
    // 相当于调用 "Hello World!".substring(3) 方法
    String subStr = biFunction.apply("Hello World!", 3);
    Assert.assertEquals("lo World!", subStr);
}
```



### 构造方法引用

```java
public class ConstructorMethodReferenceEntity {
    private String str;

    public ConstructorMethodReferenceEntity(String str) {
        this.str = str;
    }

    public String getStr() {
        return this.str;
    }
}
```

```java
public interface ConstructorMethodReferenceInterface {
    ConstructorMethodReferenceEntity get(String str);
}
```

```java
/**
 * 构造方法引用
 */
@Test
public void test_constructor_method_reference() {
    ConstructorMethodReferenceInterface referenceInterface = ConstructorMethodReferenceEntity::new;
    Assert.assertEquals("测试", referenceInterface.get("测试").getStr());
}
```



### 数组构造器方法引用

```java
// 数组构造器方法引用
@Test
public void testArrayConstructorMethodReference() {
    Function<Integer, String[]> function = String[]::new;
    String[] strArr = function.apply(10);
    Assert.assertEquals(10, strArr.length);
}
```