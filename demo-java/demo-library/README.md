## `HTTP`库 - 概念

Java 生态系统中有多个优秀的 HTTP 客户端库，每个都有其特点和适用场景。以下是主要的 Java HTTP 库及其详细使用指南。

### 一、主流 HTTP 库概览

| 库名称                    | 特点                   | 适用场景             | 依赖坐标 |
| ------------------------- | ---------------------- | -------------------- | -------- |
| **HttpURLConnection**     | JDK 内置，无需依赖     | 简单请求，JDK 环境   | 内置     |
| **HttpClient (Java 11+)** | JDK 11+ 内置，现代 API | 新项目，Java 11+     | 内置     |
| **Apache HttpClient**     | 功能全面，稳定可靠     | 企业级应用，复杂需求 | 见下文   |
| **OkHttp**                | 性能优秀，Square 出品  | 移动端，高性能需求   | 见下文   |
| **Retrofit**              | 声明式 REST 客户端     | RESTful API 调用     | 见下文   |
| **Spring RestTemplate**   | Spring 生态集成        | Spring 项目          | 见下文   |
| **WebClient**             | Spring 5 响应式客户端  | 响应式编程           | 见下文   |

### 二、HttpURLConnection (JDK 内置)

#### 基本使用示例

```java
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpURLConnectionExample {
    
    public static String get(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        try {
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    return response.toString();
                }
            } else {
                throw new IOException("HTTP error: " + responseCode);
            }
        } finally {
            conn.disconnect();
        }
    }
    
    public static String post(String urlStr, String jsonBody) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        try {
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            
            // 发送请求体
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    return response.toString();
                }
            } else {
                throw new IOException("HTTP error: " + responseCode);
            }
        } finally {
            conn.disconnect();
        }
    }
}
```

### 三、Java 11 HttpClient (推荐用于新项目)

#### Maven 依赖
```xml
<!-- Java 11+ 内置，无需额外依赖 -->
```

#### 使用示例

```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class JavaHttpClientExample {
    
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    
    // 同步 GET 请求
    public static String syncGet(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Java 11 HttpClient")
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(
            request, HttpResponse.BodyHandlers.ofString());
        
        return response.body();
    }
    
    // 异步 GET 请求
    public static CompletableFuture<String> asyncGet(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }
    
    // POST 请求（JSON）
    public static String postJson(String url, String json) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        
        HttpResponse<String> response = httpClient.send(
            request, HttpResponse.BodyHandlers.ofString());
        
        return response.body();
    }
    
    // 带认证的请求
    public static String getWithAuth(String url, String token) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(
            request, HttpResponse.BodyHandlers.ofString());
        
        return response.body();
    }
}
```

### 四、Apache HttpClient

#### Maven 依赖
```xml
<dependency>
    <groupId>org.apache.httpcomponents.client5</groupId>
    <artifactId>httpclient5</artifactId>
    <version>5.2.1</version>
</dependency>

<!-- 或使用经典版本 -->
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.5.14</version>
</dependency>
```

#### 使用示例（HttpClient 5）

```java
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;

public class ApacheHttpClientExample {
    
    public static String get(String url) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("User-Agent", "Apache HttpClient");
            
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                return EntityUtils.toString(response.getEntity());
            }
        }
    }
    
    public static String postJson(String url, String json) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            
            // 设置 JSON 请求体
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                return EntityUtils.toString(response.getEntity());
            }
        }
    }
    
    // 使用连接池的高级配置
    public static CloseableHttpClient createPoolingHttpClient() {
        return HttpClients.custom()
                .setMaxConnTotal(100)        // 最大连接数
                .setMaxConnPerRoute(20)       // 每个路由最大连接数
                .build();
    }
}
```

### 五、OkHttp

#### Maven 依赖
```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.10.0</version>
</dependency>
```

#### 使用示例

```java
import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OkHttpExample {
    
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    
    // 同步 GET 请求
    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "OkHttp Client")
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code: " + response);
            }
            return response.body().string();
        }
    }
    
    // 异步 GET 请求
    public static void asyncGet(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        
        client.newCall(request).enqueue(callback);
    }
    
    // POST 请求（JSON）
    public static String postJson(String url, String json) throws IOException {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, JSON);
        
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
    
    // 文件上传
    public static String uploadFile(String url, String filePath) throws IOException {
        MediaType MEDIA_TYPE_PNG = MediaType.get("image/png");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "image.png",
                    RequestBody.create(new java.io.File(filePath), MEDIA_TYPE_PNG))
                .build();
        
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
```

