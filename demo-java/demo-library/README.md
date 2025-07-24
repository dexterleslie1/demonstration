## `Feign`

### 基本用法

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-java/demo-library/demo-openfeign)

`POM` 配置：

```xml
<feign.version>10.10.1</feign.version>

<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-core</artifactId>
    <version>${feign.version}</version>
</dependency>
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-jackson</artifactId>
    <version>${feign.version}</version>
</dependency>
<dependency>
    <groupId>io.github.openfeign.form</groupId>
    <artifactId>feign-form</artifactId>
    <version>3.8.0</version>
</dependency>
```

定义接口：

```java
public interface Api {
    @RequestLine("GET /api/v1/test1?name={name}")
    ObjectResponse<MyVO> test1(@Param("name") String name) throws BusinessException;

    @RequestLine("POST /api/v1/testPost")
    // 因为body提交json参数，所以需要指定请求头Content-Type: application/json
    @Headers(value = {"Content-Type: application/json"})
    ObjectResponse<String> testPost(List<MyPostVO> myPostVOList) throws BusinessException;

    @RequestLine("POST /api/v1/postWwwFormUrlencoded")
    @Headers(value = {"Content-Type: application/x-www-form-urlencoded"})
    ObjectResponse<String> postWwwFormUrlencoded(@Param("parameter1") String parameter1) throws BusinessException;

    @RequestLine("PUT {uri}")
    // 因为body提交json参数，所以需要指定请求头Content-Type: application/json
    @Headers(value = {"Content-Type: application/json"})
    ObjectResponse<String> testPut(@Param("uri") String uri,
                                   List<MyPostVO> myPostVOList) throws BusinessException;

    @RequestLine("GET /api/v1/testHeaderWithToken")
    ObjectResponse<String> testHeaderWithToken() throws BusinessException;

    /**
     * Using @Headers with dynamic values in Feign client + Spring Cloud (Brixton RC2)
     * https://stackoverflow.com/questions/37066331/using-headers-with-dynamic-values-in-feign-client-spring-cloud-brixton-rc2
     *
     * @param dynamicToken
     * @return
     */
    @Headers("dynamicToken: {dynamicToken}")
    @RequestLine("GET /api/v1/testHeaderWithDynamicToken")
    ObjectResponse<String> testHeaderWithDynamicToken(@Param("dynamicToken") String dynamicToken);

    @RequestLine("GET /api/v1/testResponseWithHttpStatus400")
    ObjectResponse<String> testResponseWithHttpStatus400() throws BusinessException;

    /**
     * spring + Open Feign upload file 文件上传
     * https://blog.csdn.net/wtopps/article/details/78191953
     *
     * @param file
     * @return
     */
    @RequestLine("POST /api/v1/upload")
    @Headers("Content-Type: multipart/form-data")
    ObjectResponse<String> upload(@Param("file") File file);

    /**
     * feign for downloading file
     * https://stackoverflow.com/questions/59765206/feign-for-downloading-file
     * @param filename
     * @return
     */
    @RequestLine("GET /api/v1/download/{filename}")
    Response download(@Param("filename") String filename);

    @RequestLine("DELETE /api/v1/delete?param1={param1}")
    ObjectResponse<String> delete(@Param("param1") String param1) throws BusinessException;
}
```

创建接口实例：

```java
Api api = Feign.builder()
        // https://stackoverflow.com/questions/56987701/feign-client-retry-on-exception
        .retryer(Retryer.NEVER_RETRY)
        // https://qsli.github.io/2020/04/28/feign-method-timeout/
        .options(new Request.Options(15, TimeUnit.SECONDS, 15, TimeUnit.SECONDS, false))
        .encoder(new FormEncoder(new JacksonEncoder()))
        .decoder(new JacksonDecoder())
        // feign logger
        // https://cloud.tencent.com/developer/article/1588501
        .logger(new Logger.ErrorLogger()).logLevel(Logger.Level.FULL)
        // ErrorDecoder
        // https://cloud.tencent.com/developer/article/1588501
        .errorDecoder(new CustomizeErrorDecoder())
        .requestInterceptor(new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                template.header("token", token);
            }
        })
        .target(Api.class, "http://" + host + ":" + port);
```



### `HTTP 200` 时业务异常处理

>提示：为了解决 `http 200` 响应时有业务异常发生的情况，因为如果直接在 `Feign Decoder` 中抛出 `BusinessException`，会被包裹一层 `FeignException` 异常导致 `try catch` 代码更加臃肿。

公共工具类用于检查响应是否有业务异常，是则抛出 `BusinessException`：

```java
/**
 * OpenFeign 的工具类
 */
public class FeignUtil {

    /**
     * 如果响应失败则抛出 BusinessException 异常
     *
     * 提示：为了解决 http 200 响应时有业务异常发生的情况，
     * 因为如果直接在 Feign Decoder 中抛出 BusinessException，
     * 会被包裹一层 FeignException 异常导致 try catch 代码更加臃肿
     *
     * @param response
     */
    public static void throwBizExceptionIfResponseFailed(BaseResponse response) throws BusinessException {
        if (response != null) {
            if (response.getErrorCode() > 0) {
                throw new BusinessException(
                        response.getErrorCode(),
                        response.getErrorMessage()
                );
            }
        }
    }
}
```

调用工具类方法：

```java
List<MyPostVO> myPostVOList = new ArrayList<>();
try {
    ObjectResponse<String> response1 = api.testPost(myPostVOList);
    FeignUtil.throwBizExceptionIfResponseFailed(response1);
    Assert.fail("预期异常没有抛出");
} catch (BusinessException ex) {
    Assert.assertTrue(ex.getErrorCode() > 0);
    Assert.assertEquals("没有指定myPostVOList", ex.getErrorMessage());
}
```

