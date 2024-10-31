package com.future.demo.architecture.hello.world;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.data.redis.core.RedisTemplate;
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
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    SqlSessionFactory sqlSessionFactory;

    @GetMapping(value = "/api/v1/test1", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> test1(@RequestParam(value = "hostnameZuul") String hostnameZuul) throws UnknownHostException {
        String uuidString = UUID.randomUUID().toString();
        this.redisTemplate.opsForValue().set(uuidString, uuidString);

        try (SqlSession session = sqlSessionFactory.openSession()) {
            session.selectOne("com.future.demo.architecture.hello.world.SelectMapper.selectNow");
        }

        String hostname = InetAddress.getLocalHost().getHostName();

        return ResponseEntity.ok("zuul的主机名称：" + hostnameZuul + "，helloworld的主机名称：" + hostname + "，redis、mariadb服务正常");
    }

}
