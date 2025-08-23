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



## 构造函数和析构函数

下面演示默认构造函数、继承关系构造函数、自定义构造函数和继承自定义构造函数：

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



下面演示构造函数和析构函数用法：

- `FISPerson.h`

  ```objective-c
  #import <Foundation/Foundation.h>
  
  NS_ASSUME_NONNULL_BEGIN
  
  @interface FISPerson : NSObject
  
  @property NSString *name;
  
  - (FISPerson *) init;
  - (FISPerson *) initWithName:(NSString *) name;
  
  + (FISPerson *) personWithName:(NSString *) name;
  
  @end
  
  NS_ASSUME_NONNULL_END
  ```

- `FISPerson.m`

  ```objective-c
  #import "FISPerson.h"
  
  @implementation FISPerson
  
  - (FISPerson *) init {
      self = [self initWithName:@""];
      return self;
  }
  
  - (FISPerson *) initWithName:(NSString *)name {
      self = [super init];
      if(self) {
          self.name = name;
      }
      return self;
  }
  
  + (FISPerson *) personWithName:(NSString *) name {
      FISPerson *person = [[FISPerson alloc] initWithName:name];
      return person;
  }
  
  @end
  ```

- `FISClass.h`

  ```objective-c
  #import <Foundation/Foundation.h>
  #import "FISPerson.h"
  
  NS_ASSUME_NONNULL_BEGIN
  
  @interface FISClass : NSObject
  
  @property NSString *name;
  @property NSNumber *roomNumber;
  @property FISPerson *instructor;
  @property NSArray *students;
  
  - (FISClass *) init;
  - (FISClass *) initWithName:(NSString *) name
                   roomNumber:(NSNumber *) roomNumber;
  - (FISClass *) initWithName:(NSString *) name
                   roomNumber:(NSNumber *) roomNumber
                   instructor:(FISPerson *) instructor
                     students:(NSArray *) students;
  
  - (void) dealloc;
  
  + (FISClass *) classWithName:(NSString *) name
                    roomNumber:(NSNumber *) roomNumber
                    instructor:(FISPerson *) instructor
                      students:(NSArray *) students;
  
  @end
  
  NS_ASSUME_NONNULL_END
  
  ```

- `FISClass.m`

  ```objective-c
  #import "FISClass.h"
  
  @implementation FISClass
  
  - (FISClass *) init {
      self = [self initWithName:@"" roomNumber:0 instructor:[[FISPerson alloc] init] students:@[]];
      return self;
  }
  
  - (FISClass *) initWithName:(NSString *)name roomNumber:(NSNumber *)roomNumber {
      self = [self initWithName:name roomNumber:roomNumber instructor:[[FISPerson alloc] init] students:@[]];
      return self;
  }
  
  - (FISClass *) initWithName:(NSString *) name
                   roomNumber:(NSNumber *) roomNumber
                   instructor:(FISPerson *) instructor
                     students:(NSArray *) students {
      self = [super init];
      if(self) {
          self.name = name;
          self.roomNumber = roomNumber;
          self.instructor = instructor;
          self.students = students;
      }
      return self;
  }
  
  - (void) dealloc {
      self.name = nil;
      self.roomNumber = nil;
      self.instructor = nil;
      self.students = nil;
      NSLog(@"FISClass析构函数被调用");
  }
  
  + (FISClass *) classWithName:(NSString *) name
                    roomNumber:(NSNumber *) roomNumber
                    instructor:(FISPerson *) instructor
                      students:(NSArray *) students {
      FISClass *class = [[FISClass alloc] initWithName:name roomNumber:roomNumber instructor:instructor students:students];
      return class;
  }
  
  @end
  
  ```

- 引用 `FISPerson` 和 `FISClass` 并测试

  ```objective-c
  #import "FISPerson.h"
  #import "FISClass.h"
  
  // 构造和析构函数测试
  FISPerson *zachDrossman = [[FISPerson alloc] initWithName:@"Zach Drossman"];
  FISPerson *markMurray = [[FISPerson alloc] initWithName:@"Mark Murray"];
  FISPerson *anishKumar = [[FISPerson alloc] initWithName:@"Anish Kumar"];
  FISClass *class = [[FISClass alloc] initWithName:@"004"
                                        roomNumber:@4
                                        instructor:zachDrossman
                                          students:@[markMurray, anishKumar]];
  NSLog(@"FISClass 004: %@", class);
  
  class = [FISClass classWithName:@"005" roomNumber:@5 instructor:zachDrossman students:@[anishKumar, markMurray]];
  NSLog(@"FISClass 005: %@", class);
  ```



## 继承和重写

`ClassA.h`

