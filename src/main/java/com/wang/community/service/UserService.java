package com.wang.community.service;

import com.wang.community.dao.UserMapper;
import com.wang.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findUserById(int id){

        return userMapper.selectById(id);
    }
}
