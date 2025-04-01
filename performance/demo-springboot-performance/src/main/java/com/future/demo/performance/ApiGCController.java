package com.future.demo.performance;

import com.future.common.http.ObjectResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * GC相关的api
 */
@RestController
@RequestMapping(value="/api/v1/gc")
@Slf4j
public class ApiGCController {
    @Autowired
	MemoryLeakService memoryLeakService = null;


	@GetMapping("memoryLeak/start")
    public ObjectResponse<String> memoryLeakStart(){
		long count = this.memoryLeakService.start();

		ObjectResponse<String> response = new ObjectResponse<>();
		response.setData("成功创建 " + count + " 个对象");
        return response;
    }

	@GetMapping("memoryLeak/stop")
	public ObjectResponse<String> memoryLeakStop() {
		this.memoryLeakService.stop();

		ObjectResponse<String> response = new ObjectResponse<>();
		response.setData("成功停止");
		return response;
	}

	@GetMapping("memoryLeak/release")
	public ObjectResponse<String> memoryLeakRelease() {
		this.memoryLeakService.releaseLeak();

		ObjectResponse<String> response = new ObjectResponse<>();
		response.setData("成功释放");
		return response;
	}

	@GetMapping("memoryLeak/performgc")
	public ObjectResponse<String> memoryLeakPerformGC() {
		System.gc();

		ObjectResponse<String> response = new ObjectResponse<>();
		response.setData("成功调用System.gc()函数");
		return response;
	}
}
