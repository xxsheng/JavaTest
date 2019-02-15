package com.warm.share.service.impl;

import com.warm.share.entity.User;
import com.warm.share.exception.BusinessException;
import com.warm.share.mapper.User2Mapper;
import com.warm.share.mapper.gmapper.UserMapper;
import com.warm.share.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private User2Mapper user2Mapper;

    @Override
    public boolean insertUser(User user) {
        if (userMapper.insertSelective(user) == 1) {
            return true;
        } else {
            throw new BusinessException("2001&&注册失败");
        }
    }

    @Override
    public User selectUserBy(long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public User selectUserByPhone(String phone, String password) {
        return user2Mapper.selectUserByPhone(phone, password);
    }
}
