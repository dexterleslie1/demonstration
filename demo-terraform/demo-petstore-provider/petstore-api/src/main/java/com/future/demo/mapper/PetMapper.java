package com.future.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.future.demo.PetModel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface PetMapper extends BaseMapper<PetModel> {

}
