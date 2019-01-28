package admin.web.helper.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public final class AuthenticateAccountInterceptor extends HandlerInterceptorAdapter {
	
	// Action之前执行
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
		return true;
	}
			 
	// 生成视图之前执行
	public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final ModelAndView modelAndView) {
	
	}
			 
	// 最后执行，可用于释放资源
	public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception e) {
	
	}
}
