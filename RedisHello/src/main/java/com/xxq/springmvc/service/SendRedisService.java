/**
 * 
 */
package com.xxq.springmvc.service;

/**
 * @author Olympus_Pactera
 *
 */
public interface SendRedisService {

	void sendMessage(String channel, String message);
}