```objective-c
#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ClassA : NSObject

@property NSString * name;

- (ClassA *) initWithName:(NSString *) name;

// 定义 toString 方法
- (NSString *) toString;

@end

NS_ASSUME_NONNULL_END

```

`ClassA.m`

```objective-c
#import "ClassA.h"

@implementation ClassA

- (ClassA *) initWithName:(NSString *) name {
    self = [super init];
    if(self) {
        self.name = name;
    }
    return self;
}

- (NSString *) toString {
    NSString *result = [NSString stringWithFormat:@"name=%@", self.name];
    return result;
}

@end

```

`ClassB.h`（`ClassB` 继承 `ClassA`）：

```objective-c
#import <Foundation/Foundation.h>
#import "ClassA.h"

NS_ASSUME_NONNULL_BEGIN

// 继承 ClassA
@interface ClassB : ClassA

@property int age;

- (ClassB *) initWithName:(NSString *) name withAge:(int) age;

@end

NS_ASSUME_NONNULL_END

```

`ClassB.m`（`ClassB` 重写 `toString` 方法）：

```objective-c
#import "ClassB.h"

@implementation ClassB

- (ClassB *) initWithName:(NSString *) name withAge:(int) age {
    self = [super initWithName:name];
    if(self) {
        self.age = age;
    }
    return self;
}

// 重写 ClassA toString 实现
- (NSString *) toString {
    NSString *result = [super toString];
    result = [NSString stringWithFormat:@"%@,age=%d", result, self.age];
    return result;
}

@end

```

测试：

```objective-c
#import "ClassA.h"
#import "ClassB.h"

// 派生、重写
ClassA *classA = [[ClassA alloc] initWithName:@"ClassA实例"];
NSString *result = [classA toString];
NSLog(@"ClassA to String: %@", result);

ClassA *classB = [[ClassB alloc] initWithName:@"ClassB实例" withAge:10];
result = [classB toString];
NSLog(@"ClassB to String: %@", result);
```



## 类静态变量

`StaticVariableClass1.h`

```objective-c
#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/**
 演示使用静态变量
 */
@interface StaticVariableClass1 : NSObject

+ (NSNumber *) getStaticVariable;

@end

NS_ASSUME_NONNULL_END
```

`StaticVariableClass1.m`

```objective-c
#import "StaticVariableClass1.h"

@implementation StaticVariableClass1

static NSNumber *counter = 0;

+ (NSNumber *) getStaticVariable {
    if(!counter) {
        NSLog(@"静态counter变量不存在，已经初始化一个新的静态counter变量");
    }
    int counterTemporary = [counter intValue]+1;
    counter = [NSNumber numberWithInt:counterTemporary];
    return counter;
}

@end
```

测试

```objective-c
#import "StaticVariableClass1.h"

// 静态变量演示
NSNumber *counterTemporary = StaticVariableClass1.getStaticVariable;
assert([counterTemporary intValue] == 1);
counterTemporary = StaticVariableClass1.getStaticVariable;
assert([counterTemporary intValue] == 2);
```



## 类静态方法

`ClassStaticMethod.h`

```objective-c
#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ClassStaticMethod : NSObject

+ (void) staticMethod:(int) parameter1 parameter2:(long) parameter2;

@end

NS_ASSUME_NONNULL_END
```

`ClassStaticMethod.m`

```objective-c
#import "ClassStaticMethod.h"

@implementation ClassStaticMethod

+ (void) staticMethod:(int) parameter1 parameter2:(long) parameter2 {
    NSLog(@"parameter1=%d, parameter2=%d", parameter1, parameter1);
}

@end
```

测试

```objective-c
#import "ClassStaticMethod.h"

[ClassStaticMethod staticMethod:1 parameter2:2];
```



## 类实例方法

`InstanceMethod.h`

```objective-c
#ifndef InstanceMethod_h
#define InstanceMethod_h

@interface InstanceMethod : NSObject

- (NSString *) sayHello:(NSString *) name;

@end

#endif /* InstanceMethod_h */
```

`InstanceMethod.m`

```objective-c
#import <Foundation/Foundation.h>
#import "InstanceMethod.h"

@implementation InstanceMethod

- (NSString *) sayHello:(NSString *)name {
    NSString *helloStr = [NSString stringWithFormat: @"Hello %@", name];
    return helloStr;
}

@end
```

测试

```objective-c
#import "InstanceMethod.h"

InstanceMethod *instanceMethod = [[InstanceMethod alloc] init];
NSString *helloStr = [instanceMethod sayHello: @"Dexter"];
assert([helloStr isEqualToString: @"Hello Dexter"]);
```



## 类的`initialize`和`load`方法 - 概念

好的，我们来详细讲解一下 Objective-C 中的 `+load` 和 `+initialize` 这两个特殊的类方法。它们是运行时机制的重要组成部分，但行为和用途有显著区别。

