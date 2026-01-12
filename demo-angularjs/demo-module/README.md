# AngularJS模块最佳实践演示

这是一个展示AngularJS模块最佳实践的示例项目，演示了如何组织代码、使用各种AngularJS特性以及管理依赖项。

## 特性

- 使用模块化架构组织代码
- 包含控制器、服务、指令和组件的完整示例
- 遵循AngularJS最佳实践
- 使用npm管理依赖项
- 支持构建和开发服务器

## 项目结构

```
src/
├── app.js                 # 主应用模块
├── controllers/
│   └── demo-controller.js # 控制器示例
├── services/
│   └── demo-service.js    # 服务示例
├── directives/
│   └── demo-directive.js  # 指令示例
└── components/
    ├── demo-component.js           # 组件定义
    └── demo-component.template.html # 组件模板
```

## 安装与运行

1. 安装依赖项：
```bash
npm install
```

2. 启动开发服务器：
```bash
npm start
```

然后在浏览器中访问 `http://localhost:3000`

## 最佳实践要点

1. **立即执行函数表达式 (IIFE)**：防止全局作用域污染
2. **严格模式**：启用JavaScript严格错误检查
3. **依赖注入注解**：使用$inject数组避免压缩问题
4. **控制器As语法**：使用vm（ViewModel）模式
5. **单向数据流**：组件使用单向绑定
6. **模块化设计**：将不同功能分离到独立的服务和组件中
7. **私有数据保护**：服务内部数据不直接暴露

## AngularJS特性演示

- 模块定义和依赖管理
- 控制器和服务的创建与使用
- 自定义指令的实现
- 组件架构（Angular 1.5+）
- 数据绑定和事件处理
- 依赖注入系统