/**
 * 
 */
package com.xxq.springmvc.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xxq.springmvc.model.UserInfo;

/**
 * 操作用户消息的dao
 * @author xxq_1
 *
 */
@Repository
public interface UserInfoDao {

	/**
	 * 获取所有的用户
	 * @return
	 */
	public List<UserInfo> getUserInfos();
	
	/**
	 * 根据用户id获取用户消息
	 * @param id
	 * @return
	 */
	public UserInfo getUserInfoById(int id);
}
