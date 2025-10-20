## 概念

在SpringBoot应用中，验证码（CAPTCHA）是一种用于区分人类用户和机器程序的安全机制。以下是关于SpringBoot验证码的详细说明：

### 1. 验证码的作用

- **防止暴力破解**：保护登录、注册等敏感接口
- **防止恶意注册**：避免机器人批量注册账号
- **防止刷票/刷单**：确保操作由真实用户执行
- **提升安全性**：增加额外的安全层

### 2. 常见的验证码类型

#### 图形验证码

```
// 示例：生成图形验证码的基本原理
public class CaptchaUtil {
    public static BufferedImage generateImageCaptcha(String code) {
        // 创建图片对象
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
        // 绘制背景、干扰线、验证码文本
        g.drawString(randomCode, x, y);
        return image;
    }
}
```

#### 算术验证码

```
// 示例：生成算术表达式验证码
public class MathCaptcha {
    public static String generateMathExpression() {
        int a = random.nextInt(10);
        int b = random.nextInt(10);
        return a + " + " + b + " = ?"; // 如 "3 + 5 = ?"
    }
}
```

#### 滑动验证码

- 需要用户拖动滑块完成拼图
- 更友好的用户体验

#### 行为验证码

- 如点选文字、旋转图片等
- 基于用户交互行为判断

### 3. SpringBoot中实现验证码的步骤

#### 添加依赖

```
<dependency>
    <groupId>com.github.penggle</groupId>
    <artifactId>kaptcha</artifactId>
    <version>2.3.2</version>
</dependency>
```

#### 配置类

```
@Configuration
public class KaptchaConfig {
    
    @Bean
    public Producer kaptchaProducer() {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "100");
        properties.setProperty("kaptcha.image.height", "40");
        properties.setProperty("kaptcha.textproducer.font.size", "32");
        
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
```

#### 控制器示例

```
@RestController
public class CaptchaController {
    
    @Autowired
    private Producer kaptchaProducer;
    
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletResponse response, HttpSession session) throws IOException {
        // 生成验证码文本
        String capText = kaptchaProducer.createText();
        
        // 存储到session中（推荐使用Redis）
        session.setAttribute("captcha", capText);
        
        // 生成图片
        BufferedImage image = kaptchaProducer.createImage(capText);
        
        response.setContentType("image/png");
        ImageIO.write(image, "png", response.getOutputStream());
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username,
                                  @RequestParam String password,
                                  @RequestParam String captcha,
                                  HttpSession session) {
        // 验证验证码
        String sessionCaptcha = (String) session.getAttribute("captcha");
        if (!captcha.equalsIgnoreCase(sessionCaptcha)) {
            return ResponseEntity.badRequest().body("验证码错误");
        }
        
        // 清除已使用的验证码
        session.removeAttribute("captcha");
        
        // 继续登录逻辑...
        return ResponseEntity.ok("登录成功");
    }
}
```

### 4. 最佳实践建议

#### 安全性考虑

```
// 验证码有效期控制
@Component
public class CaptchaService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    private static final long CAPTCHA_EXPIRE_TIME = 5 * 60; // 5分钟
    
    public void saveCaptcha(String key, String code) {
        redisTemplate.opsForValue().set(
            "captcha:" + key, 
            code, 
            CAPTCHA_EXPIRE_TIME, 
            TimeUnit.SECONDS
        );
    }
    
    public boolean validateCaptcha(String key, String code) {
        String storedCode = redisTemplate.opsForValue().get("captcha:" + key);
        return code != null && code.equalsIgnoreCase(storedCode);
    }
}
```

#### 前端集成

```
<!-- HTML示例 -->
<div>
    <input type="text" id="captchaInput" placeholder="请输入验证码">
    <img id="captchaImage" src="/captcha" onclick="refreshCaptcha()">
    <button onclick="refreshCaptcha()">刷新</button>
</div>

<script>
function refreshCaptcha() {
    document.getElementById('captchaImage').src = '/captcha?t=' + new Date().getTime();
}
</script>
```

### 5. 高级功能

#### 验证码难度配置

```
// 可配置的验证码生成器
@Component
public class CustomCaptchaGenerator {
    
    public String generateText(int length, boolean includeLetters) {
        // 根据需求生成不同难度的验证码
    }
}
```

