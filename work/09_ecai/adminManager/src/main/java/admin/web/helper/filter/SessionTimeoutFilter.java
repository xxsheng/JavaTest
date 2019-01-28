package admin.web.helper.filter;

import admin.web.WSC;
import admin.web.helper.session.SessionManager;
import admin.web.helper.session.SessionUser;
import javautils.date.Moment;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * session超时任务，过滤掉轮询请求
 */
public class SessionTimeoutFilter implements Filter {
	private static final Set<String> ignorePages = new HashSet<>();
	private static final  Set<String> ignoreUrls = new HashSet<>();
	static {
		ignorePages.add("/login");
		ignorePages.add("/logout");
		ignorePages.add("/access-denied");
		ignorePages.add("/page-not-found");
		ignorePages.add("/page-error");
		ignorePages.add("/page-not-login");

		ignoreUrls.add("/global");
		ignoreUrls.add("/high-prize-unprocess-count");
		ignoreUrls.add("/lottery-user-withdraw/list");
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// skip non-http requests
		if (!(request instanceof HttpServletRequest)) {
			chain.doFilter(request, response);
			return;
		}

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession();
		String requestURI = httpRequest.getRequestURI();

		final SessionUser sessionUser = SessionManager.getCurrentUser(session);
		if (sessionUser == null) {
			chain.doFilter(request, response);
			return;
		}

		if (ignorePages.contains(requestURI)) {
			chain.doFilter(request, response);
			return;
		}

		Object expireDate = session.getAttribute(WSC.SESSION_EXPIRE_TIME);
		if (expireDate == null) {
			// 初始化过期时间
			Moment expireMoment = new Moment().add(session.getMaxInactiveInterval(), "seconds");
			session.setAttribute(WSC.SESSION_EXPIRE_TIME, expireMoment.toDate());
			chain.doFilter(request, response);
			return;
		}

		// 超时注销session
		Date expiretAt = (Date) expireDate;
		Date now = new Date();
		if (expiretAt.before(now)) {
			session.invalidate();
			chain.doFilter(request, response);
			return;
		}

		if (!ignoreUrls.contains(requestURI)) {
			Moment expireMoment = new Moment().add(session.getMaxInactiveInterval(), "seconds");
			session.setAttribute(WSC.SESSION_EXPIRE_TIME, expireMoment.toDate());
		}

		chain.doFilter(request, response);
	}

	/**
	 * Unused.
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {

	}

	/**
	 * Unused.
	 */
	@Override
	public void destroy() {

	}

}