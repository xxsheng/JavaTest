package javautils.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SpringMailUtil {

	private static final Logger logger = LoggerFactory.getLogger(SpringMailUtil.class);
	
	private String username;
	private String personal;
	private String password;
	private String host;
	private String defaultEncoding = "UTF-8";

	public SpringMailUtil(String username, String personal, String password, String host) {
		this.username = username;
		this.personal = personal;
		this.password = password;
		this.host = host;
	}

	public boolean send(String to, String subject, String htmlText) {
		return sendInternal(to, subject, htmlText);
	}

	public boolean sendInternal(String to, String subject, String htmlText) {
		try {
			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			mailSender.setDefaultEncoding(defaultEncoding);
			mailSender.setHost(host);
			mailSender.setUsername(username);
			mailSender.setPassword(password);
			Properties properties = new Properties();
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.user", username);
			properties.put("mail.smtp.password", password);
			properties.put("mail.smtp.port", "587");
			properties.put("mail.smtp.auth", "true");
			mailSender.setJavaMailProperties(properties);

			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
			messageHelper.setTo(to);
			messageHelper.setFrom(username, personal);
			messageHelper.setSubject(subject);
			messageHelper.setText(htmlText, true);
			mailSender.send(mimeMessage);
			return true;
		} catch (Exception e) {
			logger.error("发送邮件失败！", e);
		}
		return false;
	}
}