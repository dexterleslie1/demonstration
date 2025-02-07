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



## 集合 Stream 操作



### 介绍

Java Stream 是 Java 8 及更高版本引入的一个强大的特性，它允许你以声明式的方式处理集合数据。  它提供了一种简洁、高效且并行友好的方式来执行常见的集合操作，例如过滤、映射、排序、聚合等等。

以下是 Java Stream 的一些核心概念和常用操作：

**1. 创建 Stream:**

你可以从各种数据源创建 Stream，例如：

* **集合:**  `list.stream()`  或者 `list.parallelStream()` (并行流)
* **数组:** `Arrays.stream(array)`
* **文件:** `Files.lines(path)`
* **数值范围:** `IntStream.range(1, 10)`  (1 到 9)
* **其他:**  许多类都提供方法生成 Stream，例如 `String.chars()`

**2. 中间操作:**  中间操作返回一个新的 Stream，可以链式调用。  常见的中间操作包括：

* **`filter(Predicate)`:**  过滤元素，保留满足条件的元素。
* **`map(Function)`:**  将元素映射到另一个对象。
* **`flatMap(Function)`:**  将元素映射到一个 Stream，然后将所有 Stream  “扁平化”成一个 Stream。
* **`sorted()`:**  对元素排序。
* **`distinct()`:**  去除重复元素。
* **`limit(n)`:**  限制返回的元素数量。
* **`skip(n)`:**  跳过前 n 个元素。
* **`peek(Consumer)`:**  对每个元素执行一个操作，但不会改变 Stream 本身。  主要用于调试。


**3. 终端操作:**  终端操作会产生一个结果，并且会关闭 Stream。  常见的终端操作包括：

* **`collect(Collector)`:**  将 Stream 元素收集到一个集合中 (例如 `Collectors.toList()`, `Collectors.toSet()`, `Collectors.joining(",")`)
* **`forEach(Consumer)`:**  对每个元素执行一个操作。
* **`reduce(BinaryOperator)`:**  将 Stream 元素归约成一个单一值。
* **`count()`:**  返回 Stream 中元素的数量。
* **`min(Comparator)`:**  返回 Stream 中最小元素。
* **`max(Comparator)`:**  返回 Stream 中最大元素。
* **`findFirst()`:**  返回 Stream 中第一个元素 (Optional)。
* **`findAny()`:**  返回 Stream 中任意一个元素 (Optional)。
* **`allMatch(Predicate)`:**  判断所有元素是否都满足条件。
* **`anyMatch(Predicate)`:**  判断是否存在至少一个元素满足条件。
* **`noneMatch(Predicate)`:**  判断是否没有元素满足条件。


**4.  例子:**

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // 筛选出偶数，然后求平方，最后收集到一个新的列表
        List<Integer> evenSquares = numbers.stream()
                .filter(n -> n % 2 == 0)
                .map(n -> n * n)
                .collect(Collectors.toList());

        System.out.println(evenSquares); // 输出：[4, 16, 36, 64, 100]


        // 计算所有数字的和
        int sum = numbers.stream().mapToInt(Integer::intValue).sum();
        System.out.println(sum); // 输出：55
    }
}
```

这个只是 Java Stream 的一个简要介绍，它还有许多更高级的特性，例如并行流、自定义 Collector 等，需要进一步学习和实践才能掌握。 你可以参考 Java 官方文档或其他学习资源来深入了解。  请问你对 Java Stream 的哪个方面特别感兴趣，或者你想了解哪个具体的例子？



### 为何引入集合 Stream 操作

JDK 8 引入集合 Stream 操作是为了提升 Java 集合处理的效率、可读性和表达能力。  传统上，处理集合需要大量的循环和临时变量，导致代码冗长且难以理解。Stream API 使用声明式编程风格，让代码更简洁、易读，并支持并行处理以提高性能。

**例子：查找一个学生列表中所有成绩大于 90 分的学生姓名。**

**传统方法 (使用循环):**

```java
import java.util.ArrayList;
import java.util.List;

class Student {
    String name;
    int score;

    public Student(String name, int score) {
        this.name = name;
        this.score = score;
    }
}

public class TraditionalApproach {
    public static void main(String[] args) {
        List<Student> students = new ArrayList<>();
        students.add(new Student("Alice", 85));
        students.add(new Student("Bob", 92));
        students.add(new Student("Charlie", 98));
        students.add(new Student("David", 78));

        List<String> highScoreStudents = new ArrayList<>();
        for (Student student : students) {
            if (student.score > 90) {
                highScoreStudents.add(student.name);
            }
        }
        System.out.println(highScoreStudents); // 输出：[Bob, Charlie]
    }
}
```

**Stream API 方法:**

```java
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class Student {
    String name;
    int score;

    public Student(String name, int score) {
        this.name = name;
        this.score = score;
    }
}

