package lottery.web.helper;

import javautils.encrypt.TokenUtil;
import javautils.http.HttpUtil;
import javautils.redis.JedisTemplate;
import lottery.domains.content.biz.UserService;
import lottery.domains.content.entity.User;
import lottery.domains.pool.DataFactory;
import lottery.web.WSC;
import lottery.web.WebJSON;
import lottery.web.helper.session.SessionUser;
import lottery.web.helper.speed.AccessHistory;
import lottery.web.helper.speed.AccessTime;
import lottery.web.websocket.WebSocketInfo;
import lottery.web.websocket.WebSocketSessionUserHolder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

public abstract class AbstractActionController {
	private static final Logger logger = LoggerFactory.getLogger(AbstractActionController.class);
	
	@Autowired
	private UserService uService;

	@Autowired
	private JedisTemplate jedisTemplate;

	@Autowired
	private DataFactory dataFactory;

	private String getUserLogoutMsg(String sessionId) {
		String key = WSC.USER_LOGOUT_MSG_KEY + sessionId;
		return jedisTemplate.get(key);
	}
	private void setUserLogoutMsg(String sessionId, String msgId) {
		String key = WSC.USER_LOGOUT_MSG_KEY + sessionId;
		// 存储一个1小时有效期的登出消息
		jedisTemplate.setex(key, msgId, 60 * 60 * 1);
	}
	private void delUserLogoutMsg(String sessionId) {
		String key = WSC.USER_LOGOUT_MSG_KEY + sessionId;
		jedisTemplate.del(key); // 删除登出消息
	}

	/**
	 * 获取当前Session用户，如果返回null表示需要登出，登出消息在json里面
	 */
	protected SessionUser getSessionUser(WebJSON json, final HttpSession session, final HttpServletRequest request) {
		SessionUser sessionUser = (SessionUser) session.getAttribute(WSC.HTTP_SESSION_USER);
		if (sessionUser == null) {
			json.set(2, "2-1006"); // 您还没有登录
			return null;
		}

		// 查看本次会话是否有需要登出的消息
		String msgId = getUserLogoutMsg(sessionUser.getSessionId());
		if (StringUtils.isNotEmpty(msgId)) {
			json.set(2, msgId); // 由后台或其它程序设置的登出消息
			delUserLogoutMsg(sessionUser.getSessionId()); // 删除登出消息
			logOut(session, request);
			return null;
		}

		return sessionUser;
	}

	protected final void setLoginValidate(final HttpSession session, User uEntity, int cardId) {
		session.setAttribute("loginValidate", "true");
		session.setAttribute("loginValidateUser", uEntity);
		session.setAttribute("loginValidateCardId", cardId);
	}

	protected final boolean isSetLoginValidate(final HttpSession session) {
		Object loginValidate = session.getAttribute("loginValidate");
		if (loginValidate == null || !"true".equals(loginValidate.toString())) {
			return false;
		}

		Object loginValidateUser = session.getAttribute("loginValidateUser");
		if (loginValidate == null || !(loginValidateUser instanceof User)) {
			return false;
		}

		Object loginValidateCardId = session.getAttribute("loginValidateCardId");
		if (loginValidateCardId == null) {
			return false;
		}

		return true;
	}

	protected final User getLoginValidateUser(final HttpSession session) {
		Object loginValidateUser = session.getAttribute("loginValidateUser");
		return (User)loginValidateUser;
	}

	protected final int getLoginValidateCardId(final HttpSession session) {
		Object loginValidateUser = session.getAttribute("loginValidateCardId");
		return Integer.valueOf(loginValidateUser.toString());
	}

	protected final void removeLoginValidate(final HttpSession session) {
		session.removeAttribute("loginValidate");
		session.removeAttribute("loginValidateUser");
		session.removeAttribute("loginValidateCardId");
	}

	/**
	 * 保存Session用户
	 * @param session
	 */
	protected final void setSessionUser(final HttpSession session, User bean) {
		String sessionId = HttpUtil.formatSessionId(session);
		uService.setOnline(bean.getUsername(), sessionId);
		SessionUser sessionUser = new SessionUser(bean.getId(), bean.getUsername(), bean.getNickname(), bean.getType(), bean.getUpid(), bean.getUpids(), bean.getRegistTime(), sessionId);
		session.setAttribute(WSC.HTTP_SESSION_USER, sessionUser);

		// 如果用户之前的session id不为空，则表示在别处登录，在redis中存储登出消息
		if (StringUtils.isNotEmpty(bean.getSessionId())) {
			setUserLogoutMsg(bean.getSessionId(), "2-3"); // 您账号在其他地方登录，请您重新登录！
		}

		// // 更新socket在线连接
		// socket.updateUser(sessionUser);
	}
	
