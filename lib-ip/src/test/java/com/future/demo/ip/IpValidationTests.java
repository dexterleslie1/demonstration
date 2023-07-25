package com.future.demo.ip;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.junit.Assert;
import org.junit.Test;

public class IpValidationTests {
    @Test
    public void test() {
        // https://stackoverflow.com/questions/5667371/validate-ipv4-address-in-java
        String ip = "123";
        boolean b = InetAddressValidator.getInstance().isValid(ip);
        Assert.assertFalse(b);

        ip = "192.168.1.1";
        b = InetAddressValidator.getInstance().isValid(ip);
        Assert.assertTrue(b);

        // 判断ip类型ipv4、ipv6
        ip = "192.168.1.55";
        Assert.assertTrue(InetAddressValidator.getInstance().isValidInet4Address(ip));
        Assert.assertFalse(InetAddressValidator.getInstance().isValidInet6Address(ip));

        ip = "240e:47c:46f0:1170:8d5c:a746:3a0e:be43";
        Assert.assertFalse(InetAddressValidator.getInstance().isValidInet4Address(ip));
        Assert.assertTrue(InetAddressValidator.getInstance().isValidInet6Address(ip));
    }
}