public class StreamApproach {
    public static void main(String[] args) {
        List<Student> students = new ArrayList<>();
        students.add(new Student("Alice", 85));
        students.add(new Student("Bob", 92));
        students.add(new Student("Charlie", 98));
        students.add(new Student("David", 78));

        List<String> highScoreStudents = students.stream()
                .filter(student -> student.score > 90)
                .map(student -> student.name)
                .collect(Collectors.toList());

        System.out.println(highScoreStudents); // 输出：[Bob, Charlie]
    }
}
```

对比可以看出，Stream API 版本更简洁、更易读。它将筛选和映射操作清晰地表达为链式调用，避免了显式的循环和临时变量。  对于更复杂的数据处理，Stream API 的优势更加明显，并且它还支持并行化，可以进一步提高处理效率，特别是当学生列表非常庞大时。  这就是 JDK 8 引入 Stream 的主要原因之一：提供一种更优雅、高效的数据处理方式。



### 获取 Stream 的方法

```java
// 获取 Stream 实例的方法
@Test
public void testGetStreamInstance() {
    // 根据 Collection 获取 Stream 实例
    List<String> list = new ArrayList<>();
    Stream<String> stream1 = list.stream();
    Assert.assertNotNull(stream1);

    Set<String> set = new HashSet<>();
    Stream<String> stream2 = set.stream();
    Assert.assertNotNull(stream2);

    Map<String, String> map = new HashMap<>();

    Stream<String> stream3 = map.keySet().stream();
    Stream<String> stream4 = map.values().stream();
    Assert.assertNotNull(stream3);
    Assert.assertNotNull(stream4);

    Stream<Map.Entry<String, String>> stream5 = map.entrySet().stream();
    Assert.assertNotNull(stream5);

    // 使用 Stream 的静态 of 方法构造 Stream 实例
    Stream<String> stream6 = Stream.of("a", "b", "c");
    Assert.assertEquals(3, stream6.count());
    String[] strs = {"a", "b", "c"};
    Stream<String> stream7 = Stream.of(strs);
    Assert.assertEquals(3, stream7.count());
}
```



### Stream forEach 方法

```java
// 测试 Stream forEach 用法
@Test
public void testStreamForEach() {
    Stream<String> stream1 = Arrays.asList("a", "b").stream();
    stream1.forEach(e -> log.debug("element=" + e));

    Map<String, String> map = new HashMap<String, String>() {{
        this.put("k1", "v1");
        this.put("k2", "v2");
    }};
    map.entrySet().forEach(entry -> log.debug("key={}, value={}", entry.getKey(), entry.getValue()));

    // 测试带索引的遍历
    // https://stackoverflow.com/questions/18552005/is-there-a-concise-way-to-iterate-over-a-stream-with-indices-in-java-8
    List<String> list = Arrays.asList("a", "b", "c");
    List<String> stringList = IntStream.range(0, list.size()).filter(i -> !list.get(i).equals("b")).mapToObj(list::get).collect(Collectors.toList());
    Assert.assertArrayEquals(new String[]{"a", "c"}, stringList.toArray());
}
```



### Stream count 方法

```java
// 测试 Stream count 用法
@Test
public void testStreamCount() {
    Stream<String> stream1 = Arrays.asList("a", "b", "c").stream();
    Assert.assertEquals(3, stream1.count());
}
```



### Stream filter 方法

```java
// 测试 Stream filter 用法
@Test
public void testStreamFilter() {
    // 过滤
    List<UserEntity> userEntityList = new ArrayList<>();
    userEntityList.add(new UserEntity("zhangsan", 20));
    userEntityList.add(new UserEntity("lisi", 13));
    userEntityList.add(new UserEntity("wangwu", 45));
    userEntityList.add(new UserEntity("guyt", 34));
    List<UserEntity> userEntityListFiltered = userEntityList.stream()
            .filter(userEntity -> userEntity.getAge() > 25).collect(Collectors.toList());
    Assert.assertEquals(2, userEntityListFiltered.size());
    Assert.assertEquals(userEntityList.get(2), userEntityListFiltered.get(0));
    Assert.assertEquals(userEntityList.get(3), userEntityListFiltered.get(1));
}
```



### Stream skip 和 limit 方法

```java
// 测试 skip 和 limit
@Test
public void testStreamSkipAndLimit() {
    // 模拟获取第二页skip和limit
    List<UserEntity> userEntityList = new ArrayList<>();
    userEntityList.add(new UserEntity("zhangsan", 20));
    userEntityList.add(new UserEntity("lisi", 13));
    userEntityList.add(new UserEntity("wangwu", 45));
    userEntityList.add(new UserEntity("guyt", 34));
    List<UserEntity> userEntityList2Page = userEntityList.stream().skip(2).limit(2).collect(Collectors.toList());
    Assert.assertEquals(2, userEntityList2Page.size());
    Assert.assertEquals(userEntityList.get(2), userEntityList2Page.get(0));
    Assert.assertEquals(userEntityList.get(3), userEntityList2Page.get(1));
}
```



### Stream map 方法

Java Stream 的 `map()` 方法是一个中间操作，它接受一个函数作为参数，并将该函数应用于 Stream 中的每个元素，然后返回一个包含转换后元素的新 Stream。  换句话说，`map()` 方法用于将 Stream 中的元素转换成另一种类型或进行某种转换。

**语法:**

```java
<R> Stream<R> map(Function<? super T, ? extends R> mapper);
```

* `T`:  原始 Stream 元素的类型。
* `R`:  转换后元素的类型。
* `mapper`: 一个 `Function` 接口的实例，它接受一个 `T` 类型参数并返回一个 `R` 类型结果。  这个函数定义了如何转换每个元素。

**常用例子:**

1. **将字符串转换为大写:**

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MapExample {
    public static void main(String[] args) {
        List<String> strings = Arrays.asList("apple", "banana", "orange");

        List<String> uppercaseStrings = strings.stream()
                .map(String::toUpperCase) // 使用方法引用
                .collect(Collectors.toList());

        System.out.println(uppercaseStrings); // 输出：[APPLE, BANANA, ORANGE]
    }
}
```

2. **将整数转换为平方:**

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MapIntegerExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        List<Integer> squares = numbers.stream()
                .map(n -> n * n) // 使用 Lambda 表达式
                .collect(Collectors.toList());

        System.out.println(squares); // 输出：[1, 4, 9, 16, 25]
    }
}
```

3. **将对象转换为特定属性:**

假设你有一个 `Person` 对象：

```java
class Person {
    String name;
    int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }
}
```

你可以使用 `map()` 方法提取所有人的名字：

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MapObjectExample {
    public static void main(String[] args) {
        List<Person> people = Arrays.asList(new Person("Alice", 30), new Person("Bob", 25));

        List<String> names = people.stream()
                .map(Person::getName) // 使用方法引用
                .collect(Collectors.toList());

        System.out.println(names); // 输出：[Alice, Bob]
    }
}
```

4. **`flatMap()` 与 `map()` 的区别:**

`flatMap()` 与 `map()` 类似，但它将每个元素映射到一个 Stream，然后将这些 Stream 扁平化成一个新的 Stream。  如果你的 `mapper` 函数返回一个 Stream，则应该使用 `flatMap()`。


**需要注意的是:**

* `map()` 是一个中间操作，它不会立即执行任何操作，只有当遇到终端操作（例如 `collect()`、`forEach()` 等）时，才会执行转换操作。
* `map()` 方法不会修改原始 Stream，它会返回一个新的 Stream。

总而言之，`map()` 方法是 Java Stream 中非常常用的一个方法，它提供了灵活的转换机制，可以方便地对 Stream 中的元素进行各种类型的转换，使其成为数据处理过程中不可或缺的一部分。



**map 使用示例**

```java
// List<UserEntity>转换为List<String>
List<UserEntity> userEntityList = new ArrayList<>();
userEntityList.add(new UserEntity("zhangsan", 20));
userEntityList.add(new UserEntity("lisi", 13));
userEntityList.add(new UserEntity("wangwu", 45));
userEntityList.add(new UserEntity("guyt", 34));
userEntityList.add(new UserEntity("guyt", 34));
List<String> nameList = userEntityList.stream().map(userEntity -> userEntity.getName()).collect(Collectors.toList());
AtomicInteger counter = new AtomicInteger(0);
userEntityList.forEach(userEntity -> Assert.assertEquals(userEntity.getName(), nameList.get(counter.getAndIncrement())));
```



### Stream sorted 方法

```java
// 排序
userEntityList = new ArrayList<>();
userEntityList.add(new UserEntity("zhangsan", 20));
userEntityList.add(new UserEntity("lisi", 13));
userEntityList.add(new UserEntity("wangwu", 45));
userEntityList.add(new UserEntity("guyt", 34));
List<UserEntity> userEntityListSorted = userEntityList.stream()
        .sorted((o1, o2) -> o1.getAge() - o2.getAge()).collect(Collectors.toList());
Assert.assertEquals(13, userEntityListSorted.get(0).getAge());
Assert.assertEquals(20, userEntityListSorted.get(1).getAge());
Assert.assertEquals(34, userEntityListSorted.get(2).getAge());
Assert.assertEquals(45, userEntityListSorted.get(3).getAge());
```



### Stream distinct 方法

UserEntityDistinct 类

```java
@Data
@AllArgsConstructor
public class UserEntityDistinct {
    private String name;
    private int age;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntityDistinct o1 = (UserEntityDistinct) o;
        return Objects.equals(age, o1.age) && Objects.equals(name, o1.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.age);
    }

    private List<NestedClass> myList;

    @Data
    @AllArgsConstructor
    public static class NestedClass {
        private Long id;
    }
}
```

StreamUtil

