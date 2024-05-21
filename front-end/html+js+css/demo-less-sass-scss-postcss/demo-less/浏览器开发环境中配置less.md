# 浏览器开发环境中配置`less`

> 参考以下链接在浏览器中引用`less` [链接](https://lesscss.org/usage/#using-less-in-the-browser)
>
> 下面例子的具体源码参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/front-end/html%2Bjs%2Bcss/demo-less-sass-scss-postcss/demo-less)

下载离线的`less.js`文件（用于解析`less`样式文件），[链接](https://github.com/less/less.js/archive/master.zip)

`index.html`引用并配置`less`

```html
<script>
    /* 设置less */
    less = {
    	env: "development"
    };
</script>
<script src="less.js" type="text/javascript" data-env="development"></script>
```

自定义并引用`less`样式，`style.less`样式文件内容如下：

```less
// 定义变量
@link-color: red;
@link-color-hover: darken(@link-color, 10%);

// 使用变量
.demo1 a {
    color: @link-color;
}
```

在`<script ...></script>`之前引用自定义`less`样式文件

```html
<link rel="stylesheet/less" type="text/css" href="styles.less" />
```

`index.html`内容如下：

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <link rel="stylesheet/less" type="text/css" href="styles.less" />
    <script>
        /* 设置less */
        less = {
            env: "development"
        };
    </script>
    <script src="less.js" type="text/javascript" data-env="development"></script>
</head>

<body>
    <!--
        参考 https://lesscss.org/features/#variables-feature
    -->
    <div>变量使用</div>
    <div class="demo1">
        <a href="#">链接</a>
    </div>
</body>

</html>
```

