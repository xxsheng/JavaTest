/**
 * 
 */
package com.xxq.springmvc.redisListener;

import javax.annotation.Resource;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Olympus_Pactera
 *
 */
@Component
public class RedisTestMessageListener implements MessageListener {

	@Resource
	public RedisTemplate<String,Object> redisTemplate;
	
	/* (non-Javadoc)
	 * @see org.springframework.data.redis.connection.MessageListener#onMessage(org.springframework.data.redis.connection.Message, byte[])
	 */
	@Override
	public void onMessage(Message message, byte[] pattern) {
		// TODO Auto-generated method stub

		System.out.println("channel:"+ new String(message.getChannel()) 
				+ ",message:" + new String(message.getBody()));;
	}

}