### 六、Retrofit (声明式 REST 客户端)

#### Maven 依赖
```xml
<dependency>
    <groupId>com.squareup.retrofit2</groupId>
    <artifactId>retrofit</artifactId>
    <version>2.9.0</version>
</dependency>
<dependency>
    <groupId>com.squareup.retrofit2</groupId>
    <artifactId>converter-gson</artifactId>
    <version>2.9.0</version>
</dependency>
```

#### 使用示例

```java
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;
import java.io.IOException;
import java.util.List;

// 定义数据模型
class User {
    private Long id;
    private String name;
    private String email;
    
    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

// 定义 API 接口
interface UserService {
    @GET("users")
    Call<List<User>> getUsers();
    
    @GET("users/{id}")
    Call<User> getUserById(@Path("id") Long id);
    
    @POST("users")
    Call<User> createUser(@Body User user);
    
    @PUT("users/{id}")
    Call<User> updateUser(@Path("id") Long id, @Body User user);
    
    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") Long id);
}

public class RetrofitExample {
    
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";
    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    
    private static final UserService userService = retrofit.create(UserService.class);
    
    public static List<User> getUsers() throws IOException {
        Call<List<User>> call = userService.getUsers();
        Response<List<User>> response = call.execute();
        return response.body();
    }
    
    public static User createUser(User user) throws IOException {
        Call<User> call = userService.createUser(user);
        Response<User> response = call.execute();
        return response.body();
    }
    
    // 异步调用示例
    public static void getUsersAsync() {
        userService.getUsers().enqueue(new retrofit2.Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    List<User> users = response.body();
                    System.out.println("Users: " + users);
                }
            }
            
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
```

### 七、Spring RestTemplate

#### Maven 依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.7.0</version>
</dependency>
```

#### 使用示例

```java
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RestTemplateExample {
    
    private static final RestTemplate restTemplate = new RestTemplate();
    
    // GET 请求
    public static String get(String url) {
        return restTemplate.getForObject(url, String.class);
    }
    
    // GET 请求（带参数）
    public static String getWithParams(String baseUrl, Map<String, String> params) {
        return restTemplate.getForObject(baseUrl + "?name={name}&age={age}", 
            String.class, params);
    }
    
    // POST 请求（JSON）
    public static String postJson(String url, String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.exchange(
            url, HttpMethod.POST, entity, String.class);
        
        return response.getBody();
    }
    
    // POST 请求（表单）
    public static String postForm(String url, Map<String, String> formData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        formData.forEach(map::add);
        
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.exchange(
            url, HttpMethod.POST, entity, String.class);
        
        return response.getBody();
    }
    
    // 带认证的请求
    public static String getWithAuth(String url, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
            url, HttpMethod.GET, entity, String.class);
        
        return response.getBody();
    }
}
```

### 八、Spring WebClient (响应式)

#### Maven 依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
    <version>2.7.0</version>
</dependency>
```

#### 使用示例

```java
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class WebClientExample {
    
    private static final WebClient webClient = WebClient.builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .defaultHeader("User-Agent", "Spring WebClient")
            .build();
    
    // GET 请求
    public static Mono<String> get(String uri) {
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class);
    }
    
    // POST 请求（JSON）
    public static Mono<String> postJson(String uri, String json) {
        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .retrieve()
                .bodyToMono(String.class);
    }
    
    // 带错误处理
    public static Mono<String> getWithErrorHandling(String uri) {
        return webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(status -> status.isError(), 
                    response -> Mono.error(new RuntimeException("HTTP error: " + response.statusCode())))
                .bodyToMono(String.class);
    }
    
    // 流式处理
    public static void streamData(String uri) {
        webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToFlux(String.class)
                .subscribe(System.out::println);
    }
}
```

### 九、综合比较和选择建议

#### 性能比较（大致顺序）
1. **OkHttp** - 性能最优，连接池管理优秀
2. **Java 11 HttpClient** - 现代，支持 HTTP/2
3. **Apache HttpClient** - 稳定，功能全面
4. **Spring WebClient** - 响应式，非阻塞
5. **Retrofit** - 声明式，开发效率高
6. **RestTemplate** - Spring 生态集成
7. **HttpURLConnection** - 基础，无依赖

