## 说明

> import、export遵循es6规范
>
> 注意: 使用es6 export定义模块时，需要在package.json文件声明 "type": "module"，否则在调用模块时会报错

## 演示目的

> 1、演示es6 export和import的用法
>
> 2、使用export和import模拟实现第三方npm包开发到发布过程

## 目录结构说明

> my-package: 第三方npm包源代码
>
> my-test: 测试调用第三方包项目

## 运行调试

```shell
# 在my-test项目中安装my-package模块
npm install ../my-package

# 运行my-test测试
node index.js
```
