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
    /**
     * oom接口每次分配的最大字节数
     */
    private final static Integer MaximumByteToOomAlloc = 5 * 1024;
    private final Random RANDOM = new Random();

    private List<byte[]> list1 = new ArrayList<>();

    /**
     * 模拟繁忙的应用分配内存
     *
     * @return
     */
    @RequestMapping("alloc")
    public String alloc() {
        investigateMemoryAllocationRecursion(1024, 1);
        return "成功调用";
    }

    private void investigateMemoryAllocationRecursion(int maximumDepth, int currentDepth) {
        byte[] allocByteArray = new byte[1024];
        RANDOM.nextBytes(allocByteArray);

        // 模拟接口业务逻辑处理时长
        int randomInt = RANDOM.nextInt(5);
        if (randomInt > 0) {
            try {
                Thread.sleep(randomInt);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (currentDepth < maximumDepth)
            investigateMemoryAllocationRecursion(maximumDepth, currentDepth + 1);

        // 模拟接口业务逻辑处理时长
        randomInt = RANDOM.nextInt(5);
        if (randomInt > 0) {
            try {
                Thread.sleep(randomInt);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // 这里需要引用allocByteArray，否则上面allocByteArray会被jvm优化在年轻代gc就被回收
        int length = allocByteArray.length;
    }

    /**
     * 用于测试oom接口
     *
     * @return
     */
    @RequestMapping("oomAlloc")
    public String oomAlloc() {
        int length = RANDOM.nextInt(MaximumByteToOomAlloc);
        if (length > 0) {
            byte[] datum = new byte[length];
            RANDOM.nextBytes(datum);
            list1.add(datum);
        }
        return "成功分配 " + length + "B 内存";
    }
}
