package com.future.demo.casbin;

import lombok.extern.slf4j.Slf4j;
import org.casbin.jcasbin.main.Enforcer;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class CasbinTests {
    // acl测试
    @Test
    public void testAcl() throws IOException {
        ClassPathResource resource1 = new ClassPathResource("acl_model.conf");
        String resource1Path = resource1.getFile().getAbsolutePath();
        ClassPathResource resource2 = new ClassPathResource("acl_policy.csv");
        String resource2Path = resource2.getFile().getAbsolutePath();
        Enforcer enforcer = new Enforcer(resource1Path, resource2Path);

        String subject = "a";
        String object = "data2";
        String action = "read";
        boolean b = enforcer.enforce(subject, object, action);
        Assert.assertFalse(b);

        subject = "alice";
        object = "data2";
        action = "read";
        b = enforcer.enforce(subject, object, action);
        Assert.assertFalse(b);

        subject = "alice";
        object = "data1";
        action = "write";
        b = enforcer.enforce(subject, object, action);
        Assert.assertFalse(b);

        subject = "alice";
        object = "data1";
        action = "read";
        b = enforcer.enforce(subject, object, action);
        Assert.assertTrue(b);

        subject = "bob";
        object = "data2";
        action = "write";
        b = enforcer.enforce(subject, object, action);
        Assert.assertTrue(b);
    }

    @Test
    public void testRbac() throws IOException {
        ClassPathResource resource1 = new ClassPathResource("rbac_model.conf");
        String resource1Path = resource1.getFile().getAbsolutePath();
        ClassPathResource resource2 = new ClassPathResource("rbac_policy.csv");
        String resource2Path = resource2.getFile().getAbsolutePath();
        Enforcer enforcer = new Enforcer(resource1Path, resource2Path);

        String subject = "alice";
        String object = "data1";
        String action = "read";
        boolean b = enforcer.enforce(subject, object, action);
        Assert.assertTrue(b);

        subject = "alice";
        object = "data2";
        action = "read";
        b = enforcer.enforce(subject, object, action);
        Assert.assertTrue(b);
    }

    // api测试
    @Test
    public void testApi() throws IOException {
        ClassPathResource resource1 = new ClassPathResource("acl_model.conf");
        String resource1Path = resource1.getFile().getAbsolutePath();
        ClassPathResource resource2 = new ClassPathResource("acl_policy.csv");
        String resource2Path = resource2.getFile().getAbsolutePath();
        Enforcer enforcer = new Enforcer(resource1Path, resource2Path);

        List<String> subjectList = enforcer.getAllSubjects();
        Assert.assertArrayEquals(Arrays.asList("alice", "bob").toArray(), subjectList.toArray());

        List<String> objectList = enforcer.getAllObjects();
        Assert.assertArrayEquals(Arrays.asList("data1", "data2").toArray(), objectList.toArray());

        List<String> actionList = enforcer.getAllActions();
        Assert.assertArrayEquals(Arrays.asList("read", "write").toArray(), actionList.toArray());

        // 添加policy
        boolean b = enforcer.addPolicy(Arrays.asList("dexterleslie", "data3", "delete"));
        Assert.assertTrue(b);
        b = enforcer.hasPolicy(Arrays.asList("dexterleslie", "data3", "delete"));
        Assert.assertTrue(b);
        b = enforcer.enforce("dexterleslie", "data3", "delete");
        Assert.assertTrue(b);

        // 更新policy
        b = enforcer.updatePolicy(Arrays.asList("dexterleslie", "data3", "delete"), Arrays.asList("dexterleslie1", "data3", "delete"));
        Assert.assertTrue(b);
        b = enforcer.hasPolicy(Arrays.asList("dexterleslie", "data3", "delete"));
        Assert.assertFalse(b);
        b = enforcer.hasPolicy(Arrays.asList("dexterleslie1", "data3", "delete"));
        Assert.assertTrue(b);
        b = enforcer.enforce("dexterleslie", "data3", "delete");
        Assert.assertFalse(b);
        b = enforcer.enforce("dexterleslie1", "data3", "delete");
        Assert.assertTrue(b);

        // 删除permission
        b = enforcer.deletePermission("data3", "delete");
        Assert.assertTrue(b);
        b = enforcer.enforce("dexterleslie1", "data3", "delete");
        Assert.assertFalse(b);

        b = enforcer.addPolicy(Arrays.asList("dexterleslie1", "data3", "delete"));
        Assert.assertTrue(b);
        b = enforcer.deletePermissionForUser("dexterleslie1", "data3", "delete");
        Assert.assertTrue(b);
        b = enforcer.enforce("dexterleslie1", "data3", "delete");
        Assert.assertFalse(b);
    }
}
