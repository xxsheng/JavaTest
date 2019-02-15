package com.warm.share.service;

import com.warm.share.entity.User;

public interface UserService {

    boolean insertUser(User user);

    User selectUserBy(long id);

    User selectUserByPhone(String phone,String password);


}
