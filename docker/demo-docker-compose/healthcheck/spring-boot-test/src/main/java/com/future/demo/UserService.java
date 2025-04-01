package com.future.demo;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.future.demo.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, UserModel> {
}
