package com.google.firebase;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.auth.http.HttpTransportFactory;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Random;
import java.util.UUID;

/**
 * 说明:
 * 1.当前服务运行环境需要打开vpn, 能够访问 google.com
 * 2.Android客户端也可以访问 google.com, 并且客户端手机支持FMC服务(非大陆使用的手机!)
 * 3.配置 FIREBASE_ADMINSDK_JSON_PATH, 指定xxx.json文件路径(json文件通过Firebase控制台创建并下载)
 * 4.设置环境变量 GOOGLE_APPLICATION_CREDENTIALS = "E:\gitlab\chat\google-fcm\src\main\resources\firebase-adminsdk-9wf3w.json"
 * 5.设置推送目标 appRegistrationToken (前端提供)
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FMCApplication.class})
public class NotificationTest {

    //Firebase控制台创建并下载 firebase-adminsdk-9wf3w.json 文件保存在本地
    final static String FIREBASE_ADMINSDK_JSON_PATH = "/home/dexterleslie/workspace-git/demonstration/google-fcm/src/main/resources/firebase-adminsdk-9wf3w.json";

    //设置推送目标: Android客户端初始化push时生成的，上传到后端，后端保存在数据库中
    private String appRegistrationToken = "dHM23T1BRkmdhFASl3Tqcd:APA91bEfhvCKcrAUe5sE-KNdFZSrlgvWS9nY3T5jEXE0KnHFb_Kcv4zROdGGGZbhbI0GkgnebjkoIAjhKkhcO9osJhJtWTI23WXrP-3h_mlbPbriRKLUUfR5RFlnvAnkOFRlab6DIZA6";
    private FirebaseApp firebaseApp;

    //初始化
    @PostConstruct
    public void init() {
        InputStream inputStream = null;
        try {
//			ClassPathResource cpr = new ClassPathResource("firebase-adminsdk-9wf3w.json");
//			InputStream inputStream = cpr.getInputStream();

            // 设置socks5代理
            // https://stackoverflow.com/questions/49174274/set-up-firebase-admin-sdk-with-proxy-java
            String address = "192.168.1.154";
            int port = 1080;
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(address, port));
            HttpTransport httpTransport = new NetHttpTransport.Builder().setProxy(proxy).build();
            HttpTransportFactory httpTransportFactory = () -> httpTransport;

            inputStream = new FileInputStream(FIREBASE_ADMINSDK_JSON_PATH);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(inputStream, httpTransportFactory))
                    .setHttpTransport(httpTransport)
                    .build();
            firebaseApp = FirebaseApp.initializeApp(options, UUID.randomUUID().toString()/* NOTE: 需要指定firebaseapp名称，否则在创建多个firebaseapp时会报告都使用default名称冲突 */);
        } catch (Exception e) {
            log.error("google FirebaseApp initializeApp fail = {}", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    //
                }
                inputStream = null;
            }
        }
    }

    // 销毁firebaseApp
    // https://stackoverflow.com/questions/39436072/cannot-destroy-firebase-connections-making-hot-lambda-fail-due-to-firebase-app
    @After
    public void destroy() {
        if (this.firebaseApp != null) {
            this.firebaseApp.delete();
            this.firebaseApp = null;
        }
    }

    /**
     * 透传消息Data Message
     * app在后台或被杀死, 也可以收到！
     */
    @Test
    public void test_DataMsg() throws Exception {
        //Map map = new HashMap();
        // 推送目标, 标题, 内容
        String uuidStr = UUID.randomUUID().toString();
        Random random = new Random();
        for (int i = 0; i < 1; i++) {
            this.send(appRegistrationToken, i + "-标题-" + uuidStr, i + "-内容-" + uuidStr);
            int randomInt = random.nextInt(500);
            if (randomInt > 0) {
                Thread.sleep(randomInt);
            }
        }
    }

    private void send(String token, String title, String body) throws Exception {
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                //使用高级别,休眠状态也能提醒!
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .build())
                .setToken(token)
                .build();
        String response = FirebaseMessaging.getInstance(firebaseApp).send(message);
        log.info("response=={}", response);
    }

    /**
     * 通知栏消息 Notification Message.
     * app在后台或被杀死不能收到! 代码仅供参考，不常用!
     * */
/*	@Test
	public void test_Notification() throws Exception {
		Message message = Message.builder()
				.setNotification(Notification.builder()
						.setTitle("notePush标题")
						.setBody("notePush内容")
						.build())
				.setToken(appRegistrationToken)
				.build();
		String response = FirebaseMessaging.getInstance(firebaseApp).send(message);
		log.info("response=={}",response);
	}*/
}
