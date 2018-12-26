/**
 * 
 */
package com.xxq.springmvc.service.serviceImpl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xxq.springmvc.dao.RedisDao;
import com.xxq.springmvc.dao.UserInfoDao;
import com.xxq.springmvc.model.UserInfo;
import com.xxq.springmvc.service.UserInfoService;

/**
 * @author xxq_1
 *
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {

	@Resource
	UserInfoDao userInfoDao;
	
	@Resource
	RedisDao redisDao;
	
	/* (non-Javadoc)
	 * @see com.xxq.springmvc.service.UserInfoService#getUserInfos()
	 */
	@Override
	public List<UserInfo> getUserInfos() {
		// TODO Auto-generated method stub
		return userInfoDao.getUserInfos();
	}

	/* (non-Javadoc)
	 * @see com.xxq.springmvc.service.UserInfoService#getUserInfoById(int)
	 */
	@Override
	public UserInfo getUserInfoById(int id) {
		// TODO Auto-generated method stub
		return userInfoDao.getUserInfoById(id);
	}

	/* (non-Javadoc)
	 * @see com.xxq.springmvc.service.UserInfoService#getValueByRedisKey(java.lang.String)
	 */
	@Override
	public String getValueByRedisKey(String key) {
		// TODO Auto-generated method stub
		return redisDao.get(key);
	}

}
