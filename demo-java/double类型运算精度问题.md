# double 类型运算精度问题



## Java 0.1*0.2 为何会等于 0.020000000000000004 呢？

在Java中，当你计算 `0.1 * 0.2` 并得到 `0.020000000000000004` 这样的结果时，这是由于浮点数在二进制表示中的不精确性所导致的。`double` 类型使用64位二进制数来表示一个浮点数，其中包括1位符号位、11位指数位和52位尾数位（也称为有效数字位或曼提沙）。

十进制小数 `0.1` 和 `0.2` 在二进制中都是无限循环小数，类似于十进制中的 `1/3`。当这些无限循环小数被截断以适应 `double` 类型的52位尾数位时，就会发生精度损失。

具体来说，`0.1` 在二进制中表示为 `0.0001100110011001100110011001100110011001100110011010...`（这是一个无限循环序列），但当我们将其转换为 `double` 时，只能存储有限位数的二进制数，因此会对其进行截断。

同样，`0.2` 在二进制中也有一个类似的无限循环表示。

当这两个不精确的二进制数相乘时，结果也会是一个不精确的二进制数，当这个二进制数再转换回十进制表示时，就会得到一个接近但不完全等于 `0.02` 的数，即 `0.020000000000000004`。

这种精度问题在浮点数运算中是非常常见的，并且不仅仅局限于Java，几乎所有的编程语言在使用浮点数时都会遇到类似的问题。

为了解决这个问题，你可以使用 `BigDecimal` 类，它提供了高精度的浮点数运算，能够避免这种由于二进制表示不精确而导致的精度问题。如之前所示，你可以通过字符串来构造 `BigDecimal` 对象，以确保精度不会从一开始就丢失。



## 什么是 BigDecimal？

`BigDecimal` 是 Java 中的一个类，位于 `java.math` 包中。它提供了在 Java 中进行高精度计算的能力，特别是当涉及到非常大或非常小的浮点数时，以及需要精确控制舍入行为的场景。`BigDecimal` 类是不可变的，这意味着一旦创建了一个 `BigDecimal` 对象，它的值就不能改变。

### 创建 `BigDecimal` 对象

`BigDecimal` 对象可以通过多种方式创建：

1. **通过字符串构造**：

   ```java
   BigDecimal bd1 = new BigDecimal("123.45");
   ```

   使用字符串构造是推荐的方式，因为它能精确地表示数值，避免浮点数的精度问题。

2. **通过 `double` 构造**：

   ```java
   BigDecimal bd2 = new BigDecimal(123.45);
   ```

   虽然可以通过 `double` 来构造 `BigDecimal`，但这种方式可能会引入精度问题，因为 `double` 本身就不精确。

3. **通过 `BigInteger` 和小数位数构造**：

   ```java
   BigInteger bi = new BigInteger("12345");
   int scale = 2;
   BigDecimal bd3 = new BigDecimal(bi, scale); // 结果为 123.45
   ```

### 使用 `BigDecimal` 进行运算

`BigDecimal` 提供了多种方法进行加减乘除运算，例如：

- **加法**：`add(BigDecimal augend)`
- **减法**：`subtract(BigDecimal subtrahend)`
- **乘法**：`multiply(BigDecimal multiplicand)`
- **除法**：`divide(BigDecimal divisor)`

使用这些方法进行运算时，需要注意除法可能会抛出 `ArithmeticException` 异常，如果结果不能精确表示（例如，除以零或结果的小数位数超过指定的精度）。为了避免这种情况，可以使用带有舍入模式的除法方法，如 `divide(BigDecimal divisor, int scale, RoundingMode roundingMode)`。

### 舍入模式

`RoundingMode` 是一个枚举类型，定义了不同的舍入行为，例如：

- `RoundingMode.HALF_UP`：四舍五入
- `RoundingMode.HALF_DOWN`：五舍六入
- `RoundingMode.HALF_EVEN`：银行家舍入（遇到.5时，如果前一位是奇数则进位，是偶数则舍去）
- 等等

### 示例代码

```java
import java.math.BigDecimal;
import java.math.RoundingMode;
 
public class BigDecimalExample {
    public static void main(String[] args) {
        BigDecimal bd1 = new BigDecimal("123.456");
        BigDecimal bd2 = new BigDecimal("78.123");
 
        // 加法
        BigDecimal sum = bd1.add(bd2);
        System.out.println("Sum: " + sum);
 
        // 减法
        BigDecimal difference = bd1.subtract(bd2);
        System.out.println("Difference: " + difference);
 
        // 乘法
        BigDecimal product = bd1.multiply(bd2);
        System.out.println("Product: " + product);
 
        // 除法，使用舍入模式
        BigDecimal quotient = bd1.divide(bd2, 2, RoundingMode.HALF_UP);
        System.out.println("Quotient: " + quotient);
    }
}
```

在这个示例中，我们创建了两个 `BigDecimal` 对象，并对它们进行了加、减、乘、除运算。除法运算使用了舍入模式来避免异常。

### 总结

`BigDecimal` 是 Java 中处理高精度计算的强大工具，特别适用于金融和科学计算领域。通过正确使用 `BigDecimal`，可以避免浮点数运算中的精度问题，并确保结果的准确性。



## 使用 BigDecimal 高精度运算解决 double 类型精度问题

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-double-datatype-precision-problem`

```java
@Benchmark
public void testMultiplyByUsingBigDecimalDataType(Blackhole blackhole) {
    BigDecimal d1 = new BigDecimal("0.1");
    BigDecimal d2 = new BigDecimal("0.2");
    BigDecimal d3 = d1.multiply(d2);
    Assertions.assertEquals(new BigDecimal("0.02"), d3);
    blackhole.consume(d3);
}
```



## BigDecimal、double、Double 类型运算 JMH 性能测试比较

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-double-datatype-precision-problem`

```
Benchmark                                              Mode  Cnt          Score           Error  Units
PerfTests.testMultiplyByUsingBigDecimalDataType       thrpt    3   24606167.112 ±  27380966.765  ops/s
PerfTests.testMultiplyByUsingDoubleBoxingDataType     thrpt    3  384386291.062 ± 506193410.280  ops/s
PerfTests.testMultiplyByUsingDoublePrimitiveDataType  thrpt    3  978028967.023 ± 304815765.394  ops/s
```

