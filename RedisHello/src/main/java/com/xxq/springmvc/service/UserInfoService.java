/**
 * 
 */
package com.xxq.springmvc.service;

import java.util.List;

import com.xxq.springmvc.model.UserInfo;

/**
 * @author xxq_1
 *
 */
public interface UserInfoService {

	/**
	 * 获取所有的用户 
	 * 
	 * @return
	 */
	public List<UserInfo> getUserInfos();
	
	/**
	 * 通过Id获取单条用户信息
	 * 
	 * @param id
	 * @return
	 */
	public UserInfo getUserInfoById(int id);
	
	/**
	 * 根据redis的key获取redis的一个文本型value
	 * 
	 * @param key
	 * @return
	 */
	public String getValueByRedisKey(String key);
}
