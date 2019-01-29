/**
 * 
 */
package com.xxq.messgaeborad.service;

import com.xxq.messgaeborad.entity.Message;

/**
 * 	消息处理中心
 * @author Olympus_Pactera
 *
 */
public interface MessageService {

	int insertSelective(Message record);
}
