package com.xxq.springmvc.test;

import javax.annotation.Resource;

import org.junit.Test;

import com.xxq.springmvc.dao.RedisDao;
import com.xxq.springmvc.dao.daoImpl.RedisDaoImpl;
import com.xxq.springmvc.redisListener.redis.Customer;
import com.xxq.springmvc.redisListener.redis.Producer;

import redis.clients.jedis.JedisPool;

public class TestRedisMessage extends SpringTestCase {
	@Resource
	RedisDao redisDao;
	@Resource
	Customer customer;
	@Resource
	Producer producer;	
	
	@Test
	public void testMessgae() {
		
		
		//Producer producer = new Producer();
		
		Thread t1 = new Thread(customer, "customer");
		Thread t2 = new Thread(producer, "producer");
		
		t1.start();
		t2.start();
	}
	
	@Test
	public void testDao() {
		RedisDaoImpl redis = new RedisDaoImpl();
		System.out.println(redis.jedisPool==null);
	}
}
