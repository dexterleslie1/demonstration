# 浏览器开发环境中配置`scss`

> `html`中使用`scss`参考[链接](https://www.educative.io/answers/how-to-use-sass-in-your-html-code)

`ubuntu`安装`sass`命令，参考[链接](https://sass-lang.com/install/)

```bash
sudo npm install -g sass
```

查看是否成功安装`sass`命令

```bash
sass --version
```

`scss`源文件被改动会自动编译为`css`文件

```sh
sass --watch index.scss index.css
```

在`html`中通过`<link rel="stylesheet" href="index.css">`引用编译后的`css`文件。