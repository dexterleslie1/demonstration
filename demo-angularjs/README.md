## 概念

AngularJS 是由 **Google** 于 2010 年推出的一款**开源 JavaScript 框架**，主要用于构建**单页应用程序（SPA, Single-Page Application）**。它是早期前端 MVC（Model-View-Controller）/ MVVM（Model-View-ViewModel）架构的重要实践者，通过扩展 HTML 语法、双向数据绑定等特性，大幅简化了动态 Web 应用的开发流程。

### 一、核心定位与特点

AngularJS 的设计目标是解决传统 Web 开发中「HTML 静态化」与「JavaScript 动态交互」的割裂问题，让开发者能用更简洁的方式构建复杂的前端应用。其核心特点包括：

#### 1. **双向数据绑定（Two-Way Data Binding）**

这是 AngularJS 最标志性的特性之一：当 View（视图层，如 HTML）中的数据变化时，会自动同步到 Model（数据模型）；反之，Model 中的数据变化也会实时反映到 View 上，**无需手动操作 DOM**。

例如：

```
<!-- View -->
<div ng-app>
  姓名：<input type="text" ng-model="user.name"> <br>
  你好，{{ user.name }}！ <!-- 自动同步输入内容 -->
</div>

<script src="angular.js"></script>
<script>
  // 无需额外代码，ng-model 已将 input 与 $scope.user.name 绑定
</script>
```

#### 2. **MVC/MVVM 架构模式**

AngularJS 采用 **MVVM**（Model-View-ViewModel）架构（可兼容 MVC）：

- 

  **Model**：业务数据（如 JavaScript 对象）；

- 

  **View**：用户界面（HTML + AngularJS 指令）；

- 

  **ViewModel**：`$scope`（AngularJS 的核心服务），负责连接 Model 和 View，处理数据同步与业务逻辑。

这种分离让代码结构更清晰，便于维护和测试。

#### 3. **指令系统（Directives）**

AngularJS 允许通过**自定义指令**扩展 HTML 语法，将复杂的 DOM 操作封装为可复用的标签/属性。常见内置指令包括：

- 

  `ng-app`：声明 AngularJS 应用的作用域；

- 

  `ng-model`：实现双向数据绑定；

- 

  `ng-repeat`：循环渲染列表（类似 `v-for`）；

- 

  `ng-if`/`ng-show`：条件渲染；

- 

  `ng-click`：绑定点击事件。

也可自定义指令（如 `<my-component></my-component>`），实现组件化开发。

#### 4. **依赖注入（Dependency Injection, DI）**

AngularJS 内置了依赖注入机制，允许开发者声明依赖（如服务、控制器），框架会自动实例化并传递依赖，**降低模块间的耦合度**，提升代码的可测试性。

例如：

```
// 定义一个服务（依赖）
app.service('UserService', function() {
  this.getUser = function() { return { name: '张三' }; };
});

// 控制器注入 UserService
app.controller('MyCtrl', function($scope, UserService) {
  $scope.user = UserService.getUser(); // 直接使用依赖
});
```

#### 5. **模板系统（Templates）**

AngularJS 使用 HTML 作为模板语言，通过指令和数据绑定将静态 HTML 转换为动态视图。模板可直接嵌入业务逻辑（如表达式 `{{ }}`、过滤器 `|`），无需拼接字符串。

#### 6. **过滤器（Filters）**

用于对数据进行格式化或过滤，内置过滤器如：

- 

  `currency`：货币格式化（`{{ 123 | currency:'¥' }}`→ ¥123.00）；

- 

  `date`：日期格式化（`{{ 1620000000000 | date:'yyyy-MM-dd' }}`→ 2021-05-03）；

- 

  `filter`：数组过滤（`{{ [1,2,3] | filter:2 }}`→ [2]）。

### 二、基本组成

一个典型的 AngularJS 应用包含以下核心部分：

1. 

   **模块（Module）**：应用的入口，通过 `angular.module()`创建，用于组织代码（如 `var app = angular.module('myApp', []);`）；

2. 

   **控制器（Controller）**：处理业务逻辑，通过 `app.controller()`定义，依赖 `$scope`与 View 通信；

3. 

   **服务（Service/Factor/Provider）**：封装可复用的业务逻辑（如 HTTP 请求、数据存储），避免重复代码；

4. 

   **路由（ngRoute）**：通过 `ngRoute`模块实现页面跳转（SPA 无刷新切换视图），需单独引入 `angular-route.js`。

### 三、适用场景与局限性

#### 适用场景：

- 

  快速开发中小型 SPA 应用（如后台管理系统、表单密集型应用）；

- 

  需要强数据绑定的动态界面（如实时数据展示）；

- 

  团队熟悉 JavaScript 但对现代前端框架（React/Vue）不熟悉的项目。

#### 局限性（也是被替代的原因）：

1. 

   **性能瓶颈**：双向数据绑定基于「脏检查」（Digest Cycle），当数据量大或频繁更新时，性能会下降；