#### 频率限制

```
// 防止验证码被频繁请求
@RateLimiter(name = "captchaLimit", rate = 10) // 每秒10次
public ResponseEntity<?> getCaptcha() {
    // 生成验证码逻辑
}
```

SpringBoot中的验证码实现相对简单，但需要根据具体业务场景选择合适的安全策略。建议结合实际需求考虑验证码的类型、复杂度、有效期等参数配置。



## 主流开源的验证码方案

SpringBoot应用中主流开源的验证码方案非常丰富，以下是我整理的详细对比：

### 1. **Kaptcha** - 最经典的方案

#### 特点

- 历史悠久，使用广泛
- 配置简单，功能稳定
- 支持自定义验证码样式

#### 依赖配置

```
<dependency>
    <groupId>com.github.penggle</groupId>
    <artifactId>kaptcha</artifactId>
    <version>2.3.2</version>
</dependency>
```

#### 配置示例

```
@Configuration
public class KaptchaConfig {
    @Bean
    public DefaultKaptcha producer() {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.textproducer.font.color", "black");
        properties.setProperty("kaptcha.textproducer.char.space", "5");
        
        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
```

### 2. **AJ-Captcha** - 行为验证码新星

#### 特点

- 支持滑动拼图、文字点选等行为验证
- 用户体验好，安全性高
- 前后端分离支持完善

#### 依赖配置

```
<dependency>
    <groupId>com.anji-plus</groupId>
    <artifactId>captcha</artifactId>
    <version>1.3.0</version>
</dependency>
```

#### 使用示例

```
@RestController
@RequestMapping("/captcha")
public class AjCaptchaController {
    
    @Autowired
    private CaptchaService captchaService;
    
    // 获取验证码
    @PostMapping("/get")
    public CaptchaResponse getCaptcha(@RequestBody CaptchaRequest request) {
        return captchaService.get(request);
    }
    
    // 验证验证码
    @PostMapping("/check")
    public CaptchaResponse checkCaptcha(@RequestBody CaptchaRequest request) {
        return captchaService.check(request);
    }
}
```

### 3. **EasyCaptcha** - 简单易用

#### 特点

- API简洁，上手快速
- 支持多种验证码类型
- 轻量级，无复杂依赖

#### 依赖配置

```
<dependency>
    <groupId>com.github.whvcse</groupId>
    <artifactId>easy-captcha</artifactId>
    <version>1.6.2</version>
</dependency>
```

#### 使用示例

```
@RestController
public class EasyCaptchaController {
    
    @GetMapping("/captcha")
    public void captcha(HttpServletResponse response, HttpSession session) throws IOException {
        // 生成算术验证码
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 48);
        captcha.setLen(3);  // 几位数运算
        
        String result = captcha.text();  // 计算结果
        session.setAttribute("captcha", result);
        
        captcha.out(response.getOutputStream());
    }
}
```

### 4. **Hutool-Captcha** - 国产优秀工具集

#### 特点

- 基于Hutool工具集
- 功能丰富，性能优秀
- 中文文档完善

#### 依赖配置

```
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-captcha</artifactId>
    <version>5.8.22</version>
</dependency>
```

#### 使用示例

```
@Component
public class HutoolCaptchaService {
    
    public void generateCaptcha(HttpServletResponse response) {
        // 定义图形验证码的长、宽、验证码字符数、干扰元素个数
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(200, 100, 4, 20);
        
        // 输出到浏览器
        captcha.write(response.getOutputStream());
        
        // 获取验证码文字
        String code = captcha.getCode();
    }
}
```

### 5. **Google reCAPTCHA** - 企业级解决方案

#### 特点

- Google提供的企业级服务
- 支持v2复选框和v3无感验证
- 安全性极高，但有网络依赖

#### 使用方式

```
@Service
public class RecaptchaService {
    
    @Value("${recaptcha.secret-key}")
    private String secretKey;
    
    public boolean verify(String recaptchaResponse, String clientIp) {
        String url = "https://www.google.com/recaptcha/api/siteverify";
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", secretKey);
        params.add("response", recaptchaResponse);
        params.add("remoteip", clientIp);
        
        RestTemplate restTemplate = new RestTemplate();
        RecaptchaResponse response = restTemplate.postForObject(url, params, RecaptchaResponse.class);
        
        return response != null && response.isSuccess();
    }
}
```