### 总结概览

| 特性             | `+load`                                                      | `+initialize`                                                |
| :--------------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
| **调用时机**     | 非常早。在运行时将类/分类加载到内存时**立即调用**，在 `main` 函数之前。 | 相对较晚。在类**第一次**接收到消息（即方法调用）时调用。     |
| **调用顺序**     | 1. 父类 -> 子类 <br> 2. 类 -> 分类                           | 父类 -> 子类 (如果父类未初始化)                              |
| **调用次数**     | 每个类的 `+load` 方法**必定会且只会**调用一次。              | **可能**会被调用多次（如果子类没有实现，会调用父类的）。     |
| **线程安全**     | 在**单线程**环境下调用，是线程安全的。                       | 在**第一次消息发送**时调用，可能处于多线程环境，需要自己保证线程安全。 |
| **显式调用父类** | **不需要**，运行时会自动保证调用父类的 `+load`。             | **不需要**，运行时会自动保证先调用父类的 `+initialize`。     |
| **方法实现**     | 即使子类没有实现 `+load`，也不会调用父类的。                 | 如果子类没有实现 `+initialize`，运行时**会调用父类**的实现。 |
| **使用建议**     | 用于进行**方法交换（Method Swizzling）** 等非常早期的、必须的 setup。 | 用于进行类的**内部状态初始化**，如设置静态变量。             |

---

### 1. `+load` 方法

#### 调用时机
`+load` 方法是在 Objective-C 运行时加载一个类或分类时调用的。这个过程发生在 `main` 函数执行之前，所以非常早。这意味着你无法在 `+load` 方法中做任何假定当前 App 状态的事情（例如，UI 肯定还没初始化）。

#### 关键特性
*   **自动调用父类**：你不需要在子类的 `+load` 方法中写 `[super load]`。运行时会自动地、递归地确保所有父类的 `+load` 方法都先于子类被调用。
*   **类和分类都调用**：如果一个类和它的分类都实现了 `+load`，那么**两个方法都会被调用**。类的 `+load` 先调用，分类的 `+load` 后调用。
*   **手动调用无效**：你不能直接像 `[MyClass load]` 这样调用它，因为它是由运行时直接调用的，绕过了正常的消息发送机制。

#### 典型用途
最常见的用途是在分类中进行**方法交换（Method Swizzling）**。因为 `+load` 调用时机足够早，能保证在任何其他代码使用这个类的方法之前，就将原始实现和替换实现交换好。

```objective-c
// 在某个 UIViewController 的分类中
#import "UIViewController+Custom.h"
#import <objc/runtime.h>

@implementation UIViewController (Custom)

+ (void)load {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        Class class = [self class];
        
        SEL originalSelector = @selector(viewDidLoad);
        SEL swizzledSelector = @selector(custom_viewDidLoad);
        
        Method originalMethod = class_getInstanceMethod(class, originalSelector);
        Method swizzledMethod = class_getInstanceMethod(class, swizzledSelector);
        
        // 进行方法交换
        method_exchangeImplementations(originalMethod, swizzledMethod);
    });
}

- (void)custom_viewDidLoad {
    // 在调用原始实现之前做一些事情
    NSLog(@"ViewDidLoad about to be called for: %@", self);
    
    // 由于方法已经交换，这里调用的是原始的 viewDidLoad
    [self custom_viewDidLoad];
    
    // 在调用原始实现之后做一些事情
}
@end
```

---

### 2. `+initialize` 方法

#### 调用时机
`+initialize` 方法是在一个类**第一次**接收到消息时被调用的。这个消息可以是任何消息：alloc、init、甚至是某个自定义方法。它比 `+load` 的调用时机晚得多，也更加“按需”。

#### 关键特性
*   **惰性调用**：如果一个类一直没被使用，它的 `+initialize` 方法就永远不会被调用。
*   **自动调用父类**：运行时在调用子类的 `+initialize` 之前，会确保其父类已经初始化过了。你同样不需要写 `[super initialize]`。
*   **可能的多次调用**：这是最容易出错的地方。如果子类没有实现 `+initialize` 方法，那么当子类第一次收到消息时，运行时**会调用父类的 `+initialize` 方法**（因为继承机制）。因此，你的实现应该像下面例子中那样使用 `dispatch_once` 来确保代码只执行一次。

#### 典型用途
用于初始化类的静态变量或执行一次性的设置。因为它是在类第一次使用时才调用，所以比 `+load` 更适合用来设置一些可能在运行时被更改的静态状态。

