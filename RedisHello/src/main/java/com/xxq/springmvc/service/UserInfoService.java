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
	
	/**
	 * 通过redis存入key和value 
	 * @param key
	 * @param value
	 * @return
	 */
	public long setnx(String key, String value);
	
	/**
	 * 存入redis对象key-value
	 * @param key
	 * @param value
	 * @return
	 */
	public String setByteKeyValueByRedis(byte[] key, byte[] value);
	
	/**
	 * 通过key取得byte数组value
	 * @param key
	 * @return
	 */
	public byte[] getByteValueByRedis(byte[] key);
}
