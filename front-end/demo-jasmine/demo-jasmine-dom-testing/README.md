## jasmine dom testing

> https://www.testim.io/blog/dom-testing/
> https://jasmine.github.io/setup/browser.html
>
> NOTE: 没有找到资料描述测试dom前能够预置html，只能够支持编程动态预置html
> NOTE: jasmine-browser-runner runSpecs命令默认启动浏览器执行jasmine测试，可以在jasmine中通过window、document操作dom
> NOTE: jasmine不支持模拟鼠标移动操作
> NOTE: jasmine+karma启动浏览器测试spec，也可以使用jasmine-browser-runner runSpecs命令启动浏览器测试spec



### 运行

```
# 启动服务器以便能够通过界面运行指定的spec
npx jasmine-browser-runner serve

# 访问服务器
http://localhost:8888/

# 或者不采用服务器方式运行测试而使用命令行方式运行测试
yarn test
```

