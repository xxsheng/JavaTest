/**
 * 
 */
package com.xxq.springmvc.controller;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.xxq.springmvc.model.UserInfo;
import com.xxq.springmvc.service.UserInfoService;

/**
 * @author Olympus_Pactera
 *
 */
@Controller
public class UserInfoController {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource
	UserInfoService userInfoService;
	
	@RequestMapping("/userInfo")
	public ModelAndView getUserInfoById(int id) {
		//指定view层的页面，参数"user"即为对应的jsp的名称
		ModelAndView modelAndView = new ModelAndView("user");
		if(id<0) {
			id=0;
		}
		UserInfo userInfo = userInfoService.getUserInfoById(id);
		modelAndView.addObject("user", userInfo);
		
		return modelAndView;
	}
	
	@RequestMapping("/getuserInfos")
	@ResponseBody
	public List<UserInfo> getUserInfos(){
		List<UserInfo> userList = userInfoService.getUserInfos();
		logger.info(JSON.toJSONString(userList));
		return userList;
	}
}
