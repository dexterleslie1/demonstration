## 新建 `objective-c` 项目用于调试

使用 `Xcode` 新建名为 `demo2` 的  `objective-c App` 项目

禁用项目运行 `UI` 测试，只运行 `Unit` 测试：打开功能 `Product` > `Scheme` > `Edit Scheme` > `Test`，取消选择 `demo2UITests` 测试目标，只勾选 `demo2Tests` 测试目标。此时运行测试只会运行 `Unit` 测试。



## `main` 主函数

使用 `Xcode` 新建 `objective-c` 项目，打开 `main` 文件替换为以下代码：

```objective-c
#import <Foundation/Foundation.h>

@interface Person :NSObject
    @property int age;
@end

@implementation Person

-(id)init{
    if(self=[super init]){
        _age=10;
    }
    return self;
}
@end

int main(int argc, const char * argv[]) {
    @autoreleasepool {
        Person *p=[[Person alloc]init];
        NSLog(@"p.age=%d",p.age);
    }
    return 0;
}

```



## 指针

>[指针解引用](https://baike.baidu.com/item/%E8%A7%A3%E5%BC%95%E7%94%A8)
>
>[指针](https://blog.csdn.net/senwin2009/article/details/48439361)

操作系统会为每个内存单元进行编号，这个编号就是内存单元的地址。指针也就是指这些单元的内存地址。简单的说,变量的内存地址就是该变量的指针，而用于保存内存地址（指针）的变量就是指针变量。

```objective-c
int iTemporary = 10;
// 打印变量指针地址
NSLog(@"&Temporary=%p", &iTemporary);

// iTemporaryPointer 指针变量指向 iTemporary 变量
int *iTemporaryPointer = &iTemporary;
// 打印变量 iTemporary 指针地址
NSLog(@"&iTemporary=%p", &iTemporary);
// 打印 iTemporaryPointer 指针地址
NSLog(@"iTemporaryPointer=%p", iTemporaryPointer);
// 打印 iTemporaryPointer 指针指向的变量
NSLog(@"*iTemporaryPointer=%d", *iTemporaryPointer);
```



## `alloc`

在 Objective-C 中，`alloc` 是一个**类方法**（Class Method），属于 `NSObject` 类及其子类的方法。它的核心作用是为对象**分配内存空间**，并返回一个指向该内存地址的指针（即对象的「未初始化实例」）。后续需要配合 `init` 方法完成对象的初始化，最终得到一个可用的对象实例。


### **一、`alloc` 的核心作用**
`alloc` 的主要职责是**为对象分配内存**，具体包括：
1. **计算内存大小**：根据类的实例变量（包括自身和继承自父类的所有实例变量）的大小，计算需要分配的内存空间。
2. **分配内存**：向系统申请一块符合要求的内存，并将内存初始化为 0（避免脏数据）。
3. **设置 isa 指针**：在分配的内存起始位置设置 `isa` 指针（指向对象的类对象），标识该内存属于哪个类。


### **二、`alloc` 的基本用法**
`alloc` 是类方法，调用方式为 `[类名 alloc]`，返回一个未初始化的实例对象（类型为 `id` 或具体类的指针）。

#### **示例：创建 NSObject 实例**
```objective-c
// 分配内存（未初始化）
NSObject *uninitializedObj = [NSObject alloc];

// 初始化（必须步骤！）
NSObject *initializedObj = [uninitializedObj init];
```

#### **更常见的写法：合并 `alloc` 和 `init`**
实际开发中，通常将 `alloc` 和 `init` 合并为一步，称为「构造对象」：
```objective-c
NSObject *obj = [[NSObject alloc] init];
```


### **三、`alloc` 与 `init` 的关系**
`alloc` 仅负责内存分配，不保证对象可用；`init` 负责初始化对象的状态（如设置成员变量的初始值、执行必要的配置）。**必须调用 `init` 后，对象才能被安全使用**。

#### **为什么必须初始化？**
未初始化的对象（仅 `alloc` 未 `init`）虽然内存已分配，但：
- 成员变量（如实例变量 `ivar`）的值是不确定的（可能是随机垃圾值）。
- 部分类可能在 `init` 中完成关键逻辑（如文件句柄初始化、网络连接建立等），未调用会导致功能异常。
- 直接使用未初始化的对象可能导致崩溃（如调用其方法时访问未初始化的成员变量）。


### **四、`alloc` 的底层实现**
`alloc` 方法的底层实现由 `NSObject` 提供，核心逻辑大致如下（简化版）：
```objective-c
// NSObject 的 alloc 方法伪代码
+ (instancetype)alloc {
    return [self allocWithZone:nil]; // 调用带 zone 参数的版本
}

+ (instancetype)allocWithZone:(struct _NSZone *)zone {
    // 1. 计算类需要的实例变量总大小（包括父类）
    size_t size = class_getInstanceSize(self);
    
    // 2. 向系统申请内存（使用 zone 或默认内存区）
    void *memory = malloc_zone_memalign(zone ?: malloc_default_zone(), size, alignof(id));
    
    // 3. 将内存初始化为 0（避免脏数据）
    bzero(memory, size);
    
    // 4. 设置 isa 指针（指向当前类对象）
    ((id)memory)->isa = self;
    
    return (id)memory;
}
```
- `class_getInstanceSize(self)`：通过类的 `info` 结构获取实例变量总大小。
- `malloc_zone_memalign`：向系统申请对齐的内存（保证内存地址符合对象对齐要求）。
- `bzero`：将内存初始化为 0，避免未初始化的内存数据干扰。
- `isa` 指针：每个 Objective-C 对象的首地址存储 `isa` 指针，指向其所属的类对象（`Class` 类型）。


### **五、注意事项**
1. **`alloc` 不保证成功**：极端情况下（如内存不足），`alloc` 可能返回 `nil`，因此建议在使用前检查指针是否为 `nil`（尽管现代系统中内存不足概率极低）。
   ```objective-c
   NSObject *obj = [NSObject alloc];
   if (obj) { // 检查是否分配成功
       obj = [obj init];
   }
   ```

2. **自定义初始化方法**：除了默认的 `init`，类通常会提供自定义的初始化方法（如 `initWithFrame:`、`initWithString:`），这些方法内部仍会调用 `init` 完成基础初始化。
   ```objective-c
   // 自定义初始化示例
   @interface Person : NSObject
   - (instancetype)initWithName:(NSString *)name age:(NSInteger)age;
   @end
   
   @implementation Person
   - (instancetype)initWithName:(NSString *)name age:(NSInteger)age {
       if (self = [super init]) { // 先调用父类的 init
           _name = name; // 初始化子类成员变量
           _age = age;
       }
       return self;
   }
   @end
   ```

3. **`new` 方法的本质**：`[Class new]` 是 `[[Class alloc] init]` 的语法糖，仅当类没有自定义初始化逻辑时推荐使用（否则应显式调用 `alloc` + 自定义 `init` 方法）。

4. **ARC 下的内存管理**：在 ARC（自动引用计数）环境下，`alloc` 生成的对象会被自动加入引用计数管理，无需手动调用 `retain`/`release`（但仍需注意循环引用问题）。


### **总结**
`alloc` 是 Objective-C 对象创建的第一步，负责内存分配；`init` 负责初始化对象状态。两者结合（`[[Class alloc] init]`）是创建对象的标准方式。使用时需注意：

- 必须调用 `init` 保证对象可用；
- 自定义类建议提供带参数的初始化方法；
- 极端情况检查 `alloc` 返回值（尽管罕见）。



## 构造函数

>说明：下面代码演示默认构造函数、继承关系构造函数、自定义构造函数和继承自定义构造函数。

```objective-c
#import <XCTest/XCTest.h>

// 默认构造函数
@interface Person :NSObject
    @property int age;
@end

@implementation Person

-(id)init{
    if(self=[super init]){
        _age=10;
    }
    return self;
}
@end

// 继承关系构造函数
@interface Student:Person
    @property int no;
@end

@implementation Student

-(id) init{
    if(self=[super init]){
        _no=1;
    }
    return self;
}

@end

// 自定义构造函数和继承自定义构造函数
@interface PersonConstructorCustomize :NSObject
    @property NSString *name;
    @property int no;
    -(id) initWithName:(NSString *)name;
    -(id) initWithName:(NSString *)name andNo:(int)no;
@end

@implementation PersonConstructorCustomize
    -(id) initWithName:(NSString *)name{
        if(self=[super init]){
            _name=name;
        }
        return self;
    }
    -(id) initWithName:(NSString *)name andNo:(int)no{
        if(self=[super init]){
            _name=name;
            _no=no;
        }
        return self;
    }
@end

@interface StudentConstructorCustomize:PersonConstructorCustomize
    @property int age;
    -(id) initWithName:(NSString *)name andNo:(int)no andAge:(int)age;
@end

@implementation StudentConstructorCustomize
    -(id) initWithName:(NSString *)name andNo:(int)no andAge:(int)age{
        if(self=[super initWithName:name andNo:no]){
            _age=age;
        }
        return self;
    }
@end

@interface demo_objective_c_mainTests : XCTestCase

@end

@implementation demo_objective_c_mainTests

- (void)setUp {
    // Put setup code here. This method is called before the invocation of each test method in the class.
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
}

- (void)testExample {
    // This is an example of a functional test case.
    // Use XCTAssert and related functions to verify your tests produce the correct results.
    Person *p=[[Person alloc]init];
    NSLog(@"p.age=%d",p.age);
    
    Student *student=[[Student alloc]init];
    NSLog(@"student.age=%d,student.no=%d",student.age,student.no);
    
    PersonConstructorCustomize *personConstructorCustomize=[[PersonConstructorCustomize alloc]initWithName:@"Dexter1"];
    NSLog(@"personConstructorCustomize.name=%@",personConstructorCustomize.name);
    personConstructorCustomize=[[PersonConstructorCustomize alloc]initWithName:@"Dexter2" andNo:1];
    NSLog(@"personConstructorCustomize.name=%@,personConstructorCustomize.no=%d",personConstructorCustomize.name,personConstructorCustomize.no);
    StudentConstructorCustomize *studentConstructorCustomize=[[StudentConstructorCustomize alloc]initWithName:@"Dexter3" andNo:1 andAge:22];
    NSLog(@"studentConstructorCustomize.name=%@,studentConstructorCustomize.no=%d,studentConstructorCustomize.age=%d",studentConstructorCustomize.name,studentConstructorCustomize.no,studentConstructorCustomize.age);
}

- (void)testPerformanceExample {
    // This is an example of a performance test case.
    [self measureBlock:^{
        // Put the code you want to measure the time of here.
    }];
}

@end

```



## `@字符串`、`@整数值`、`@[]` 是什么呢？

在 Objective-C 中，`@` 符号结合特定语法（如字符串、数值、数组等）可以**快速生成 Objective-C 对象**，这些语法是编译器提供的语法糖（Syntactic Sugar），让代码更简洁易读。以下是 `@字符串`、`@整数值`、`@[]`（数组）的具体含义和用法：


### 一、`@字符串`：`@"..."` —— 快速生成 `NSString` 对象
`@"..."` 是 Objective-C 中**字符串字面量**的语法，用于直接创建一个**不可变的 `NSString` 对象**（`NSString` 是 Objective-C 中表示字符串的类）。  

#### 核心特点：
- **自动生成 `NSString` 对象**：无需手动调用 `alloc` 和 `init`，编译器会隐式调用 `+[NSString stringWithUTF8String:...]` 或更高效的方式生成字符串。
- **不可变性**：生成的 `NSString` 对象是不可变的（`immutable`），无法修改其内容（强行修改会导致崩溃）。若需修改，需转换为 `NSMutableString`（如 `[NSMutableString stringWithString:原字符串]`）。
- **与 C 字符串的兼容**：可通过 `UTF8String` 方法获取 C 风格的 `char*` 字符串（如 `[@"Hello" UTF8String]` 返回 `const char*`）。

#### 示例：
```objective-c
NSString *ocStr = @"Hello, Objective-C";  // 直接生成 NSString 对象
const char *cStr = "Hello, C";            // C 语言字符串（char*）

// 比较字符串（推荐用 NSString 的方法）
BOOL isEqual = [ocStr isEqualToString:@"Hello, Objective-C"];  // YES

// 错误：尝试修改不可变字符串（崩溃）
// ocStr = [ocStr stringByAppendingString:@" World"];  // 正确做法是生成新对象：
NSString *newStr = [ocStr stringByAppendingString:@" World"];  // newStr 是新的 NSString
```


### 二、`@整数值`：`@数字` —— 快速生成 `NSNumber` 对象
`@` 符号后跟数值（如整数、浮点数、布尔值等）时，会生成一个**`NSNumber` 对象**，用于将基本数据类型（如 `int`、`float`、`BOOL` 等）包装成 Objective-C 对象，以便在集合（如 `NSArray`、`NSDictionary`）中使用（因为集合只能存储对象）。

#### 核心规则：
- **自动推断数值类型**：编译器会根据数值的字面量自动推断对应的 `NSNumber` 包装类型（如 `int`、`long`、`double`、`BOOL` 等）。
- **支持的数值类型**：包括整数（`123`）、浮点数（`123.45`）、科学计数法（`1e3`）、布尔值（`@YES`/`@NO`）等。

#### 示例：
```objective-c
// 整数（自动推断为 int 或 long，取决于数值大小）
NSNumber *intNum = @123;        // 等价于 [NSNumber numberWithInt:123]
NSNumber *bigIntNum = @123456789012345LL;  // 大整数（LL 表示 long long）

// 浮点数（自动推断为 float 或 double）
NSNumber *floatNum = @123.45f;    // 显式 float（等价于 [NSNumber numberWithFloat:123.45f]）
NSNumber *doubleNum = @123.45;    // 自动推断为 double（等价于 [NSNumber numberWithDouble:123.45]）

// 布尔值（特殊关键字 @YES/@NO）
NSNumber *boolTrue = @YES;        // 等价于 [NSNumber numberWithBool:YES]（值为 1）
NSNumber *boolFalse = @NO;        // 等价于 [NSNumber numberWithBool:NO]（值为 0）

// 字符（特殊前缀 @c）
NSNumber *charNum = @'A';         // 等价于 [NSNumber numberWithChar:'A']（ASCII 值 65）
```

#### 注意：
- 布尔值的 `@YES`/`@NO` 是 Objective-C 的特殊语法，对应 `NSNumber` 中的 `1` 和 `0`（与 C 语言的 `true`/`false` 不同，C 的 `true` 通常为 `1`，但在 Objective-C 中 `BOOL` 是 `signed char` 类型）。
- 若需其他数值类型（如 `uint64_t`），可使用 `NSNumber` 的分类方法（如 `+[NSNumber numberWithUnsignedLongLong:]`），但字面量语法不直接支持，需手动创建。


### 三、`@[]`：数组字面量 —— 快速生成 `NSArray` 对象
`@[]` 是 Objective-C 中**数组字面量**的语法，用于快速创建一个**不可变的 `NSArray` 对象**（`NSArray` 是 Objective-C 中表示有序集合的类）。

#### 核心规则：
- **元素类型**：数组中的元素必须是 Objective-C 对象（如 `NSString`、`NSNumber`、自定义对象等），不能直接存储基本数据类型（需先包装为 `NSNumber` 或 `NSValue`）。
- **不可变性**：生成的 `NSArray` 是不可变的，无法添加、删除或修改元素（强行操作会崩溃）。若需可变数组，需调用 `mutableCopy` 生成 `NSMutableArray`。
- **语法格式**：`@[元素1, 元素2, ..., 元素n]`，元素之间用逗号分隔。

#### 示例：
```objective-c
// 基础用法：包含 NSString 和 NSNumber
NSArray *array = @[@"Apple", @123, @YES];

// 嵌套数组（元素可以是其他数组）
NSArray *nestedArray = @[array, @[@"Banana", @456]];

// 访问元素（与传统 NSArray 一致）
NSString *firstElement = array[0];       // @"Apple"（等价于 [array objectAtIndex:0]）
NSNumber *secondElement = array[1];      // @123（等价于 [array objectAtIndex:1]）

// 错误：尝试修改不可变数组（崩溃）
// array[0] = @"Orange";  // 正确做法是生成可变数组：
NSMutableArray *mutableArray = [array mutableCopy];
mutableArray[0] = @"Orange";  // 可行（mutableArray 是 NSMutableArray）
```

#### 注意：
- 数组字面量至少需要一个元素吗？不，空数组可以用 `@[]` 表示（如 `NSArray *emptyArray = @[];`）。
- 若元素包含 `nil`，编译器会报错（因为 `NSArray` 不能存储 `nil`，需用 `[NSNull null]` 表示空值）。例如：  
  ```objective-c
  // 错误：不能直接包含 nil
  // NSArray *badArray = @[@"A", nil, @"B"]; 
  
  // 正确：用 [NSNull null] 代替 nil
  NSArray *goodArray = @[@"A", [NSNull null], @"B"];
  ```


### 总结
Objective-C 中的 `@` 字面量（`@字符串`、`@整数值`、`@[]`）是编译器提供的语法糖，核心作用是**简化对象创建过程**，让代码更简洁。它们的本质是：
- `@"..."` → 不可变 `NSString` 对象；
- `@数字` → `NSNumber` 对象（包装基本数据类型）；
- `@[]` → 不可变 `NSArray` 对象（元素必须是对象）。

这些语法从 Xcode 4.4（LLVM 编译器 4.0）开始引入，大幅提升了 Objective-C 代码的可读性和开发效率。



## `NSArray` 和 `NSMutableArray`

> [`NSArray` 与 `NSMutableArray` 的排序](https://blog.csdn.net/weixin_33735676/article/details/94287705)

在 Objective-C 中，`NSArray` 是**不可变数组**（元素一旦创建不可修改、添加或删除），其可变子类 `NSMutableArray` 支持动态操作。构造 `NSArray` 的核心是通过**元素对象**初始化，以下是常用的构造方式，覆盖不可变和可变数组的场景。


### **一、不可变数组（NSArray）的构造**
不可变数组适合存储固定的、无需修改的数据集合，构造方式灵活多样。


#### **1. 字面量语法（最常用，简洁高效）**
Objective-C 从 iOS 6/macOS 10.8 开始支持字面量语法，通过 `@[]` 直接构造不可变数组，元素用逗号分隔。  
**语法格式**：  
```objective-c
NSArray *array = @[Element1, Element2, Element3, ...];
```
- **元素要求**：所有元素必须是 **Objective-C 对象**（如 `NSString`、`NSNumber`、自定义类实例等），**不能为 `nil`**（否则会崩溃）。  
- **空数组**：`@[]` 表示空数组。  


**示例**：  
```objective-c
// 基础元素数组
NSArray *fruits = @[@"苹果", @"香蕉", @"橙子"];

// 混合类型数组（对象类型）
NSArray *mixed = @[
    @"文本", 
    @123,                  // 自动装箱为 NSNumber
    [NSDate date],         // NSDate 对象
    [UIColor redColor]     // UIColor 对象
];

// 空数组
NSArray *emptyArray = @[];
```


#### **2. 类方法构造**
`NSArray` 提供了多个类方法（Class Method）用于构造数组，适合动态生成或从其他数据转换。  


##### **(1) `array`：空数组**
创建一个空的不可变数组：  
```objective-c
NSArray *emptyArray = [NSArray array];
```


##### **(2) `arrayWithObjects:`：按元素列表构造**
**语法**：  
```objective-c
NSArray *array = [NSArray arrayWithObjects:
    Element1, Element2, Element3, 
    nil];  // 必须以 nil 结尾（标记元素结束）
```
- **注意**：参数是元素列表，最后一个参数必须是 `nil`（否则会崩溃）。  


**示例**：  
```objective-c
NSArray *colors = [NSArray arrayWithObjects:
    @"红色", 
    @"绿色", 
    @"蓝色", 
    nil];  // 等价于字面量 @[@"红色", @"绿色", @"蓝色"]
```


##### **(3) `arrayWithArray:`：从另一个数组复制**
创建一个与原数组内容相同的新数组（浅拷贝，元素是原数组元素的引用）：  
```objective-c
NSArray *originalArray = @[@"A", @"B", @"C"];
NSArray *copiedArray = [NSArray arrayWithArray:originalArray];
```


##### **(4) `arrayWithContentsOfFile:`：从文件读取**
从 plist 文件中读取数组（文件需符合 plist 格式）：  
```objective-c
// 假设文件路径为 path.plist，内容为 <array><string>张三</string><string>李四</string></array>
NSArray *fileArray = [NSArray arrayWithContentsOfFile:path];
```


##### **(5) `arrayWithCapacity:`：预分配容量（优化性能）**
指定初始容量（非严格限制），适用于已知大致元素数量的场景，减少内存重分配次数：  
```objective-c
NSArray *preallocatedArray = [NSArray arrayWithCapacity:10];  // 预分配10个元素的容量
```


#### **3. 初始化方法（Instance Method）**
通过 `init` 系列方法初始化不可变数组，与类方法类似，但需先创建实例再初始化。  


##### **(1) `init`：空数组**
```objective-c
NSArray *emptyArray = [[NSArray alloc] init];
```


##### **(2) `initWithObjects:`：按元素列表初始化**
与类方法 `arrayWithObjects:` 类似，但需手动调用 `alloc`：  
```objective-c
NSArray *numbers = [[NSArray alloc] initWithObjects:
    @10, 
    @20, 
    @30, 
    nil];
```


##### **(3) `initWithArray:`：从其他数组初始化**
```objective-c
NSArray *sourceArray = @[@"X", @"Y", @"Z"];
NSArray *targetArray = [[NSArray alloc] initWithArray:sourceArray];
```


##### **(4) `initWithContentsOfFile:`：从文件初始化**
```objective-c
NSArray *fileArray = [[NSArray alloc] initWithContentsOfFile:path];
```


### **二、可变数组（NSMutableArray）的构造**
可变数组（`NSMutableArray`）支持动态添加、修改或删除元素，构造方式通常基于不可变数组或直接初始化。  


#### **1. 从不可变数组初始化**
通过 `NSArray` 的实例创建可变数组（浅拷贝）：  
```objective-c
NSArray *immutableArray = @[@"a", @"b", @"c"];
NSMutableArray *mutableArray = [NSMutableArray arrayWithArray:immutableArray];
```


#### **2. 空可变数组**
```objective-c
NSMutableArray *emptyMutableArray = [NSMutableArray array];
// 或
NSMutableArray *emptyMutableArray2 = [[NSMutableArray alloc] init];
```


#### **3. 预分配容量**
```objective-c
NSMutableArray *mutableArray = [NSMutableArray arrayWithCapacity:20];  // 预分配20个元素的容量
```


#### **4. 动态构造（初始化后修改）**
可变数组的核心优势是支持动态操作，例如：  
```objective-c
NSMutableArray *dynamicArray = [NSMutableArray array];
// 添加元素
[dynamicArray addObject:@"Hello"];
[dynamicArray addObject:@123];
// 插入元素（在索引0处插入）
[dynamicArray insertObject:@"World" atIndex:0];
// 最终数组：@[@"World", @"Hello", @123]
```


### **三、注意事项**
1. **元素类型限制**：  
   `NSArray` 只能存储 **Objective-C 对象**（如 `NSString`、`NSNumber` 等），不能存储基本数据类型（如 `int`、`float`）或 C 语言结构体（如 `CGPoint`）。若需存储基本类型，需用 `NSNumber` 或 `NSValue` 包装：  
   ```objective-c
   NSArray *validArray = @[
       [NSNumber numberWithInt:100],  // 包装 int
       [NSValue valueWithCGPoint:CGPointMake(10, 20)]  // 包装 CGPoint
   ];
   ```


2. **禁止 `nil` 元素**：  
   `NSArray` 不能包含 `nil`（会导致崩溃）。若需表示「空值」，可使用 `NSNull` 对象（常用于 JSON 解析）：  
   ```objective-c
   NSArray *safeArray = @[
       @"有效数据",
       [NSNull null]  // 表示空值，而非 nil
   ];
   ```


3. **不可变数组的修改限制**：  
   尝试修改不可变数组（如调用 `addObject:`、`removeObject:`）会抛出 `-[__NSArrayI addObject:]: unrecognized selector sent to instance` 异常。若需修改，应使用 `NSMutableArray`。  


4. **字面量的性能优势**：  
   字面量语法（`@[...]`）编译时直接生成不可变数组，比运行时调用类方法更高效，推荐优先使用。  


### **四、完整示例**
```objective-c
#import <Foundation/Foundation.h>

int main(int argc, const char * argv[]) {
    @autoreleasepool {
        // 1. 字面量构造不可变数组
        NSArray *fruits = @[@"苹果", @"香蕉", @"橙子"];
        NSLog(@"字面量数组：%@", fruits);  // 输出：字面量数组：(苹果, 香蕉, 橙子)

        // 2. 类方法构造不可变数组
        NSArray *colors = [NSArray arrayWithObjects:@"红", @"绿", @"蓝", nil];
        NSLog(@"类方法数组：%@", colors);  // 输出：类方法数组：(红, 绿, 蓝)

        // 3. 初始化方法构造不可变数组
        NSArray *numbers = [[NSArray alloc] initWithObjects:@10, @20, @30, nil];
        NSLog(@"初始化数组：%@", numbers);  // 输出：初始化数组：(10, 20, 30)

        // 4. 构造可变数组（从不可变数组初始化）
        NSMutableArray *mutableFruits = [NSMutableArray arrayWithArray:fruits];
        [mutableFruits addObject:@"葡萄"];  // 动态添加元素
        NSLog(@"可变数组：%@", mutableFruits);  // 输出：可变数组：(苹果, 香蕉, 橙子, 葡萄)

        // 5. 处理 nil（使用 NSNull）
        NSArray *mixedWithNull = @[
            @"数据1",
            [NSNull null],
            @"数据2"
        ];
        for (id element in mixedWithNull) {
            if (element == [NSNull null]) {
                NSLog(@"遇到空值");
            } else {
                NSLog(@"正常数据：%@", element);
            }
        }
    }
    return 0;
}
```


### **总结**
- **不可变数组**：推荐使用字面量语法 `@[...]` 构造（简洁高效），或通过类方法（如 `arrayWithObjects:`）、初始化方法（如 `initWithArray:`）生成。  
- **可变数组**：通过 `NSMutableArray` 的类方法（如 `arrayWithArray:`）或 `alloc/init` 初始化，支持动态修改。  
- **关键细节**：避免 `nil` 元素（用 `NSNull` 替代）、确认元素为对象类型、优先使用字面量提升性能。

### 示例

```objective-c
// 字面量语法创建数组
NSArray *arrayEntries = @[
    [[NSDictionary alloc] initWithObjectsAndKeys: @"2", @"title", nil],
    [[NSDictionary alloc] initWithObjectsAndKeys: @"1", @"title", nil],
    [[NSDictionary alloc] initWithObjectsAndKeys: @"4", @"title", nil],
    [[NSDictionary alloc] initWithObjectsAndKeys: @"3", @"title", nil]
];

// 数组排序
arrayEntries = [arrayEntries sortedArrayUsingComparator:^NSComparisonResult(NSDictionary *obj1, NSDictionary *obj2) {
    NSString *title = [obj1 objectForKey:@"title"];
    NSString *title2 = [obj2 objectForKey:@"title"];
    return [title compare: title2];
}];
assert([[(NSDictionary *)arrayEntries[0] objectForKey:@"title"] isEqualToString: @"1"]);
assert([[(NSDictionary *)arrayEntries[1] objectForKey:@"title"] isEqualToString: @"2"]);
assert([[(NSDictionary *)arrayEntries[2] objectForKey:@"title"] isEqualToString: @"3"]);
assert([[(NSDictionary *)arrayEntries[3] objectForKey:@"title"] isEqualToString: @"4"]);
```



## `NSDictionary` 和 `NSMutableDictionary`

在 Objective-C 中，`NSDictionary`（不可变字典）和其子类 `NSMutableDictionary`（可变字典）是用于存储键值对（Key-Value）的集合类。构造字典的核心是通过**键（Key）**和**值（Value）**的配对来初始化，以下是常用的构造方式，覆盖不可变和可变字典的场景。


### **一、不可变字典（NSDictionary）的构造**
不可变字典一旦创建，键值对不可修改、添加或删除，适合存储固定的配置或数据。


#### **1. 字面量语法（最常用，简洁高效）**
Objective-C 从 iOS 6/macOS 10.8 开始支持字面量语法，通过 `@{}` 直接构造不可变字典，键值对用逗号分隔。  
**语法格式**：  
```objective-c
NSDictionary *dict = @{
    Key1: Value1,
    Key2: Value2,
    // 更多键值对...
};
```
- **键（Key）**：必须是对象类型（通常是 `NSString`，也支持 `NSNumber` 等），且需唯一（重复键会导致后一个覆盖前一个）。  
- **值（Value）**：可以是任意 Objective-C 对象（如 `NSString`、`NSArray`、`NSNumber` 等），但**不能为 `nil`**（否则会崩溃）。  


**示例**：  
```objective-c
// 构造包含基础类型的字典
NSDictionary *userInfo = @{
    @"name": @"张三",
    @"age": @25,  // 自动装箱为 NSNumber
    @"isStudent": @NO,
    @"hobbies": @[@"阅读", @"编程"]  // 值可以是数组
};

// 构造嵌套字典
NSDictionary *data = @{
    @"user": userInfo,
    @"timestamp": [NSDate date]  // 值可以是 NSDate 对象
};
```


#### **2. 类方法构造**
`NSDictionary` 提供了多个类方法（Class Method）用于构造字典，适合动态生成键值对或从其他集合转换。  


##### **(1) `dictionary`：空字典**
创建一个空的不可变字典：  
```objective-c
NSDictionary *emptyDict = [NSDictionary dictionary];
```


##### **(2) `dictionaryWithObjectsAndKeys:`：按顺序传入值和键**
**语法**：  
```objective-c
NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:
    Value1, Key1,
    Value2, Key2,
    nil];  // 必须以 nil 结尾
```
- **注意**：参数顺序是「值在前，键在后」，最后一个参数必须是 `nil`（标记结束）。  


**示例**：  
```objective-c
NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:
    @"张三", @"name",
    @25, @"age",
    nil];
// 等价于字面量：@{@"name": @"张三", @"age": @25}
```


##### **(3) `dictionaryWithDictionary:`：从另一个字典复制**
创建一个与原字典内容相同的新字典（浅拷贝，键值对对象是原对象的引用）：  
```objective-c
NSDictionary *originalDict = @{@"a": @1, @"b": @2};
NSDictionary *copiedDict = [NSDictionary dictionaryWithDictionary:originalDict];
```


##### **(4) `dictionaryWithObjects:forKeys:`：键数组和值数组配对**
通过两个数组（键数组和值数组）构造字典，数组长度需一致，否则会截断到较短数组的长度。  
**语法**：  
```objective-c
NSDictionary *dict = [NSDictionary dictionaryWithObjects:values 
                                                 forKeys:keys];
```


**示例**：  
```objective-c
NSArray *keys = @[@"name", @"age"];
NSArray *values = @[@"李四", @30];
NSDictionary *dict = [NSDictionary dictionaryWithObjects:values forKeys:keys];
// 结果：@{@"name": @"李四", @"age": @30}
```


##### **(5) `dictionaryWithCapacity:`：预分配容量（优化性能）**
指定初始容量（非严格限制），适用于已知大致键值对数量的场景，减少内存重分配次数。  
```objective-c
NSDictionary *dict = [NSDictionary dictionaryWithCapacity:10];  // 预分配10个键值对的容量
```


#### **3. 初始化方法（Instance Method）**
通过 `init` 系列方法初始化不可变字典，与类方法类似，但需先创建实例再初始化。  


##### **(1) `init`：空字典**
```objective-c
NSDictionary *emptyDict = [[NSDictionary alloc] init];
```


##### **(2) `initWithObjectsAndKeys:`：按顺序传入值和键**
与类方法 `dictionaryWithObjectsAndKeys:` 类似，但需手动调用 `alloc`：  
```objective-c
NSDictionary *dict = [[NSDictionary alloc] initWithObjectsAndKeys:
    @"王五", @"name",
    @28, @"age",
    nil];
```


##### **(3) `initWithDictionary:`：从其他字典复制**
```objective-c
NSDictionary *originalDict = @{@"x": @100, @"y": @200};
NSDictionary *copiedDict = [[NSDictionary alloc] initWithDictionary:originalDict];
```


### **二、可变字典（NSMutableDictionary）的构造**
可变字典（`NSMutableDictionary`）支持动态添加、修改或删除键值对，通常通过以下方式构造：  


#### **1. 从不可变字典初始化**
通过 `NSDictionary` 的实例创建可变字典（浅拷贝）：  
```objective-c
NSDictionary *immutableDict = @{@"a": @1, @"b": @2};
NSMutableDictionary *mutableDict = [NSMutableDictionary dictionaryWithDictionary:immutableDict];
```


#### **2. 空可变字典**
```objective-c
NSMutableDictionary *emptyMutableDict = [NSMutableDictionary dictionary];
// 或
NSMutableDictionary *emptyMutableDict2 = [[NSMutableDictionary alloc] init];
```


#### **3. 预分配容量**
```objective-c
NSMutableDictionary *mutableDict = [NSMutableDictionary dictionaryWithCapacity:20];
```


#### **4. 动态添加键值对（构造后修改）**
可变字典的核心优势是支持动态操作，例如：  
```objective-c
NSMutableDictionary *mutableDict = [NSMutableDictionary dictionary];
// 添加键值对
[mutableDict setObject:@"张三" forKey:@"name"];
[mutableDict setObject:@25 forKey:@"age"];
// 等价于简化写法：
mutableDict[@"email"] = @"zhangsan@example.com";  // 语法糖（推荐）
```


### **三、注意事项**
1. **键的唯一性**：字典中的键必须唯一，若插入重复键，后插入的值会覆盖之前的值。  
   ```objective-c
   NSDictionary *dict = @{@"a": @1, @"a": @2};  // 最终结果：@{@"a": @2}
   ```


2. **值不能为 `nil`**：  
   - 使用字面量语法时，值为 `nil` 会导致崩溃（如 `@{@"key": nil}`）。  
   - 若需表示「空值」，应使用 `NSNull` 对象（常用于 JSON 解析等场景）：  
     ```objective-c
     NSDictionary *safeDict = @{
         @"name": @"张三",
         @"address": [NSNull null]  // 表示空值，而非 nil
     };
     ```


3. **有序性**：  
   - 在 iOS 7+ 和 macOS 10.9+ 中，`NSDictionary` 内部使用 `NSOrderedDictionary` 实现，**键值对按插入顺序存储**（但遍历时仍需通过 `allKeys` 或 `objectForKey:` 访问，不能直接按索引访问）。  


4. **性能优化**：  
   - 若已知键值对数量，使用 `dictionaryWithCapacity:` 或 `initWithCapacity:` 预分配容量，可减少内存重分配次数，提升性能。  


### **总结**
- **不可变字典**：推荐使用字面量语法 `@{}` 构造（简洁高效），适合存储固定数据。  
- **可变字典**：通过 `NSMutableDictionary` 的类方法（如 `dictionaryWithDictionary:`）或 `alloc/init` 初始化，支持动态修改。  
- **关键细节**：避免值为 `nil`（用 `NSNull` 替代）、键的唯一性、预分配容量优化性能。

### 示例

```objective-c
// 字面量语法创建 NSDictionary
NSDictionary *userIdToVersionDictionary = @{
    @1001: @"1.1",
    @1002: @"1.2"
};
NSDictionary *userIdToVersionLocal = @{
    @1001: @"1.0",
    @1002: @"1.2"
};

// 遍历 NSDictionary
NSMutableArray * userIdsArray = [[NSMutableArray alloc] init];
for(NSNumber *key in [userIdToVersionDictionary allKeys]) {
    NSNumber *version = [userIdToVersionDictionary objectForKey:key];
    NSNumber *versionLocal = [userIdToVersionLocal objectForKey:key];
    if(versionLocal==nil) {
        continue;
    }
    if(version!=versionLocal && ![userIdsArray containsObject:key]) {
        [userIdsArray addObject:key];
    }
}
assert([userIdsArray count] == 1);
assert([(NSNumber *)userIdsArray[0] intValue] == 1001);

// 获取 NSDictionary 所有 key
NSArray<NSNumber *> *userIds = [userIdToVersionDictionary allKeys];
assert([userIds count] == 2);

// 是否包含 key
// https://blog.csdn.net/wanggsx918/article/details/21618023
assert([[userIdToVersionDictionary allKeys] containsObject:@1001]);
```



## `NSDictionary` 和 `json` 相互转换

>[参考链接](https://www.jianshu.com/p/81a00071b0c3)

```objective-c
// NSDictionary 转换为 json
NSDictionary *dictionary = @{
    @"k1": @"v1",
    @"k2": @"v2"
};
NSData *data = [NSJSONSerialization dataWithJSONObject:dictionary options:NSJSONWritingPrettyPrinted error:nil];
NSString *json = json = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];

// json 转换为 NSDictionary
NSData *jsonData = [json dataUsingEncoding:NSUTF8StringEncoding];
NSError *error;
dictionary = [NSJSONSerialization JSONObjectWithData:jsonData
                                             options:NSJSONReadingMutableContainers
                                               error:&error];
assert(error == nil);
assert([[dictionary objectForKey:@"k1"] isEqualToString:@"v1"]);
assert([[dictionary objectForKey:@"k2"] isEqualToString:@"v2"]);
```