```java
public class StreamUtil {
    public StreamUtil() {
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return (t) -> {
            return seen.add(keyExtractor.apply(t));
        };
    }
}
```

测试代码

```java
// distinct根据hashCode和equals方法去重
List<UserEntityDistinct> userEntityDistinctList = new ArrayList<>();
userEntityDistinctList.add(new UserEntityDistinct("zhangsan", 20, null));
userEntityDistinctList.add(new UserEntityDistinct("lisi", 13, null));
userEntityDistinctList.add(new UserEntityDistinct("wangwu", 45, null));
userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, Arrays.asList(new UserEntityDistinct.NestedClass(1L), new UserEntityDistinct.NestedClass(2L))));
userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, Arrays.asList(new UserEntityDistinct.NestedClass(1L))));
// 需要重写 UserEntityDistinct 的 hashCode 和 equals 方法以协助无参数 distinct 去重
Assert.assertEquals(userEntityDistinctList.size() - 1, userEntityDistinctList.stream().distinct().collect(Collectors.toList()).size());
Assert.assertEquals(userEntityDistinctList.size() - 1, userEntityDistinctList.stream().filter(StreamUtil.distinctByKey(UserEntityDistinct::getName)).collect(Collectors.toList()).size());

// List<Map<String, Object>>去重
// distinctByKey方法使用
// https://www.cnblogs.com/zwh0910/p/15877284.html
List<Map<String, Object>> mapList = new ArrayList<>();
Map<String, Object> oMap = new HashMap<>();
oMap.put("id", 1L);
oMap.put("code", "1-1");
mapList.add(oMap);
oMap = new HashMap<>();
oMap.put("id", 2L);
oMap.put("code", "1-2");
mapList.add(oMap);
oMap = new HashMap<>();
oMap.put("id", 1L);
oMap.put("code", "1-3");
mapList.add(oMap);
Assert.assertEquals(mapList.size() - 1, mapList.stream().filter(StreamUtil.distinctByKey(o -> o.get("id"))).collect(Collectors.toList()).size());
```



### Stream match 方法

```java
// 匹配anyMatch
userEntityList = new ArrayList<>();
userEntityList.add(new UserEntity("zhangsan", 20));
userEntityList.add(new UserEntity("lisi", 13));
userEntityList.add(new UserEntity("wangwu", 45));
userEntityList.add(new UserEntity("guyt", 34));
boolean matchResult = userEntityList.stream()
        .anyMatch(userEntity -> userEntity.getName().equals("wangwu"));
Assert.assertTrue(matchResult);

matchResult = userEntityList.stream()
        .anyMatch(userEntity -> userEntity.getName().equals("wangwu5"));
Assert.assertFalse(matchResult);

// 匹配noneMatch
userEntityList = new ArrayList<>();
userEntityList.add(new UserEntity("zhangsan", 20));
userEntityList.add(new UserEntity("lisi", 13));
userEntityList.add(new UserEntity("wangwu", 45));
userEntityList.add(new UserEntity("guyt", 34));
matchResult = userEntityList.stream()
        .noneMatch(userEntity -> userEntity.getName().equals("wangwu"));
Assert.assertFalse(matchResult);

matchResult = userEntityList.stream()
        .noneMatch(userEntity -> userEntity.getName().equals("wangwu5"));
Assert.assertTrue(matchResult);

// 匹配allMatch
userEntityList = new ArrayList<>();
userEntityList.add(new UserEntity("zhangsan", 20));
userEntityList.add(new UserEntity("lisi", 13));
matchResult = userEntityList.stream()
        .allMatch(userEntity -> userEntity.getAge() == 20 || userEntity.getAge() == 13);
Assert.assertTrue(matchResult);

matchResult = userEntityList.stream()
        .allMatch(userEntity -> userEntity.getAge() == 20 && userEntity.getAge() == 13);
Assert.assertFalse(matchResult);
```



### Stream find 方法

**findFirst 和 findAny 区别**

`findFirst()` 和 `findAny()` 是 Java Stream API 中的两个终端操作，它们都用于查找 Stream 中的元素，但它们在以下方面有所区别：

**1. 返回值:**

* `findFirst()`：返回 Stream 中的**第一个**元素，如果 Stream 为空则返回一个空的 `Optional`。
* `findAny()`：返回 Stream 中的**任意一个**元素，如果 Stream 为空则返回一个空的 `Optional`。


**2. 并行流行为:**

这是两者最主要的区别。  在**串行流**中，`findFirst()` 和 `findAny()` 的行为完全相同，因为它们都会从 Stream 的开头开始遍历，并返回遇到的第一个元素。

但是在**并行流**中，它们的行为不同：

* `findFirst()`：仍然保证返回 Stream 中的第一个元素。  并行流会对元素进行分区，每个分区独立搜索，找到第一个元素后，会通知其他分区停止搜索，最终返回第一个找到的元素。  这保证了结果的顺序性，但可能会略微降低性能，因为它需要额外的协调工作。

* `findAny()`：可以返回 Stream 中的任何一个元素。 并行流会对元素进行分区，每个分区独立搜索，哪个分区先找到元素，就返回哪个元素。  这不需要协调，因此在并行流中通常比 `findFirst()` 效率更高。  但是，它并不能保证返回的是 Stream 中的第一个元素。


**3. 使用场景:**

* `findFirst()`：适用于需要保证返回 Stream 中第一个元素的情况，例如处理有序数据，或者需要根据顺序选择特定元素。

* `findAny()`：适用于只需要 Stream 中的任何一个元素，而元素的顺序无关紧要的情况。  在并行流中，`findAny()` 通常效率更高，因为它可以更快地找到一个满足条件的元素，无需等待其他分区完成搜索。


**总结:**

| 特性       | `findFirst()`                          | `findAny()`                                        |
| ---------- | -------------------------------------- | -------------------------------------------------- |
| 返回值     | Stream 中的第一个元素                  | Stream 中的任意一个元素                            |
| 串行流行为 | 返回第一个元素                         | 返回第一个元素                                     |
| 并行流行为 | 返回第一个元素，需要协调，性能可能略低 | 返回任意一个元素，无需协调，性能通常更高           |
| 使用场景   | 需要第一个元素，元素顺序很重要         | 只需要任意一个元素，元素顺序不重要，尤其适合并行流 |

总而言之，在大多数情况下，如果不需要保证返回第一个元素且使用并行流，`findAny()` 是更好的选择，因为它通常效率更高。  只有当元素顺序很重要时，才应该使用 `findFirst()`。  如果使用串行流，则两者没有区别。



**测试 find 方法**

```java
// 测试 Stream find 方法
@Test
public void testFind() {
    Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5);
    Optional<Integer> result = stream.findFirst();
    Assert.assertEquals(Integer.valueOf(1), result.get());
    stream = Stream.of(1, 2, 3, 4, 5);
    result = stream.findAny();
    Assert.assertEquals(Integer.valueOf(1), result.get());
}
```



### Stream max、min 方法

