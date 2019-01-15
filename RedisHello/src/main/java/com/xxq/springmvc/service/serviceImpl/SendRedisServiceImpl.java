/**
 * 
 */
package com.xxq.springmvc.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.xxq.springmvc.service.SendRedisService;

/**
 * @author Olympus_Pactera
 *
 */
@Service("sendRedisService")
public class SendRedisServiceImpl implements SendRedisService {

	@Autowired
	public RedisTemplate<String,String> redisTemplate;
	
	/* (non-Javadoc)
	 * @see com.xxq.springmvc.service.SendRedisService#sendMessage(java.lang.String, java.lang.String)
	 */
	@Override
	public void sendMessage(String channel, String message) {
		// TODO Auto-generated method stub

		System.out.println("开始向"+channel+"发布消息"+message);
		redisTemplate.convertAndSend(channel, message);
		System.out.println("发布成功");
	}

}
