package com.warm.share.controller;

import com.warm.share.entity.User;
import com.warm.share.model.BaseModel;
import com.warm.share.model.dto.LoginDto;
import com.warm.share.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {



    @Autowired
    private UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public BaseModel register(@Validated LoginDto loginDto) {
        User user = new User();
        user.setPhone(loginDto.getPhone());
        user.setName(loginDto.getPhone());
        user.setPassword(loginDto.getPassword());
        user.setRegisterTime(System.currentTimeMillis());
        userService.insertUser(user);
        System.out.println("-----------------");
        return BaseModel.success();

    }

    @RequestMapping(value = "/login")
    public Object login(@Validated LoginDto loginDto, BindingResult result) {

        if (!result.hasErrors()) {
            return BaseModel.success(userService.selectUserByPhone(loginDto.getPhone(), loginDto.getPassword()));
        } else {
            return BaseModel.fail(result.getAllErrors().get(0).getDefaultMessage());
        }
    }


}
