package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Slf4j
public class ApplicationTests {

    @Test
    public void contextLoads() throws InterruptedException {
        SnowflakeIdWorker snowflake = new SnowflakeIdWorker(0, 0);
        int evenCounter = 0;
        int oddsCounter = 0;
        for (int i = 0; i < 100; i++) {
            long id = snowflake.nextId();
            log.debug("id {}", id);
            TimeUnit.MILLISECONDS.sleep(RandomUtils.nextInt(1, 100));

            if (id % 2 == 0) {
                evenCounter++;
            } else {
                oddsCounter++;
            }
        }

        log.debug("even {}, odds {}", evenCounter, oddsCounter);
    }
}
