package com.future.demo;

import org.junit.Assert;
import org.junit.Test;

public class EnumTests {

    @Test
    public void test() {
        // 获取 RED 对应的 duration
        Assert.assertEquals(30, TrafficLight.RED.getDuration());
        // 使用 GREEN 实例调用 getSignal 函数
        Assert.assertEquals("GO", TrafficLight.GREEN.getSignal());

        // 枚举常量数组
        Assert.assertArrayEquals(new TrafficLight[]{TrafficLight.RED, TrafficLight.YELLOW, TrafficLight.GREEN}, TrafficLight.values());

        // 从字符串创建枚举
        Assert.assertEquals(TrafficLight.RED, TrafficLight.valueOf("RED"));

        // `ordinal()` 方法返回枚举常量在枚举定义中的索引，从 0 开始。
        Assert.assertEquals(0, TrafficLight.RED.ordinal());
        Assert.assertEquals(1, TrafficLight.YELLOW.ordinal());
        Assert.assertEquals(2, TrafficLight.GREEN.ordinal());

        // `name()` 方法返回枚举常量的名称 (字符串)。
        Assert.assertEquals("RED", TrafficLight.RED.name());
    }

    public enum TrafficLight {
        RED(30), YELLOW(5), GREEN(25);

        private final int duration;

        TrafficLight(int duration) {
            this.duration = duration;
        }

        public int getDuration() {
            return duration;
        }

        public String getSignal() {
            switch (this) {
                case RED:
                    return "STOP";
                case YELLOW:
                    return "CAUTION";
                case GREEN:
                    return "GO";
                default:
                    return "UNKNOWN";
            }
        }
    }
}