2. 

   **学习曲线**：指令、`$scope`、`digest cycle`等概念较抽象，新手入门门槛高；

3. 

   **移动端支持弱**：设计时未针对移动端优化，性能和体验不如现代框架；

4. 

   **已停止维护**：Google 于 2018 年宣布 AngularJS 进入「长期支持（LTS）」，不再添加新功能，推荐迁移到 **Angular（2+，完全重构的 TypeScript 框架）**。

### 四、与 Angular（2+）的区别

注意：**AngularJS ≠ Angular**！

- 

  AngularJS 是 1.x 版本，基于 JavaScript，采用 MVC/MVVM，已停止更新；

- 

  Angular（2+）是 2016 年推出的全新框架，基于 TypeScript，采用组件化架构（类似 React/Vue），性能更强，生态更完善，是目前的主流选择。

### 总结

AngularJS 是前端发展史上的重要里程碑，它推动了「数据驱动视图」的理念普及，但其设计缺陷也催生了后续更优秀的前端框架。如今，AngularJS 仅用于维护 legacy 项目，新项目建议直接使用 Angular（2+）、React 或 Vue。

## 借助开源项目实践

### AngularJS1.x

>Gitee项目：https://gitee.com/honvay/hdms-community，Trae分析这个项目AngularJS1.x的使用符合最佳实践。

## angular-tree-control使用

>参考链接：https://github.com/wix-incubator/angular-tree-control
>
>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-angularjs/demo-angular-tree-control

安装依赖

```sh
npm install --registry=https://registry.npmmirror.com
```

直接使用Five Server打开index.html运行示例。

## AngularJS模块

我们来深入探讨 AngularJS 1.x 中的**模块（Module）**。这是理解 AngularJS 应用架构的基石。

### 一句话概括

**AngularJS 模块是一个容器，用于把你的应用程序组织在一起——它包含了控制器（Controllers）、服务（Services）、指令（Directives）、过滤器（Filters）、配置（Config）和运行块（Run Block）等所有部件。**

你可以把它想象成乐高积木的**底板**或一个**工具箱**，所有相关的零件都放在这个底板上，最终组合成一个完整的模型。

------

### 为什么需要模块？（核心目的）

在没有模块的时代（或者说在更简单的脚本中），我们可能会把所有 JavaScript 代码都扔在一个文件里，或者散落在 HTML 的各个 `<script>`标签中。这样做的问题是：

1. 

   **全局命名空间污染**：所有变量和函数都暴露在全局作用域（`window`对象）中，极易发生命名冲突。

2. 

   **代码难以维护和测试**：上千行的代码混杂在一起，要找到特定的功能非常困难。

3. 

   **依赖关系混乱**：不清楚哪个文件依赖哪个文件，加载顺序错误会导致应用崩溃。

4. 

   **无法实现懒加载**：所有代码在应用启动时就必须加载完毕。

模块系统就是为了解决这些问题而生的，它提供了**封装**、**依赖管理**和**代码组织**的能力。

------

### 如何定义一个模块？

使用 `angular.module`方法来定义模块。这个方法有两种主要的用法，理解它们的区别至关重要。

#### 1. 创建模块（带依赖）

当你要**创建一个新的模块**时，需要传入两个参数：

- 

  **模块名称**（字符串）

- 

  **依赖数组**（字符串数组，包含了这个模块所依赖的其他模块名称）

**语法：**

```
// 定义一个名为 'myApp' 的新模块，它依赖于 'ngRoute' 和 'ui.bootstrap' 模块
var app = angular.module('myApp', ['ngRoute', 'ui.bootstrap']);
```

**关键点：** 这个操作是**创建**。如果这个模块名之前已经存在，它会**覆盖**掉旧的模块定义。这通常在你的主应用文件（如 `app.js`）中只出现一次。

#### 2. 获取已有模块（无依赖）

当你只是想**获取一个已经定义好的模块**以便向其添加控制器、服务等时，只需要传入一个参数：模块名称。

**语法：**

```
// 获取名为 'myApp' 的模块（这个模块必须已经被创建过）
var app = angular.module('myApp');
```

**关键点：** 这个操作是**检索**。它不会重新创建模块，而是返回对该模块的引用，让你可以链式地调用 `.controller()`, `.service()`等方法。

------

### 模块的组成部分（往模块里放什么？）

定义好模块后，你就可以向其中添加各种“零件”：

#### 1. 控制器 (Controllers)

管理和准备视图所需的数据和逻辑。

```
app.controller('MyController', function($scope) {
  $scope.message = 'Hello from MyController!';
});
```

#### 2. 服务 (Services / Factories / Providers)

用于封装可重用的业务逻辑、数据共享或与其他 API 交互的代码。它们在应用的整个生命周期内是单例的。

```
// 使用 .factory 定义一个服务
app.factory('UserService', function() {
  var users = ['Alice', 'Bob'];
  return {
    getUsers: function() { return users; },
    addUser: function(user) { users.push(user); }
  };
});
```

#### 3. 指令 (Directives)

