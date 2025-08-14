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

