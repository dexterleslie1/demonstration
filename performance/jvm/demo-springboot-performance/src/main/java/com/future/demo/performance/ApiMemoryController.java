package com.future.demo.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping(value = "/api/v1/memory")
@Slf4j
public class ApiMemoryController {
    private final static Integer MaximumByteToAlloc = 5 * 1024;
    private final Random RANDOM = new Random();

    private List<byte[]> list1 = new ArrayList<>();

    @RequestMapping("alloc")
    public String alloc() {
        int length = RANDOM.nextInt(MaximumByteToAlloc);
        if (length > 0) {
            byte[] datum = new byte[length];
            list1.add(datum);
        }
        return "成功分配 " + length + "B 内存";
    }
}
