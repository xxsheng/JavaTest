/**
 * 
 */
package com.xxq.messgaeborad.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xxq.messgaeborad.entity.Message;
import com.xxq.messgaeborad.service.MessageService;
import com.xxq.messgaeborad.util.RestUtil;

/**
 * 	处理messgae
 * @author Olympus_Pactera
 *
 */
@RestController
@RequestMapping("/message")
public class MessageController {

	@Autowired
	private MessageService messageService;
	
	@PostMapping
	public RestUtil insert(HttpServletRequest request, Message message)throws Exception {
		RestUtil restUtil = new RestUtil();
		
		return null;
	}
}
