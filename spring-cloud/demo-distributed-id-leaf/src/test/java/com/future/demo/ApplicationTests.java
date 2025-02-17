package com.future.demo;

import com.tencent.devops.leaf.common.Result;
import com.tencent.devops.leaf.service.SnowflakeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Slf4j
public class ApplicationTests {

    @Autowired
    SnowflakeService snowflakeService;

    @Test
    public void contextLoads() throws InterruptedException {
        int evenCounter = 0;
        int oddsCounter = 0;
        for (int i = 0; i < 100; i++) {
            Result result = this.snowflakeService.getId("x");
            log.debug("status {}, id {}", result.getStatus(), result.getId());
            TimeUnit.MILLISECONDS.sleep(RandomUtils.nextInt(1, 100));

            if (result.getId() % 2 == 0) {
                evenCounter++;
            } else {
                oddsCounter++;
            }
        }

        log.debug("even {}, odds {}", evenCounter, oddsCounter);
    }
}