```java
// 最大最小值
userEntityDistinctList = new ArrayList<>();
userEntityDistinctList.add(new UserEntityDistinct("zhangsan", 20, null));
userEntityDistinctList.add(new UserEntityDistinct("lisi", 13, null));
userEntityDistinctList.add(new UserEntityDistinct("wangwu", 45, null));
userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, null));
UserEntityDistinct userEntityDistinctMax = userEntityDistinctList.stream()
        .max((o1, o2) -> o1.getAge() - o2.getAge())
        .orElse(null);
Assert.assertEquals(userEntityDistinctList.get(2), userEntityDistinctMax);

UserEntityDistinct userEntityDistinctMin = userEntityDistinctList.stream()
        .min((o1, o2) -> o1.getAge() - o2.getAge())
        .orElse(null);
Assert.assertEquals(userEntityDistinctList.get(1), userEntityDistinctMin);
```



### Stream reduce 方法

**介绍**

Java Stream 的 `reduce()` 方法是一个终端操作，它可以将 Stream 中的元素组合成一个单一的结果。  它通过重复地将一个累加器（accumulator）应用于 Stream 中的元素，最终得到一个累积值。

`reduce()` 方法有两种重载形式：

**1.  `reduce(BinaryOperator<T> accumulator)`:**

这种形式接受一个 `BinaryOperator` 作为参数，该操作符是一个函数，它接受两个同类型的参数，并返回一个同类型的结果。  `reduce()` 方法会从 Stream 的第一个元素开始，将累加器应用于相邻的两个元素，然后将结果与下一个元素进行累加，以此类推，直到 Stream 中的所有元素都被处理完毕。

* **`T`**: Stream 中元素的类型。
* **`BinaryOperator<T>`**:  一个函数，它接受两个 `T` 类型的参数，并返回一个 `T` 类型的结果。  这个函数定义了如何组合两个元素。

**例子：求整数列表的和**

```java
import java.util.Arrays;
import java.util.List;

public class ReduceExample1 {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        Integer sum = numbers.stream().reduce((a, b) -> a + b).orElse(0); //orElse(0) 处理空流的情况
        System.out.println("Sum: " + sum); // Output: Sum: 15
    }
}
```

在这个例子中，`BinaryOperator` 是 `(a, b) -> a + b`，它将两个整数相加。  `orElse(0)` 用于处理空 Stream 的情况，如果没有元素，则返回 0。


**2. `reduce(T identity, BinaryOperator<T> accumulator)`:**

这种形式除了 `BinaryOperator` 外，还接受一个 `identity` 参数。  `identity` 是一个初始值，它在 Stream 为空的情况下作为结果返回。  如果 Stream 不为空，则 `reduce()` 方法会从 `identity` 开始，将累加器应用于 `identity` 和 Stream 的第一个元素，然后将结果与下一个元素进行累加，以此类推。

* **`T`**: Stream 中元素的类型。
* **`T identity`**:  初始值。
* **`BinaryOperator<T>`**:  一个函数，它接受两个 `T` 类型的参数，并返回一个 `T` 类型的结果。


**例子：求整数列表的和，初始值为 0**

```java
import java.util.Arrays;
import java.util.List;

public class ReduceExample2 {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        Integer sum = numbers.stream().reduce(0, (a, b) -> a + b); //0是初始值
        System.out.println("Sum: " + sum); // Output: Sum: 15
    }
}
```


**3.  `reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner)`:**

这种形式用于并行流。它比前两种形式更复杂，用于处理并行流中的结果合并。  `accumulator` 函数用于处理单个分区的结果，`combiner` 函数用于合并不同分区的结果。 详细解释较复杂，通常在处理并行流且结果类型与Stream元素类型不同的情况下使用。


**总结:**

`reduce()` 方法提供了一种强大的方式来将 Stream 中的元素聚合为单个结果。 选择哪种 `reduce()` 方法取决于你的具体需求和是否使用并行流。  对于简单的聚合操作，第一种或第二种形式就足够了。  对于复杂的并行聚合操作，则需要使用第三种形式，并正确处理 `accumulator` 和 `combiner` 函数。  记住处理空流的情况，使用 `orElse()` 方法或提供合适的初始值。



**示例**

```java
// 使用reduce实现年龄相加
userEntityList = new ArrayList<>();
userEntityList.add(new UserEntity("zhangsan", 20));
userEntityList.add(new UserEntity("lisi", 13));
userEntityList.add(new UserEntity("wangwu", 45));
userEntityList.add(new UserEntity("guyt", 34));
AtomicInteger totalAgeSum = new AtomicInteger(0);
userEntityList.forEach(userEntity -> totalAgeSum.addAndGet(userEntity.getAge()));
int totalAge = userEntityList.stream()
        // 先变换List<UserEntity>为List<Integer>
        .map(userEntity -> userEntity.getAge())
        // 实现年龄相加
        .reduce((a, b) -> a + b)
        // List<UserEntity>为空时获取orElse值
        .orElse(0);
Assert.assertEquals(totalAgeSum.get(), totalAge);
```



### Stream mapToInt 方法

```java
// 测试 Stream mapToInt
@Test
public void testMapToInt() {
    List<ListEntry> list = new ArrayList<ListEntry>() {{
        this.add(new ListEntry(1));
        this.add(new ListEntry(2));
        this.add(new ListEntry(3));
    }};
    IntStream intStream = list.stream().mapToInt(o -> o.getNumber());
    Assert.assertEquals(1, intStream.findFirst().getAsInt());
}
```



### Stream concat 方法

```java
// 测试 Stream concat
@Test
public void testConcat() {
    Stream<String> stream1 = Stream.of("a", "b");
    Stream<String> stream2 = Stream.of("c", "d");
    Stream<String> stream3 = Stream.concat(stream1, stream2);
    Assert.assertEquals(4, stream3.count());
}
```



### Stream 转换为 Array

```java
// 测试 Stream 转换为 Array
@Test
public void testStreamToArray() {
    List<String> originalList = new ArrayList<String>() {{
        this.add("a");
        this.add("b");
        this.add("c");
        this.add("b");
    }};
    String[] strArr = originalList.stream().toArray(String[]::new);
    Assert.assertArrayEquals(originalList.toArray(new String[]{}), strArr);
}
```



### Stream collect 方法

```java
// 测试 stream collect
@Test
public void testCollect() {
    List<String> originalList = new ArrayList<String>() {{
        this.add("a");
        this.add("b");
        this.add("c");
        this.add("b");
    }};
    // List 转换为 List
    List<String> list = originalList.stream().collect(Collectors.toCollection(ArrayList::new));
    Assert.assertEquals(originalList, list);
    list = originalList.stream().collect(Collectors.toList());
    Assert.assertEquals(originalList, list);

    // List 转换为 HashSet
    Set<String> hashSet = originalList.stream().collect(Collectors.toCollection(HashSet::new));
    Assert.assertEquals(originalList.size() - 1, hashSet.size());
    Assert.assertEquals(Arrays.copyOf(originalList.toArray(), originalList.size() - 1), hashSet.toArray());
    hashSet = originalList.stream().collect(Collectors.toSet());
    Assert.assertEquals(originalList.size() - 1, hashSet.size());
    Assert.assertEquals(Arrays.copyOf(originalList.toArray(), originalList.size() - 1), hashSet.toArray());

    // List 转换为 HashMap
    List<UserEntityDistinct> userEntityDistinctList = new ArrayList<>();
    userEntityDistinctList.add(new UserEntityDistinct("zhangsan", 20, null));
    userEntityDistinctList.add(new UserEntityDistinct("lisi", 13, null));
    userEntityDistinctList.add(new UserEntityDistinct("wangwu", 45, null));
    userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, null));
    userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, null));
    Map<String, Integer> map = userEntityDistinctList.stream().collect(Collectors.toMap(UserEntityDistinct::getName, UserEntityDistinct::getAge, (a, b) -> a/* 重复键合并策略，返回第一个年龄值 */));
    Assert.assertEquals(4, map.size());
    Assert.assertTrue(map.containsKey("zhangsan"));
}
```



