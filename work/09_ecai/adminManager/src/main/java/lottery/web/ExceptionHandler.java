package lottery.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Nick on 2017-06-02.
 */
@Component
public class ExceptionHandler extends ExceptionHandlerExceptionResolver {
    private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    protected ModelAndView doResolveHandlerMethodException(
            HttpServletRequest request, HttpServletResponse response,
            HandlerMethod handlerMethod, Exception exception) {
        // 捕捉未知异常
        String exceptionName = exception.getClass().getName();
        if (exceptionName != null && !"org.apache.catalina.connector.ClientAbortException".equals(exceptionName)) {
            log.error("发生异常", exception);
        }
        return super.doResolveHandlerMethodException(request, response, handlerMethod, exception);
    }
}