	// /**
	//  * 获取当前用户
	//  * @param session
	//  * @param request
	//  * @return
	//  */
	// protected final User getCurrUser(final HttpSession session, final HttpServletRequest request) {
	// 	try {
	// 		User uEntity = getSessionUser(session, request);
	// 		if(uEntity != null) {
	// 			return uEntity;
	// 		}
	// 	} catch (Exception e) {
	// 		logger.error("从session获取用户失败", e);
	// 	}
	// 	return null;
	// }
	
	/**
	 * 用户退出
	 */
	protected final void logOut(final HttpSession session, final HttpServletRequest request) {
		SessionUser sessionUser = (SessionUser) session.getAttribute(WSC.HTTP_SESSION_USER);
		if(sessionUser != null) {
			// socket.removeUser(sessionUser);
			uService.setOffline(sessionUser.getSessionId());
			jedisTemplate.del("spring:session:sessions:" + sessionUser.getSessionId());
		}

		WebSocketInfo info = (WebSocketInfo) session.getAttribute(WSC.WEB_SOCKET_TOKEN);
		if (info != null) {
			WebSocketSessionUserHolder.remove(info.getToken());
		}

		// session失效
		session.invalidate();
	}

	protected final WebSocketInfo getWebSocketToken(HttpSession session, HttpServletRequest request) {
		WebSocketInfo info = (WebSocketInfo) session.getAttribute(WSC.WEB_SOCKET_TOKEN);
		if (info == null) {
			return null;
		}

		if (!WebSocketSessionUserHolder.contains(info.getToken())) {
			return null;
		}

		// 检查web socket时效性
		if (WebSocketSessionUserHolder.hashExpired(info)) {
			WebSocketSessionUserHolder.remove(info.getToken());
			return null;
		}

		// 检查web socket有效性
		if (!WebSocketSessionUserHolder.checkValidate(info, request)) {
			return null;
		}

		return info;
	}

	protected final void setWebSocketToken(HttpSession session, WebSocketInfo info) {
		session.setAttribute(WSC.WEB_SOCKET_TOKEN, info);
		WebSocketSessionUserHolder.save(info.getToken(), info);
	}

	/**
	 * 生成一次性(一次性是指跟session生命周期一样)使用的加密token,如果session中已经存在，则会返回原来的
	 */
	protected String generateDisposableToken(HttpSession session, HttpServletRequest request) {
		Object attribute = session.getAttribute(WSC.DISPOSABLE_TOKEN);
		if (attribute != null) {
			return attribute.toString();
		}

		String tokenStr = TokenUtil.generateDisposableToken();
		session.setAttribute(WSC.DISPOSABLE_TOKEN, tokenStr);

		return tokenStr;
	}

	/**
	 * 获取一次性token
	 */
	protected String getDisposableToken(HttpSession session, HttpServletRequest request) {
		Object disposableToken = session.getAttribute(WSC.DISPOSABLE_TOKEN);
		if (disposableToken == null) return null;

		// session.removeAttribute(WSC.DISPOSABLE_TOKEN);

		return disposableToken.toString();
	}

	/**
	 * 检测用户访问频率是否过快，即N秒内允许访问N次， isTooFast("/UserBetsGeneral", 1, 3)表示/UserBetsGeneral接口1秒内最多访问3次
	 * @param currentSubUrl 接口路径
	 * @param inMilliSeconds 多少秒内
	 * @param times 最多访问多少次
	 * @param intervalMilSeconds 与上次访问最小间隔毫秒数
	 * @return
	 */
	protected boolean validateAccessTimeForAPI(HttpSession session, HttpServletRequest request, String currentSubUrl, int inMilliSeconds, int times, int intervalMilSeconds) {
		return validateAccessTime(session, request, "API-" + currentSubUrl, null, inMilliSeconds, times, intervalMilSeconds);
	}

	/**
	 * 检测用户访问频率是否过快，即N秒内允许访问N次， isTooFast("/UserBetsGeneral", 1, 3)表示/UserBetsGeneral接口1秒内最多访问3次
	 * 接口之间共享时间
	 * @param currentSubUrl 接口路径
	 * @param inMilliSeconds 多少秒内
	 * @param times 最多访问多少次
	 * @param intervalMilSeconds 与上次访问最小间隔毫秒数
	 * @return
	 */
	protected boolean validateAccessTimeForAPIShare(HttpSession session, HttpServletRequest request, String currentSubUrl, String shareKey, int inMilliSeconds, int times, int intervalMilSeconds) {
		return validateAccessTime(session, request, "API-" + currentSubUrl, "API-" + shareKey, inMilliSeconds, times, intervalMilSeconds);
	}

