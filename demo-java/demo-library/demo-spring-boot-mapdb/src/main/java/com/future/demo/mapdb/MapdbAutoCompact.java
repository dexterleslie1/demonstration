package com.future.demo.mapdb;

import org.mapdb.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class MapdbAutoCompact {
    private static final Logger log = LoggerFactory.getLogger(MapdbAutoCompact.class);

    private static final long INITIAL_DELAY_MINUTES = 1;
    private static final long INTERVAL_MINUTES = 30;

    public MapdbAutoCompact(DB db, ScheduledExecutorService mapdbCompactExecutor) {
        mapdbCompactExecutor.scheduleWithFixedDelay(() -> compact(db), INITIAL_DELAY_MINUTES, INTERVAL_MINUTES, TimeUnit.MINUTES);
        log.info("MapDB auto compact enabled, initialDelay={}min, interval={}min", INITIAL_DELAY_MINUTES, INTERVAL_MINUTES);
    }

    private void compact(DB db) {
//        if (db.isClosed()) {
//            return;
//        }
//        try {
//            log.info("MapDB compact start");
//            db.getStore().compact();
//            log.info("MapDB compact done");
//        } catch (Exception e) {
//            log.warn("MapDB compact failed", e);
//        }
    }
}
