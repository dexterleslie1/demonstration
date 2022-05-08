package com.future.demo.robot.detection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Controller
@RequestMapping(value="/api/v1/biz")
public class ApiController {
    @Autowired
    JedisPool jedisPool = null;

    @RequestMapping("test.do")
    public @ResponseBody AjaxResponse test() {
        AjaxResponse response = new AjaxResponse();
        response.setDataObject("接口调用成功");
        return response;
    }

    @RequestMapping("setEnable.do")
    public @ResponseBody AjaxResponse setEnable(
            @RequestParam(name = "enabled", required = false, defaultValue = "false") boolean enabled) {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();

            jedis.set("enabled", String.valueOf(enabled));

            AjaxResponse response = new AjaxResponse();
            response.setDataObject("设置成功");
            return response;
        } finally {
           if(jedis != null) {
               jedis.close();
               jedis = null;
           }
        }
    }
}
