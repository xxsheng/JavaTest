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
	
	
	@Override
	public List<UserInfo> getUserInfos() {
		// TODO Auto-generated method stub
		return userInfoDao.getUserInfos();
	}

	
	@Override
	public UserInfo getUserInfoById(int id) {
		// TODO Auto-generated method stub
		return userInfoDao.getUserInfoById(id);
	}


	@Override
	public String getValueByRedisKey(String key) {
		// TODO Auto-generated method stub
		return redisDao.get(key);
	}

	@Override
	public long setnx(String key, String value) {
		// TODO Auto-generated method stub
		
		return redisDao.setnx(key, value);
	}

	@Override
	public String setByteKeyValueByRedis(byte[] key, byte[] value) {
		// TODO Auto-generated method stub
		
		return redisDao.setKeyValue(key, value);
	}


	@Override
	public byte[] getByteValueByRedis(byte[] key) {
		// TODO Auto-generated method stub
		return redisDao.getByteValue(key);
	}

}
