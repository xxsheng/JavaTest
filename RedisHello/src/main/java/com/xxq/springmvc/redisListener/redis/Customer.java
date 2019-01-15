package com.xxq.springmvc.redisListener.redis;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xxq.springmvc.util.JedisUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class Customer implements Runnable {
	
	Jedis jedis;
	
/*	@Autowired
	public Customer(JedisPool jedisPool) {
		System.out.println("customer 构造函数初始化");
		// TODO Auto-generated constructor stub
		jedis = new JedisUtil(jedisPool).getJedis();
		
	}*/
	
	
	public Customer() {
		System.out.println("customer 构造函数初始化");
		// TODO Auto-generated constructor stub
		jedis = new Jedis("127.0.0.1", 6379);
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("---------------");
		while(true) {
			System.out.println("---------------------------------");
			List<String> list= jedis.brpop(1, "redis-test2");
			System.out.println("取出元素："+list);
			if(list != null) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
