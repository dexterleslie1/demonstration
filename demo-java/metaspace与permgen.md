# `metaspace`和`permgen`

## 什么是`metaspace`和`permgen`？

在Java虚拟机（JVM）中，`Metaspace` 和 `PermGen`（永久代）都与类的元数据存储有关，但它们属于不同的JVM版本和内存管理策略。

### PermGen（永久代）

- **存在版本**：在Java 8之前的HotSpot JVM版本中，方法区（Method Area）被实现为永久代（PermGen space）。永久代主要用于存储类的元数据，包括类的结构信息（如运行时常量池、字段和方法数据、构造函数和普通方法的字节码内容）以及类型信息（如类的名称、父类、实现的接口等）。
- **限制**：永久代的大小是有限的，并且需要通过JVM启动参数（如`-XX:MaxPermSize`）来设置。如果永久代被填满，JVM会抛出`OutOfMemoryError: PermGen space`错误。
- **废弃**：由于永久代的大小限制和性能问题，Java 8中永久代被元空间（Metaspace）所取代。

### Metaspace（元空间）

- **存在版本**：从Java 8开始，HotSpot JVM使用元空间（Metaspace）来取代永久代。元空间位于本地内存中（即堆外内存），而不是JVM堆内存中。
- **优势**：元空间的最大好处是它只受物理内存的限制，而不是像永久代那样有一个固定的限制。这意呀着，如果应用程序加载了大量的类，它不会受到像永久代那样的内存限制。
- **配置**：元空间的大小可以通过JVM启动参数来配置，例如`-XX:MetaspaceSize`（元空间的初始大小）和`-XX:MaxMetaspaceSize`（元空间的最大大小，如果不设置，则只受物理内存的限制）。
- **错误**：如果元空间不足，JVM会抛出`OutOfMemoryError: Metaspace`错误。

### 总结

- **PermGen** 是Java 8之前HotSpot JVM中用于存储类元数据的内存区域，它的大小有限，并且可能导致`OutOfMemoryError: PermGen space`错误。
- **Metaspace** 是Java 8及以后版本中用于替代永久代的内存区域，它位于本地内存中，只受物理内存的限制，并通过`-XX:MetaspaceSize`和`-XX:MaxMetaspaceSize`等JVM启动参数来配置。如果元空间不足，JVM会抛出`OutOfMemoryError: Metaspace`错误。



## `metaspace`和`permgen`作用是什么？

- `permgen`作用

  `PermGen`（永久代）在Java 8之前的HotSpot JVM版本中，是用于存储类的元数据的内存区域。它的全称是Permanent Generation space，即永久保存区域。PermGen主要存储以下几类信息：

  1. 类的元数据：
     - **类结构信息**：包括类的名称、父类、实现的接口等。
     - **字段和方法数据**：类的字段（包括静态和非静态字段）和方法（包括方法的字节码）的描述信息。
     - **构造函数信息**：类的构造函数的描述信息。
  2. 运行时常量池：
     - 常量池是一个特殊的存储区域，用于存放编译期生成的各种字面量和符号引用。这些字面量包括字符串常量、数字常量等，而符号引用则包括类和接口的全限定名、字段的名称和描述符、方法的名称和描述符等。
  3. 类型信息：
     - 与类相关的类型信息，如数组类型、泛型类型等。

  ### 举例说明

  假设我们有一个简单的Java类`Example`，其内容如下：

  ```java
  public class Example {  
      public static final String CONSTANT = "Hello, PermGen!";  
      private int number;  
    
      public Example(int number) {  
          this.number = number;  
      }  
    
      public void printNumber() {  
          System.out.println(number);  
      }  
  }
  ```

  当这个类被JVM加载时，它的以下信息会被存储在PermGen中：

  - **类的元数据**：`Example`类的名称、其父类（`Object`）、它没有实现的接口（因为默认继承自`Object`类）等信息。
  - **字段信息**：`number`字段的描述信息，包括其名称、类型（`int`）和访问修饰符（`private`）。
  - **方法信息**：`Example`类的构造函数`Example(int number)`和`printNumber()`方法的描述信息，包括方法的名称、返回类型、参数列表、方法体（以字节码形式）等。
  - **常量信息**：`CONSTANT`常量字符串`"Hello, PermGen!"`会被存储在运行时常量池中。

  ### 注意事项

  - PermGen的大小是有限的，并且需要通过JVM启动参数（如`-XX:MaxPermSize`）来设置。如果PermGen被填满，JVM会抛出`OutOfMemoryError: PermGen space`错误。
  - 由于PermGen的这些限制，Java 8及以后的版本引入了元空间（Metaspace）来取代永久代，以提供更灵活和高效的内存管理。

  ### 实际应用

  在实际应用中，如果应用程序加载了大量的类（比如使用了大量的第三方库或动态生成类），就可能会遇到PermGen空间不足的问题。为了解决这个问题，可以通过增加`-XX:MaxPermSize`的值来扩大PermGen空间的大小。但是，在Java 8及以后的版本中，应该使用`-XX:MaxMetaspaceSize`来配置元空间的大小。

