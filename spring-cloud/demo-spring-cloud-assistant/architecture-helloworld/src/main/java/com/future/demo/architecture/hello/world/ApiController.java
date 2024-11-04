package com.future.demo.architecture.hello.world;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

@RestController
public class ApiController {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Resource
    SqlSessionFactory sqlSessionFactory;

    @GetMapping(value = "/api/v1/test1", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> test1(@RequestParam(value = "hostnameGateway") String hostnameGateway) throws UnknownHostException {
        for (int i = 0; i < 100; i++) {
            String uuidString = UUID.randomUUID().toString();
            this.redisTemplate.opsForValue().set(uuidString, uuidString);
        }

        // 判断redis缓存中的数据是否都存在
        boolean consistent = true;
        for (int i = 1; i <= 15; i++) {
            String value = this.redisTemplate.opsForValue().get("key" + i);
            if (!("value" + i).equals(value)) {
                consistent = false;
                break;
            }
        }

        boolean dbConsistent = true;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            for (int i = 1; i <= 5; i++) {
                String value = session.selectOne("com.future.demo.architecture.hello.world.SelectMapper.getTest1ByKey", "key" + i);
                if (!("value" + i).equals(value)) {
                    dbConsistent = false;
                    break;
                }
            }
        }

        String hostname = InetAddress.getLocalHost().getHostName();

        return ResponseEntity.ok(
                "网关的主机名称：" + hostnameGateway +
                        "，helloworld的主机名称：" + hostname +
                        "，redis缓存数据是" + (consistent ? "完整的" : "不完整的") +
                        "，db数据是" + (dbConsistent ? "完整的" : "不完整的"));
    }

}