用于创建自定义的、可复用的 HTML 标签或属性，扩展 HTML 的功能。例如 `ng-click`, `ng-repeat`，或者你自己写的树形控件指令。

```
app.directive('myCustomTag', function() {
  return {
    restrict: 'E', // 作为元素使用 <my-custom-tag></my-custom-tag>
    template: '<h1>This is a custom directive!</h1>'
  };
});
```

#### 4. 过滤器 (Filters)

用于格式化视图中显示的数据，例如日期、货币、大小写转换。

```
app.filter('capitalize', function() {
  return function(input) {
    if (!input) return '';
    return input.charAt(0).toUpperCase() + input.slice(1);
  };
});
// 在视图中使用: {{ 'hello world' | capitalize }}
```

#### 5. 配置 (Config) 和运行 (Run) 块

- 

  **`.config()`**: 用于在提供者（Providers）注册和配置服务时运行代码。这是设置路由或配置第三方库的地方。只能注入**提供者（Provider）**和**常量（Constant）**。

  ```
  app.config(function($routeProvider) { // $routeProvider 是一个提供者
    $routeProvider
      .when('/', { templateUrl: 'home.html', controller: 'HomeCtrl' })
      .otherwise({ redirectTo: '/' });
  });
  ```

- 

  **`.run()`**: 在所有服务都已配置完毕，应用开始运行前执行一次。通常用于初始化全局数据。可以注入**实例（Instance）**和**常量（Constant）**。

  ```
  app.run(function(AuthService) {
    // 检查用户登录状态等初始化任务
    AuthService.checkLoginStatus();
  });
  ```

------

### 模块与 `ng-app`的关系

还记得我们之前讲的 `ng-app`吗？`ng-app`的值就是你的**根模块**的名称。

```
<html ng-app="myApp"> <!-- 告诉 AngularJS，以 'myApp' 模块为根来启动应用 -->
...
</html>
```

你的 `app.js`文件就应该创建这个根模块：

```
// 创建根模块 'myApp'，它可以依赖其他模块
angular.module('myApp', ['ngRoute', 'treeControlModule']);
```

### 最佳实践与文件组织

一个典型的 AngularJS 1.x 项目结构会按模块的功能来组织文件：

```
my-app/
├── index.html
├── app.js                 // 定义根模块 'myApp'
├── controllers/
│   ├── MainController.js
│   └── UserController.js
├── services/
│   └── UserService.js
├── directives/
│   └── treeControl.js     // 定义 'treeControlModule' 模块
└── views/
    └── main.html
```

**controllers/MainController.js:**

```
// 1. 获取主模块
angular.module('myApp')
  // 2. 向该模块添加一个控制器
  .controller('MainController', function($scope) {
    // ... 控制器逻辑
  });
```

**总结：** 模块是 AngularJS 应用的骨架和蓝图。它通过强制一种有组织、可依赖的方式来构建代码，极大地提高了大型应用的可维护性、可测试性和可扩展性。

### 示例

具体用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-angularjs/demo-module

运行示例：

1. 安装依赖

   ```sh
   npm install --registry=https://registry.npmmirror.com
   ```

2. 启动应用

   ```sh
   npm run start
   ```

3. 访问应用 http://localhost:3000

## AngularJS控制器概念

在 AngularJS（Angular 1.x）中，**控制器（Controller）** 是核心组件之一，负责**管理视图（View）与模型（Model）之间的交互逻辑**，本质上是 JavaScript 函数，用于初始化作用域（Scope）、定义业务逻辑，并将数据/方法暴露给视图使用。

### 一、控制器的核心作用

AngularJS 基于 **MVC（或 MVVM）架构**，控制器扮演“中间层”角色：

- 

  **初始化数据**：为视图准备初始状态（如默认值、列表数据）。

- 

  **处理逻辑**：封装业务规则（如表单验证、数据计算、API 调用）。

- 

  **绑定视图与模型**：通过 `$scope`（作用域）将数据和函数暴露给视图模板，实现双向数据绑定。

### 二、控制器的基本用法

#### 1. 定义控制器

通过 `angular.module().controller()`方法注册控制器，本质是创建一个关联特定模块的函数：

```
// 1. 先创建/获取模块（module）
var app = angular.module('myApp', []);

// 2. 注册控制器（controller）
app.controller('MyController', function($scope) {
  // $scope 是控制器与视图之间的“桥梁”
  $scope.message = 'Hello, AngularJS!'; // 暴露数据给视图
  $scope.greet = function() {         // 暴露方法给视图
    $scope.message = 'Hi there!';
  };
});
```

#### 2. 关联视图与控制器

在 HTML 中通过 `ng-controller`指令将控制器绑定到 DOM 元素（该元素及其子元素会受此控制器管理）：

```
<div ng-app="myApp" ng-controller="MyController">
  <p>{{ message }}</p> <!-- 双向绑定：显示 $scope.message -->
  <button ng-click="greet()">点击问候</button> <!-- 调用 $scope.greet() -->
</div>
```

### 三、关键概念：`$scope`