```objective-c
@implementation MyClass

static NSMutableDictionary *myStaticDictionary = nil;

+ (void)initialize {
    // 使用 dispatch_once 来防止父类的 initialize 由于子类未实现而被多次调用
    if (self == [MyClass class]) { 
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
            myStaticDictionary = [[NSMutableDictionary alloc] init];
            NSLog(@"MyClass has been initialized for the first time!");
            // 其他一次性设置...
        });
    }
}

// 或者更常见的写法，不检查类，直接使用 dispatch_once
+ (void)initialize {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        myStaticDictionary = [[NSMutableDictionary alloc] init];
        NSLog(@"%@ initialized", NSStringFromClass(self));
        // 注意：这样写，每个子类第一次使用时也会触发一次自己的初始化。
    });
}

@end
```

### 重要提醒和最佳实践

1.  **保持轻量**：无论是 `+load` 还是 `+initialize`，都应该保持方法的轻量，避免执行耗时操作，以免影响 App 的启动时间或首次使用某个类时的性能。
2.  **避免依赖**：在 `+load` 中，不要假定其他类已经被加载了（除非你有明确的控制顺序），也不要创建复杂的初始化依赖关系。
3.  **`dispatch_once` 是必须的**：在 `+initialize` 中，**总是**使用 `dispatch_once` 来包装你的初始化代码，这是防止由于子类未实现而导致父类初始化逻辑被多次执行的唯一安全方式。
4.  **`+load` 中的线程安全**：虽然在 `+load` 中是单线程，但你交换的方法可能会在多线程环境下被调用，所以 Swizzling 本身也要考虑线程安全问题。

总而言之，`+load` 是进行**全局、早期、必须的**设置（如方法交换），而 `+initialize` 是进行**类内部、惰性的、按需的**初始化。理解它们的区别对于编写正确和高效的 Objective-C 代码至关重要。



## 类的`initialize`和`load`方法 - 实验

`StaticClass1.h`

```objective-c
#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface StaticClass1 : NSObject

@end

NS_ASSUME_NONNULL_END
```

`StaticClass1.m`

```objective-c
#import "StaticClass1.h"

@implementation StaticClass1

+ (void) initialize {
    NSLog(@"+++++++++:%s", __func__);
}

+ (void) load {
    NSLog(@"+++++++++:%s", __func__);
}

- (StaticClass1 *) init {
    self = [super init];
    if(!self) {
        return nil;
    }
        
    NSLog(@"+++++++++:%s %@", __func__, [self class]);
    return self;
}

@end

```

`StaticClass2.h`

```objective-c
#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/**
 StaticClass2在程序中不被调用
 查看+initialize、+load调用情况
 */
@interface StaticClass2 : NSObject

@end

NS_ASSUME_NONNULL_END
```

`StaticClass2.m`

```objective-c
#import "StaticClass2.h"

@implementation StaticClass2

+ (void) initialize {
    NSLog(@"+++++++++:%s", __func__);
}

+ (void) load {
    NSLog(@"+++++++++:%s", __func__);
}

@end

```

`StaticClass1Sub1.h`

```objective-c
#import <Foundation/Foundation.h>
#import "StaticClass1.h"

NS_ASSUME_NONNULL_BEGIN

@interface StaticClass1Sub1 : StaticClass1

@end

NS_ASSUME_NONNULL_END
```

`StaticClass1Sub1.m`

```objective-c
#import "StaticClass1Sub1.h"

@implementation StaticClass1Sub1

@end

```

测试

```objective-c
#import "StaticClass1.h"
#import "StaticClass1Sub1.h"

// 静态+initialize、+load方法演示
StaticClass1 *staticClass1 = [[StaticClass1 alloc] init];
StaticClass1Sub1 *staticClass1Sub1 = [[StaticClass1Sub1 alloc] init];
```



## `@property`和`@synthesize` - 概念

好的，我们来深入浅出地讲解 Objective-C 中的 `@property` 和 `@synthesize`。它们是 Objective-C 中定义和管理类属性的核心机制。

### 总结概览

| 特性         | `@property`                                                  | `@synthesize`                                                |
| :----------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
| **角色**     | **声明/接口**。在头文件（.h）中声明一个属性的名字和特性（如读写权限、内存管理语义等）。 | **实现/后端**。在实现文件（.m）中告诉编译器自动生成或指定一个具体的实例变量（ivar）和属性访问器方法（getter/setter）的实现。 |
| **目的**     | 向外部世界公开一个属性，并定义其行为规则。                   | 省去手动编写重复的 getter 和 setter 方法的代码。             |
| **现代版本** | 从 Xcode 4.4 (LLVM Compiler 4.0) 开始，`@synthesize` 在大多数情况下是**隐式**的、可省略的。 | 编译器会自动为你 `@synthesize propertyName = _propertyName;` |

---

### 1. `@property` - 属性的声明