#### 选择建议

**简单项目/学习用途**：

- HttpURLConnection（无依赖）
- Java 11 HttpClient（如果使用 Java 11+）

**企业级应用**：
- Apache HttpClient（功能全面，稳定）
- OkHttp（高性能需求）

**RESTful API 调用**：
- Retrofit（声明式，代码简洁）
- Spring RestTemplate/WebClient（Spring 项目）

**响应式编程**：
- Spring WebClient
- Java 11 HttpClient（异步支持）

### 十、最佳实践和常见问题

#### 1. 连接池配置

```java
// OkHttp 连接池配置
OkHttpClient client = new OkHttpClient.Builder()
    .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
    .build();

// Apache HttpClient 连接池
PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
cm.setMaxTotal(100);
cm.setDefaultMaxPerRoute(20);
```

#### 2. 超时设置

```java
// 统一超时配置
public class HttpConfig {
    public static final int CONNECT_TIMEOUT = 10000;  // 10秒
    public static final int READ_TIMEOUT = 30000;     // 30秒
    public static final int WRITE_TIMEOUT = 30000;    // 30秒
}
```

#### 3. 重试机制

```java
// OkHttp 重试拦截器
public class RetryInterceptor implements Interceptor {
    private int maxRetries;
    
    public RetryInterceptor(int maxRetries) {
        this.maxRetries = maxRetries;
    }
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = null;
        IOException exception = null;
        
        for (int i = 0; i <= maxRetries; i++) {
            try {
                response = chain.proceed(request);
                if (response.isSuccessful()) {
                    return response;
                }
            } catch (IOException e) {
                exception = e;
            }
            
            if (i < maxRetries) {
                try {
                    Thread.sleep(1000 * i); // 指数退避
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Interrupted", ie);
                }
            }
        }
        
        throw exception != null ? exception : new IOException("Max retries exceeded");
    }
}
```

#### 4. 日志记录

```java
// OkHttp 日志拦截器
public class LoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long startTime = System.nanoTime();
        
        System.out.println(String.format("Sending request %s on %s%n%s",
            request.url(), chain.connection(), request.headers()));
        
        Response response = chain.proceed(request);
        
        long endTime = System.nanoTime();
        System.out.println(String.format("Received response for %s in %.1fms%n%s",
            response.request().url(), (endTime - startTime) / 1e6d, response.headers()));
        
        return response;
    }
}
```

根据您的具体需求选择合适的 HTTP 库，考虑因素包括：项目复杂度、性能要求、团队熟悉度、现有技术栈等。

## `HTTP`库 - `Feign`

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



引用 `future-common` 中的 `FeignUtil`

- `POM` 配置

  ```xml
  <dependency>
      <groupId>com.github.dexterleslie1</groupId>
      <artifactId>future-common</artifactId>
      <version>1.2.3</version>
  </dependency>
  
  <repositories>
      <repository>
          <id>aliyun-maven</id>
          <url>https://maven.aliyun.com/repository/public</url>
      </repository>
      <repository>
          <id>jitpack.io</id>
          <url>https://jitpack.io</url>
      </repository>
  </repositories>
  ```

- `FeignUtil.throwBizExceptionIfResponseFailed();` 使用

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

  

## `HTTP`库 - `RestTemplate`

`RestTemplate` 是 Spring Framework 提供的**同步 HTTP 客户端工具**，用于简化与 RESTful Web 服务的交互。它是 Spring Web 模块的核心组件之一，特别适合在传统 Spring MVC 应用中调用外部 API。

### 核心特性

1. **同步阻塞模型**：
   
   - 发送请求后线程会阻塞，直到收到响应
   - 适用于传统服务端应用（非响应式架构）
   
2. **丰富的 HTTP 方法支持**：
   ```java
   restTemplate.getForObject(...)       // GET
   restTemplate.postForEntity(...)      // POST
   restTemplate.put(...)                // PUT
   restTemplate.delete(...)             // DELETE
   restTemplate.exchange(...)            // 通用方法（支持所有HTTP方法）
   ```

3. **自动类型转换**：
   - 通过 `HttpMessageConverter` 自动序列化/反序列化
   - 支持 JSON、XML、表单数据等多种格式

4. **URI 模板支持**：
   ```java
   String result = restTemplate.getForObject(
       "http://api.example.com/users/{id}", 
       String.class, 
       123  // 替换 {id} 占位符
   );
   ```