`$scope`是控制器与视图之间的**上下文对象**，具有以下特点：

- 

  **继承性**：子作用域（$scope）会继承父作用域的属性（类似 JS 原型链）。

- 

  **双向绑定**：视图中修改 `$scope`的数据会自动同步到控制器，反之亦然。

- 

  **生命周期**：控制器销毁时，`$scope`也会被销毁（需手动清理定时器、事件监听等避免内存泄漏）。

### 四、控制器的注意事项

1. 

   **避免过度逻辑**

   控制器应专注于**视图交互逻辑**，不应包含：

   - 

     DOM 操作（交给指令 `Directive`）；

   - 

     数据持久化（交给服务 `Service`/`Factory`）；

   - 

     复杂业务逻辑（可抽离到服务中复用）。

2. 

   **依赖注入（DI）**

   控制器函数的参数（如 `$scope`、`$http`）通过 AngularJS 的依赖注入机制解析，需注意：

   - 

     参数名不能随意修改（AngularJS 按名称匹配依赖）；

   - 

     生产环境建议用**数组注入**（避免代码压缩导致参数名被篡改）：

     ```
     app.controller('MyController', ['$scope', '$http', function($scope, $http) {
       // $http 可用于发起 API 请求
       $http.get('/api/data').then(function(response) {
         $scope.list = response.data;
       });
     }]);
     ```

3. 

   **控制器嵌套**

   可通过 `ng-controller`嵌套多个控制器，子控制器会继承父 `$scope`的属性：

   ```
   <div ng-controller="ParentCtrl">
     <p>父：$parentMsg = {{ parentMsg }}</p>
     <div ng-controller="ChildCtrl">
       <p>子：继承父的 parentMsg = {{ parentMsg }}</p>
       <p>子：自己的 childMsg = {{ childMsg }}</p>
     </div>
   </div>
   ```

### 五、控制器的局限性（AngularJS 1.x 痛点）

随着应用复杂度提升，控制器的缺陷逐渐暴露：

- 

  **难以测试**：控制器依赖 `$scope`，且逻辑与视图强耦合；

- 

  **复用性差**：控制器无法直接在多个场景复用（服务更适合复用）；

- 

  **状态管理混乱**：复杂应用的 `$scope`层级过深，易导致状态冲突。

因此，Angular（2+）彻底重构了架构，**移除了控制器**，改用**组件（Component）+ 服务（Service）+ 模块（Module）**的模式。

### 总结

AngularJS 控制器是**视图与模型的交互枢纽**，通过 `$scope`连接数据与视图，适合处理简单的页面逻辑。但在大型应用中，需结合服务、指令等组件，避免控制器过度臃肿。

## AngularJS服务概念

在 AngularJS（Angular 1.x）中，**服务（Service）** 是一种**可复用的单例对象**，用于封装**跨组件共享的逻辑、数据或功能**（如 API 请求、工具函数、状态管理等）。它的核心目标是解决代码的**复用性**和**解耦**，让控制器、指令等组件更专注于自身职责。

### 一、服务的核心特性

1. 

   **单例模式**：AngularJS 启动时创建服务实例，后续所有依赖该服务的组件都会共享同一个实例（避免重复创建，节省资源）。

2. 

   **依赖注入（DI）**：服务通过依赖注入机制传递给控制器、指令等组件，无需手动实例化。

3. 

   **职责单一**：每个服务专注于一类功能（如 `$http`处理 HTTP 请求、`$timeout`处理延时），符合“单一职责原则”。

### 二、服务的常见类型

AngularJS 内置了多种服务（以 `$`开头），同时支持自定义服务。常见的内置服务包括：

| 服务名       | 作用                                        |
| ------------ | ------------------------------------------- |
| `$http`      | 发起 HTTP/HTTPS 请求                        |
| `$timeout`   | 替代原生 `setTimeout`（带 Angular 上下文）  |
| `$interval`  | 替代原生 `setInterval`（带 Angular 上下文） |
| `$rootScope` | 全局作用域（所有控制器共享）                |
| `$location`  | 操作浏览器 URL                              |
| `$log`       | 日志打印（替代 `console`）                  |

### 三、自定义服务的创建方式

AngularJS 提供 **5 种方式**创建自定义服务，本质都是单例对象，但语法和适用场景略有不同：

#### 1. `factory()`：最灵活的方式（推荐新手）

通过工厂函数返回一个对象/函数，适合需要**动态生成实例**或**复杂初始化**的场景。

```
// 1. 定义模块
var app = angular.module('myApp', []);

// 2. 用 factory 创建服务
app.factory('UserService', function() {
  // 工厂函数返回服务对象（可包含属性、方法）
  var user = null; // 私有变量（仅服务内部可见）
  
  return {
    setUser: function(data) {
      user = data; // 设置用户信息
    },
    getUser: function() {
      return user; // 获取用户信息
    },
    isLogin: function() {
      return !!user; // 判断登录状态
    }
  };
});
```

#### 2. `service()`：面向对象的构造函数方式

