// jQuery 等待 document 准备就绪
$(function() {
    $("#displayDiv").html("从远程加载 html 和 js 资源")

    // JS 调用 Android 接口按钮点击事件
    $("#buttonJsCallAndroidApi").click(function() {
        if(window.AndroidBridge) {
            var result = window.AndroidBridge.makeToast("JS调用Android接口触发的Toast弹窗")
            console.log("调用 window.AndroidBridge.makeToast() 返回值：" + result)
        }
    })

    // URL 加载拦截
    $("#buttonOverrideUrlLoading").click(function() {
        // 使用 document.location 触发 URL 加载
        document.location = "wx://myUrl?p1=v1&p2=v2"
    })
})
