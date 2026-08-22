package com.future.demo.mapdb;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class ApplicationTests {
    @Autowired
    ConcurrentMap<String, String> mapTest;
    @Autowired
    ConcurrentMap<String, String> mapMemoryFootprint;

    @Test
    void contextLoads() throws InterruptedException {
        // region 测试 put 和 get

        String uuidStr = UUID.randomUUID().toString();
        this.mapTest.put(uuidStr, uuidStr);
        String uuidStrResult = this.mapTest.get(uuidStr);
        Assertions.assertEquals(uuidStr, uuidStrResult);

        // endregion

        // region 测试基于时间的过期

        String uuidStr1 = UUID.randomUUID().toString();
        this.mapMemoryFootprint.put(uuidStr1, uuidStr1);
        Assertions.assertEquals(uuidStr1, this.mapMemoryFootprint.get(uuidStr1));
        TimeUnit.SECONDS.sleep(16);
        Assertions.assertNull(this.mapMemoryFootprint.get(uuidStr1));

        // endregion
    }

    @Test
    void testFileDbPersistence() {
        // region 测试文件持久化：关闭后重新打开仍能读到数据

        File dbFile = new File("target/mapdb-persistence-test.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }

        String uuidStr = UUID.randomUUID().toString();
        try (DB db = DBMaker.fileDB(dbFile).make()) {
            ConcurrentMap<String, String> map = db.hashMap("mapPersistence")
                    .keySerializer(Serializer.STRING)
                    .valueSerializer(Serializer.STRING)
                    .createOrOpen();
            map.put(uuidStr, uuidStr);
        }

        try (DB db = DBMaker.fileDB(dbFile).make()) {
            ConcurrentMap<String, String> map = db.hashMap("mapPersistence")
                    .keySerializer(Serializer.STRING)
                    .valueSerializer(Serializer.STRING)
                    .createOrOpen();
            Assertions.assertEquals(uuidStr, map.get(uuidStr));
        } finally {
            if (dbFile.exists()) {
                dbFile.delete();
            }
        }

        // endregion
    }
}