通过构造函数（`new`操作符）创建服务，适合需要**面向对象封装**的场景（如类继承）。

```
app.service('ProductService', function() {
  // 构造函数内的 this 指向服务实例
  this.products = []; // 公开属性
  
  this.addProduct = function(product) {
    this.products.push(product);
  };
  
  this.getProducts = function() {
    return this.products;
  };
});
```

#### 3. `provider()`：最底层、可配置的方式

所有服务本质上都是通过 `provider`创建的，`factory`和 `service`是其语法糖。`provider`允许在**模块配置阶段（`config`）注入并修改服务的初始化逻辑**，适合需要**全局配置**的场景（如 API 基础地址）。

```
// 1. 用 provider 创建服务
app.provider('ApiService', function() {
  var baseUrl = 'https://default-api.com'; // 私有配置（默认地址）
  
  // 配置方法：在 config 阶段调用，修改 baseUrl
  this.setBaseUrl = function(url) {
    baseUrl = url;
  };
  
  // $get 方法是服务的“工厂函数”，返回实际的服务实例
  this.$get = function() {
    return {
      getBaseUrl: function() {
        return baseUrl;
      },
      fetchData: function(path) {
        return '请求地址：' + baseUrl + path;
      }
    };
  };
});

// 2. 在模块配置阶段注入 provider（注意：provider 名称是 ApiServiceProvider）
app.config(function(ApiServiceProvider) {
  ApiServiceProvider.setBaseUrl('https://my-api.com'); // 覆盖默认地址
});
```

#### 4. `value()`：存储简单值（无方法）

用于保存**不可变的简单数据**（如常量、配置项），不能注入到其他服务的 `config`阶段（只能注入到 `run`或其他组件的控制器）。

```
// 存储常量
app.value('APP_NAME', '我的AngularJS应用');
app.value('MAX_ITEMS', 100);
```

#### 5. `constant()`：存储常量（可注入 config 阶段）

与 `value()`类似，但**可在 `config`阶段注入**，适合存放全局不变的配置（如 API 版本号）。

```
// 存储可配置的常量
app.constant('API_VERSION', 'v1');
app.constant('DEBUG_MODE', true);

// 在 config 阶段注入 constant
app.config(function(API_VERSION) {
  console.log('当前API版本：', API_VERSION); // 输出 v1
});
```

### 四、服务的使用：依赖注入

创建服务后，通过**依赖注入**将其注入到控制器、指令等组件中即可使用：

```
// 控制器注入自定义服务
app.controller('MyController', function($scope, UserService, ProductService, ApiService) {
  // 使用 UserService 管理用户
  UserService.setUser({ name: '张三', age: 20 });
  $scope.user = UserService.getUser();
  $scope.isLogin = UserService.isLogin();

  // 使用 ProductService 管理商品
  ProductService.addProduct('手机');
  $scope.products = ProductService.getProducts();

  // 使用 ApiService 发起请求
  $scope.apiUrl = ApiService.fetchData('/users');
});
```

对应的 HTML 视图：

```
<div ng-app="myApp" ng-controller="MyController">
  <p>用户名：{{ user.name }}</p>
  <p>登录状态：{{ isLogin ? '已登录' : '未登录' }}</p>
  <p>商品列表：{{ products.join(', ') }}</p>
  <p>API地址：{{ apiUrl }}</p>
</div>
```

### 五、服务 vs 控制器：为什么用服务？

| 维度         | 控制器（Controller）                 | 服务（Service）                   |
| ------------ | ------------------------------------ | --------------------------------- |
| **职责**     | 视图交互逻辑（初始化数据、绑定事件） | 跨组件共享逻辑（API、工具、状态） |
| **复用性**   | 仅当前视图可用                       | 全应用复用                        |
| **生命周期** | 随视图创建/销毁                      | 应用启动时创建，全局存在          |
| **测试性**   | 依赖 `$scope`，难单独测试            | 独立于视图，易单元测试            |

### 六、注意事项

1. 

   **避免滥用服务**：不是所有逻辑都需要封装成服务，简单的视图逻辑仍放在控制器中。

2. 

   **依赖注入顺序**：服务的注入顺序不影响结果（AngularJS 按名称匹配），但建议按“内置服务 → 自定义服务”的顺序书写，提高可读性。

3. 

   **单例陷阱**：服务的单例特性意味着其内部状态会被所有组件共享，若需独立状态，需手动隔离（如每次返回新对象）。

### 总结

AngularJS 服务是**封装可复用功能的单例容器**，通过依赖注入实现组件间的解耦与复用。它弥补了控制器的不足，是构建大型 AngularJS 应用的核心组件之一。常见的内置服务（如 `$http`）可直接使用，自定义服务则根据场景选择 `factory`、`service`等方式创建。

## AngularJS指令概念

在 AngularJS（Angular 1.x）中，**指令（Directive）** 是**扩展 HTML 能力的核心机制**，允许开发者通过自定义 HTML 标签、属性、类名或注释来封装**DOM 操作、视图渲染、交互逻辑**等功能。简单来说，指令就是“自定义的 HTML 增强标签”，让 HTML 从静态标记变成动态的、可复用的组件。

