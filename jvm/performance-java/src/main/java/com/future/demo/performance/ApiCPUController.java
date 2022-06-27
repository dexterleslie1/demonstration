package com.future.demo.performance;

import com.yyd.common.http.response.ObjectResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping(value="/api/v1/cpu")
@Slf4j
public class ApiCPUController {
	@Autowired
	PasswordEncoder passwordEncoder;

	/**
	 * 用于协助测试cpu负载
	 *
	 * @param durationInSecond cpu负载测试的持续时间，秒数
	 * @return
	 */
	@RequestMapping("loaded")
    public ObjectResponse<String> cpuLoaded(
    		@RequestParam(value = "durationInSecond", defaultValue = "60") int durationInSecond) {
		if(durationInSecond <=0) {
			durationInSecond = 60;
		}

    	// 接口模拟cpu繁忙指定持续时间
		log.debug("准备开始模拟cpu繁忙，持续{}秒", durationInSecond);
    	Date startTime = new Date();
    	Date timeNow = new Date();
    	long count = 0;
    	while(timeNow.getTime() - startTime.getTime() < durationInSecond * 1000) {
    		String uuid = UUID.randomUUID().toString();
    		this.passwordEncoder.encode(uuid);
    		timeNow = new Date();
    		count++;
		}
    	log.debug("结束模拟cpu繁忙，调用passwordencoder {} 次", count);

    	ObjectResponse<String> response = new ObjectResponse<>();
    	response.setData("调用passwordencoder " + count + " 次");
        return response;
    }
}