`@property` 是一个编译器指令，用于在类的接口（`@interface`）部分声明一个属性。它的本质是**自动声明了对应的 setter 和 getter 方法**。

#### 基本语法
```objective-c
@property (<attributes>) <type> <propertyName>;
```

#### 属性特性（Attributes）
属性特性分为三类：**原子性**、**读写权限**和**内存管理语义**。

1.  **原子性 (Atomicity)**
    *   `atomic`（默认）：保证 setter 和 getter 方法的原子性，是线程安全的（但并**不能保证整个对象是线程安全的**）。性能有轻微开销。
    *   `nonatomic`：不保证原子性，性能更好。在单线程或明确管理线程安全的情况下，**绝大多数情况下都使用这个**。

2.  **读写权限 (Readwrite/Readonly)**
    *   `readwrite`（默认）：自动生成 getter 和 setter 方法。
    *   `readonly`：只生成 getter 方法，不生成 setter。通常用于实现只读属性或在类扩展中重新声明为 `readwrite`。

3.  **内存管理语义 (Memory Management)**
    *   `strong` (默认用于对象类型)：强引用，拥有对象的所有权。只要强引用存在，对象就不会被释放。
    *   `weak`：弱引用，不拥有对象所有权。当被引用的对象被销毁时，此属性会自动设置为 `nil`，防止野指针。非常适用于 delegate 和 IBOutlet 来避免循环引用。
    *   `copy`：在 setter 中，对传入的对象调用 `copy` 方法生成一个副本，然后对副本进行强引用。常用于保护 `NSString`, `NSArray`, `NSDictionary` 等可变子类的封装性。
    *   `assign`（默认用于基本数据类型如 `int`, `float`, `NSInteger`等）：简单的赋值操作，不进行任何内存管理。也用于弱引用但不自动置 nil 的情况（如 `delegate` 在 ARC 之前使用 `assign`，现在应使用 `weak`）。
    *   `unsafe_unretained`：类似于 `weak`，但当对象被销毁时，指针**不会自动置为 nil**，是不安全的（可能产生野指针）。

#### 示例：声明属性
```objective-c
// Person.h
@interface Person : NSObject

// 声明一个非原子的、强引用的 NSString 属性
@property (nonatomic, strong) NSString *name;

// 声明一个只读的、基本的 NSInteger 属性
@property (nonatomic, readonly) NSInteger age;

// 声明一个非原子的、copy 的 NSString 属性（防止外部可变字符串被修改后影响内部）
@property (nonatomic, copy) NSString *identifier;

// 声明一个弱引用的 delegate 属性（避免循环引用）
@property (nonatomic, weak) id<PersonDelegate> delegate;

@end
```
上面的声明等价于在 `.h` 中告诉外界，`Person` 类拥有 `- (NSString *)name;` 和 `- (void)setName:(NSString *)name;` 这两个方法。

---

### 2. `@synthesize` - 属性的实现

`@synthesize` 也是一个编译器指令，用于在类的实现（`@implementation`）部分。它的作用是**告诉编译器自动生成属性所需的 getter 和 setter 方法的实现，以及一个背后存储值的实例变量（ivar）**。

#### 基本语法
```objective-c
@synthesize <propertyName> = <instanceVariableName>;
```
*   `<propertyName>`：对应的属性名。
*   `<instanceVariableName>`：编译器生成的实例变量的名字。如果省略 `= <instanceVariableName>`，则默认生成的实例变量名就是属性名（例如 `@synthesize name;` 会生成 `name` 变量）。

#### 历史与现代用法

1.  **早期版本 (显式合成)**
    在 Xcode 4.4 之前，你必须手动使用 `@synthesize`，否则编译器只会声明方法而不会实现它们，导致链接错误。
    ```objective-c
    // Person.m (古老的方式)
    @implementation Person
    @synthesize name = _name; // 生成实例变量 _name 以及 getter/setter
    @synthesize age;          // 生成实例变量 age 以及 getter/setter
    @end
    ```
    这里，`_name` 是实例变量的名字，而 `self.name` 或 `[self name]` 是调用方法。

2.  **现代版本 (自动合成 || Auto Synthesis)**
    **从 Xcode 4.4 开始，编译器做了一个极大的改进：如果你没有显式地写 `@synthesize`，编译器会自动为你完成这一步。**
    
    默认的规则是：
    ```objective-c
    @synthesize propertyName = _propertyName;
    ```
    也就是说，编译器会：
    *   自动生成一个以下划线（`_`）开头的实例变量（如 `_name`）。
    *   自动生成 getter (`- (NSString *)name`) 和 setter (`- (void)setName:(NSString *)name`) 的标准实现。

    因此，在现代 Objective-C 开发中，**.m 文件中的 `@synthesize` 在绝大多数情况下是可以省略的**。