### 一、指令的核心作用

HTML 原生标签（如 `<div>`、`<input>`）的能力有限，而 AngularJS 指令的目标是：

- 

  **扩展 HTML 语义**：用更直观的标签描述功能（如 `<user-card></user-card>`代替复杂的 `<div class="user">`）。

- 

  **封装 DOM 操作**：将 DOM 元素的创建、修改、事件绑定等逻辑封装起来，避免散落在控制器中。

- 

  **复用视图逻辑**：一次定义，多处使用（如分页组件、模态框组件）。

- 

  **实现组件化**：AngularJS 1.5+ 引入 `component`（组件指令），为向 Angular（2+）迁移铺路。

### 二、指令的类型（匹配 HTML 的方式）

AngularJS 指令通过**匹配 HTML 的不同形式**触发，常见类型有 4 种（可通过 `restrict`属性指定）：

| 类型               | 描述                      | 示例                               |
| ------------------ | ------------------------- | ---------------------------------- |
| **E（Element）**   | 匹配自定义 HTML 标签      | `<my-directive></my-directive>`    |
| **A（Attribute）** | 匹配 HTML 元素的属性      | `<div my-directive></div>`         |
| **C（Class）**     | 匹配 HTML 元素的 CSS 类名 | `<div class="my-directive"></div>` |
| **M（Comment）**   | 匹配 HTML 注释（极少用）  | `<!-- directive: my-directive -->` |

**推荐优先使用 E（标签）或 A（属性）**，因为语义更清晰，兼容性更好。

### 三、自定义指令的基本用法

通过 `angular.module().directive()`方法注册指令，该函数返回一个**配置对象**（定义指令的行为）。

#### 示例 1：最简单的属性指令（A 类型）

创建一个高亮文本的指令，当鼠标悬停时文字变红：

```
var app = angular.module('myApp', []);

// 注册指令（指令名建议用驼峰式，HTML 中用短横线分隔）
app.directive('highlightText', function() {
  return {
    restrict: 'A', // 仅匹配属性（Attribute）
    link: function(scope, element, attrs) { 
      // link 函数是指令的核心：操作 DOM、绑定事件
      // scope：指令所在的作用域
      // element：指令关联的 DOM 元素（jQuery 包装对象）
      // attrs：指令的属性集合（如 attrs.highlightText）
      
      // 鼠标悬停时添加高亮样式
      element.on('mouseenter', function() {
        element.css('color', 'red');
      });
      
      // 鼠标离开时移除高亮
      element.on('mouseleave', function() {
        element.css('color', 'black');
      });
    }
  };
});
```

在 HTML 中使用：

```
<div ng-app="myApp">
  <p highlight-text>鼠标悬停我，我会变红！</p>
</div>
```

#### 示例 2：自定义标签指令（E 类型，封装组件）

创建一个用户卡片组件，用 `<user-card>`标签展示用户信息：

```
app.directive('userCard', function() {
  return {
    restrict: 'E', // 匹配自定义标签（Element）
    scope: { 
      // 隔离作用域：避免指令内部作用域污染外部
      // @：单向绑定（字符串），=：双向绑定（变量），&：绑定方法
      userName: '@', // 接收外部传入的用户名（字符串）
      userAge: '=',  // 接收外部传入的年龄（双向绑定）
      onDelete: '&'  // 接收外部的删除方法
    },
    template: `
      <div class="card">
        <h3>{{ userName }}（{{ userAge }}岁）</h3>
        <button ng-click="onDelete()">删除用户</button>
      </div>
    `,
    // 或用 templateUrl 引入外部模板文件：
    // templateUrl: 'user-card-template.html'
  };
});
```

在 HTML 中使用（传递数据和方法）：

```
<div ng-controller="UserController">
  <!-- 双向绑定 user.age：修改输入框会影响指令内的 userAge -->
  <user-card 
    user-name="李四" 
    user-age="user.age" 
    on-delete="deleteUser()">
  </user-card>

  <!-- 测试双向绑定 -->
  <input type="number" ng-model="user.age" placeholder="修改年龄">
</div>
```

控制器中定义数据和删除方法：

```
app.controller('UserController', function($scope) {
  $scope.user = { age: 25 }; // 初始年龄
  
  $scope.deleteUser = function() {
    alert('删除用户：' + $scope.user.age + '岁的李四');
  };
});
```

### 四、指令的关键配置项

自定义指令时，通过配置对象控制其行为，常用属性如下：

