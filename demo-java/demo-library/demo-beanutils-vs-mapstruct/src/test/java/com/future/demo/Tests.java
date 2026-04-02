package com.future.demo;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;

public class Tests {

    @Test
    public void testSpringBeanUtilsCopyProperties() {
        SaleCustomerOrderFhslResponseVo saleCustomerOrderFhslResponseVo = new SaleCustomerOrderFhslResponseVo();
        saleCustomerOrderFhslResponseVo.setSaleCustomerOrderGoodsId(1001L);
        saleCustomerOrderFhslResponseVo.setFhsl(1.1);
        saleCustomerOrderFhslResponseVo.setFhps(2);
        saleCustomerOrderFhslResponseVo.setIsJd(3);
        saleCustomerOrderFhslResponseVo.setJdSj("jdSj");
        saleCustomerOrderFhslResponseVo.setJdrId(4004L);
        saleCustomerOrderFhslResponseVo.setThsl(5.5);
        saleCustomerOrderFhslResponseVo.setThps(6);
        saleCustomerOrderFhslResponseVo.setWfsl(7.7);
        saleCustomerOrderFhslResponseVo.setWfps(8);
        saleCustomerOrderFhslResponseVo.setYfPs(9);
        saleCustomerOrderFhslResponseVo.setYfSl(10.1);
        saleCustomerOrderFhslResponseVo.setYfps(11);
        saleCustomerOrderFhslResponseVo.setYfsl(12.2);
        saleCustomerOrderFhslResponseVo.setDjFhsl(13.3);
        saleCustomerOrderFhslResponseVo.setDjFhps(14);
        saleCustomerOrderFhslResponseVo.setJd("jd");
        saleCustomerOrderFhslResponseVo.setWphPs(15);
        saleCustomerOrderFhslResponseVo.setWphSl(16.6);
        saleCustomerOrderFhslResponseVo.setJxzJd("jxzJd");
        saleCustomerOrderFhslResponseVo.setWcl(17.7);

        SaleCustomerOrderInfoForListVo saleCustomerOrderInfoForListVo = new SaleCustomerOrderInfoForListVo();
        BeanUtils.copyProperties(saleCustomerOrderFhslResponseVo, saleCustomerOrderInfoForListVo);

        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getFhsl(), saleCustomerOrderInfoForListVo.getFhsl());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getFhps(), saleCustomerOrderInfoForListVo.getFhps());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getIsJd(), saleCustomerOrderInfoForListVo.getIsJd());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getJdSj(), saleCustomerOrderInfoForListVo.getJdSj());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getJdrId(), saleCustomerOrderInfoForListVo.getJdrId());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getThsl(), saleCustomerOrderInfoForListVo.getThsl());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getThps(), saleCustomerOrderInfoForListVo.getThps());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getWfsl(), saleCustomerOrderInfoForListVo.getWfsl());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getWfps(), saleCustomerOrderInfoForListVo.getWfps());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getYfPs(), saleCustomerOrderInfoForListVo.getYfPs());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getYfSl(), saleCustomerOrderInfoForListVo.getYfSl());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getYfps(), saleCustomerOrderInfoForListVo.getYfps());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getYfsl(), saleCustomerOrderInfoForListVo.getYfsl());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getDjFhsl(), saleCustomerOrderInfoForListVo.getDjFhsl());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getDjFhps(), saleCustomerOrderInfoForListVo.getDjFhps());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getJd(), saleCustomerOrderInfoForListVo.getJd());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getWphPs(), saleCustomerOrderInfoForListVo.getWphPs());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getWphSl(), saleCustomerOrderInfoForListVo.getWphSl());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getJxzJd(), saleCustomerOrderInfoForListVo.getJxzJd());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getWcl(), saleCustomerOrderInfoForListVo.getWcl());
    }

    @Test
    public void testApacheCommonsBeanUtilsCopyProperties() throws InvocationTargetException, IllegalAccessException {
        SaleCustomerOrderFhslResponseVo saleCustomerOrderFhslResponseVo = new SaleCustomerOrderFhslResponseVo();
        saleCustomerOrderFhslResponseVo.setSaleCustomerOrderGoodsId(1001L);
        saleCustomerOrderFhslResponseVo.setFhsl(1.1);
        saleCustomerOrderFhslResponseVo.setFhps(2);
        saleCustomerOrderFhslResponseVo.setIsJd(3);
        saleCustomerOrderFhslResponseVo.setJdSj("jdSj");
        saleCustomerOrderFhslResponseVo.setJdrId(4004L);
        saleCustomerOrderFhslResponseVo.setThsl(5.5);
        saleCustomerOrderFhslResponseVo.setThps(6);
        saleCustomerOrderFhslResponseVo.setWfsl(7.7);
        saleCustomerOrderFhslResponseVo.setWfps(8);
        saleCustomerOrderFhslResponseVo.setYfPs(9);
        saleCustomerOrderFhslResponseVo.setYfSl(10.1);
        saleCustomerOrderFhslResponseVo.setYfps(11);
        saleCustomerOrderFhslResponseVo.setYfsl(12.2);
        saleCustomerOrderFhslResponseVo.setDjFhsl(13.3);
        saleCustomerOrderFhslResponseVo.setDjFhps(14);
        saleCustomerOrderFhslResponseVo.setJd("jd");
        saleCustomerOrderFhslResponseVo.setWphPs(15);
        saleCustomerOrderFhslResponseVo.setWphSl(16.6);
        saleCustomerOrderFhslResponseVo.setJxzJd("jxzJd");
        saleCustomerOrderFhslResponseVo.setWcl(17.7);

        SaleCustomerOrderInfoForListVo saleCustomerOrderInfoForListVo = new SaleCustomerOrderInfoForListVo();
        org.apache.commons.beanutils.BeanUtils.copyProperties(saleCustomerOrderInfoForListVo, saleCustomerOrderFhslResponseVo);

        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getFhsl(), saleCustomerOrderInfoForListVo.getFhsl());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getFhps(), saleCustomerOrderInfoForListVo.getFhps());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getIsJd(), saleCustomerOrderInfoForListVo.getIsJd());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getJdSj(), saleCustomerOrderInfoForListVo.getJdSj());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getJdrId(), saleCustomerOrderInfoForListVo.getJdrId());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getThsl(), saleCustomerOrderInfoForListVo.getThsl());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getThps(), saleCustomerOrderInfoForListVo.getThps());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getWfsl(), saleCustomerOrderInfoForListVo.getWfsl());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getWfps(), saleCustomerOrderInfoForListVo.getWfps());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getYfPs(), saleCustomerOrderInfoForListVo.getYfPs());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getYfSl(), saleCustomerOrderInfoForListVo.getYfSl());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getYfps(), saleCustomerOrderInfoForListVo.getYfps());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getYfsl(), saleCustomerOrderInfoForListVo.getYfsl());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getDjFhsl(), saleCustomerOrderInfoForListVo.getDjFhsl());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getDjFhps(), saleCustomerOrderInfoForListVo.getDjFhps());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getJd(), saleCustomerOrderInfoForListVo.getJd());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getWphPs(), saleCustomerOrderInfoForListVo.getWphPs());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getWphSl(), saleCustomerOrderInfoForListVo.getWphSl());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getJxzJd(), saleCustomerOrderInfoForListVo.getJxzJd());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getWcl(), saleCustomerOrderInfoForListVo.getWcl());
    }

    @Test
    public void testMapStruct() {
        SaleCustomerOrderFhslResponseVo saleCustomerOrderFhslResponseVo = new SaleCustomerOrderFhslResponseVo();
        saleCustomerOrderFhslResponseVo.setSaleCustomerOrderGoodsId(1001L);
        saleCustomerOrderFhslResponseVo.setFhsl(1.1);
        saleCustomerOrderFhslResponseVo.setFhps(2);
        saleCustomerOrderFhslResponseVo.setIsJd(3);
        saleCustomerOrderFhslResponseVo.setJdSj("jdSj");
        saleCustomerOrderFhslResponseVo.setJdrId(4004L);
        saleCustomerOrderFhslResponseVo.setThsl(5.5);
        saleCustomerOrderFhslResponseVo.setThps(6);
        saleCustomerOrderFhslResponseVo.setWfsl(7.7);
        saleCustomerOrderFhslResponseVo.setWfps(8);
        saleCustomerOrderFhslResponseVo.setYfPs(9);
        saleCustomerOrderFhslResponseVo.setYfSl(10.1);
        saleCustomerOrderFhslResponseVo.setYfps(11);
        saleCustomerOrderFhslResponseVo.setYfsl(12.2);
        saleCustomerOrderFhslResponseVo.setDjFhsl(13.3);
        saleCustomerOrderFhslResponseVo.setDjFhps(14);
        saleCustomerOrderFhslResponseVo.setJd("jd");
        saleCustomerOrderFhslResponseVo.setWphPs(15);
        saleCustomerOrderFhslResponseVo.setWphSl(16.6);
        saleCustomerOrderFhslResponseVo.setJxzJd("jxzJd");
        saleCustomerOrderFhslResponseVo.setWcl(17.7);

        SaleCustomerOrderInfoForListVo saleCustomerOrderInfoForListVo = new SaleCustomerOrderInfoForListVo();
        SaleCustomerOrderFhslToListMapper.INSTANCE.copy(saleCustomerOrderFhslResponseVo, saleCustomerOrderInfoForListVo);

        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getFhsl(), saleCustomerOrderInfoForListVo.getFhsl());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getFhps(), saleCustomerOrderInfoForListVo.getFhps());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getIsJd(), saleCustomerOrderInfoForListVo.getIsJd());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getJdSj(), saleCustomerOrderInfoForListVo.getJdSj());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getJdrId(), saleCustomerOrderInfoForListVo.getJdrId());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getThsl(), saleCustomerOrderInfoForListVo.getThsl());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getThps(), saleCustomerOrderInfoForListVo.getThps());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getWfsl(), saleCustomerOrderInfoForListVo.getWfsl());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getWfps(), saleCustomerOrderInfoForListVo.getWfps());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getYfPs(), saleCustomerOrderInfoForListVo.getYfPs());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getYfSl(), saleCustomerOrderInfoForListVo.getYfSl());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getYfps(), saleCustomerOrderInfoForListVo.getYfps());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getYfsl(), saleCustomerOrderInfoForListVo.getYfsl());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getDjFhsl(), saleCustomerOrderInfoForListVo.getDjFhsl());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getDjFhps(), saleCustomerOrderInfoForListVo.getDjFhps());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getJd(), saleCustomerOrderInfoForListVo.getJd());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getWphPs(), saleCustomerOrderInfoForListVo.getWphPs());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getWphSl(), saleCustomerOrderInfoForListVo.getWphSl());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getJxzJd(), saleCustomerOrderInfoForListVo.getJxzJd());
        Assert.assertEquals(saleCustomerOrderFhslResponseVo.getWcl(), saleCustomerOrderInfoForListVo.getWcl());
    }
}
