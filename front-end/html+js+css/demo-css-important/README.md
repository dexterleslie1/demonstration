## css !important用法

### 参考

https://www.php.cn/faq/410145.html
demo-css-important演示



### 为何使用!important

在工作中经常会遇到因为选择器优先级导致的样式无法呈现，这个时候就要用到一个特殊的css属性，就是!important。!important使属性值有最高优先级，可以用它来设置想要的样式。

作用：提升指定样式规则的应用优先权，即!important提供了一个增加样式权重的方法，让浏览器首选执行这个语句。

使用方法：选择器 {样式：值 !important}。比如，在CSS中给了.box{color:red !important;}这个属性，就意味着类名为box的这个标签具有最高优先级，不管你把默认的颜色设置为什么，他最终只会显示红色。