	/**
	 * 检测用户访问频率是否过快，即N秒内允许访问N次， isTooFast("/UserBetsGeneral", 1, 3)表示/UserBetsGeneral接口1秒内最多访问3次
	 * @param currentSubUrl 接口路径
	 * @param inMilliSeconds 多少秒内
	 * @param times 最多访问多少次
	 * @param intervalMilSeconds 与上次访问最小间隔毫秒数
	 * @return
	 */
	protected boolean validateAccessTimeForPage(HttpSession session, HttpServletRequest request, String currentSubUrl, int inMilliSeconds, int times, int intervalMilSeconds) {
		return validateAccessTime(session, request, "PAGE-" + currentSubUrl, null, inMilliSeconds, times, intervalMilSeconds);
	}

	/**
	 * 检测用户访问频率是否过快，即N秒内允许访问N次， isTooFast("/UserBetsGeneral", 1, 3)表示/UserBetsGeneral接口1秒内最多访问3次
	 * @param currentSubUrl 接口路径
	 * @param inMilliSeconds 多少秒内
	 * @param times 最多访问多少次
	 * @param intervalMilSeconds 与上次访问最小间隔毫秒数
	 * @return
	 */
	protected boolean validateAccessTime(HttpSession session, HttpServletRequest request, String currentSubUrl, String shareKey, int inMilliSeconds, int times, int intervalMilSeconds) {
		String key;
		if (StringUtils.isNotEmpty(shareKey)) {
			key = "REQ_SP_S_" + shareKey;
		}
		else {
			key = "REQ_SP_" + currentSubUrl;
		}

		Date now = new Date();
		AccessHistory history = (AccessHistory) session.getAttribute(key);
		if (history != null) {
			AccessTime accessTime = history.getAccessTimeQueue();
			if (accessTime.getInMilliSeconds() != inMilliSeconds || accessTime.getTimes() != times) {
				logger.warn("频率控制器提示：检查到路径{}出现不同设置，请检查，本次强制设置为新值；旧设置[inMilliSeconds={},times={},shareKey={}]，新设置[inMilliSeconds={},times={},shareKey={}]", currentSubUrl, accessTime.getInMilliSeconds(), accessTime.getTimes(), shareKey, inMilliSeconds, times, shareKey);
				accessTime.setInMilliSeconds(inMilliSeconds);
				accessTime.setTimes(times);
				accessTime.init();
			}

			if (intervalMilSeconds > 0 && accessTime.getLast() != 0) {
				long last = accessTime.getLast();
				long thisSpan = now.getTime() - last;
				if (thisSpan < intervalMilSeconds) {
					SessionUser sessionUser = (SessionUser) session.getAttribute(WSC.HTTP_SESSION_USER);
					if (sessionUser == null) {
						String referer = HttpUtil.getReferer(request);
						String userAgent = HttpUtil.getUserAgent(request);
						logger.warn("频率控制器提示：未登录用户访问路径[URL={},shareKey={}]间隔{}ms低于最低设置{}ms，请求域名：{}, User-Agent：{}", currentSubUrl, shareKey, thisSpan, intervalMilSeconds, referer, userAgent);
						return true;
					}
					else {
						logger.warn("频率控制器提示：用户{}访问路径[URL={},shareKey={}]间隔{}ms低于最低设置{}ms", sessionUser.getUsername(), currentSubUrl, shareKey, thisSpan, intervalMilSeconds);
						return true;
					}
				}
			}

			// 得到最后一次和第一次的访问时间差
			accessTime.insert(now.getTime());
			long span = accessTime.getLast()- accessTime.getFirst();
			if (span < inMilliSeconds && accessTime.getLast() != 0) {
				SessionUser sessionUser = (SessionUser) session.getAttribute(WSC.HTTP_SESSION_USER);
				if (sessionUser == null) {
					String referer = HttpUtil.getReferer(request);
					String userAgent = HttpUtil.getUserAgent(request);
					logger.warn("频率控制器提示：未登录用户访问路径[URL={},shareKey={}]超速，其在{}ms内访问了{}次，但设置允许仅为{}ms，请求域名：{}, User-Agent：{}", currentSubUrl, shareKey, span, times, inMilliSeconds, referer, userAgent);
					return true;
				}
				else {
					logger.warn("频率控制器提示：用户{}访问路径[URL={},shareKey={}]超速，其在{}ms内访问了{}次，但设置允许仅为{}ms", sessionUser.getUsername(), currentSubUrl, shareKey, span, times, inMilliSeconds);
					return true;
				}
			}
		}
		else {
			history = new AccessHistory(inMilliSeconds, times);
			history.getAccessTimeQueue().insert(now.getTime());
			history.setSessionID(session.getId());
		}

		session.setAttribute(key, history);

		return false;
	}

}
