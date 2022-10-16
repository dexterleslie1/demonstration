package com.future.demo.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="/api/v1/memory")
@Slf4j
public class ApiMemoryController {
	private final static Integer Size = 1024*1024;

	private List<byte []> list = new ArrayList<>();
	private List<Integer> list1 = new ArrayList<>();

	@RequestMapping("alloc")
	void alloc() {
		byte []datum = new byte[Size];
		int length = datum.length;
//		list.add(datum);
		list1.add(length);
	}
}
