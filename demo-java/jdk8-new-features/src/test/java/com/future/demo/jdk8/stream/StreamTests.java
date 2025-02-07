package com.future.demo.jdk8.stream;

import com.future.demo.jdk8.lambda.ListEntry;
import com.future.demo.jdk8.util.StreamUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
public class StreamTests {

    // 获取 Stream 实例的方法
    @Test
    public void testGetStreamInstance() {
        // 根据 Collection 获取 Stream 实例
        List<String> list = new ArrayList<>();
        Stream<String> stream1 = list.stream();
        Assert.assertNotNull(stream1);

        Set<String> set = new HashSet<>();
        Stream<String> stream2 = set.stream();
        Assert.assertNotNull(stream2);

        Map<String, String> map = new HashMap<>();

        Stream<String> stream3 = map.keySet().stream();
        Stream<String> stream4 = map.values().stream();
        Assert.assertNotNull(stream3);
        Assert.assertNotNull(stream4);

        Stream<Map.Entry<String, String>> stream5 = map.entrySet().stream();
        Assert.assertNotNull(stream5);

        // 使用 Stream 的静态 of 方法构造 Stream 实例
        Stream<String> stream6 = Stream.of("a", "b", "c");
        Assert.assertEquals(3, stream6.count());
        String[] strs = {"a", "b", "c"};
        Stream<String> stream7 = Stream.of(strs);
        Assert.assertEquals(3, stream7.count());
    }

    // 测试 Stream forEach 用法
    @Test
    public void testStreamForEach() {
        Stream<String> stream1 = Arrays.asList("a", "b").stream();
        stream1.forEach(e -> log.debug("element=" + e));

        Map<String, String> map = new HashMap<String, String>() {{
            this.put("k1", "v1");
            this.put("k2", "v2");
        }};
        map.entrySet().forEach(entry -> log.debug("key={}, value={}", entry.getKey(), entry.getValue()));

        // 测试带索引的遍历
        // https://stackoverflow.com/questions/18552005/is-there-a-concise-way-to-iterate-over-a-stream-with-indices-in-java-8
        List<String> list = Arrays.asList("a", "b", "c");
        List<String> stringList = IntStream.range(0, list.size()).filter(i -> !list.get(i).equals("b")).mapToObj(list::get).collect(Collectors.toList());
        Assert.assertArrayEquals(new String[]{"a", "c"}, stringList.toArray());
    }

    // 测试 Stream count 用法
    @Test
    public void testStreamCount() {
        Stream<String> stream1 = Arrays.asList("a", "b", "c").stream();
        Assert.assertEquals(3, stream1.count());
    }

    // 测试 Stream filter 用法
    @Test
    public void testStreamFilter() {
        // 过滤
        List<UserEntity> userEntityList = new ArrayList<>();
        userEntityList.add(new UserEntity("zhangsan", 20));
        userEntityList.add(new UserEntity("lisi", 13));
        userEntityList.add(new UserEntity("wangwu", 45));
        userEntityList.add(new UserEntity("guyt", 34));
        List<UserEntity> userEntityListFiltered = userEntityList.stream()
                .filter(userEntity -> userEntity.getAge() > 25).collect(Collectors.toList());
        Assert.assertEquals(2, userEntityListFiltered.size());
        Assert.assertEquals(userEntityList.get(2), userEntityListFiltered.get(0));
        Assert.assertEquals(userEntityList.get(3), userEntityListFiltered.get(1));
    }

    // 测试 skip 和 limit
    @Test
    public void testStreamSkipAndLimit() {
        // 模拟获取第二页skip和limit
        List<UserEntity> userEntityList = new ArrayList<>();
        userEntityList.add(new UserEntity("zhangsan", 20));
        userEntityList.add(new UserEntity("lisi", 13));
        userEntityList.add(new UserEntity("wangwu", 45));
        userEntityList.add(new UserEntity("guyt", 34));
        List<UserEntity> userEntityList2Page = userEntityList.stream().skip(2).limit(2).collect(Collectors.toList());
        Assert.assertEquals(2, userEntityList2Page.size());
        Assert.assertEquals(userEntityList.get(2), userEntityList2Page.get(0));
        Assert.assertEquals(userEntityList.get(3), userEntityList2Page.get(1));
    }

