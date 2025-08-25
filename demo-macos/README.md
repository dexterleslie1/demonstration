## 删除隔离文件权限

> [参考链接](https://malcontentboffin.com/2019/12/macOS-Error-bin-bash-bad-interpreter-Operation-not-permitted.html)

显示文件隔离属性

```sh
xattr -l my-file.sh
```

删除文件隔离属性

```sh
xattr -d com.apple.quarantine my-file.sh
```

递归删除文件和目录的隔离属性

```sh
xattr -r -d com.apple.quarantine Kap.app/
```



## 远程桌面管理方案

> 提示：没有找到相关资料在 `macOS` 中安装 `rdp` 服务。没有尝试内置的远程管理功能(`ARD`)，因为 `windows` 没有 `ARD` 客户端软件。

启用内置的屏幕共享和文件共享功能，其中文件共享功能用于解决 `realvnc viewer` 无法复制粘贴内容时使用。

下载最新版本的 `realvnc viewer` 连接到服务器。



## 安装 `macOS` 虚拟机

> 提示：尝试使用 `virtualbox` 运行``macOS11``和 `macOS12` 未能够成功，提示 `“amd-v is not available”` 错误可能是因为使用 `amd cpu` 虚拟机化时需要特殊配置，没有做实验证明。



### `vmware workstation17` 安装 `macOS11` 和 `macOS12`

> 注意：`macOS` 虚拟机不能启用 `CPU虚拟化IOMMU(IO内存管理单元)` 特性，否则在启动过程中崩溃。

下载 `macOS11` 和 `macOS12` 操作系统 `iso`，`https://archive.org/details/macos_iso`

上传 `macOS11` 和 `macOS12` 操作系统 `iso` 到 `esxi7` 存储中

从 `https://github.com/DrDonk/unlocker` 下载最新版本 `unlocker427.zip` 并执行其中的 `windows/unlock.exe`，注意：执行 `unlock.exe` 时先关闭运行中的 `vmware workstation`。

重启 `windows` 操作系统后创建虚拟机，注意：固件类型选择 `UEFI`，否则无法从 `iso` 引导安装；操作系统类型选择苹果系统的相应版本。

针对 `AMD cpu` 需要编辑虚拟机对应的 `xxx.vmx` 文件并加入下面内容才能够启动 `macOS` 虚拟机，否则虚拟机一直停顿在苹果 `logo` 界面

> `AMD CPU` 安装 `macOS` 需要修改 `vmx` 文件，参考链接 `https://www.nakivo.com/blog/run-mac-os-on-vmware-esxi/`、`https://stackoverflow.com/questions/67025805/vmware-macos-bigsur-in-win10`

```properties
cpuid.0.eax = "0000:0000:0000:0000:0000:0000:0000:1011"
cpuid.0.ebx = "0111:0101:0110:1110:0110:0101:0100:0111"
cpuid.0.ecx = "0110:1100:0110:0101:0111:0100:0110:1110"
cpuid.0.edx = "0100:1001:0110:0101:0110:1110:0110:1001"
cpuid.1.eax = "0000:0000:0000:0001:0000:0110:0111:0001"
cpuid.1.ebx = "0000:0010:0000:0001:0000:1000:0000:0000"
cpuid.1.ecx = "1000:0010:1001:1000:0010:0010:0000:0011"
cpuid.1.edx = "0000:0111:1000:1011:1111:1011:1111:1111"
```

`vmware workstation17` 破解序列号

> [序列号参考链接](https://gist.github.com/PurpleVibe32/30a802c3c8ec902e1487024cdea26251)

```
MC60H-DWHD5-H80U9-6V85M-8280D
```





### `esxi7 update3` 安装 `macOS11` 和 `macOS12`

> 参考链接 `https://vmscrub.com/installing-macos-12-monterey-on-vmware-esxi-7-update-3/`

下载 `macOS11` 和 `macOS12` 操作系统 `iso`，`https://archive.org/details/macos_iso`

上传 `macOS11` 和 `macOS12` 操作系统 `iso` 到 `esxi7` 存储中

从 `https://github.com/erickdimalanta/esxi-unlocker` 下载 `esxi-unlocker-master.zip`

上传 `esxi-unlocker-master.zip` 到 `esxi7` 存储后，`ssh exsi7` 主机解压 `zip`

```sh
unzip esxi-unlocker-master.zip
```

设置 `esxi-unlocker-master` 权限

```sh
chmod 775 -R esxi-unlocker-301/
```

检查是否已经安装过 `unlocker`，如果没有安装过 `unlocker` 下面命令会输出 `smcPresent = false`

```sh
./esxi-smctest.sh
```

安装 `unlocker`

```sh
./esxi-install.sh
```

重启 `esxi7` 服务器

```sh
reboot
```

重启成功后再次检查 `unlocker` 是否成功安装，如果成功安装下面命令会输出 `smcPresent = true`

```sh
./esxi-smctest.sh
```

针对 `AMD cpu` 需要编辑虚拟机对应的 `xxx.vmx` 文件并加入下面内容才能够启动 `macOS` 虚拟机，否则虚拟机一直停顿在苹果 `logo` 界面

> `AMD CPU` 安装 `macOS` 需要修改 `vmx` 文件，参考链接 `https://www.nakivo.com/blog/run-mac-os-on-vmware-esxi/`、`https://stackoverflow.com/questions/67025805/vmware-macos-bigsur-in-win10`

```properties
cpuid.0.eax = "0000:0000:0000:0000:0000:0000:0000:1011"
cpuid.0.ebx = "0111:0101:0110:1110:0110:0101:0100:0111"
cpuid.0.ecx = "0110:1100:0110:0101:0111:0100:0110:1110"
cpuid.0.edx = "0100:1001:0110:0101:0110:1110:0110:1001"
cpuid.1.eax = "0000:0000:0000:0001:0000:0110:0111:0001"
cpuid.1.ebx = "0000:0010:0000:0001:0000:1000:0000:0000"
cpuid.1.ecx = "1000:0010:1001:1000:0010:0010:0000:0011"
cpuid.1.edx = "0000:0111:1000:1011:1111:1011:1111:1111"
```



## `macOS` 虚拟机和宿主机共享文件夹

>`https://blog.csdn.net/qq_30386941/article/details/126457834`

登录到宿主机

导航到`虚拟机设置`>`选项`>`共享文件夹`功能，设置信息如下：

- 文件夹共享选择`总是启用`
- 文件夹添加宿主机要共享的文件夹

重启 `macOS` 虚拟机

登录到 `macOS` 虚拟机

导航到 `访达` > `偏好设置` 功能，设置信息如下：

- 已连接的服务器 `勾选` 状态

此时桌面就会出现挂载的共享目录



## 安装 `sourcetree`

> 提醒：下载最新的 `sourcetree macOS` 客户端会自动支持 `git-lfs`

下载 https://www.sourcetreeapp.com/

解压 `sourcetree` 后，拖动 `sourcetree` 到 `Applications` 分类中



## 安装搜狗拼音输入法

访问搜狗拼音输入法官方网站 `https://shurufa.sogou.com/`，下载 `macOS` 版本输入法的 `xxx.zip` 包后，双击运行再根据提示安装输入法即可。

使用 `ctrl+空格` 或者 `shift` 快捷键切换中文和英文输入状态。



## 苹果开发者帐号注册

登录 https://developer.apple.com/cn/programs/enroll/

其他资料照常填写，最后付款购买一年开发者计划。



## `XCTest`

在 iOS 开发中，**XCTest** 是苹果官方提供的**原生测试框架**，用于为 iOS、macOS、watchOS 和 tvOS 应用编写自动化测试。它深度集成在 Xcode 开发工具中，是苹果生态内最主流的测试解决方案，主要用于验证代码逻辑、功能模块和用户体验的正确性。


### 核心定位
XCTest 的核心目标是帮助开发者**提前发现代码缺陷**、**保证功能稳定性**，并通过自动化测试流程提升开发效率。无论是小型项目还是大型团队，XCTest 都是构建“可测试代码”的基础工具。


### 主要支持的测试类型
XCTest 覆盖了应用开发中常见的测试场景，主要包括三类：

#### 1. **单元测试（Unit Tests）**
   - **目标**：验证代码中最小可测试单元（如函数、方法、类）的逻辑正确性。
   - **特点**：隔离外部依赖（如网络、数据库），通过模拟（Mock）或存根（Stub）替代真实组件，确保测试仅关注当前单元的行为。
   - **示例**：测试一个计算工具类的加法方法是否返回正确结果。

#### 2. **UI 测试（UI Tests）**
   - **目标**：模拟真实用户操作（点击、输入、滑动等），验证应用界面的交互逻辑和视觉表现是否符合预期。
   - **特点**：通过系统提供的 UI 测试工具（如 XCUITest）定位界面元素（按钮、文本框等），并触发操作，最终检查界面状态或数据变化。
   - **示例**：测试用户登录流程：输入正确的账号密码 → 点击登录按钮 → 验证是否跳转到主页面。

#### 3. **性能测试（Performance Tests）**
   - **目标**：监控代码的执行效率（如耗时、内存占用），确保关键路径满足性能指标。
   - **特点**：通过 `XCTAssert` 结合性能度量（如 `measure` 块），统计多次执行的平均耗时或资源消耗，定位性能瓶颈。
   - **示例**：测试一个图片加载函数在处理大图时的耗时是否低于 100ms。


### 核心组件与使用方式
XCTest 的核心设计围绕 `XCTestCase` 类展开，开发者通过继承该类并编写测试方法来实现具体测试逻辑。以下是其关键要素：

#### 1. **测试类与测试方法**
   - 测试类需继承 `XCTestCase`，每个测试方法以 `test` 开头（如 `testAddition()`）。
   - Xcode 会自动识别这些方法，并在界面中显示为可执行的测试用例。

#### 2. **测试生命周期方法**
   - `setUpWithError()`: 在每个测试方法执行前调用，用于初始化测试环境（如创建临时对象、启动应用）。
   - `tearDownWithError()`: 在每个测试方法执行后调用，用于清理资源（如删除临时文件、退出应用）。

#### 3. **断言（Assertions）**
   - XCTest 提供了一系列 `XCTAssert` 系列方法，用于验证测试结果是否符合预期。例如：
     - `XCTAssertEqual(a, b)`: 验证两个值相等。
     - `XCTAssertTrue(condition)`: 验证条件为真。
     - `XCTAssertNil(object)`: 验证对象为 `nil`。
   - 断言失败时，Xcode 会高亮标记失败的测试方法，并输出详细的错误信息（如预期值与实际值）。

#### 4. **异步测试**
   - 对于异步操作（如网络请求、定时器），XCTest 提供 `XCTestExpectation` 机制：
     - 创建一个 `XCTestExpectation` 实例（表示“期望的事件”）。
     - 异步操作完成后，调用 `fulfill()` 方法标记期望达成。
     - 使用 `waitForExpectations(timeout:handler:)` 等待期望在指定时间内完成，超时会测试失败。


### 与 Xcode 的深度集成
XCTest 与 Xcode 深度绑定，提供了便捷的测试操作界面：
- **可视化运行测试**：通过 Xcode 工具栏的菱形图标（▶️）运行单个测试方法、整个测试类或全部测试。
- **测试导航器**：通过 `Cmd + 6` 打开测试导航器，查看所有测试用例的状态（通过/失败）、历史记录和覆盖率数据。
- **调试支持**：测试失败时可直接跳转到对应代码行，支持断点调试。
- **测试覆盖率报告**：Xcode 可生成测试覆盖率统计（`Product > Scheme > Edit Scheme > Test > Code Coverage`），帮助开发者识别未被测试覆盖的代码。


### 优势与适用场景
- **官方原生支持**：与 iOS 系统底层（如 UIKit、Core Data）深度集成，兼容最新的 Swift/Objective-C 特性（如 SwiftUI 的 UI 测试）。
- **持续集成友好**：支持通过 `xcodebuild` 命令行工具或 CI/CD 平台（如 Jenkins、GitHub Actions）自动化执行测试，适配团队协作流程。
- **跨平台覆盖**：同一套测试框架可用于 iOS、macOS 等多个苹果平台，降低多端开发的测试成本。


### 简单示例
以下是一个单元测试的示例代码（Swift）：

```swift
import XCTest
@testable import YourApp // 导入被测试的主模块

class MathUtilsTests: XCTestCase {
    // 测试加法方法
    func testAddition() {
        let result = MathUtils.add(3, 5)
        XCTAssertEqual(result, 8, "3 + 5 应等于 8")
    }
    
    // 测试异步网络请求
    func testNetworkRequest() {
        let expectation = self.expectation(description: "网络请求应在 2 秒内完成")
        
        NetworkManager.fetchData(from: "https://api.example.com") { data, error in
            XCTAssertNil(error, "请求不应出错")
            XCTAssertNotNil(data, "应返回有效数据")
            expectation.fulfill() // 异步操作完成后标记期望达成
        }
        
        waitForExpectations(timeout: 2.0) { error in
            if let error = error {
                XCTFail("等待超时: \(error)")
            }
        }
    }
}
```


### 总结
XCTest 是 iOS 开发中不可或缺的测试工具，通过它可以系统地为应用编写单元测试、UI 测试和性能测试，确保代码质量和功能稳定性。结合 Xcode 的可视化工具和持续集成支持，能有效提升开发效率和团队协作体验。



## `XCTest` - `XCTestExpectation` 等待异步操作

>说明：在测试中，测试主线程需要等待异步测试子线程结束才退出主线程。

`Swift` 项目测试代码中使用 `XCTestExpectation` 示例：

```swift
import XCTest
@testable import demo2
import Alamofire

final class demo2Tests: XCTestCase {

    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func testExample() throws {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        // Any test you write for XCTest can be annotated as throws and async.
        // Mark your test throws to produce an unexpected failure when your test encounters an uncaught error.
        // Mark your test async to allow awaiting for asynchronous code to complete. Check the results with assertions afterwards.
        
        // 1. 创建一个 XCTestExpectation，描述期望的内容
        let expectation = XCTestExpectation(description: "AFNetworking GET 请求完成")
        
        AF.request("http://192.168.235.128:8080/api/v1/get?param1=p1").response { response in
            debugPrint(response)
            
            // 无论成功或失败，都标记期望完成（必须调用！）
            expectation.fulfill()
        }
        
        // 4. 等待期望完成（最长等待 10 秒，超时会报错）
        wait(for: [expectation], timeout: 10)
    }

    func testPerformanceExample() throws {
        // This is an example of a performance test case.
        self.measure {
            // Put the code you want to measure the time of here.
        }
    }

}
```

`objective-c` 测试：

```objective-c
// __block 声明变量表示允许在 block 中修改外部变量
__block NSString *str = nil;

// 模拟异步回调
dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
dispatch_async(queue, ^{
    str = @"Hello";
    [expectation fulfill];
});

// 等待异步回调结果
[self waitForExpectations: @[expectation] timeout: 10];

assert([@"Hello" isEqualToString: str]);
```



## `XCTest` - 断言

```objective-c
/* 测试布尔断言 */
// 验证条件为真
BOOL condition = true;
XCTAssertTrue(condition, @"条件应该为真");

// 验证条件为假
condition = false;
XCTAssertFalse(condition, @"条件应该为假");

/* 测试空值断言 */
// 验证对象不为 nil
NSObject *object = [[NSObject alloc] init];
XCTAssertNotNil(object, @"对象不应该为 nil");

// 验证对象为 nil
object = nil;
XCTAssertNil(object, @"对象应该为 nil");

/* 测试相等断言 */
// 验证对象相等，比较内容
NSObject *obj1 = [[NSObject alloc] init];
NSObject *obj2 = obj1;
XCTAssertEqualObjects(obj1, obj2, @"对象应该相等");

// 验证基本类型相等或者比较对象的内存地址相等
NSString *value1 = @"Dexter";
NSString *value2 = @"Dexter";
XCTAssertEqual(value1, value2, @"值应该相等");

// 验证浮点数相等（带精度）
XCTAssertEqualWithAccuracy(3.14159, M_PI, 0.001, @"PI 值应该在精度范围内");
```



## `LLDB`调试器

```objective-c
// 使用 po (print object) - 最常用，打印 NSString *myString 字符串
po myString
```

