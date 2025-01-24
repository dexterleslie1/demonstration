package com.future.demo.jdk8.stream;

import com.yyd.common.jdk8.feature.StreamUtil;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.StreamUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class StreamTests {

    @Test
    public void test() {
        // distinct根据hashCode和equals方法去重
        List<UserEntityDistinct> userEntityDistinctList = new ArrayList<>();
        userEntityDistinctList.add(new UserEntityDistinct("zhangsan", 20, null));
        userEntityDistinctList.add(new UserEntityDistinct("lisi", 13, null));
        userEntityDistinctList.add(new UserEntityDistinct("wangwu", 45, null));
        userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, Arrays.asList(new UserEntityDistinct.NestedClass(1L), new UserEntityDistinct.NestedClass(2L))));
        userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, Arrays.asList(new UserEntityDistinct.NestedClass(1L))));
//        Assert.assertEquals(userEntityDistinctList.size() - 1, userEntityDistinctList.stream().distinct().collect(Collectors.toList()).size());
        Assert.assertEquals(userEntityDistinctList.size() - 1, userEntityDistinctList.stream().filter(StreamUtil.distinctByKey(UserEntityDistinct::getName)).collect(Collectors.toList()).size());

        // List<Map<String, Object>>去重
        // distinctByKey方法使用
        // https://www.cnblogs.com/zwh0910/p/15877284.html
        List<Map<String, Object>> mapList = new ArrayList<>();
        Map<String, Object> oMap = new HashMap<>();
        oMap.put("id", 1L);
        oMap.put("code", "1-1");
        mapList.add(oMap);
        oMap = new HashMap<>();
        oMap.put("id", 2L);
        oMap.put("code", "1-2");
        mapList.add(oMap);
        oMap = new HashMap<>();
        oMap.put("id", 1L);
        oMap.put("code", "1-3");
        mapList.add(oMap);
        Assert.assertEquals(mapList.size() - 1, mapList.stream().filter(StreamUtil.distinctByKey(o -> o.get("id"))).collect(Collectors.toList()).size());

        // List<UserEntity>转换为List<String>
        List<UserEntity> userEntityList = new ArrayList<>();
        userEntityList.add(new UserEntity("zhangsan", 20));
        userEntityList.add(new UserEntity("lisi", 13));
        userEntityList.add(new UserEntity("wangwu", 45));
        userEntityList.add(new UserEntity("guyt", 34));
        userEntityList.add(new UserEntity("guyt", 34));
        List<String> nameList = userEntityList.stream().map(userEntity -> userEntity.getName()).collect(Collectors.toList());
        AtomicInteger counter = new AtomicInteger(0);
        userEntityList.forEach(userEntity -> Assert.assertEquals(userEntity.getName(), nameList.get(counter.getAndIncrement())));

        // List转换为Set
        Set<UserEntity> userEntitySet =
                userEntityList.stream().collect(Collectors.toSet());
        Assert.assertEquals(userEntityList.size(), userEntitySet.size());
        userEntitySet.stream().forEach(userEntity -> Assert.assertTrue(nameList.contains(userEntity.getName())));

        // List转换为map
        userEntityList = new ArrayList<>();
        userEntityList.add(new UserEntity("zhangsan", 20));
        userEntityList.add(new UserEntity("lisi", 13));
        userEntityList.add(new UserEntity("wangwu", 45));
        userEntityList.add(new UserEntity("guyt", 34));
        List<String> nameList1 = userEntityList.stream().map(userEntity -> userEntity.getName()).collect(Collectors.toList());

        Map<String, UserEntity> nameToUserEntityMapper =
                userEntityList.stream().collect(Collectors.toMap(userEntity -> userEntity.getName(), userEntity -> userEntity));
        Assert.assertEquals(userEntityList.size(), nameToUserEntityMapper.size());
        nameToUserEntityMapper.forEach((name, userEntity) -> Assert.assertTrue(nameList1.contains(name)));

        // Collectors.toMap java.lang.IllegalStateException: Duplicate key
        // https://blog.csdn.net/zl18603543572/article/details/105722076
        userEntityList = new ArrayList<>();
        userEntityList.add(new UserEntity("zhangsan", 20));
        userEntityList.add(new UserEntity("zhangsan", 20));
        userEntityList.add(new UserEntity("lisi", 13));
        try {
            userEntityList.stream().collect(Collectors.toMap(userEntity -> userEntity.getName(), userEntity -> userEntity));
            Assert.fail("预期异常没有抛出");
        } catch (IllegalStateException ex) {
            Assert.assertTrue(ex.getMessage().contains("Duplicate key"));
        }
        // 针对以上问题使用 list to set + lombok @Data声明entity 或者 Collectors.toMap重载方法 解决
        userEntityDistinctList = new ArrayList<>();
        userEntityDistinctList.add(new UserEntityDistinct("zhangsan", 20, null));
        userEntityDistinctList.add(new UserEntityDistinct("zhangsan", 20, null));
        userEntityDistinctList.add(new UserEntityDistinct("lisi", 13, null));
        Map<String, UserEntityDistinct> userEntityDistinctMap = userEntityDistinctList.stream().collect(Collectors.toSet()).stream().collect(Collectors.toMap(userEntity -> userEntity.getName(), userEntity -> userEntity));
        Assert.assertEquals(2, userEntityDistinctMap.size());
        Assert.assertTrue(userEntityDistinctMap.containsKey("zhangsan"));
        Assert.assertTrue(userEntityDistinctMap.containsKey("lisi"));

        userEntityDistinctMap = userEntityDistinctList.stream().collect(Collectors.toMap(userEntity -> userEntity.getName(), userEntity -> userEntity, (userEntity1, userEntity2) -> userEntity2));
        Assert.assertEquals(2, userEntityDistinctMap.size());
        Assert.assertTrue(userEntityDistinctMap.containsKey("zhangsan"));
        Assert.assertTrue(userEntityDistinctMap.containsKey("lisi"));

        // 使用reduce实现年龄相加
        userEntityList = new ArrayList<>();
        userEntityList.add(new UserEntity("zhangsan", 20));
        userEntityList.add(new UserEntity("lisi", 13));
        userEntityList.add(new UserEntity("wangwu", 45));
        userEntityList.add(new UserEntity("guyt", 34));
        AtomicInteger totalAgeSum = new AtomicInteger(0);
        userEntityList.forEach(userEntity -> totalAgeSum.addAndGet(userEntity.getAge()));
        int totalAge = userEntityList.stream()
                // 先变换List<UserEntity>为List<Integer>
                .map(userEntity -> userEntity.getAge())
                // 实现年龄相加
                .reduce((a, b) -> a + b)
                // List<UserEntity>为空时获取orElse值
                .orElse(0);
        Assert.assertEquals(totalAgeSum.get(), totalAge);

        // 最大最小值
        userEntityDistinctList = new ArrayList<>();
        userEntityDistinctList.add(new UserEntityDistinct("zhangsan", 20, null));
        userEntityDistinctList.add(new UserEntityDistinct("lisi", 13, null));
        userEntityDistinctList.add(new UserEntityDistinct("wangwu", 45, null));
        userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, null));
        UserEntityDistinct userEntityDistinctMax = userEntityDistinctList.stream()
                .max((o1, o2) -> o1.getAge() - o2.getAge())
                .orElse(null);
        Assert.assertEquals(userEntityDistinctList.get(2), userEntityDistinctMax);

        UserEntityDistinct userEntityDistinctMin = userEntityDistinctList.stream()
                .min((o1, o2) -> o1.getAge() - o2.getAge())
                .orElse(null);
        Assert.assertEquals(userEntityDistinctList.get(1), userEntityDistinctMin);

        // 匹配anyMatch
        userEntityList = new ArrayList<>();
        userEntityList.add(new UserEntity("zhangsan", 20));
        userEntityList.add(new UserEntity("lisi", 13));
        userEntityList.add(new UserEntity("wangwu", 45));
        userEntityList.add(new UserEntity("guyt", 34));
        boolean matchResult = userEntityList.stream()
                .anyMatch(userEntity -> userEntity.getName().equals("wangwu"));
        Assert.assertTrue(matchResult);

        matchResult = userEntityList.stream()
                .anyMatch(userEntity -> userEntity.getName().equals("wangwu5"));
        Assert.assertFalse(matchResult);

        // 匹配noneMatch
        userEntityList = new ArrayList<>();
        userEntityList.add(new UserEntity("zhangsan", 20));
        userEntityList.add(new UserEntity("lisi", 13));
        userEntityList.add(new UserEntity("wangwu", 45));
        userEntityList.add(new UserEntity("guyt", 34));
        matchResult = userEntityList.stream()
                .noneMatch(userEntity -> userEntity.getName().equals("wangwu"));
        Assert.assertFalse(matchResult);

        matchResult = userEntityList.stream()
                .noneMatch(userEntity -> userEntity.getName().equals("wangwu5"));
        Assert.assertTrue(matchResult);

        // 匹配allMatch
        userEntityList = new ArrayList<>();
        userEntityList.add(new UserEntity("zhangsan", 20));
        userEntityList.add(new UserEntity("lisi", 13));
        matchResult = userEntityList.stream()
                .allMatch(userEntity -> userEntity.getAge() == 20 || userEntity.getAge() == 13);
        Assert.assertTrue(matchResult);

        matchResult = userEntityList.stream()
                .allMatch(userEntity -> userEntity.getAge() == 20 && userEntity.getAge() == 13);
        Assert.assertFalse(matchResult);

        // 过滤
        userEntityList = new ArrayList<>();
        userEntityList.add(new UserEntity("zhangsan", 20));
        userEntityList.add(new UserEntity("lisi", 13));
        userEntityList.add(new UserEntity("wangwu", 45));
        userEntityList.add(new UserEntity("guyt", 34));
        List<UserEntity> userEntityListFiltered = userEntityList.stream()
                .filter(userEntity -> userEntity.getAge() > 25).collect(Collectors.toList());
        Assert.assertEquals(2, userEntityListFiltered.size());
        Assert.assertEquals(userEntityList.get(2), userEntityListFiltered.get(0));
        Assert.assertEquals(userEntityList.get(3), userEntityListFiltered.get(1));

        // 模拟获取第二页skip和limit
        userEntityList = new ArrayList<>();
        userEntityList.add(new UserEntity("zhangsan", 20));
        userEntityList.add(new UserEntity("lisi", 13));
        userEntityList.add(new UserEntity("wangwu", 45));
        userEntityList.add(new UserEntity("guyt", 34));
        List<UserEntity> userEntityList2Page = userEntityList.stream().skip(2).limit(2).collect(Collectors.toList());
        Assert.assertEquals(2, userEntityList2Page.size());
        Assert.assertEquals(userEntityList.get(2), userEntityList2Page.get(0));
        Assert.assertEquals(userEntityList.get(3), userEntityList2Page.get(1));

        // 排序
        userEntityList = new ArrayList<>();
        userEntityList.add(new UserEntity("zhangsan", 20));
        userEntityList.add(new UserEntity("lisi", 13));
        userEntityList.add(new UserEntity("wangwu", 45));
        userEntityList.add(new UserEntity("guyt", 34));
        List<UserEntity> userEntityListSorted = userEntityList.stream()
                .sorted((o1, o2) -> o1.getAge() - o2.getAge()).collect(Collectors.toList());
        Assert.assertEquals(13, userEntityListSorted.get(0).getAge());
        Assert.assertEquals(20, userEntityListSorted.get(1).getAge());
        Assert.assertEquals(34, userEntityListSorted.get(2).getAge());
        Assert.assertEquals(45, userEntityListSorted.get(3).getAge());
    }

    /**
     * Map转换为List
     */
    @Test
    public void testMapToList() {
        Map<String, UserEntity> testMap = new HashMap<>();
        testMap.put("1", new UserEntity("zhangsan", 20));
        testMap.put("2", new UserEntity("lisi", 13));
        List<UserEntity> testList = new ArrayList<>(testMap.values());
        Assert.assertEquals(2, testList.size());
        Assert.assertEquals(new UserEntity("zhangsan", 20), testList.get(0));
        Assert.assertEquals(new UserEntity("lisi", 13), testList.get(1));
    }

    /**
     * 演示flatMap用法
     */
    @Test
    public void testFlatMap() {
        List<MenuVo> menuVoList = new ArrayList<>();
        menuVoList.add(new MenuVo() {{
            setName("menu1");
            setFunctionVoList(Arrays.asList(
                    new FunctionVo() {{
                        setName("fun1-1");
                    }},
                    new FunctionVo() {{
                        setName("fun1-2");
                    }}
            ));
        }});
        menuVoList.add(new MenuVo() {{
            setName("menu2");
            setFunctionVoList(Arrays.asList(
                    new FunctionVo() {{
                        setName("fun2-1");
                    }},
                    new FunctionVo() {{
                        setName("fun2-2");
                    }}
            ));
        }});

        List<FunctionVo> functionVoList = menuVoList.stream().flatMap(o -> o.getFunctionVoList().stream()).collect(Collectors.toList());
        Assert.assertEquals(4, functionVoList.size());
        Assert.assertEquals("fun1-1", functionVoList.get(0).getName());
        Assert.assertEquals("fun2-2", functionVoList.get(3).getName());
    }

    // todo
    @Test
    public void testGroupingBy() {

    }

    @Data
    public static class MenuVo {
        private String name;
        private List<FunctionVo> functionVoList;
    }

    @Data
    public static class FunctionVo {
        private String name;
    }

}
