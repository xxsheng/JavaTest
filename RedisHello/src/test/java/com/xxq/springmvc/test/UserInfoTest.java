/**
 * 
 */
package com.xxq.springmvc.test;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.xxq.springmvc.model.UserInfo;
import com.xxq.springmvc.service.UserInfoService;
import com.xxq.springmvc.util.SerializeUtil;

/**
 * @author Olympus_Pactera
 *
 */
public class UserInfoTest extends SpringTestCase {

	@Resource
	UserInfoService userInfoService;
	
	@Test
	public void selectUserByIdTest() {
		UserInfo userInfo = userInfoService.getUserInfoById(1);
		logger.debug("查找结果:"+userInfo);
	}
	
	@Test
	public void selectUserInfos() {
		List<UserInfo> userInfoList = userInfoService.getUserInfos();
		logger.debug("查找结果："+JSON.toJSONString(userInfoList));
	}
	
	@Test
	public void getValueByRedisKey() {
		String value = userInfoService.getValueByRedisKey("name1");
		logger.debug("查找结果："+value);
	}
	
	@Test
	public void setKeyValueByRedis() {
		long tmp = userInfoService.setnx("zhang", "zhang");
		System.out.println(tmp);
		logger.debug("插入结果：" + (tmp == 1 ? "插入成功":"插入失败") );
	}
	
	@Test
	public void setByteKeyValueByRedis() {
		UserInfo userInfo = userInfoService.getUserInfoById(2);
		
		byte [] userInfobytes = SerializeUtil.seriaJlize(userInfo);
		System.out.println(userInfobytes.length);
		String result = userInfoService.setByteKeyValueByRedis("user3".getBytes(), userInfobytes);
		
		System.out.println(result);
		logger.debug(result.equals("OK") ? "插入成功":"插入失败");
	}
	
	@Test
	public void getByteValueByRedis() {
		byte [] userInfoByte = userInfoService.getByteValueByRedis("user3".getBytes());
		System.out.println(userInfoByte.length);
		System.out.println(userInfoByte[142]);
		UserInfo userInfo = (UserInfo) SerializeUtil.deserialize(userInfoByte);
		System.out.println(userInfo.toString());
	}
	
	@Test
	public void seyByteKeyValueByRedisTest() {
		
		String result = userInfoService.setByteKeyValueByRedis("xiaxiuqiang".getBytes(), "xiaxiansheng".getBytes());
		
		System.out.println(result);
		logger.debug(result.equals("OK") ? "插入成功":"插入失败");
	}
	
	@Test
	public void setListObjectByRedisTest() {
		
	}
}