### Stream 聚合函数

```java
// 测试 Stream 聚合函数
@Test
public void testAggregation() {
    List<UserEntityDistinct> userEntityDistinctList = new ArrayList<>();
    userEntityDistinctList.add(new UserEntityDistinct("zhangsan", 20, null));
    userEntityDistinctList.add(new UserEntityDistinct("lisi", 13, null));
    userEntityDistinctList.add(new UserEntityDistinct("wangwu", 45, null));
    userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, null));
    userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, null));

    // maxBy
    UserEntityDistinct userEntityDistinct = userEntityDistinctList.stream().collect(Collectors.maxBy((a, b) -> a.getAge() - b.getAge())).get();
    Assert.assertEquals(45, userEntityDistinct.getAge());

    // minBy
    userEntityDistinct = userEntityDistinctList.stream().collect(Collectors.minBy((a, b) -> a.getAge() - b.getAge())).get();
    Assert.assertEquals(13, userEntityDistinct.getAge());

    // 求和
    int totalAge = userEntityDistinctList.stream().collect(Collectors.summingInt(o -> o.getAge()));
    Assert.assertEquals(146, totalAge);

    // 求平均
    int averageAge = userEntityDistinctList.stream().collect(Collectors.averagingInt(o -> o.getAge())).intValue();
    Assert.assertEquals(29, averageAge);

    // 统计数量
    int count = userEntityDistinctList.stream().collect(Collectors.counting()).intValue();
    Assert.assertEquals(5, count);
}
```



### Stream 分组

```java
// 测试分组
@Test
public void testGroupingBy() {
    List<UserEntityDistinct> userEntityDistinctList = new ArrayList<>();
    userEntityDistinctList.add(new UserEntityDistinct("zhangsan", 20, null));
    userEntityDistinctList.add(new UserEntityDistinct("wangwu", 13, null));
    userEntityDistinctList.add(new UserEntityDistinct("wangwu", 45, null));
    userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, null));
    userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, null));

    // 根据年龄分组
    Map<Integer, List<UserEntityDistinct>> map = userEntityDistinctList.stream().collect(Collectors.groupingBy(o -> o.getAge()));
    Assert.assertEquals(2, map.get(34).size());

    // 根据年龄区间分组
    Map<String, List<UserEntityDistinct>> map1 = userEntityDistinctList.stream().collect(Collectors.groupingBy(o -> {
        if (o.getAge() <= 20) {
            return "<=20";
        } else if (o.getAge() <= 40) {
            return "<=40";
        } else {
            return "其他";
        }
    }));
    Assert.assertEquals(3, map1.size());
    Assert.assertEquals(2, map1.get("<=20").size());
    Assert.assertEquals(2, map1.get("<=40").size());
    Assert.assertEquals(1, map1.get("其他").size());

    // 多级分组
    Map<String, Map<Integer, List<UserEntityDistinct>>> map2 = userEntityDistinctList.stream().collect(Collectors.groupingBy(UserEntityDistinct::getName, Collectors.groupingBy(UserEntityDistinct::getAge)));
    Assert.assertEquals(3, map2.size());
    Assert.assertEquals(1, map2.get("guyt").size());
    Assert.assertEquals(2, map2.get("guyt").get(34).size());
    Assert.assertEquals(2, map2.get("wangwu").size());
    Assert.assertEquals(1, map2.get("wangwu").get(13).size());
    Assert.assertEquals(1, map2.get("wangwu").get(45).size());
}
```



### Stream 分区

```java
// 测试分区
@Test
public void testPartitionBy() {
    List<UserEntityDistinct> userEntityDistinctList = new ArrayList<>();
    userEntityDistinctList.add(new UserEntityDistinct("zhangsan", 20, null));
    userEntityDistinctList.add(new UserEntityDistinct("wangwu", 13, null));
    userEntityDistinctList.add(new UserEntityDistinct("wangwu", 45, null));
    userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, null));
    userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, null));

    Map<Boolean, List<UserEntityDistinct>> map1 = userEntityDistinctList.stream().collect(Collectors.partitioningBy(o -> o.getAge() > 30));
    Assert.assertEquals(2, map1.size());
    Assert.assertEquals(2, map1.get(Boolean.FALSE).size());
    Assert.assertEquals(3, map1.get(Boolean.TRUE).size());
}
```



### Stream joining

```java
// 测试 joining
@Test
public void testJoining() {
    List<UserEntityDistinct> userEntityDistinctList = new ArrayList<>();
    userEntityDistinctList.add(new UserEntityDistinct("zhangsan", 20, null));
    userEntityDistinctList.add(new UserEntityDistinct("wangwu", 13, null));
    userEntityDistinctList.add(new UserEntityDistinct("wangwu", 45, null));
    userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, null));
    userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, null));

    String str = userEntityDistinctList.stream().map(UserEntityDistinct::getName).collect(Collectors.joining("_", "|", "|"));
    Assert.assertEquals(str, "|zhangsan_wangwu_wangwu_guyt_guyt|");
}
```



### 并行流

#### 获取并行流

在 Java 中，获取并行流主要有两种方式：

**1. 使用 `parallelStream()` 方法:**

这是最常用的方法，可以直接从一个集合（例如 `List`、`Set`、`Array`）或其他支持流操作的数据结构获取一个并行流。  `parallelStream()` 方法会创建一个新的并行流，它会利用多核处理器来并行执行流操作。

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class ParallelStreamExample {
    public static void main(String[] args) {
        // 从列表获取并行流
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        numbers.parallelStream().forEach(System.out::println);


        // 从数组获取并行流
        int[] numbersArray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Arrays.stream(numbersArray).parallel().forEach(System.out::println); // 使用parallel()方法

        // 从IntStream获取并行流
        IntStream.range(1,11).parallel().forEach(System.out::println);
    }
}
```


**2. 使用 `stream().parallel()` 方法:**

这种方法可以将一个已存在的串行流转换为并行流。  如果你已经创建了一个串行流，但后来需要将其转换为并行流，可以使用这种方法。

```java
import java.util.Arrays;
import java.util.List;

