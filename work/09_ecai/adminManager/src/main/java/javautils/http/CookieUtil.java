package javautils.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Http Cookie
 */
public class CookieUtil {
	
	protected static final Logger logger = LoggerFactory.getLogger(CookieUtil.class);
	
	private static CookieUtil instance;
	
	private CookieUtil(){
		
	}
	
	private static synchronized void synInit() {
		if(instance == null) {
			instance = new CookieUtil();
		}
	}
	
	public static CookieUtil getInstance(){
		if(instance == null) {
			synInit();
		}
		return instance;
	}
	
	/**
	 * 设置cookie
	 * 
	 * @param response
	 * @param name
	 *            cookie名字
	 * @param value
	 *            cookie值
	 * @param maxAge
	 *            cookie生命周期 以秒为单位
	 */
	public void addCookie(HttpServletResponse response, String name,
			String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		if (maxAge > 0) {
			cookie.setMaxAge(maxAge);
		}
		response.addCookie(cookie);
		logger.debug(name + "：Cookie已经设置");
	}
	
	/**
	 * 立刻使cookie失效
	 * @param request
	 * @param name
	 */
	public void cleanCookie(HttpServletRequest request, HttpServletResponse response, String name) {
		Cookie cookie = getCookieByName(request, name);
		if(cookie != null) {
			cookie.setMaxAge(0);
			response.addCookie(cookie);
			logger.debug(name + "：Cookie已经删除");
		} else {
			logger.debug(name + "：Cookie为空");
		}
	}

	/**
	 * 根据名字获取cookie
	 * 
	 * @param request
	 * @param name
	 *            cookie名字
	 * @return
	 */
	public Cookie getCookieByName(HttpServletRequest request, String name) {
		Map<String, Cookie> cookieMap = ReadCookieMap(request);
		if (cookieMap.containsKey(name)) {
			Cookie cookie = (Cookie) cookieMap.get(name);
			return cookie;
		}
		return null;
	}
	
	/**
	 * 将cookie封装到Map里面
	 * 
	 * @param request
	 * @return
	 */
	public Map<String, Cookie> ReadCookieMap(HttpServletRequest request) {
		Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				cookieMap.put(cookie.getName(), cookie);
			}
		}
		return cookieMap;
	}
}