#### 什么时候还需要显式使用 `@synthesize`？
虽然很少见，但仍有几种情况需要你手动使用 `@synthesize`：

1.  **修改默认的实例变量名**：如果你不喜欢默认的 `_propertyName` 命名风格，可以改回旧的风格。
    ```objective-c
    @implementation Person
    @synthesize name = ivar_name; // 现在实例变量叫 ivar_name，而不是 _name
    @end
    ```

2.  **为 `readonly` 属性生成 setter**：在类扩展（Class Extension）中将一个公开的 `readonly` 属性重新声明为 `readwrite`，然后需要手动合成来生成 setter。
    ```objective-c
    // Person.h
    @interface Person : NSObject
    @property (nonatomic, readonly) NSInteger uniqueId;
    @end
    
    // Person.m
    @interface Person () // 类扩展
    @property (nonatomic, readwrite) NSInteger uniqueId; // 重新声明为 readwrite
    @end
    
    @implementation Person
    @synthesize uniqueId = _uniqueId; // 显式合成以生成 setter 方法
    - (void)generateId {
        self.uniqueId = 123; // 现在在内部可以使用了
    }
    @end
    ```

3.  **重写 getter/setter 但仍需实例变量**：如果你自定义了 getter 或 setter 方法，编译器就不会自动生成实例变量。此时如果你还需要一个实例变量，就必须使用 `@synthesize` 来告诉编译器生成一个。
    ```objective-c
    @implementation Person
    @synthesize name = _customName; // 自定义实例变量名
    
    - (NSString *)name {
        // 自定义 getter 逻辑
        return _customName ?: @"Unknown";
    }
    
    - (void)setName:(NSString *)name {
        // 自定义 setter 逻辑
        _customName = [name copy];
    }
    @end
    ```

### 总结与最佳实践

1.  **默认情况**：只需在 `.h` 文件中使用 `@property` 声明属性，在 `.m` 文件中**完全省略 `@synthesize`**。让编译器自动处理一切，这是最现代和简洁的做法。
2.  **访问方式**：
    *   **外部**：使用 `object.propertyName`（点语法）或 `[object propertyName]`（方法调用）。
    *   **内部**：在实例方法中，直接使用 `_propertyName` 来访问背后的实例变量（通常更快，且避免了触发 KVO）。使用 `self.propertyName` 来调用访问器方法（会触发 KVO，并且执行自定义的 getter/setter 逻辑）。
3.  **属性特性选择**：
    *   对象类型几乎总是用 `nonatomic`。
    *   `NSString`, `NSArray`, `NSDictionary` 等常用 `copy`。
    *   `delegate` 和 `IBOutlet` 永远用 `weak`。
    *   基本数据类型用 `assign`。

理解了 `@property` 和 `@synthesize` 的关系，就掌握了 Objective-C 中封装和数据管理的核心。



## `@property`和`@synthesize` - 实验

`Person.h`

```objective-c
#import <Foundation/Foundation.h>

/* 演示@property和@synthesize用法 */
@interface Person : NSObject

@property NSString * name;
@property NSString * password;

- (void) toString:(int) age;

@end
```

`Person.m`

```objective-c
#import "Person.h"

@implementation Person

@synthesize name;
@synthesize password;

- (void) toString:(int) age {
    NSLog(@"age=%d, name=%@, password=%@", age, name, password);
}

@end
```

测试

```objective-c
#import "Person.h"

Person *person = [[Person alloc] init];
person.name = @"姓名";
person.password = @"123456789";
NSLog(@"name=%@, password=%@", person.name, person.password);
[person toString:12];
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



## 日期时间

>参考链接：https://www.jianshu.com/p/6bd6ae5a11f1

```objective-c
// 从字符串创建 NSDate 对象
NSString *dateTimeStr = @"2012-02-11 15:03:11";
NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
dateFormatter.dateFormat = @"yyyy-MM-dd HH:mm:ss";
NSDate *dateTime = [dateFormatter dateFromString: dateTimeStr];

// 时间差（两个时间之间秒数）
dateTimeStr = @"2012-02-11 15:05:12";
NSDate *endTime = [dateFormatter dateFromString: dateTimeStr];
NSDate *createTime = dateTime;
NSTimeInterval timeIntervalSeconds = [endTime timeIntervalSinceDate:createTime];
assert(timeIntervalSeconds == 121);

// 获取当前时间
NSDate *currentTime = [NSDate date];
dateTimeStr = [dateFormatter stringFromDate: currentTime];
NSLog(@"currentTime=%@", dateTimeStr);
```



## 全局静态常量

在 `objective-c` 项目中使用 `New Group with Folder` 创建文件夹 `Constant`

声明全局静态常量：在 `Constant` 文件夹中创建 `ConstantClass.h` 头文件：

```objective-c
#import <Foundation/Foundation.h>