5. **错误处理机制**：
   - 可自定义 `ResponseErrorHandler` 处理非 2xx 响应

### 基本用法示例

#### 1. GET 请求（简单对象）
```java
User user = restTemplate.getForObject(
    "http://api.example.com/users/1", 
    User.class
);
```

#### 2. POST 请求（带请求体）
```java
HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.APPLICATION_JSON);

HttpEntity<User> request = new HttpEntity<>(newUser, headers);

ResponseEntity<User> response = restTemplate.postForEntity(
    "http://api.example.com/users",
    request,
    User.class
);
```

#### 3. 使用 exchange 方法（处理复杂泛型）
```java
ResponseEntity<ObjectResponse<List<User>>> response = restTemplate.exchange(
    "http://api.example.com/users",
    HttpMethod.GET,
    null,
    new ParameterizedTypeReference<ObjectResponse<List<User>>>() {}
);
```

### 配置说明

#### 基础配置
```java
RestTemplate restTemplate = new RestTemplate();

// 配置超时
SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
factory.setConnectTimeout(5000);
factory.setReadTimeout(10000);
restTemplate.setRequestFactory(factory);

// 添加消息转换器
restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

// 设置错误处理器
restTemplate.setErrorHandler(new CustomErrorHandler());
```

#### 高级配置（使用 RestTemplateBuilder）
```java
RestTemplate restTemplate = new RestTemplateBuilder()
    .rootUri("https://api.example.com")
    .defaultHeader("Authorization", "Bearer token")
    .setConnectTimeout(Duration.ofSeconds(5))
    .additionalMessageConverters(new JacksonConverter())
    .build();
```

### 与现代替代品的比较

| 特性         | RestTemplate | WebClient (Spring 5+) |
| ------------ | ------------ | --------------------- |
| **编程模型** | 同步阻塞     | 异步非阻塞            |
| **并发支持** | 线程阻塞     | 基于 Reactor 的响应式 |
| **性能**     | 中等         | 高（资源利用率更优）  |
| **依赖**     | spring-web   | spring-webflux        |
| **适用场景** | 传统应用     | 微服务/云原生应用     |

### 最佳实践建议

1. **单例使用**：
   
   - `RestTemplate` 是线程安全的，推荐作为单例使用
   
2. **异常处理**：
   ```java
   try {
       restTemplate.exchange(...);
   } catch (HttpClientErrorException e) {
       // 处理 4xx 错误
   } catch (HttpServerErrorException e) {
       // 处理 5xx 错误
   }
   ```

3. **连接池配置**：
   ```java
   PoolingHttpClientConnectionManager connectionManager = 
       new PoolingHttpClientConnectionManager();
   connectionManager.setMaxTotal(100);
   connectionManager.setDefaultMaxPerRoute(20);
   
   HttpClient httpClient = HttpClientBuilder.create()
       .setConnectionManager(connectionManager)
       .build();
   
   HttpComponentsClientHttpRequestFactory factory = 
       new HttpComponentsClientHttpRequestFactory(httpClient);
   
   RestTemplate restTemplate = new RestTemplate(factory);
   ```

4. **迁移建议**：
   - 新项目推荐使用 `WebClient`
   - 现有项目可继续使用 `RestTemplate`，但建议逐步迁移

> **注意**：虽然 Spring 官方已宣布 `RestTemplate` 进入维护模式（不再添加新特性），但它仍然是数百万现有应用的核心组件，短期内不会被移除。

### 示例

>说明：`TestRestTemplate` 的用法和 `RestTemplate` 支持 `cookies` 特性用法。
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-spring-boot/demo-spring-boot-resttemplate)



### 示例 - 提交`Query`参数

