/**
 * 
 */
package com.xxq.springmvc.dao.daoImpl;

import javax.annotation.Resource;

import com.xxq.springmvc.dao.RedisDao;
import com.xxq.springmvc.util.JedisUtil;

import redis.clients.jedis.JedisPool;

/**
 * @author xxq_1
 *
 */
public class RedisDaoImpl implements RedisDao {

	@Resource
	JedisPool jedisPool;
	
	/* (non-Javadoc)
	 * @see com.xxq.springmvc.dao.RedisDao#get(java.lang.String)
	 */
	@Override
	public String get(String key) {
		// TODO Auto-generated method stub
		JedisUtil jedisUtil = new JedisUtil(jedisPool);
		JedisUtil.Strings strings = jedisUtil.new Strings();
		System.out.println("cache:"+ strings.get(key));
		return strings.get(key);
	}

}
