package com.future.demo;

import org.junit.Assert;
import org.junit.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        // 测试List<UserMode>转换为List<UserVo>对象
        List<UserModel> userModelList = new ArrayList<>();
        userModelList.add(userModel);
        List<UserVo> userVoList = userModelList.stream().map(o-> modelMapper.map(o, UserVo.class)).collect(Collectors.toList());
        Assert.assertEquals(1, userVoList.size());
        Assert.assertEquals(userModel.getId(), userVoList.get(0).getId());
        Assert.assertEquals(userModel.getName(), userVoList.get(0).getName());
    }
}
