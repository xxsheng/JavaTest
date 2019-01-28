package admin.web.helper.cookie;

import admin.web.WSC;
import javautils.encrypt.DESUtil;
import javautils.http.CookieUtil;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CookieManager {
	
	public static void cleanCookie(final HttpServletRequest request, final HttpServletResponse response, final String[] cookieKeys) {
		for (String cookieKey : cookieKeys) {
			CookieUtil.getInstance().cleanCookie(request, response, cookieKey);
		}
	}
	
	public static void cleanCookie(final HttpServletRequest request, final HttpServletResponse response, final String cookieKey) {
		CookieUtil.getInstance().cleanCookie(request, response, cookieKey);
	}

	public static void cleanUserCookie(final HttpServletRequest request, final HttpServletResponse response) {
		cleanCookie(request, response, WSC.COOKIE_USER_STOKEN);
		cleanCookie(request, response, WSC.COOKIE_USER_SAVEUSER);
		cleanCookie(request, response, WSC.COOKIE_USER_PTOKEN);
	}

	public static void setCurrentUser(final HttpServletResponse response, final CookieUser cookieUser) {
		// String stoken = OrderUtil.createString(32); // 生成随机密钥
		String stoken = ObjectId.get().toString(); // 生成随机密钥
		String desEmial = DESUtil.getInstance().encryptStr(cookieUser.getUsername(), stoken); // 加密用户名
		String desPassword = DESUtil.getInstance().encryptStr(cookieUser.getPassword(), stoken); // 加密用户密码
		CookieUtil.getInstance().addCookie(response, WSC.COOKIE_USER_STOKEN, stoken, WSC.COOKIE_USER_TIMEOUT);
		CookieUtil.getInstance().addCookie(response, WSC.COOKIE_USER_SAVEUSER, desEmial, WSC.COOKIE_USER_TIMEOUT);
		CookieUtil.getInstance().addCookie(response, WSC.COOKIE_USER_PTOKEN, desPassword, WSC.COOKIE_USER_TIMEOUT);
	}

	public static CookieUser getCurrentUser(final HttpServletRequest request) {
		Cookie cookie_stoken = CookieUtil.getInstance().getCookieByName(request, WSC.COOKIE_USER_STOKEN);
		Cookie cookie_saveuser = CookieUtil.getInstance().getCookieByName(request, WSC.COOKIE_USER_SAVEUSER);
		Cookie cookie_ptoken = CookieUtil.getInstance().getCookieByName(request, WSC.COOKIE_USER_PTOKEN);
		
		if(cookie_stoken != null && cookie_saveuser != null && cookie_ptoken != null) {
			String stoken = cookie_stoken.getValue(); // 获取Cookie密钥
			String desUsername = cookie_saveuser.getValue(); // 加密的用户名
			String desPassword = cookie_ptoken.getValue(); // 加密用户密码
			String username = DESUtil.getInstance().decryptStr(desUsername, stoken); // 解密用户名
			String password = DESUtil.getInstance().decryptStr(desPassword, stoken); // 解密密码
			
			CookieUser cookieUser = new CookieUser();
			cookieUser.setUsername(username);
			cookieUser.setPassword(password);
			return cookieUser;
		}
		return null;
	}
}
