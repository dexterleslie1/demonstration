package com.future.demo;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.junit.Assert;
import org.junit.Test;

public class Tests {

    @Test
    public void testAuthentication() {
        IniSecurityManagerFactory factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
        Subject subject = SecurityUtils.getSubject();
        AuthenticationToken authenticationToken = new UsernamePasswordToken("zhangsan11", "zs");
        try {
            subject.login(authenticationToken);
            Assert.fail("预期异常没有抛出");
        }  catch (UnknownAccountException ignored) {

        }

        authenticationToken = new UsernamePasswordToken("zhangsan", "zs11");
        try {
            subject.login(authenticationToken);
            Assert.fail("预期异常没有抛出");
        }  catch (IncorrectCredentialsException ignored) {

        }

        authenticationToken = new UsernamePasswordToken("zhangsan", "zs");
        subject.login(authenticationToken);
    }

    @Test
    public void testAuthorization() {
        IniSecurityManagerFactory factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
        Subject subject = SecurityUtils.getSubject();
        AuthenticationToken authenticationToken = new UsernamePasswordToken("zhangsan11", "zs");
        try {
            subject.login(authenticationToken);
            Assert.fail("预期异常没有抛出");
        }  catch (UnknownAccountException ignored) {

        }

        authenticationToken = new UsernamePasswordToken("zhangsan", "zs11");
        try {
            subject.login(authenticationToken);
            Assert.fail("预期异常没有抛出");
        }  catch (IncorrectCredentialsException ignored) {

        }

        authenticationToken = new UsernamePasswordToken("zhangsan", "zs");
        subject.login(authenticationToken);
        Assert.assertFalse(subject.hasRole("role1111"));
        Assert.assertTrue(subject.hasRole("role1"));
        Assert.assertFalse(subject.isPermitted("user:update"));
        Assert.assertTrue(subject.isPermitted("user:delete"));
        try {
            subject.checkPermission("user:update");
            Assert.fail("预期异常没有抛出");
        } catch (AuthorizationException ignored) {
        }
        subject.checkPermission("user:insert");
    }
}
