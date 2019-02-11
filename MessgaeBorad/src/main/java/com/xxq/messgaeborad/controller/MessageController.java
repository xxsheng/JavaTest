/**
 * 
 */
package com.xxq.messgaeborad.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xxq.messgaeborad.entity.Message;
import com.xxq.messgaeborad.service.MessageService;
import com.xxq.messgaeborad.util.IPUtil;
import com.xxq.messgaeborad.util.MailUtil;
import com.xxq.messgaeborad.util.RestUtil;

/**
 * 	处理messgae
 * @author Olympus_Pactera
 * @Date 2019/01/30 2:39
 * @Todo json跨域问题待解决
 */
@RestController
@RequestMapping("/message")
public class MessageController {
 
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
    private MessageService messageService;
 
    @Autowired
    private MailUtil mailUtil;
 
    @PostMapping("/insert")
    public RestUtil insert(HttpServletRequest request, Message message) throws Exception{
        RestUtil restUtil = new RestUtil();
        String IP = IPUtil.getIpAddr(request);
        message.setUserIp(IP);
        //先发邮件在操作数据库
        try {
        	mailUtil.sendEmailToMe(message, IP);
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("邮箱地址错误");
		}
        
        if (messageService.insertSelective(message) > 0) {
            restUtil.setMsg("我会尽快回复您的留言");
            restUtil.setStatus(20000);
 
        } else {
            restUtil.setMsg("未知错误，请联系中边");
            restUtil.setStatus(20001);
            restUtil.setData(message);
        }
        return restUtil;
    }
}