/**
 * 
 */
package com.xxq.messgaeborad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xxq.messgaeborad.entity.Message;
import com.xxq.messgaeborad.mapper.MessageMapper;
import com.xxq.messgaeborad.service.MessageService;

/**
 * 	消息处理一类
 * @author Olympus_Pactera
 *
 */
@Service
public class MessageServiceImpl implements MessageService {

	@Autowired
	private MessageMapper messageMapper;
	
	/* (non-Javadoc)
	 * @see com.xxq.messgaeborad.service.MessageService#insertSelective(com.xxq.messgaeborad.entity.Message)
	 */
	@Override
	public int insertSelective(Message record) {
		// TODO Auto-generated method stub
		return 0;
	}

}