| 配置项        | 作用                                                         |
| ------------- | ------------------------------------------------------------ |
| `restrict`    | 指定指令匹配 HTML 的类型（E/A/C/M，默认 EA）。               |
| `scope`       | 定义指令的作用域： - `false`（默认）：共享父作用域； - `true`：继承父作用域（独立子作用域）； - `{}`：隔离作用域（推荐，避免污染）。 |
| `template`    | 指令的 HTML 模板字符串（简单模板用）。                       |
| `templateUrl` | 指令的 HTML 模板文件路径（复杂模板用，异步加载）。           |
| `link`        | 操作 DOM、绑定事件的函数（`function(scope, element, attrs) {}`）。 |
| `controller`  | 指令内部的控制器（用于指令间的通信，如父子指令传参）。       |
| `require`     | 依赖其他指令的控制器（如 `require: '^parentDir'`表示依赖父级指令）。 |
| `replace`     | 是否替换指令所在的 HTML 元素（`true`替换，`false`追加，默认 false）。 |
| `transclude`  | 是否保留指令内原有的内容（`true`保留，配合 `ng-transclude`插入）。 |

### 五、内置常用指令

AngularJS 提供了许多内置指令，覆盖大部分日常需求：

| 指令名          | 作用                                                  |
| --------------- | ----------------------------------------------------- |
| `ng-app`        | 定义 AngularJS 应用的根元素（必须，启动应用）。       |
| `ng-controller` | 绑定控制器到 DOM 元素。                               |
| `ng-model`      | 双向数据绑定（表单元素与 `$scope`数据同步）。         |
| `ng-bind`       | 单向数据绑定（替代 `{{ }}`，避免闪烁）。              |
| `ng-click`      | 绑定点击事件。                                        |
| `ng-repeat`     | 循环渲染列表（`item in list`）。                      |
| `ng-if`         | 条件渲染（满足条件才插入 DOM，`false`时移除）。       |
| `ng-show/hide`  | 显示/隐藏元素（通过 CSS `display`控制，DOM 仍存在）。 |
| `ng-class`      | 动态添加/移除 CSS 类。                                |
| `ng-include`    | 嵌入外部 HTML 片段。                                  |

### 六、指令的优势与注意事项

#### 优势：

- 

  **语义化 HTML**：用 `<carousel></carousel>`代替 `<div class="carousel">`，代码更易读。

- 

  **封装性**：DOM 操作和逻辑集中在指令内，控制器更简洁。

- 

  **复用性**：一次定义，全应用复用（如通用的模态框、分页组件）。

#### 注意事项：

- 

  **避免过度使用**：简单的 DOM 操作可不用指令，直接用控制器 + `ng-*`。

- 

  **隔离作用域**：复杂指令建议使用隔离作用域（`scope: {}`），避免与外部作用域冲突。

- 

  **性能优化**：`templateUrl`比 `template`更适合大模板，但需注意缓存；`ng-repeat`需用 `track by`优化列表渲染性能。

- 

  **命名规范**：指令名在 JS 中用驼峰式（如 `myDirective`），HTML 中用短横线分隔（如 `my-directive`）。

### 七、指令与组件（Component）的关系

AngularJS 1.5 引入了 `component`（组件指令），它是一种**特殊的指令**，默认具有：

- 

  隔离作用域（`scope: {}`）；

- 

  支持 `bindings`（更清晰的属性绑定语法）；

- 

  生命周期钩子（如 `$onInit`、`$onDestroy`）。

`component`简化了指令的配置，是 AngularJS 向 Angular（2+）“组件化”架构过渡的关键。例如，上面的 `userCard`指令可用 `component`重写为：

```
app.component('userCard', {
  bindings: {
    userName: '@',
    userAge: '=',
    onDelete: '&'
  },
  template: `
    <div class="card">
      <h3>{{ $ctrl.userName }}（{{ $ctrl.userAge }}岁）</h3>
      <button ng-click="$ctrl.onDelete()">删除用户</button>
    </div>
  `,
  controller: function() {
    // 组件控制器（可选），$ctrl 是默认的控制别名
    this.$onInit = function() {
      console.log('组件初始化完成');
    };
  }
});
```

### 总结

AngularJS 指令是**扩展 HTML 能力的核心工具**，通过自定义标签/属性封装 DOM 操作、视图逻辑和交互行为，实现代码复用和组件化。无论是内置指令（如 `ng-repeat`）还是自定义指令，都围绕“增强 HTML 语义”和“封装复杂逻辑”展开。掌握指令是精通 AngularJS 的关键，也是理解现代前端组件化思想的基础。

## 内置指令ng-app

`<html ng-app="demoModule">`里的 **ng-app** 是 AngularJS 中的一个内置指令（Directive）。

它的主要作用是：**声明 AngularJS 应用的入口和作用范围**。

------

### 具体含义

1. 

   **标记应用根节点**

   - 

     `ng-app="demoModule"`表示：

     当前这个 `<html>`标签及其内部的所有 DOM，都属于一个名为 **demoModule** 的 AngularJS 模块。

   - 

     AngularJS 会从带有 `ng-app`的这个节点开始，**向下编译整个 DOM**，并建立数据绑定等作用机制。

2. 

   **指定模块名称**

   - 

     `"demoModule"`是这个 AngularJS 应用的模块名。

   - 

     你可以在 JavaScript 中通过这个名字定义模块及其控制器、服务等：

     ```
     angular.module('demoModule', [])
       .controller('DemoCtrl', function($scope) {
         $scope.message = 'Hello Angular!';
       });
     ```

