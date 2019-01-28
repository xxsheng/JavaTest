package lottery.domains.content.biz;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserRegistLink;
import lottery.web.WebJSON;

public interface UserRegistLinkService {

	/**
	 * 手机或维码返回二维码
	 * 网页返回域名地址（如https://yf1.yinfengyule.cc 供手机端使用，网页端取当前网址）
     */
	String add(WebJSON json, int userId, int type, String code, double point, String expTime, int amount, Integer deviceType);

	PageList search(int userId, Integer deviceType, int start, int limit);

	boolean delete(int id, int userId);

	UserRegistLink get(String code);
	
}