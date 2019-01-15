/**
 * 
 */
package com.xxq.springmvc.dao.daoImpl;

import javax.annotation.Resource;

import com.xxq.springmvc.dao.RedisDao;
import com.xxq.springmvc.util.JedisUtil;
import com.xxq.springmvc.util.JedisUtil.Strings;

import redis.clients.jedis.JedisPool;

/**
 * @author xxq_1
 *
 */
public class RedisDaoImpl implements RedisDao {

	@Resource
	public JedisPool jedisPool;
	
	public Strings getStringsByRedis() {
		JedisUtil jedisUtil = new JedisUtil(jedisPool);
		JedisUtil.Strings strings = jedisUtil.new Strings();
		return strings;
	}
	
	@Override
	public String get(String key) {
		// TODO Auto-generated method stub
		JedisUtil jedisUtil = new JedisUtil(jedisPool);
		JedisUtil.Strings strings = jedisUtil.new Strings();
		System.out.println("cache:"+ strings.get(key));
		return strings.get(key);
	}

	@Override
	public long setnx(String key, String value) {
		JedisUtil jedisUtil = new JedisUtil(jedisPool);
		JedisUtil.Strings strings = jedisUtil.new Strings();
		return strings.setnx(key, value);
	}

	@Override
	public String setKeyValue(byte[] key, byte[] value) {
		// TODO Auto-generated method stub
		Strings strings = getStringsByRedis();
		
		return strings.set(key, value);
	}

	@Override
	public byte[] getByteValue(byte[] key) {
		// TODO Auto-generated method stub
		Strings strings = getStringsByRedis();
		return strings.get(key);
	}

}
