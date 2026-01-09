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

