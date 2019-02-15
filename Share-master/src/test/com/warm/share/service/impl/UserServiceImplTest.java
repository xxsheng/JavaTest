package com.warm.share.service.impl;

import com.warm.share.entity.User;
import com.warm.share.service.UserService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UserServiceImplTest {

    ApplicationContext context;
    UserService userService;

    @Before
    public void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
        userService = (UserService) context.getBean("userService");

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testInsert() {
        User user = new User();
//        user.setId(1);
//        user.setSex(1);
        user.setPhone("15261116854");
        user.setName("张三2");
        userService.insertUser(user);
        System.out.println(user);
    }

    @Test
    public void selectUserBy() {

        User user = userService.selectUserBy(4);
        System.out.println(user);

    }

}