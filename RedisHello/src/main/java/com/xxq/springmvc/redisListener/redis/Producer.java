package com.xxq.springmvc.redisListener.redis;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xxq.springmvc.util.JedisUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class Producer implements Runnable {

	
	Jedis jedis = null;
	
/*	@Autowired
	public Producer(JedisPool jedisPool) {
		// TODO Auto-generated constructor stub
		jedis = new JedisUtil(jedisPool).getJedis();
	}*/
	
	
	public Producer() {
		// TODO Auto-generated constructor stub
		jedis = new Jedis("127.0.0.1", 6379);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub

		while(true) {
			String temp = String.valueOf(((int)(Math.random()*1000)));
			System.out.println(Math.random());
			jedis.lpush("redis-test2", temp);
			System.out.println("往队列插入"+temp);
			
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("生产者产生物品失败");
				e.printStackTrace();
			}
		}
		
		
	}
	
	public static void main(String[] args) {
		Customer customer = new Customer();
		Producer producer = new Producer();
		Thread t1 = new Thread(customer, "customer");
		Thread t2 = new Thread(producer, "producer");
		
		t1.start();
		t2.start();
	}

}
