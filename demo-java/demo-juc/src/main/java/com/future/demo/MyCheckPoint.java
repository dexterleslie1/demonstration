package com.future.demo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class MyCheckPoint {
    static Map<String, LocalDateTime> checkPointToLocalDateTimeMap = new HashMap<>();

    public static void check(String checkPoint) {
        checkPointToLocalDateTimeMap.put(checkPoint, LocalDateTime.now());
    }

    public static boolean isBefore(String checkPoint1, String checkPoint2) {
        if (!checkPointToLocalDateTimeMap.containsKey(checkPoint1)) {
            return false;
        }
        if (!checkPointToLocalDateTimeMap.containsKey(checkPoint2)) {
            return true;
        }

        return checkPointToLocalDateTimeMap.get(checkPoint1).isBefore(checkPointToLocalDateTimeMap.get(checkPoint2));
    }

    public static void clear() {
        checkPointToLocalDateTimeMap.clear();
    }
}