```java
/**
 * 测试提交 query 参数
 */
@Test
public void testPostWithQueryParameters() {
    String name = "Dexter1";
    // 方法1
    MultiValueMap<String, String> multiValueParams = new LinkedMultiValueMap<>();
    multiValueParams.add("name", name);
    HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(multiValueParams, null);
    ResponseEntity<String> responseEntity =
            this.restTemplate.exchange(this.getBasePath() + "/api/v1/postWithQueryParams",
                    HttpMethod.POST, httpEntity, String.class);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    String response = responseEntity.getBody();
    String message = "你提交的参数 name=" + name;
    Assertions.assertEquals(message, response);

    // 方法2
    String url = UriComponentsBuilder.fromUriString(this.getBasePath() + "/api/v1/postWithQueryParams")
            .queryParam("name", name)
            .build()
            .toUriString();
    responseEntity =
            this.restTemplate.exchange(url,
                    HttpMethod.POST, null, String.class);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    response = responseEntity.getBody();
    message = "你提交的参数 name=" + name;
    Assertions.assertEquals(message, response);

    // 方法3
    Map<String, String> params = new HashMap<>();
    params.put("name", name);
    responseEntity =
            this.restTemplate.exchange(this.getBasePath() + "/api/v1/postWithQueryParams?name={name}",
                    HttpMethod.POST, null, String.class, params);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    response = responseEntity.getBody();
    message = "你提交的参数 name=" + name;
    Assertions.assertEquals(message, response);
}
```

```java
/**
 * 用于协助测试提交 query 参数
 *
 * @param name
 * @return
 */
@PostMapping("postWithQueryParams")
ResponseEntity<String> postWithQueryParams(@RequestParam(value = "name") String name) {
    String message = "你提交的参数 name=" + name;
    return ResponseEntity.ok(message);
}
```



## `HTTP`库 - `RestTemplate`和`TestRestTemplate`区别

`RestTemplate` 和 `TestRestTemplate` 都是 Spring 框架提供的 HTTP 客户端工具，但它们在设计目的和使用场景上有显著区别：

---

### **1. RestTemplate**
**定位**：生产环境使用的通用 HTTP 客户端  
**包路径**：`org.springframework.web.client.RestTemplate`  
**核心特点**：
- **同步阻塞式**：发送请求后会阻塞线程直到收到响应
- **功能完整**：支持所有 HTTP 方法（GET/POST/PUT/DELETE 等）、请求头定制、异常处理
- **配置灵活**：可自定义消息转换器、错误处理器、拦截器等
- **无自动上下文**：需要手动指定完整的 URL 路径

**典型用法**：
```java
// 生产代码中调用外部 API
RestTemplate rt = new RestTemplate();
String url = "https://api.example.com/data";
ResponseEntity<User> response = rt.getForEntity(url, User.class);
```

---

### **2. TestRestTemplate**
**定位**：专为**集成测试**设计的增强客户端  
**包路径**：`org.springframework.boot.test.web.client.TestRestTemplate`  
**核心特点**：
- **测试专用**：主要配合 `@SpringBootTest` 进行集成测试
- **自动解析上下文**：
  - 自动绑定测试服务器的端口（如 `localhost:8080`）
  - 支持使用相对路径（如 `"/api/users"`）
- **内置认证支持**：
  ```java
  testRestTemplate.withBasicAuth("user", "password")
  ```
- **简化断言**：返回的 `ResponseEntity` 可直接用于测试断言
- **自动处理重定向**：默认跟随重定向（测试常用行为）

**典型用法**：
```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
class UserControllerTest {
    
    @Autowired
    TestRestTemplate testRestTemplate; // 自动注入

    @Test
    void testGetUser() {
        // 使用相对路径（自动补全base URL）
        ResponseEntity<User> response = 
            testRestTemplate.getForEntity("/users/1", User.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("John");
    }
}
```

---

### **关键区别对比**
| 特性           | RestTemplate   | TestRestTemplate            |
| -------------- | -------------- | --------------------------- |
| **使用场景**   | 生产代码       | 集成测试                    |
| **路径处理**   | 需完整 URL     | 支持相对路径                |
| **服务器绑定** | 手动指定       | 自动绑定测试服务器          |
| **认证支持**   | 手动配置       | `.withBasicAuth()` 快捷方法 |
| **重定向处理** | 默认不跟随     | **默认自动跟随**            |
| **依赖关系**   | spring-web     | spring-boot-test            |
| **线程安全**   | 是（推荐单例） | 是（通常自动注入）          |

---

### **何时使用？**
- 用 `RestTemplate`：
  - 业务代码中调用第三方 API
  - 需要精细控制 HTTP 行为（如自定义超时、拦截器）

- 用 `TestRestTemplate`：
  - 测试 `@SpringBootTest` 启动的完整应用
  - 需要测试认证/重定向等场景
  - 避免硬编码测试服务器的地址和端口

> **注意**：Spring 5 后官方推荐使用 `WebClient` 替代 `RestTemplate`，但 `TestRestTemplate` 在集成测试中仍是首选工具。
