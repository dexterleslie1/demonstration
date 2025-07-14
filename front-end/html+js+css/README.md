## 响应式web设计

> https://www.w3schools.com/html/html_responsive.asp



### 设置viewport meta

> 参考 demo-responsive-design/demo-setting-the-viewport



### 响应式图片

> 参考 demo-responsive-design/demo-responsive-image



### 响应式文本

> 参考 demo-responsive-design/demo-responsive-text



### 根据浏览器宽度显示不同的图片

> 参考 demo-responsive-design/demo-show-different-image-depending-on-browser-width



### 自适应和响应式区别

> https://answer.baidu.com/answer/land?params=2pYqV2dFvx1ITq44PzikNEKfsp%2B5%2BHhj%2FesMN9eVgdr5pUNi0t1d87a4dqi4uJGdIGQMev5escaVyMS3IiMTD%2Biv347YglgAXMZx5kK3fb%2FKTvd3r%2BvHBcIgXHL3TJt7qwipUX6VzeB8RMsGVTssnMG0jisEf%2BYsEP1Zo3Kh83%2BgCwExVW86e6BL45uww4v9&from=dqa&lid=c309cf4e00036f62&word=%E8%87%AA%E9%80%82%E5%BA%94%E5%92%8C%E5%93%8D%E5%BA%94%E5%BC%8F%E5%8C%BA%E5%88%AB
>
> 自适应设计通常涉及为不同设备的不同屏幕尺寸设计多个不同的布局。这包括手机端、平板端和电脑端的布局。
>
> 响应式设计则是创建一套页面，并通过媒体查询（media queries）和可能的JavaScript和CSS控制，根据用户的设备和屏幕尺寸动态地调整布局和内容。



## `localStorage` 和 `sessionStorage`



### 区别

- 在使用 `sessionStorage` 方法时，如果关闭了浏览器，这个数据就丢失了，下一次打开浏览器单击"读取数据"按钮时，读取不到任何数据。在使用`localStorage` 方法时，即使浏览器关闭了，下次打开浏览器时仍然能够读取保存的数据。不过，数据保存是按不同的浏览器分别进行保存的，也就是说，打开别的浏览器是读取不到在这个浏览器中保存的数据的。
- 同一个浏览器中，`sessionStorage` 在不同的 `TAB` 页面保存数据是分开互相不影响的，`localStorage` 在不同 `TAB` 页面保存数据时共享的相互影响的。



### 用法

>[`sessionStorage`、`localStorage` 使用](https://www.cnblogs.com/pengc/p/8714475.html)
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/html+js+css/demo-h5-storage)

```javascript
window.sessionStorageOnSave = function() {
    let value = document.getElementById("sessionStorageValue").value;
    sessionStorage.setItem("sessionStorageKey", value);
}

window.sessionStorageOnRead = function() {
    let value = sessionStorage.getItem("sessionStorageKey");
    alert("sessionStorage值为：" + value);
}

window.localStorageOnSave = function() {
    let value = document.getElementById("localStorageValue").value;
    localStorage.setItem("localStorageKey", value);
}

window.localStorageOnRead = function() {
    let value = localStorage.getItem("localStorageKey");
    alert("localStorage值为：" + value);
}
```











