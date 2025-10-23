package com.future.demo;

import com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1")
public class ApiController {

    /**
     * 后端服务调用阿里云接口校验验证码验签参数是否合法
     *
     * @param sceneId
     * @param captchaVerifyParam
     * @throws Exception
     */

    @GetMapping("captchaVerifyParam")
    public ResponseEntity<VerifyIntelligentCaptchaResponse> captchaVerifyParam(@RequestParam("sceneId") String sceneId,
                                                                               @RequestParam("captchaVerifyParam") String captchaVerifyParam) throws Exception {
        com.aliyun.captcha20230305.Client client = createClient();
        com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaRequest verifyIntelligentCaptchaRequest =
                new com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaRequest()
                        .setCaptchaVerifyParam(captchaVerifyParam)
                        .setSceneId(sceneId);
        // Copy the code to run, please print the return value of the API by yourself.
        return ResponseEntity.ok(client.verifyIntelligentCaptchaWithOptions(verifyIntelligentCaptchaRequest, new com.aliyun.teautil.models.RuntimeOptions()));
    }

    /**
     * <b>description</b> :
     * <p>Initialize the Client with the credentials</p>
     *
     * @return Client
     * @throws Exception
     */
    public static com.aliyun.captcha20230305.Client createClient() throws Exception {
        // It is recommended to use the default credential. For more credentials, please refer to: https://www.alibabacloud.com/help/en/alibaba-cloud-sdk-262060/latest/credentials-settings-2.
        com.aliyun.credentials.Client credential = new com.aliyun.credentials.Client();
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setCredential(credential);
        // See https://api.alibabacloud.com/product/captcha.
        config.endpoint = "captcha.cn-shanghai.aliyuncs.com";
        return new com.aliyun.captcha20230305.Client(config);
    }
}
