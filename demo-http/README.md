# http、springboot http学习

## 调试环境准备

> 运行 demo-http/spring-boot-http demo 作为rest api调试环境

# http get、post、put、delete

### GET

> 获取服务器上的资源
>
> NOTE: 使用get方法请求资源时，无法使用multipart/form-data、application/x-www-form-urlencoded提交表单数据

#### 提交参数方式

**使用query param方式**

```shell
curl -X GET http://localhost:8080/api/v1/testGetSubmitParamByUrl?param1=v1
```

**使用application/json方式**

```shell
curl -X GET -d '{"param1":"v1"}' -H "Content-Type: application/json" http://localhost:8080/api/v1/testGetSubmitParamByJSON
```

### POST

> 在服务器上创建资源

#### 提交参数方式

**使用query param方式**

```shell
curl -X POST http://localhost:8080/api/v1/testPostSubmitParamByUrl1?param1=v1
```

**使用multipart/form-data方式**

```shell
curl -X POST -F "param1=v1" -H "Content-Type: multipart/form-data" http://localhost:8080/api/v1/testPostSubmitParamByMultipartFormData
```

**使用application/x-www-form-urlencoded方式**

```shell
curl -X POST -d "param1=v1" -H "Content-Type: application/x-www-form-urlencoded" http://localhost:8080/api/v1/testPostSubmitParamByFormUrlencoded1
```

**使用application/json方式**

```shell
curl -X POST -d '{"param1":"v1"}' -H "Content-Type: application/json" http://localhost:8080/api/v1/testPostSubmitParamByJSON
```

### PUT

> 更新服务器上的资源
>
> NOTE: 使用put方法请求资源时，无法使用multipart/form-data提交表单数据

#### 提交参数方式

**使用query param方式**

```shell
curl -X PUT http://localhost:8080/api/v1/testPutSubmitParamByUrl1?param1=v1
```

**使用application/x-www-form-urlencoded方式**

```shell
curl -X PUT -d "param1=v1" -H "Content-Type: application/x-www-form-urlencoded" http://localhost:8080/api/v1/testPutSubmitParamByFormUrlencoded1
```

**使用application/json方式**

```shell
curl -X PUT -d '{"param1":"v1"}' -H "Content-Type: application/json" http://localhost:8080/api/v1/testPutSubmitParamByJSON
```

### DELETE

> 删除服务器上的资源
>
> NOTE: 使用delete方法请求资源时，无法使用application/x-www-form-urlencoded、multipart/form-data提交表单数据

#### 提交参数方式

使用query param方式**

```shell
curl -X DELETE http://localhost:8080/api/v1/testDeleteSubmitParamByUrl1?param1=v1
```

**使用application/json方式**

```shell
curl -X DELETE -d '{"param1":"v1"}' -H "Content-Type: application/json" http://localhost:8080/api/v1/testDeleteSubmitParamByJSON
```

## spring mvc获取参数

### @RequestParam

> 所有请求方法通过query param提交的参数都能通过此注解获取
>
> 此注解能够获取multipart/form-data、application/x-www-form-urlencoded提交的参数
>
> NOTE：此注解不能获取GET、DELETE通过application/x-www-form-urlencoded提交的参数，因为这两个http方法不能通过application/x-www-form-urlencoded提交参数

### @RequestBody

> 所有请求方法通过content-type: application/json提交参数都能通过此注解获取

### 参数绑定(parameter binding)

> 实质是pojo属性注入，具体用法参考 spring-boot-http demo
>
> 所有方法通过query param提交的参数都能通过此方法获取

https://www.logicbig.com/tutorials/spring-framework/spring-web-mvc/spring-request-param-data-binding.html