### 6. **Cloudflare Turnstile** - 新兴替代方案

#### 特点

- Cloudflare提供的免费服务
- 用户体验优秀
- 无需显示"我不是机器人"复选框

#### 使用示例

```
@Service
public class TurnstileService {
    
    private static final String VERIFY_URL = "https://challenges.cloudflare.com/turnstile/v0/siteverify";
    
    public boolean verify(String token, String clientIp) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", "your-secret-key");
        params.add("response", token);
        params.add("remoteip", clientIp);
        
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        RestTemplate restTemplate = new RestTemplate();
        
        TurnstileResponse response = restTemplate.postForObject(VERIFY_URL, entity, TurnstileResponse.class);
        return response != null && response.isSuccess();
    }
}
```

### 7. 方案对比总结

| 方案                     | 类型       | 安全性 | 用户体验 | 部署难度 | 适用场景       |
| ------------------------ | ---------- | ------ | -------- | -------- | -------------- |
| **Kaptcha**              | 图形验证码 | 中等   | 一般     | 简单     | 传统Web应用    |
| **AJ-Captcha**           | 行为验证码 | 高     | 优秀     | 中等     | 高安全要求应用 |
| **EasyCaptcha**          | 图形验证码 | 中等   | 良好     | 简单     | 快速开发项目   |
| **Hutool-Captcha**       | 图形验证码 | 中等   | 良好     | 简单     | 已有Hutool项目 |
| **Google reCAPTCHA**     | 云服务     | 极高   | 优秀     | 简单     | 企业级应用     |
| **Cloudflare Turnstile** | 云服务     | 极高   | 优秀     | 简单     | 国际化项目     |

### 8. 选择建议

#### 根据业务场景选择：

- **内部管理系统**：EasyCaptcha 或 Hutool-Captcha
- **电商/金融应用**：AJ-Captcha 或 Google reCAPTCHA
- **国际化项目**：Cloudflare Turnstile
- **传统Web应用**：Kaptcha

#### 技术考量：

- **网络环境**：云服务方案需要外网访问
- **安全性要求**：金融级应用建议使用行为验证码
- **用户体验**：移动端优先考虑无感验证
- **维护成本**：自建方案需要自行维护，云服务更省心

根据你的具体需求选择合适的验证码方案，平衡安全性、用户体验和开发维护成本。



## `happy-captcha`

>提示：因为验证码服务器端信息默认保存到`session`上下文中，如果需求需要把验证码信息保存到`redis`中默认是不支持的，所以暂时不使用此方案生成验证码。

### `POM`依赖配置

```xml
<dependency>
    <groupId>com.ramostear</groupId>
    <artifactId>Happy-Captcha</artifactId>
    <version>1.0.1</version>
</dependency>
```

### 示例

>说明：详细用法请参考`Gitee`说明。
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/captcha/demo-happy-captcha)
>
>`Gitee`地址：https://gitee.com/ramostear/Happy-Captcha

```java
package com.future.demo;

import com.ramostear.captcha.HappyCaptcha;
import com.ramostear.captcha.support.CaptchaStyle;
import com.ramostear.captcha.support.CaptchaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/api/v1/captcha")
public class ApiController {
    /**
     * 数字、大小写字母随机组合
     *
     * @param request
     * @param response
     */
    @GetMapping("/default")
    public void captchaDefault(HttpServletRequest request, HttpServletResponse response) {
        HappyCaptcha.require(request, response).build().finish();
    }

    /**
     * 加、减、乘算数运算表达式
     *
     * @param request
     * @param response
     */
    @GetMapping("/arithmetic")
    public void captchaArithmetic(HttpServletRequest request, HttpServletResponse response) {
        HappyCaptcha.require(request, response).type(CaptchaType.ARITHMETIC).build().finish();
    }

    /**
     * 中文简体加、减、乘算数运算表达式描述
     *
     * @param request
     * @param response
     */
    @GetMapping("/arithmeticzh")
    public void captchaArithmeticzh(HttpServletRequest request, HttpServletResponse response) {
        HappyCaptcha.require(request, response).style(CaptchaStyle.ANIM).type(CaptchaType.ARITHMETIC_ZH).build().finish();
    }
}
```