/**
api网关
*/
static const NSString *GATEWAY_TYPE_API = @"GATEWAY_TYPE_API";
/**
图片api网关
*/
static const NSString *GATEWAY_TYPE_IMAGE_API = @"GATEWAY_TYPE_IMAGE_API";
/**
官网
*/
static const NSString *GATEWAY_TYPE_OFFICIAL = @"GATEWAY_TYPE_OFFICIAL";
/**
Websocket网关，用于消息推送
*/
static const NSString *GATEWAY_TYPE_SOCKET_PUSH = @"GATEWAY_TYPE_SOCKET_PUSH";

NS_ASSUME_NONNULL_BEGIN

@interface ConstantClass : NSObject

@end

NS_ASSUME_NONNULL_END
```

引用全局静态常量：

```objective-c
#import <XCTest/XCTest.h>
// 引用 ConstantClass.h 头文件中声明的全局静态常量
#import "ConstantClass.h"

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
    
    // 引用全局静态常量
    assert([GATEWAY_TYPE_API isEqualToString: @"GATEWAY_TYPE_API"]);
}

- (void)testPerformanceExample {
    // This is an example of a performance test case.
    [self measureBlock:^{
        // Put the code you want to measure the time of here.
    }];
}

@end

```



## `NSString`



### `char *`转换为`NSString *`

```objective-c
// char*转换为NSString
// https://stackoverflow.com/questions/10797350/convert-char-to-nsstring
char *ptr = "Dexter";
NSString *str = [NSString stringWithUTF8String: ptr];
assert([str isEqualToString: @"Dexter"]);
```



### `NSString *`转换为`char *`

```objective-c
// NSString *转换为char *
// https://stackoverflow.com/questions/2996657/converting-an-nsstring-to-char
NSString *uuid = [[NSUUID UUID] UUIDString];
NSString *instanceId = [NSString stringWithFormat:@";+sip.instance=\"<urn:uuid:%@>\"", uuid];
const char *charTemporary = [instanceId UTF8String];
NSLog(@"char=%@", [NSString stringWithUTF8String: charTemporary]);
```



### `char`转`NSString`

```objective-c
// char转NSString
// https://www.jianshu.com/p/a0f62ff8e0fe
NSMutableArray<NSString *> *arrayAZ = [[NSMutableArray alloc] init];
for(int i=65; i<=90; i++) {
    char charTemporary = (char)i;
    [arrayAZ addObject:[NSString stringWithFormat:@"%c", charTemporary]];
}
assert([arrayAZ[0] isEqualToString: @"A"]);
assert([arrayAZ[25] isEqualToString: @"Z"]);
```



### `padding`

```objective-c
// https://stackoverflow.com/questions/5386351/objective-c-code-to-right-pad-a-nsstring
NSString *someString = @"1234";
NSString *padded = [someString stringByPaddingToLength:8 withString:@" " startingAtIndex:0];
NSLog(@"[%@]", someString);
NSLog(@"[%@]", padded);
```



### 字符串格式化

```objective-c
NSString *str = [NSString stringWithFormat:@"Hello %@", @"Dexter"];
assert([str isEqualToString:@"Hello Dexter"]);
```



### 截取子字符串

```objective-c
// 截取子字符串
NSString *sipAccountUri = @"sip:5@192.168.1.66:5060";
NSRange range = [sipAccountUri rangeOfString:@"@"];
NSString *registerUri = [sipAccountUri substringFromIndex:range.location+range.length];
NSAssert([registerUri isEqualToString:@"192.168.1.66:5060"], @"意料之外错误");
```



## `NSObject`的`class`方法

这是一个非常基础且核心的方法，理解它对于掌握 Objective-C 的运行时机制和对象模型至关重要。

### 1. 方法定义与作用

在 `NSObject` 协议和类中，`class` 方法的定义如下：

```objectivec
- (Class)class;
+ (Class)class;
```

**作用：返回接收者的类对象（Class Object）。**

- **实例方法 `-class`**：当被一个**对象实例**调用时，它返回该对象所属的类。
- **类方法 `+class`**：当被一个**类**调用时，它返回这个类本身。

在绝大多数情况下，无论你是向实例还是向类发送 `class` 消息，你得到的结果是**相同**的。

### 2. 核心概念：类对象（Class Object）

在 Objective-C 中，**类本身也是一个对象**。这被称为“类对象”。类对象是运行时在内存中创建的一种特殊结构，它存储了类的所有元信息，例如：

- 类的名称 (`NSStringFromClass([obj class])`)
- 父类 (`[obj superclass]`)
- 遵循的协议列表
- 属性的信息
- 最重要的：实例方法列表

当你调用 `[someObject class]` 时，你获取到的就是这个描述 `someObject` 的“蓝图”或“模具”的对象。

### 3. 代码示例

```objectivec
#import <Foundation/Foundation.h>