public class ParallelStreamExample2 {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        numbers.stream().parallel().forEach(System.out::println); //将串行流转换为并行流
    }
}
```


**需要注意的几点：**

* **性能:**  并行流并不总是比串行流更快。  对于小型数据集，并行流的开销可能会超过其带来的性能提升。  只有在处理大型数据集时，并行流才能充分发挥其优势。

* **线程安全:**  在并行流中，你需要确保你的操作是线程安全的。  如果你的操作会修改共享状态，则需要使用同步机制来避免数据竞争。

* **数据分割:**  并行流会将数据分割成多个部分，每个部分由不同的线程处理。  数据的分割方式会影响并行流的性能。  默认情况下，Java 会使用一种高效的分割算法，但你也可以自定义分割策略。

* **开销:** 创建和管理并行流会有一定的开销，因此对于小型数据集，使用并行流可能反而会降低性能。


选择哪种方式取决于你的具体情况。  如果你是从一个集合开始处理数据，那么直接使用 `parallelStream()` 是最方便的方法。  如果已经存在一个串行流，则可以使用 `parallel()` 方法将其转换为并行流。  记住在使用并行流时要考虑线程安全和性能问题。

总而言之，`parallelStream()` 是从集合直接创建并行流的首选方法，而 `stream().parallel()` 用于将已有的串行流转换为并行流。  选择哪个方法取决于你的代码结构和数据来源。



#### 性能

```java
// 并行和非并行 Stream 性能测试对比
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark) //使用的SpringBoot容器，都是无状态单例Bean，无安全问题，可以直接使用基准作用域BenchMark
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS) //预热1s
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
@Threads(-1)
public class PerfTests {

    Integer sumTotal = null;
    List<Integer> dataList = null;

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(PerfTests.class.getSimpleName())
                // 断点调试时fork=0
                .forks(1)
                // 发生错误停止测试
                .shouldFailOnError(true)
                .jvmArgs("-Xmx2G", "-server")
                .build();
        new Runner(opt).run();
    }

    /**
     * 初始化
     */
    @Setup(Level.Trial)
    public void setup() {
        dataList = new ArrayList<>();
        sumTotal = 0;
        for (int i = 0; i < 1000000; i++) {
            dataList.add(i);
            sumTotal += i;
        }
    }

    /**
     * 测试的后处理操作
     */
    @TearDown(Level.Trial)
    public void teardown() {
    }

    @Benchmark
    public void testNoneParallel(Blackhole blackhole) {
        Stream<Integer> stream = this.dataList.stream();
        Integer sum = stream.collect(Collectors.summingInt(o -> o));
        Assert.assertEquals(sumTotal, sum);
        blackhole.consume(sum);
    }

    @Benchmark
    public void testParallel(Blackhole blackhole) {
        Stream<Integer> stream = this.dataList.parallelStream();
        Integer sum = stream.collect(Collectors.summingInt(o -> o));
        Assert.assertEquals(sumTotal, sum);
        blackhole.consume(sum);
    }
}
```

```
Benchmark                    Mode  Cnt     Score      Error  Units
PerfTests.testNoneParallel  thrpt    3   836.622 ± 2146.560  ops/s
PerfTests.testParallel      thrpt    3  1956.849 ±  441.611  ops/s
```



## Optional

### 介绍

JDK 8 的 `Optional` 是一个容器类，它可以包含一个非空值，或者不包含任何值（表示空值）。它旨在解决 Java 中臭名昭著的 `NullPointerException`（空指针异常）问题，并提供一种更优雅的方式来处理可能为空的值。

以下是 `Optional` 的一些关键特性和使用方法：

**1. 创建 Optional 对象:**

* **`Optional.of(T value)`:** 创建一个包含指定值的 `Optional` 对象。如果 `value` 为 `null`，则会抛出 `NullPointerException`。
* **`Optional.ofNullable(T value)`:** 创建一个包含指定值的 `Optional` 对象。如果 `value` 为 `null`，则返回一个空的 `Optional` 对象。
* **`Optional.empty()`:** 创建一个空的 `Optional` 对象。

**示例:**

```java
Optional<String> optionalString = Optional.of("Hello"); // 包含值
Optional<String> optionalNull = Optional.ofNullable(null); // 空
Optional<String> emptyOptional = Optional.empty(); // 空
```

**2. 检查 Optional 对象是否包含值:**

* **`isPresent()`:** 返回一个布尔值，指示 `Optional` 对象是否包含值。
* **`ifPresent(Consumer<? super T> consumer)`:** 如果 `Optional` 对象包含值，则执行指定的 `Consumer` 操作。

**示例:**

```java
if (optionalString.isPresent()) {
    System.out.println(optionalString.get()); // 获取值
}

optionalString.ifPresent(System.out::println); // 使用方法引用简化
```

**3. 获取 Optional 对象的值:**

* **`get()`:** 返回 `Optional` 对象包含的值。如果 `Optional` 对象为空，则抛出 `NoSuchElementException`。  **应该尽量避免直接使用 `get()`，因为它容易导致 `NullPointerException`。**
* **`orElse(T other)`:** 如果 `Optional` 对象包含值，则返回该值；否则，返回指定的默认值。
* **`orElseGet(Supplier<? extends T> other)`:** 如果 `Optional` 对象包含值，则返回该值；否则，调用指定的 `Supplier` 来生成一个默认值。  这比 `orElse` 更高效，因为它只有在需要时才生成默认值。
* **`orElseThrow(Supplier<? extends X> exceptionSupplier)`:** 如果 `Optional` 对象包含值，则返回该值；否则，抛出指定的异常。


**示例:**

```java
String value = optionalString.orElse("World"); // 如果optionalString为空，则value为"World"
String value2 = optionalString.orElseGet(() -> "World"); // 类似orElse，但延迟计算
String value3 = optionalNull.orElseThrow(() -> new IllegalArgumentException("Value cannot be null")); // 抛出异常
```

**4.  映射和过滤:**

* **`map(Function<? super T, ? extends U> mapper)`:** 对 `Optional` 对象包含的值应用一个映射函数。如果 `Optional` 对象为空，则返回一个空的 `Optional` 对象。
* **`flatMap(Function<? super T, ? extends Optional<? extends U>> mapper)`:**  类似于 `map`，但映射函数返回一个 `Optional` 对象。 这允许对嵌套的 `Optional` 对象进行处理。
* **`filter(Predicate<? super T> predicate)`:**  如果 `Optional` 对象包含的值满足指定的谓词，则返回该 `Optional` 对象；否则，返回一个空的 `Optional` 对象。

**示例:**

```java
Optional<Integer> optionalLength = optionalString.map(String::length); // 获取字符串长度

Optional<String> upperCaseOptional = optionalString.map(String::toUpperCase); // 转大写

Optional<String> filteredOptional = optionalString.filter(s -> s.startsWith("H")); // 过滤
```


总而言之，`Optional` 提供了一种更安全、更清晰的方式来处理可能为空的值，避免了大量的 `null` 检查，提高了代码的可读性和可维护性。  记住优先使用 `orElse`、`orElseGet`、`ifPresent` 等方法来处理 `Optional` 对象，避免使用 `get()` 方法，以减少 `NullPointerException` 的风险。



### 以前对 null 的处理方式

```java
// 以前对 null 的处理方式，需要使用 if else 判断变量是否为 null 导致代码不优雅
String username = "Dexter";
if (username != null) {
    Assert.assertNotNull(username);
} else {
    Assert.assertNull(username);
}
```



### 创建方式

```java
// Optional.of(null)会抛出NullPointerException
try {
    Optional.of(null);
    Assert.fail("预期异常没有抛出");
} catch (NullPointerException ex) {
}

// Optional.ofNullable(null).get()会抛出异常
try {
    Optional.ofNullable(null).get();
    Assert.fail("预期异常没有抛出");
} catch (NoSuchElementException ex) {

}

// Optional.ofNullable(null)不会抛出异常
Optional.ofNullable(null);

