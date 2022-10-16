package com.future.demo.performance;

import com.yyd.common.http.response.ObjectResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	 * @return
	 */
	@RequestMapping("encode")
    public ObjectResponse<String> encode() {
		String uuid = UUID.randomUUID().toString();
		String encoded = this.passwordEncoder.encode(uuid);

    	ObjectResponse<String> response = new ObjectResponse<>();
    	response.setData("密码 " + uuid + " encode结果 " + encoded);
        return response;
    }
}