// 定义一个简单的 Person 类
@interface Person : NSObject
@property (nonatomic, copy) NSString *name;
- (void)sayHello;
@end

@implementation Person
- (void)sayHello {
    NSLog(@"Hello, my name is %@", self.name);
}
@end

int main(int argc, const char * argv[]) {
    @autoreleasepool {
        // 1. 创建一个实例对象
        Person *aPerson = [[Person alloc] init];
        aPerson.name = @"Alice";
        
        // 2. 调用实例方法 -class
        // 返回：aPerson 实例所属的类，即 Person 类对象
        Class instanceClass = [aPerson class];
        NSLog(@"Instance's class: %@", instanceClass); // 输出：Person
        
        // 3. 调用类方法 +class
        // 返回：Person 类本身
        Class classClass = [Person class];
        NSLog(@"Class's class: %@", classClass); // 输出：Person
        
        // 4. 证明两者是相等的
        if (instanceClass == classClass) {
            NSLog(@"-class and +class return the SAME Class object.");
        }
        
        // 5. 使用类对象
        // 你可以用类对象来做很多事情，比如创建实例、判断类型等
        
        // a) 创建实例 (等同于 [[Person alloc] init])
        Person *anotherPerson = [[instanceClass alloc] init];
        anotherPerson.name = @"Bob";
        [anotherPerson sayHello];
        
        // b) 判断一个对象是否是某个类或其子类的实例
        if ([aPerson isKindOfClass:instanceClass]) {
            NSLog(@"aPerson is a kind of Person");
        }
        
        // c) 判断一个对象是否 precisely 是某个类的实例（不包括子类）
        if ([aPerson isMemberOfClass:instanceClass]) {
            NSLog(@"aPerson is a member of precisely the Person class");
        }
        
        // d) 获取类名
        NSLog(@"Class name: %s", class_getName(instanceClass)); // C 函数，输出：Person
    }
    return 0;
}
```

### 4. 与 `superclass` 方法的关联

`NSObject` 还有一个类似的方法 `superclass`。

- `- (Class)superclass;`：返回接收者实例的**父类**的类对象。
- `+ (Class)superclass;`：返回接收者类的**父类**的类对象。

```objectivec
// 接上面的例子
Class superClassOfInstance = [aPerson superclass]; // 返回 NSObject 的类对象
Class superClassOfClass = [Person superclass];     // 同样返回 NSObject 的类对象

NSLog(@"Person's superclass: %@", superClassOfClass); // 输出：NSObject
```

### 5. 底层实现原理

在 Objective-C 的运行时中，每个对象的结构体（`struct objc_object`）的第一个成员都是一个 `isa` 指针。这个指针指向对象所属的类对象（`struct objc_class`）。

当你向一个实例对象发送 `-class` 消息时，运行时实际上就是直接返回这个 `isa` 指针所指向的地址。

而类对象的 `isa` 指针则指向它的**元类（Meta Class）**，这是另一个话题了。

**简单来说，`-class` 方法可以理解为：**
```objectivec
- (Class)class {
    return self->isa;
}
```
（注意：这是概念上的解释，现代运行时的实际实现会更复杂，但逻辑相同）。

### 总结

| 特性         | 描述                                                         |
| :----------- | :----------------------------------------------------------- |
| **方法签名** | `- (Class)class` 和 `+ (Class)class`                         |
| **核心作用** | **返回接收者的类对象**。实例方法返回实例所属的类，类方法返回类自身。 |
| **返回值**   | 一个 `Class` 类型的对象，代表 Objective-C 中的一个类。       |
| **主要用途** | 1. **运行时 introspection**：检查对象的类型 (`isKindOfClass:`, `isMemberOfClass:`)。 <br> 2. **动态创建**：使用类对象来动态创建实例 (`[[aClass alloc] init]`)。 <br> 3. **获取信息**：通过运行时 API 查询类的属性、方法等信息。 |
| **关键点**   | 类本身也是对象（类对象），`class` 方法就是获取这个对象的最佳方式。 |

因此，`class` 方法是连接实例和其类型定义（类）的桥梁，是 Objective-C 动态特性的基石之一。



## `isKindOfClass`

>方法用于判断一个对象是否是某个特定类或其继承链中任意子类的实例。

```objective-c
NSString *str = @"D";
assert([str isKindOfClass: [NSString class]]);

NSNumber *number = @5;
assert([number isKindOfClass: [NSNumber class]]);
```