// Optional.empty() 创建空 Optional 对象
String str = (String) Optional.empty().orElse("Hello world!");
Assert.assertEquals("Hello world!", str);
```



### 判断是否有值

```java
// isPresent用法
Assert.assertFalse(Optional.ofNullable(null).isPresent());
Assert.assertTrue(Optional.ofNullable("").isPresent());
Assert.assertTrue(Optional.of(new ArrayList<>()).isPresent());
```



### 获取值

```java
// 如果为空则 get 方法抛出 NoSuchElementException
try {
    Optional.ofNullable(null).get();
    Assert.fail();
} catch (NoSuchElementException ignored) {
}

// 如果为空则orElse返回默认值
Assert.assertEquals("default", Optional.ofNullable(null).orElse("default"));
Assert.assertEquals("myValue", Optional.ofNullable("myValue").orElse("default"));

// 如果为空则调用orElseGet提供的 Supplier 获取值
AtomicInteger atomicInteger3 = new AtomicInteger();
Assert.assertEquals("default", Optional.ofNullable(null).orElseGet(() -> {
    atomicInteger3.incrementAndGet();
    return "default";
}));
Assert.assertEquals(1, atomicInteger3.get());
```



### ifPresent 用法

如果值存在则调用 ifPresent 提供的 Consumer，否则不调用

```java
// 如果值存在则调用 ifPresent 提供的 Consumer，否则不调用
AtomicInteger atomicInteger = new AtomicInteger();
Optional.ofNullable(null).ifPresent(value -> atomicInteger.incrementAndGet());
Assert.assertEquals(0, atomicInteger.get());

AtomicInteger atomicInteger1 = new AtomicInteger();
Optional.ofNullable("Dexter").ifPresent(value -> atomicInteger1.incrementAndGet());
Assert.assertEquals(1, atomicInteger1.get());
```



### map 用法

```java
// map 用法
InternalOrder order = null;
String ordername = Optional.ofNullable(order).map(orderT -> orderT.getName()).map(ordernameT -> ordernameT.toLowerCase()).orElse("default");
Assert.assertEquals("default", ordername);
ordername = Optional.ofNullable(new InternalOrder("Dexter")).map(orderT -> orderT.getName()).map(ordernameT -> ordernameT.toLowerCase()).orElse("default");
Assert.assertEquals("dexter", ordername);
```



### filter 用法

```java
// filter 用法
Assert.assertFalse(Optional.ofNullable(null).filter(value -> "Dexter".equals(value)).isPresent());
Assert.assertFalse(Optional.ofNullable("dexter").filter(value -> "Dexter".equals(value)).isPresent());
Assert.assertTrue(Optional.ofNullable("Dexter").filter(value -> "Dexter".equals(value)).isPresent());
```



## 新的日期和时间 API

### 介绍

Java 8 引入了全新的日期时间 API，以取代旧的 `java.util.Date`、`java.util.Calendar` 等类，这些旧的类存在设计缺陷，例如线程不安全和易混淆的API。新的API位于 `java.time` 包及其子包中，提供了更清晰、更简洁、更易于使用的日期和时间处理方式。  主要的新增类包括：

* **`java.time.LocalDate`:**  表示日期，例如 2024-03-08，不包含时间信息。
* **`java.time.LocalTime`:** 表示时间，例如 10:30:15，不包含日期信息。
* **`java.time.LocalDateTime`:**  表示日期和时间，例如 2024-03-08T10:30:15。
* **`java.time.ZonedDateTime`:**  表示包含时区信息的日期和时间。这是处理不同时区日期时间非常重要的一个类。
* **`java.time.Instant`:**  表示自纪元（1970-01-01T00:00:00Z）以来的时间，以纳秒为单位，通常用于与旧的日期时间API兼容或与数据库交互。
* **`java.time.Duration`:**  表示两个时间点之间的时间差，以纳秒为单位。
* **`java.time.Period`:** 表示两个日期之间的时间差，以年、月、日为单位。
* **`java.time.format.DateTimeFormatter`:** 用于格式化和解析日期和时间字符串。


**一些例子:**

```java
import java.time.*;
import java.time.format.DateTimeFormatter;

public class JavaTimeExample {
    public static void main(String[] args) {
        // 创建 LocalDate 对象
        LocalDate today = LocalDate.now();
        System.out.println("今天的日期: " + today);

        // 创建 LocalDateTime 对象
        LocalDateTime now = LocalDateTime.now();
        System.out.println("当前日期和时间: " + now);

        // 创建 ZonedDateTime 对象，指定时区
        ZonedDateTime zonedNow = ZonedDateTime.now(ZoneId.of("America/New_York"));
        System.out.println("纽约的当前日期和时间: " + zonedNow);

        // 创建日期和时间对象，指定日期和时间
        LocalDateTime specificDateTime = LocalDateTime.of(2024, 3, 8, 10, 30, 15);
        System.out.println("指定的日期和时间: " + specificDateTime);

        // 计算两个日期之间的差值
        LocalDate date1 = LocalDate.of(2023, 1, 1);
        LocalDate date2 = LocalDate.of(2024, 3, 8);
        Period period = Period.between(date1, date2);
        System.out.println("两个日期之间的差值: " + period);

        // 格式化日期和时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        System.out.println("格式化后的日期和时间: " + formattedDateTime);

        // 解析日期时间字符串
        LocalDateTime parsedDateTime = LocalDateTime.parse("2024-03-08 10:30:15", formatter);
        System.out.println("解析后的日期和时间: " + parsedDateTime);
    }
}
```

这个例子展示了如何创建、操作和格式化日期时间对象。  新的API更加面向对象，并且提供了更好的可读性和可维护性。  它避免了旧API中许多容易出错的地方，并且更好地支持国际化。  学习和使用 `java.time` 包中的类可以显著提高你处理日期和时间的效率和代码质量。



### 旧版本日期时间 API 存在的问题

问题如下：

- 设计很差：在 java.util 和 java.sql 的包中都有日期类，java.util.Date 同时包含日期和时间，而 java.sql.Date 仅包含日期。此外用于格式化和解析的类在 java.text 包中定义。
- 非线程安全： java.util.Date 是非线程安全的，所有的日期类都是可变的。这是 Java 日期类最大的问题之一。
- 时区处理麻烦：日期类并不提供国际化，没有时区支持，因此 Java 引入了 java.util.Calendar 和 java.util.TimeZone 类，但他们同样存在上述所有问题。

示例代码：

```java
// region 旧版本日期时间 API 存在的问题

// 1、设计很差：在 java.util 和 java.sql 的包中都有日期类，java.util.Date 同时包含日期和时间，而 java.sql.Date 仅包含日期。此外用于格式化和解析的类在 java.text 包中定义。
// 2、非线程安全： java.util.Date 是非线程安全的，所有的日期类都是可变的。这是 Java 日期类最大的问题之一。
// 3、时区处理麻烦：日期类并不提供国际化，没有时区支持，因此 Java 引入了 java.util.Calendar 和 java.util.TimeZone 类，但他们同样存在上述所有问题。

// 输出为 Thu Oct 23 00:00:00 CST 3890 时间不合理
Date date = new Date(1990, 9, 23);
System.out.println(date);

