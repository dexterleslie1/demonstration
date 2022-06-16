<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <script type="text/javascript" src="js/jquery.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            $("#btn1").click(function() {
                $.post("/api/v1/biz/test.do",function(data, status) {
                    console.log(data)
                    data = JSON.parse(data)
                    if(data.errorCode > 0) {
                        window.location.href = data.dataObject.location
                    } else {
                        alert(data.dataObject)
                    }
                })
            })

            $("#btn2").click(function() {
                $.post("/api/v1/biz/removeWhitelist.do",function(data, status) {
                    data = JSON.parse(data)
                    if(data.errorCode > 0) {
                        window.location.href = data.dataObject.location
                    } else {
                        alert(data.dataObject)
                    }
                })
            })
        })
    </script>
<body>
    <div>
        <input id="btn1" type="button" value="模拟调用接口被引导到验证界面"/>
        <input id="btn2" type="button" value="重新进行机器人验证"/>
    </div>
</body>
</html>