## sass/scss

### 在浏览器开发环境中引入scss

需要先把scss文件编译为css文件，然后在html中通过<link rel="stylesheet" href="index.css">引用编译后的css文件。

ubuntu安装sass命令

```sh
sudo npm install -g sass
```

查看是否成功安装sass命令

```sh
sass --version
```

scss源文件被改动会自动编译为css文件

```sh
sass --watch index.scss index.css
```

参考

https://sass-lang.com/install/
https://www.educative.io/answers/how-to-use-sass-in-your-html-code

### scss变量声明和使用

参考 demo-scss

### 嵌套样式(父子样式)

参考 demo-scss