    // 测试 Stream find 方法
    @Test
    public void testFind() {
        Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5);
        Optional<Integer> result = stream.findFirst();
        Assert.assertEquals(Integer.valueOf(1), result.get());
        stream = Stream.of(1, 2, 3, 4, 5);
        result = stream.findAny();
        Assert.assertEquals(Integer.valueOf(1), result.get());
    }

    // 测试 Stream mapToInt
    @Test
    public void testMapToInt() {
        List<ListEntry> list = new ArrayList<ListEntry>() {{
            this.add(new ListEntry(1));
            this.add(new ListEntry(2));
            this.add(new ListEntry(3));
        }};
        IntStream intStream = list.stream().mapToInt(o -> o.getNumber());
        Assert.assertEquals(1, intStream.findFirst().getAsInt());
    }

    // 测试 Stream concat
    @Test
    public void testConcat() {
        Stream<String> stream1 = Stream.of("a", "b");
        Stream<String> stream2 = Stream.of("c", "d");
        Stream<String> stream3 = Stream.concat(stream1, stream2);
        Assert.assertEquals(4, stream3.count());
    }

    // 测试 Stream 转换为 Array
    @Test
    public void testStreamToArray() {
        List<String> originalList = new ArrayList<String>() {{
            this.add("a");
            this.add("b");
            this.add("c");
            this.add("b");
        }};
        String[] strArr = originalList.stream().toArray(String[]::new);
        Assert.assertArrayEquals(originalList.toArray(new String[]{}), strArr);
    }

    // 测试 stream collect
    @Test
    public void testCollect() {
        List<String> originalList = new ArrayList<String>() {{
            this.add("a");
            this.add("b");
            this.add("c");
            this.add("b");
        }};
        // List 转换为 List
        List<String> list = originalList.stream().collect(Collectors.toCollection(ArrayList::new));
        Assert.assertEquals(originalList, list);
        list = originalList.stream().collect(Collectors.toList());
        Assert.assertEquals(originalList, list);

        // List 转换为 HashSet
        Set<String> hashSet = originalList.stream().collect(Collectors.toCollection(HashSet::new));
        Assert.assertEquals(originalList.size() - 1, hashSet.size());
        Assert.assertEquals(Arrays.copyOf(originalList.toArray(), originalList.size() - 1), hashSet.toArray());
        hashSet = originalList.stream().collect(Collectors.toSet());
        Assert.assertEquals(originalList.size() - 1, hashSet.size());
        Assert.assertEquals(Arrays.copyOf(originalList.toArray(), originalList.size() - 1), hashSet.toArray());

        // List 转换为 HashMap
        List<UserEntityDistinct> userEntityDistinctList = new ArrayList<>();
        userEntityDistinctList.add(new UserEntityDistinct("zhangsan", 20, null));
        userEntityDistinctList.add(new UserEntityDistinct("lisi", 13, null));
        userEntityDistinctList.add(new UserEntityDistinct("wangwu", 45, null));
        userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, null));
        userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, null));
        Map<String, Integer> map = userEntityDistinctList.stream().collect(Collectors.toMap(UserEntityDistinct::getName, UserEntityDistinct::getAge, (a, b) -> a/* 重复键合并策略，返回第一个年龄值 */));
        Assert.assertEquals(4, map.size());
        Assert.assertTrue(map.containsKey("zhangsan"));
    }

    // 测试 Stream 聚合函数
    @Test
    public void testAggregation() {
        List<UserEntityDistinct> userEntityDistinctList = new ArrayList<>();
        userEntityDistinctList.add(new UserEntityDistinct("zhangsan", 20, null));
        userEntityDistinctList.add(new UserEntityDistinct("lisi", 13, null));
        userEntityDistinctList.add(new UserEntityDistinct("wangwu", 45, null));
        userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, null));
        userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, null));

        // maxBy
        UserEntityDistinct userEntityDistinct = userEntityDistinctList.stream().collect(Collectors.maxBy((a, b) -> a.getAge() - b.getAge())).get();
        Assert.assertEquals(45, userEntityDistinct.getAge());

        // minBy
        userEntityDistinct = userEntityDistinctList.stream().collect(Collectors.minBy((a, b) -> a.getAge() - b.getAge())).get();
        Assert.assertEquals(13, userEntityDistinct.getAge());

        // 求和
        int totalAge = userEntityDistinctList.stream().collect(Collectors.summingInt(o -> o.getAge()));
        Assert.assertEquals(146, totalAge);

        // 求平均
        int averageAge = userEntityDistinctList.stream().collect(Collectors.averagingInt(o -> o.getAge())).intValue();
        Assert.assertEquals(29, averageAge);

        // 统计数量
        int count = userEntityDistinctList.stream().collect(Collectors.counting()).intValue();
        Assert.assertEquals(5, count);
    }

    // 测试分组
    @Test
    public void testGroupingBy() {
        List<UserEntityDistinct> userEntityDistinctList = new ArrayList<>();
        userEntityDistinctList.add(new UserEntityDistinct("zhangsan", 20, null));
        userEntityDistinctList.add(new UserEntityDistinct("wangwu", 13, null));
        userEntityDistinctList.add(new UserEntityDistinct("wangwu", 45, null));
        userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, null));
        userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, null));

        // 根据年龄分组
        Map<Integer, List<UserEntityDistinct>> map = userEntityDistinctList.stream().collect(Collectors.groupingBy(o -> o.getAge()));
        Assert.assertEquals(2, map.get(34).size());

        // 根据年龄区间分组
        Map<String, List<UserEntityDistinct>> map1 = userEntityDistinctList.stream().collect(Collectors.groupingBy(o -> {
            if (o.getAge() <= 20) {
                return "<=20";
            } else if (o.getAge() <= 40) {
                return "<=40";
            } else {
                return "其他";
            }
        }));
        Assert.assertEquals(3, map1.size());
        Assert.assertEquals(2, map1.get("<=20").size());
        Assert.assertEquals(2, map1.get("<=40").size());
        Assert.assertEquals(1, map1.get("其他").size());

        // 多级分组
        Map<String, Map<Integer, List<UserEntityDistinct>>> map2 = userEntityDistinctList.stream().collect(Collectors.groupingBy(UserEntityDistinct::getName, Collectors.groupingBy(UserEntityDistinct::getAge)));
        Assert.assertEquals(3, map2.size());
        Assert.assertEquals(1, map2.get("guyt").size());
        Assert.assertEquals(2, map2.get("guyt").get(34).size());
        Assert.assertEquals(2, map2.get("wangwu").size());
        Assert.assertEquals(1, map2.get("wangwu").get(13).size());
        Assert.assertEquals(1, map2.get("wangwu").get(45).size());
    }

    // 测试分区
    @Test
    public void testPartitionBy() {
        List<UserEntityDistinct> userEntityDistinctList = new ArrayList<>();
        userEntityDistinctList.add(new UserEntityDistinct("zhangsan", 20, null));
        userEntityDistinctList.add(new UserEntityDistinct("wangwu", 13, null));
        userEntityDistinctList.add(new UserEntityDistinct("wangwu", 45, null));
        userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, null));
        userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, null));

        Map<Boolean, List<UserEntityDistinct>> map1 = userEntityDistinctList.stream().collect(Collectors.partitioningBy(o -> o.getAge() > 30));
        Assert.assertEquals(2, map1.size());
        Assert.assertEquals(2, map1.get(Boolean.FALSE).size());
        Assert.assertEquals(3, map1.get(Boolean.TRUE).size());
    }

    // 测试 joining
    @Test
    public void testJoining() {
        List<UserEntityDistinct> userEntityDistinctList = new ArrayList<>();
        userEntityDistinctList.add(new UserEntityDistinct("zhangsan", 20, null));
        userEntityDistinctList.add(new UserEntityDistinct("wangwu", 13, null));
        userEntityDistinctList.add(new UserEntityDistinct("wangwu", 45, null));
        userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, null));
        userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, null));

        String str = userEntityDistinctList.stream().map(UserEntityDistinct::getName).collect(Collectors.joining("_", "|", "|"));
        Assert.assertEquals(str, "|zhangsan_wangwu_wangwu_guyt_guyt|");
    }

    @Test
    public void test() {
        // distinct根据hashCode和equals方法去重
        List<UserEntityDistinct> userEntityDistinctList = new ArrayList<>();
        userEntityDistinctList.add(new UserEntityDistinct("zhangsan", 20, null));
        userEntityDistinctList.add(new UserEntityDistinct("lisi", 13, null));
        userEntityDistinctList.add(new UserEntityDistinct("wangwu", 45, null));
        userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, Arrays.asList(new UserEntityDistinct.NestedClass(1L), new UserEntityDistinct.NestedClass(2L))));
        userEntityDistinctList.add(new UserEntityDistinct("guyt", 34, Arrays.asList(new UserEntityDistinct.NestedClass(1L))));
        // 需要重写 UserEntityDistinct 的 hashCode 和 equals 方法以协助无参数 distinct 去重
        Assert.assertEquals(userEntityDistinctList.size() - 1, userEntityDistinctList.stream().distinct().collect(Collectors.toList()).size());
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
