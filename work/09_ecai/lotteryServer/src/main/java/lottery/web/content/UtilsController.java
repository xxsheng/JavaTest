package lottery.web.content;

import javautils.image.ImageCodeUtil;
import lottery.web.WSC;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class UtilsController {
	
	@RequestMapping(value = "/LoginCode", method = { RequestMethod.GET })
	@ResponseBody
	public void LoginCode(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		ImageCodeUtil.generate(WSC.LOGIN_VALIDATE_CODE, request, response);
	}
	
	@RequestMapping(value = "/RegistCode", method = { RequestMethod.GET })
	@ResponseBody
	public void RegistCode(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		ImageCodeUtil.generate(WSC.REGIST_VALIDATE_CODE, request, response);
	}
	
}