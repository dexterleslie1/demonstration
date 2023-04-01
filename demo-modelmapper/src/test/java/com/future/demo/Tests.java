package com.future.demo;

import org.junit.Assert;
import org.junit.Test;
import org.modelmapper.ModelMapper;

public class Tests {
    @Test
    public void test() {
        ModelMapper modelMapper = new ModelMapper();
        UserModel userModel = new UserModel();
        userModel.setId(1L);
        userModel.setName("user1");
        UserVo userVo = modelMapper.map(userModel, UserVo.class);
        Assert.assertEquals(userModel.getId(), userVo.getId());
        Assert.assertEquals(userModel.getName(), userVo.getName());
    }
}