------

### 补充说明

- 

  **如果不写 ng-app**：AngularJS 不会自动初始化应用，你需要手动调用 `angular.bootstrap(...)`来启动。

- 

  **只能有一个顶层 ng-app**（除非手动 bootstrap）：通常放在 `<html>`或 `<body>`上即可。

一句话概括：`ng-app="demoModule"`就是在告诉 AngularJS——“从这里开始，这块区域归 demoModule 这个模块管”。

## 内置指令ng-controller

<div ng-controller="DemoController as vm">中的 ng-controller 是 AngularJS 中的一个核心指令，用于创建控制器并管理特定 DOM 区域的数据和逻辑。

------

### 核心作用

#### 1. **创建控制器实例**

- 

  `ng-controller="DemoController"`告诉 AngularJS：在当前 `<div>`及其子元素范围内，使用 `DemoController`这个构造函数来创建一个控制器实例。

- 

  这个控制器负责管理该区域内的业务逻辑和数据模型。

#### 2. **建立作用域（Scope）**

- 

  每个控制器都会创建一个独立的 **$scope** 对象（或使用 `controllerAs`语法时的视图模型）。

- 

  该作用域内的数据和方法可以在对应的 HTML 模板中通过数据绑定访问。

#### 3. **实现 MVC/MVVM 架构**

- 

  **Model（模型）**：控制器中定义的属性和方法

- 

  **View（视图）**：HTML 模板

- 

  **Controller（控制器）**：连接模型和视图的桥梁

------

### 语法详解：`as vm`

这里的 `as vm`是 **controllerAs 语法**，是现代 AngularJS 开发中的最佳实践。

#### 传统方式 vs ControllerAs 方式

**传统方式（使用 $scope）：**

```
app.controller('DemoController', function($scope) {
    $scope.message = 'Hello World';
    $scope.showAlert = function() {
        alert('Clicked!');
    };
});
<!-- 在视图中使用 -->
<p>{{message}}</p>
<button ng-click="showAlert()">Click</button>
```

**ControllerAs 方式（推荐）：**

```
app.controller('DemoController', function() {
    var vm = this; // vm 代表 ViewModel
    vm.message = 'Hello World';
    vm.showAlert = function() {
        alert('Clicked!');
    };
});
<!-- 在视图中使用别名 -->
<p>{{vm.message}}</p>
<button ng-click="vm.showAlert()">Click</button>
```

------

### 实际示例

#### JavaScript 控制器

```
angular.module('demoModule')
  .controller('DemoController', function() {
    var vm = this;
    
    // 初始化数据
    vm.title = '用户信息';
    vm.user = {
        name: '张三',
        age: 25,
        email: 'zhangsan@example.com'
    };
    
    // 定义方法
    vm.updateAge = function(newAge) {
        if (newAge > 0) {
            vm.user.age = newAge;
        }
    };
    
    vm.resetUser = function() {
        vm.user = { name: '', age: 0, email: '' };
    };
    
    // 计算属性（通过方法模拟）
    vm.getBirthYear = function() {
        return new Date().getFullYear() - vm.user.age;
    };
});
```

#### HTML 模板

```
<div ng-controller="DemoController as vm">
    <h2>{{vm.title}}</h2>
    
    <div class="user-info">
        <p>姓名：{{vm.user.name}}</p>
        <p>年龄：{{vm.user.age}}</p>
        <p>出生年份：{{vm.getBirthYear()}}</p>
        <p>邮箱：{{vm.user.email}}</p>
    </div>
    
    <div class="actions">
        <input type="number" ng-model="vm.newAge" placeholder="输入新年龄">
        <button ng-click="vm.updateAge(vm.newAge)">更新年龄</button>
        <button ng-click="vm.resetUser()">重置信息</button>
    </div>
</div>
```

------

### ControllerAs 的优势

1. 

   **避免 $scope 嵌套问题**：在嵌套控制器中不会产生作用域继承的混淆

2. 

   **更清晰的代码结构**：明确知道数据来源（`vm.property`而不是 `$scope.property`）

3. 

   **更好的调试体验**：在浏览器控制台中可以直接检查控制器实例

4. 

   **便于迁移到 Angular**：与 Angular 的组件架构更相似

5. 

   **避免原型继承陷阱**：减少因 JavaScript 原型继承导致的数据覆盖问题

------

### 嵌套控制器示例

```
<div ng-controller="ParentController as parent">
    <p>父控制器：{{parent.parentData}}</p>
    
    <div ng-controller="ChildController as child">
        <!-- 可以访问父控制器的数据 -->
        <p>子控制器：{{child.childData}}</p>
        <p>来自父级：{{parent.parentData}}</p>
    </div>
</div>
```

总之，`ng-controller`是 AngularJS 中实现业务逻辑分层和视图数据绑定的核心机制，而 `as vm`语法让代码更加清晰和可维护。