// 线程不安全
SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
ExecutorService executorService = Executors.newCachedThreadPool();
for (int i = 0; i < 20; i++) {
    executorService.submit(() -> {
        try {
            Date date1 = simpleDateFormat.parse("2019-09-23");
            System.out.println(date1);
        } catch (Exception ex) {

        }
    });
}
executorService.shutdown();
while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

// endregion
```



### LocalDate

```java
// region `java.time.LocalDate`: 表示日期，例如 2024-03-08，不包含时间信息。

// 当前时间
LocalDate localDate = LocalDate.now();
System.out.println("localData=" + localDate);

// 创建指定时间
localDate = LocalDate.of(2019, 5, 12);
System.out.println("localDate=" + localDate);

// endregion
```



### LocalTime

```java
// region **`java.time.LocalTime`:** 表示时间，例如 10:30:15，不包含日期信息。

LocalTime localTime = LocalTime.now();
System.out.println("localTime = " + localTime);

localTime = LocalTime.of(13, 12, 15);
System.out.println("localTime = " + localTime);

// endregion
```



### LocalDateTime

```java
// region **`java.time.LocalDateTime`:**  表示日期和时间，例如 2024-03-08T10:30:15。

LocalDateTime localDateTime = LocalDateTime.now();
System.out.println("localDateTime = " + localDateTime);

localDateTime = LocalDateTime.of(2015, 2, 5, 13, 25, 21);
System.out.println("localDateTime = " + localDateTime);

// endregion
```



### 修改日期和时间

```java
// region 修改日期和时间

// 使用设置方式修改日期和时间
localDate = localDate.withYear(2011);
System.out.println("localDate = " + localDate);

// 使用增加或者减去方式修改日期和时间
localDate = localDate.plusYears(10);
System.out.println("localDate = " + localDate);

// endregion
```



### 比较日期和时间

```java
// region 比较日期和时间

localDateTime = LocalDateTime.of(2015, 2, 23, 13, 22, 11);
LocalDateTime now = LocalDateTime.now();
System.out.println("localDateTime.isAfter(now) = " + localDateTime.isAfter(now));
System.out.println("localDateTime.isBefore(now) = " + localDateTime.isBefore(now));
System.out.println("localDateTime.isEqual(now) = " + localDateTime.isEqual(now));

// endregion
```



### 时间格式化和解析

```java
// region 时间格式化和解析

// 时间格式化
DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
localDateTime = LocalDateTime.now();
String localDateTimeStr = localDateTime.format(dateTimeFormatter);
System.out.println("localDateTimeStr = " + localDateTimeStr);

// 解析日期和时间
localDateTime = LocalDateTime.parse(localDateTimeStr, dateTimeFormatter);
System.out.println("localDateTime = " + localDateTime);

// endregion
```



### Instant

`java.time.Instant` 类表示的是自 Unix 纪元 (1970-01-01T00:00:00Z) 以来的时间，以纳秒为单位。它是一个时间戳，不包含任何日期或时区信息（虽然它总是以 UTC 为基准）。  因此，`Instant` 的使用场景主要集中在以下几个方面：

1. **与旧的日期时间 API 兼容:**  `Instant` 可以方便地与 `java.util.Date` 类进行转换。  这是在迁移到 Java 8 新日期时间 API 时一个重要的桥梁，让你能够逐步替换旧代码，而不会直接破坏与旧系统的兼容性。

2. **数据库交互:**  许多数据库系统使用 Unix 时间戳来存储时间数据。 `Instant` 提供了方便的方法来将时间戳转换为 `Instant` 对象，反之亦然，从而简化了与数据库的交互。

3. **网络通信:**  在网络通信中，经常需要传输时间信息。使用 `Instant` 可以确保时间信息在不同的系统之间能够一致地表示和解析，因为它基于一个通用的时间基准。

4. **需要高精度的时间戳:**  `Instant` 使用纳秒来表示时间，因此它比 `java.util.Date` 提供了更高的精度。  在需要精确到纳秒级别的时间戳时，`Instant` 是最佳选择。

5. **记录事件的时间戳:**  在日志记录、事件跟踪等场景中，可以使用 `Instant` 来记录事件发生的确切时间。


**示例:**

```java
import java.time.Instant;
import java.util.Date;

public class InstantExample {
    public static void main(String[] args) {
        // 获取当前时间戳
        Instant now = Instant.now();
        System.out.println("当前时间戳: " + now);

        // 将 Instant 转换为 Date 对象
        Date date = Date.from(now);
        System.out.println("转换为 Date 对象: " + date);

        // 将 Date 对象转换为 Instant 对象
        Instant instantFromDate = date.toInstant();
        System.out.println("从 Date 对象转换回 Instant 对象: " + instantFromDate);


        // 将时间戳转换为特定时区的日期时间
        //  注意：Instant 本身不包含时区信息, 需要使用 ZonedDateTime 来表示特定时区的日期和时间
        java.time.ZonedDateTime zonedDateTime = now.atZone(java.time.ZoneId.of("America/New_York"));
        System.out.println("纽约时间: " + zonedDateTime);

        //  计算时间差
        Instant later = Instant.now().plusSeconds(60); // 60秒后
        java.time.Duration duration = java.time.Duration.between(now, later);
        System.out.println("时间差: " + duration);
    }
}
```

总而言之，`Instant` 类主要用于处理与时间戳相关的操作，特别是那些需要高精度或与旧系统兼容的场景。  记住，`Instant` 本身只代表一个时间点，不包含任何时区信息，你需要结合 `ZoneId` 和 `ZonedDateTime` 来处理与时区相关的操作。



### Duration/Period 类计算日期和时间差

```java
// region Duration/Period 类计算日期和时间差

LocalTime localTime1 = LocalTime.of(13, 23, 35);
LocalTime localTime2 = LocalTime.now();
Duration duration1 = Duration.between(localTime2, localTime1);
System.out.println("相差秒数 = " + duration1.toSeconds());

LocalDate localDate1 = LocalDate.of(2011, 2, 23);
LocalDate localDate2 = LocalDate.now();
Period period = Period.between(localDate1, localDate2);
System.out.println("相差天数 = " + period.getDays());

// endregion
```



### 时间调整器

```java
// region 时间调整器

localDateTime = LocalDateTime.now();
localDateTime = localDateTime.with(temporal -> {
    // 设置下个月第一天
    temporal = ((LocalDateTime) temporal).plusMonths(1).withDayOfMonth(1);
    return temporal;
});
System.out.println("localDateTime = " + localDateTime);

localDateTime = LocalDateTime.now();
localDateTime = localDateTime.with(TemporalAdjusters.firstDayOfNextMonth());
System.out.println("localDateTime = " + localDateTime);

// endregion
```



### 日期时间的时区

```java
// region 日期时间的时区

// 获取所有时区
ZoneId.getAvailableZoneIds().forEach(System.out::println);

ZonedDateTime zonedDateTime1 = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
System.out.println("zonedDateTime1 = " + zonedDateTime1);

// 修改时区
zonedDateTime1 = zonedDateTime1.withZoneSameInstant(ZoneId.of("America/Vancouver"));
System.out.println("zonedDateTime1 = " + zonedDateTime1);
zonedDateTime1 = zonedDateTime1.withZoneSameInstant(ZoneId.of("Asia/Shanghai"));
System.out.println("zonedDateTime1 = " + zonedDateTime1);

// endregion
```
