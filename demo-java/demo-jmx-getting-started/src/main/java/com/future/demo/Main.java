package com.future.demo;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class Main {
    public static void main(String[] args) throws Exception {
        // 获取平台默认的 MBean Server
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        // 为我们的 MBean 创建一个对象名（唯一标识）
        ObjectName name = new ObjectName("com.example:type=AppConfig");

        // 实例化我们的 MBean 并注册到 MBean Server 中
        AppConfig mbean = new AppConfig();
        mbs.registerMBean(mbean, name);

        // ... 你的应用主逻辑，保持运行
        Thread.sleep(Long.MAX_VALUE);
    }
}
