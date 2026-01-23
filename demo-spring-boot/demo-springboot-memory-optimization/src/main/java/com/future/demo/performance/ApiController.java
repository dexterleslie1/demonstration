package com.future.demo.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/api/v1")
@Slf4j
public class ApiController {

	@GetMapping("test")
	ResponseEntity<String> test() {
		return ResponseEntity.ok("调用成功");
	}
}
