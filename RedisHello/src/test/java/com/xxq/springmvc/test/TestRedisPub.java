package com.xxq.springmvc.test;

import javax.annotation.Resource;

import org.junit.Test;

import com.xxq.springmvc.service.SendRedisService;

public class TestRedisPub extends SpringTestCase {

	@Resource
	SendRedisService sendRedisService;
	
	@Test
	public void testRedisPub() {
		sendRedisService.sendMessage("channel1", "订阅发布");
	}
}
