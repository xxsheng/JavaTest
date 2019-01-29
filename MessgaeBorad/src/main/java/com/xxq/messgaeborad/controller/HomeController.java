/**
 * 
 */
package com.xxq.messgaeborad.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xxq.messgaeborad.util.IPUtil;

/**
 * @author xxq_1
 * @Date: 2019/01/29 22:43
 * @Todo:
 *
 */
@Controller
public class HomeController {

	@RequestMapping("/")
	public String toIndex(HttpServletRequest request) {
		String ip = IPUtil.getIpAddr(request);
		
		System.out.println(ip);
		return "index";
	}
	
}
