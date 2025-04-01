package com.future.demo.util;

import org.junit.Test;

import java.util.*;

public class CollectionUtilTests {
    /**
     * https://stackoverflow.com/questions/369512/how-to-compare-objects-by-multiple-fields
     */
    @Test
    public void testMultipleFieldsComparator() {
        Random random = new Random();

        List<Integer> nodeIdList = new ArrayList<>(Arrays.asList(6001, 6002));
        List<Integer> userIdZList = new ArrayList<>(Arrays.asList(23, 26, 28));
        List<Integer> playTypeList = new ArrayList<>(Arrays.asList(0, 33, 36));
        List<Map<String, Object>> mapList = new ArrayList<>();
        for(int i=0; i<5; i++) {
            int randomNodeId = nodeIdList.get(random.nextInt(nodeIdList.size()));
            int randomUserIdZ = userIdZList.get(random.nextInt(userIdZList.size()));
            int randomPlayType = playTypeList.get(random.nextInt(playTypeList.size()));
            Map<String, Object> mapObject = new HashMap<>();
            mapObject.put("nodeId", randomNodeId);
            mapObject.put("userIdZ", randomUserIdZ);
            mapObject.put("playType", randomPlayType);
            mapList.add(mapObject);
        }

        Comparator<Map<String, Object>> comparator = Comparator
                .comparing((Map<String, Object> mapObject)->(Integer)mapObject.get("nodeId"))
                .thenComparing(mapObject->(Integer)mapObject.get("userIdZ"))
                .thenComparing(mapObject->(Integer)mapObject.get("playType"));
        mapList.sort(comparator);
        System.out.println(mapList);
    }
}
