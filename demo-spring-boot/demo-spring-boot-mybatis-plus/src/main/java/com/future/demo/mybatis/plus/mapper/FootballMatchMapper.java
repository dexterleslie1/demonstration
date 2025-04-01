package com.future.demo.mybatis.plus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.future.demo.mybatis.plus.entity.FootballMatch;
import com.future.demo.mybatis.plus.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface FootballMatchMapper extends BaseMapper<FootballMatch> {
}
