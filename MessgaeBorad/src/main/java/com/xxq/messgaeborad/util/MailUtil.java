/**
 * 
 */
package com.xxq.messgaeborad.util;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.xxq.messgaeborad.entity.Message;

/**
 * mail class
 * 
 * @author Olympus_Pactera
 *
 */
@Component
public class MailUtil {

	@Autowired
	private JavaMailSender javaMailSender;

	/**
	 * @author xiaxiuqiang
	 * @Date 2019/02/11
	 * @todo 给管理员的邮箱发送邮件
	 * @param message
	 * @param IP
	 */
	public void sendEmailToMe(Message message, String IP) {
		MimeMessage mimeMessage = null;
		try {
			mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setFrom("1558281773@qq.com");
			helper.setTo("1558281773@qq.com");
			String subject = "来自" + message.getFullName() + "的留言";
			helper.setSubject(subject);
			String sb = MailUtil.toHtml2(message, IP);
			helper.setText(sb, true);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		javaMailSender.send(mimeMessage);
	}

	public static String toHtml2(Message message, String IP) {
		// TODO Auto-generated method stub
		String subject = message.getSubject();
		String name = message.getFullName();
		String msg = message.getMessage();
		String from = message.getEmailAddress();
		String phone = message.getPhoneNumber();
		StringBuffer sb = new StringBuffer();
		sb.append("<h3 style='color: #d9001f'>您有新的留言：</h3>");
		sb.append("<p><strong>访客: </strong>" + name + "</p>");
		sb.append("<p><strong>留言主题: </strong>" + subject + "</p>");
		sb.append("<p><strong>他的电话: </strong>" + phone + "</p>");
		sb.append("<p><strong>他的邮箱: </strong>" + from + "</p>");
		sb.append("<p><strong>他的IP: </strong>" + IP + "</p>");
		sb.append("<p><strong>正文: </strong>" + msg + "</p>");
		return sb.toString();
	}
}
