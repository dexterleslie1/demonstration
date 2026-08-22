package com.future.demo.mapdb;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/v1")
public class ApiController {
    private static final Logger log = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private TestDtoListService testDtoListService;
    @Autowired
    private ConcurrentMap<String, String> mapMemoryFootprint;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping(value = "testList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StreamingResponseBody> testList() {
        String requestId = this.testDtoListService.newRequestId();
        List<TestDto> requestList = this.testDtoListService.open(requestId);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 20_000; i++) {
            requestList.add(randomTestDto(i + 1L, random));
        }

        StopWatch stopWatch = new StopWatch("update-description");
        stopWatch.start("update-description");
        for (int i = 0; i < requestList.size(); i++) {
            TestDto dto = requestList.get(i);
            dto.setDescription("desc-" + UUID.randomUUID());
            requestList.set(i, dto);
        }
        stopWatch.stop();
        log.info("update-description 耗时: {} ms", stopWatch.getLastTaskTimeMillis());

        StreamingResponseBody body = outputStream -> {
            try (JsonGenerator generator = this.objectMapper.getFactory().createGenerator(outputStream)) {
                generator.writeStartArray();
                for (TestDto dto : requestList) {
                    this.objectMapper.writeValue(generator, dto);
                }
                generator.writeEndArray();
            } finally {
                this.testDtoListService.clear(requestId);
            }
        };
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    private TestDto randomTestDto(long id, ThreadLocalRandom random) {
        TestDto dto = new TestDto();
        dto.setId(id);
        dto.setName("name-" + UUID.randomUUID());
        dto.setCode("code-" + random.nextInt(1000000));
        dto.setDescription("desc-" + UUID.randomUUID());
        dto.setStatus(random.nextBoolean() ? "ENABLED" : "DISABLED");
        dto.setRemark("remark-" + random.nextInt(1000000));
        dto.setCategory("category-" + random.nextInt(100));
        dto.setSource("source-" + random.nextInt(50));

        dto.setAmount(randomBigDecimal(random));
        dto.setPrice(randomBigDecimal(random));
        dto.setCost(randomBigDecimal(random));
        dto.setDiscount(randomBigDecimal(random));
        dto.setTax(randomBigDecimal(random));
        dto.setTotal(randomBigDecimal(random));
        dto.setBalance(randomBigDecimal(random));

        dto.setQuantity(random.nextInt(1, 10000));
        dto.setCount(random.nextInt(1, 10000));
        dto.setVersion(random.nextInt(1, 100));
        dto.setSort(random.nextInt(1, 1000));
        dto.setFlag(random.nextInt(0, 2));
        dto.setLevel(random.nextInt(1, 10));

        dto.setUserId(random.nextLong(1, 100000));
        dto.setOrgId(random.nextLong(1, 10000));
        dto.setParentId(random.nextLong(1, 100000));
        dto.setCreateBy(random.nextLong(1, 100000));
        dto.setUpdateBy(random.nextLong(1, 100000));

        LocalDateTime now = LocalDateTime.now();
        dto.setCreateTime(now.minusDays(random.nextInt(0, 365)));
        dto.setUpdateTime(now.minusHours(random.nextInt(0, 24)));
        dto.setStartTime(now.minusDays(random.nextInt(0, 30)));
        dto.setEndTime(now.plusDays(random.nextInt(1, 30)));
        return dto;
    }

    private BigDecimal randomBigDecimal(ThreadLocalRandom random) {
        return BigDecimal.valueOf(random.nextDouble(0.01, 100000.00))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private boolean runLoopStop = true;

    /**
     * 用于开始测试 MapDB 内存占用情况
     *
     * @return
     */
    @GetMapping("memory/footprint/start")
    public ResponseEntity<String> memoryFootprintStart() {
        this.runLoopStop = false;
        while (!this.runLoopStop) {
            String uuid = UUID.randomUUID().toString();
            this.mapMemoryFootprint.put(uuid, uuid);
        }
        return ResponseEntity.ok("调用成功");
    }

    /**
     * 用于停止测试 MapDB 内存占用情况
     *
     * @return
     */
    @GetMapping("memory/footprint/stop")
    public ResponseEntity<String> memoryFootprintStop() {
        this.runLoopStop = true;
        return ResponseEntity.ok("调用成功");
    }
}
