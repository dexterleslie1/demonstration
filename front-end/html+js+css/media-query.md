# Media Query



## 介绍

在网页设计中，**媒体查询（Media Queries）** 是 CSS3 引入的一项关键技术，它允许开发者根据设备的特性（如屏幕尺寸、分辨率、方向等）动态调整页面样式，从而实现**响应式设计**。简单来说，就是让网页在不同设备上（手机、平板、电脑等）自动适配最佳布局和显示效果。

### **媒体查询的核心作用**

1. **设备适配**
   根据屏幕宽度（`width`）、高度（`height`）、方向（`orientation`）等参数，为不同设备应用不同的 CSS 规则。

2. **断点控制**
   通过定义“断点”（如 `max-width: 768px`），在特定屏幕尺寸下触发样式调整，例如：

   ```css
   @media (max-width: 768px) {
     .nav-menu { display: none; } /* 手机端隐藏导航菜单 */
   }
   ```

3. **优化用户体验**
   确保内容在移动设备上易读（如调整字体大小）、布局合理（如从多列切换为单列）。

### **常见媒体特性**



| 特性               | 说明                             | 示例                       |
| ------------------ | -------------------------------- | -------------------------- |
| `width` / `height` | 屏幕视口的宽度/高度              | `(max-width: 1024px)`      |
| `orientation`      | 屏幕方向（横向/纵向）            | `(orientation: portrait)`  |
| `resolution`       | 屏幕分辨率（dpi/dpcm）           | `(min-resolution: 300dpi)` |
| `hover`            | 设备是否支持悬停（如触屏无悬停） | `(hover: hover)`           |



### **实际应用场景**

1. **响应式布局调整**

   ```css
   /* 平板横向模式 */
   @media (min-width: 768px) and (max-width: 1024px) {
     .container { flex-direction: row; }
   }
   ```

2. **移动端优化**
   隐藏桌面端专属功能（如侧边栏）、简化表单交互。

3. **高分辨率屏幕适配**
   为 Retina 屏幕提供更高清的图标：

   ```css
   @media (-webkit-min-device-pixel-ratio: 2) {
     .logo { background-image: url(logo@2x.png); }
   }
   ```

### **最佳实践**

- **断点选择**：基于主流设备尺寸（如手机 375px、平板 768px、桌面 1200px）。
- **移动优先**：先编写移动端样式，再通过 `min-width` 逐步增强大屏样式。
- **测试工具**：使用浏览器开发者工具（Chrome DevTools）模拟不同设备。

### **总结**

媒体查询是响应式设计的基石，通过灵活的条件判断，让网页像“变色龙”一样适应不同环境，确保用户无论使用何种设备都能获得良好的浏览体验。



## 媒体查询的调用方式



### 通过 html 方式调用

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/blob/main/front-end/html+js+css/css-media-query/demo-using-media-query-with-html.html)

实质上被称为使用 `<link>` 标签根据媒体查询条件加载不同的 CSS 文件。或者使用内嵌的 `<style>` 标签根据媒体查询条件加载不同的样式。

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <!--
        https://css-tricks.com/a-complete-guide-to-css-media-queries/
        根据不同的浏览器尺寸使用不同的css文件
    -->
    <style>
        .div1 {
            width: 100px;
            height: 100px;
        }
    </style>
    <!-- <=600px -->
    <link rel="stylesheet" href="le.css" media="only screen and (max-width: 600px)">
    <!-- >600px and <=700px -->
    <link rel="stylesheet" href="gt-and-le.css" media="only screen and (min-width: 600px) and (max-width: 700px)">
    <!-- >700px -->
    <style media="only screen and (min-width: 700px)">
        .div1 {
            background-color: blue;
        }
    </style>
</head>
<body>
    <div class="div1">

    </div>
</body>
</html>
```



### 通过 css 方式调用

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/blob/main/front-end/html+js+css/css-media-query/demo-using-media-query-with-css.html)

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <!--
        https://css-tricks.com/a-complete-guide-to-css-media-queries/
        根据不同的屏幕尺寸使用不同的css
    -->
    <style>
        .div1 {
            width: 100px;
            height: 100px;
        }

        @media only screen and (max-width: 600px) {
            .div1 {
                background-color: red;
            }
        }

        @media only screen and (min-width: 600px) and (max-width: 700px) {
            .div1 {
                background-color: yellow;
            }
        }

        @media only screen and (min-width: 700px) {
            .div1 {
                background-color: skyblue;
            }
        }
    </style>
</head>
<body>
    <div class="div1">

    </div>
</body>
</html>
```



### 通过 javascript 方式调用

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/blob/main/front-end/html+js+css/css-media-query/demo-using-media-query-with-javascript.html)

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <!--
        https://css-tricks.com/a-complete-guide-to-css-media-queries/
        通过javascript使用media query，当屏幕尺寸变化时候console.log并且判断是否小于500px
    -->
    <script>
        // 创建关注的 media query 表达式
        let mediaQuery = window.matchMedia("only screen and (min-width: 500px)")

        function handleScreenSizeChange(mediaQuery) {
            // 如果 media query 大于 500px
            if(mediaQuery.matches) {
                console.log("screen width greater than 500px")
            } else {
                console.log("screen width less than 500px")
            }
        }

        // 监听指定的 media query 变化事件
        mediaQuery.addListener(handleScreenSizeChange)

        // 页面初始化后调用一次change
        handleScreenSizeChange(mediaQuery)
    </script>
</head>
<body>
    
</body>
</html>
```



## 使用媒体查询判断系统当前主题

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/blob/main/front-end/html+js+css/css-media-query/demo-media-query-dark-mode.html)

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        .demo1>div {
            width: 200px;
            height: 100px;
            background-color: var(--background-color);
            color: var(--color);
        }

        :root {
            --background-color: yellowgreen;
            --color: blue;
        }

        /* 这是一个CSS媒体查询，用于检测用户是否将系统主题设置为深色模式（Dark Mode）*/
        @media (prefers-color-scheme: dark) {
            :root {
                --background-color: black;
                --color: white;
            }
        }
    </style>
</head>

<body>
    <!--
        https://stackoverflow.com/questions/50840168/how-to-detect-if-the-os-is-in-dark-mode-in-browsers
    -->
    <div>演示使用media query检测系统是否黑暗模式</div>
    <div class="demo1">
        <div>
            Hello world!
        </div>
    </div>
    <hr>
</body>

</html>
```

