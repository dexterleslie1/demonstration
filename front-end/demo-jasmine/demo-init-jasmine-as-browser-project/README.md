## 演示初始化jasmine browser项目

> https://jasmine.github.io/pages/getting_started.html

```
# 初始化项目
npm init

# 添加browser依赖
npm install --save-dev jasmine-browser-runner jasmine-core

# 初始化jasmine项目
npx jasmine-browser-runner init

# package.json 添加如下内容:
"scripts": {"test": "jasmine-browser-runner runSpecs"}

# 运行测试
npm test
```