- `metaspace`作用

  在Java中，`Metaspace`（元空间）是从JDK 8开始引入的，用于替代之前版本的永久代（PermGen space）。Metaspace主要用于存储类的元数据，这些信息对于Java应用程序的运行至关重要。以下是Metaspace存储内容的详细归纳，并辅以例子：

  ### Metaspace存储内容

  1. 类的结构信息
     - 包括类名、父类、接口、字段（包括静态和非静态字段）等信息。这些信息是JVM在运行时识别和操作类的基础。
     - **例子**：假设有一个名为`Person`的类，它有一个名为`name`的字段和一个名为`age`的字段。那么，`Person`类的这些信息（包括类名、字段名、字段类型等）将被存储在Metaspace中。
  2. 常量池
     - 常量池是类文件的一部分，用于存放编译期生成的各种字面量和符号引用。这些常量包括字符串常量、数值常量、类引用、字段引用和方法引用等。
     - **例子**：在`Person`类中，如果定义了一个字符串常量`public static final String GREETING = "Hello, Metaspace!";`，那么这个字符串常量将被存储在常量池中，进而存储在Metaspace中。
  3. 字段描述
     - 描述类中各个字段的属性，如名称、类型、修饰符（如public、private、static等）等。
     - **例子**：在`Person`类中，`name`字段的描述信息（包括字段名、字段类型、是否为静态等）将被存储在Metaspace中。
  4. 方法描述
     - 描述类中各个方法的属性，如名称、参数列表、返回类型、方法体（以字节码形式）等。
     - **例子**：`Person`类中的构造方法`Person(String name, int age)`和任何其他方法的描述信息（包括方法名、参数类型、返回类型等）都将被存储在Metaspace中。

  ### Metaspace的特点

  - **动态扩展**：Metaspace的大小是动态的，可以根据应用程序的需要进行扩展，避免了像永久代那样因为空间不足而导致的内存溢出问题。
  - **内存限制**：虽然Metaspace的大小默认是动态扩展的，但也可以通过JVM启动参数（如`-XX:MaxMetaspaceSize`）来设置其最大大小，以防止其无限制地扩展。
  - **性能优化**：Metaspace的引入提高了JVM的性能，因为它避免了永久代在垃圾收集时可能导致的长时间停顿。

  ### 总结

  Metaspace是Java 8及以后版本中用于存储类元数据的内存区域，它取代了之前的永久代。Metaspace主要存储类的结构信息、常量池、字段描述和方法描述等内容，这些信息对于Java应用程序的运行至关重要。通过合理配置Metaspace的大小，可以确保应用程序的稳定运行，并避免内存溢出等问题的发生。