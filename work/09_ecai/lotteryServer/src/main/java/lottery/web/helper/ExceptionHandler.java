// package lottery.web.helper;
//
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.stereotype.Component;
// import org.springframework.web.servlet.HandlerExceptionResolver;
// import org.springframework.web.servlet.ModelAndView;
//
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
//
// /**
//  * Created by Nick on 2017-04-16.
//  */
// @Component
// public class ExceptionHandler implements HandlerExceptionResolver {
//     private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);
//     @Override
//     public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
//         log.error("发生异常", ex);
//         return null;
//     }
// }
