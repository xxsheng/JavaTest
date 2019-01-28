package lottery.web.content;

import javautils.encrypt.PasswordUtil;
import javautils.http.HttpUtil;
import lottery.domains.content.biz.UserActionLogService;
import lottery.domains.content.biz.UserSecurityService;
import lottery.domains.content.entity.UserSecurity;
import lottery.domains.content.vo.user.UserSecurityVO;
import lottery.domains.pool.DataFactory;
import lottery.web.WUC;
import lottery.web.WebJSON;
import lottery.web.content.validate.UserSecurityValidate;
import lottery.web.content.validate.UserValidate;
import lottery.web.helper.AbstractActionController;
import lottery.web.helper.session.SessionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class UserSecurityController extends AbstractActionController {
	/**
	 * Service
	 */
	@Autowired
	private UserSecurityService uSecurityService;

	@Autowired
	private UserActionLogService uActionLogService;

	/**
	 * Validate
	 */
	@Autowired
	private UserSecurityValidate uSecurityValidate;

	@Autowired
	private UserValidate uValidate;

	/**
	 * Util
	 */

	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;

	@RequestMapping(value = WUC.USER_SECURITY_GET, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_SECURITY_GET(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		UserSecurityVO bean = uSecurityService.getRandomByUserId(sessionUser.getId());
		json.data("data", bean);
		json.set(0, "0-1");
		return json.toJson();
	}
	
	@RequestMapping(value = WUC.USER_SECURITY_BIND, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_SECURITY_BIND(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		//先增加ip黑名单验证
		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}
		boolean blackList = uValidate.testIpBlackList(ip);
		if(blackList){
			json.set(2, "2-1073", ip);
			return json.toJson();
		}

		String question1 = request.getParameter("question1");
		question1 = HttpUtil.escapeInput(question1);
		String answer1 = request.getParameter("answer1");
		String question2 = request.getParameter("question2");
		question2 = HttpUtil.escapeInput(question2);
		String answer2 = request.getParameter("answer2");
		String question3 = request.getParameter("question3");
		question3 = HttpUtil.escapeInput(question3);
		String answer3 = request.getParameter("answer3");

		if (!uSecurityValidate.required(json, question1, answer1, question2, answer2, question3, answer3)) {
			return json.toJson();
		}

		if (uSecurityValidate.isSame(json, answer1, answer2, answer3)) {
			return json.toJson();
		}

		String usernamePwd = PasswordUtil.generatePasswordByPlainString(sessionUser.getUsername());
		if (PasswordUtil.validateSamePassword(usernamePwd, answer1)) {
			json.set(2, "2-1088", "1");
			return json.toJson();
		}
		String answer1Md5 = PasswordUtil.generatePasswordByMD5(answer1);
		if (PasswordUtil.isSimplePassword(answer1Md5)) {
			json.set(2, "2-1089", "1");
			return json.toJson();
		}
		if (PasswordUtil.validateSamePassword(usernamePwd, answer2)) {
			json.set(2, "2-1088", "2");
			return json.toJson();
		}
		String answer2Md5 = PasswordUtil.generatePasswordByMD5(answer2);
		if (PasswordUtil.isSimplePassword(answer2Md5)) {
			json.set(2, "2-1089", "2");
			return json.toJson();
		}
		if (PasswordUtil.validateSamePassword(usernamePwd, answer3)) {
			json.set(2, "2-1088", "3");
			return json.toJson();
		}
		String answer3Md5 = PasswordUtil.generatePasswordByMD5(answer3);
		if (PasswordUtil.isSimplePassword(answer3Md5)) {
			json.set(2, "2-1089", "3");
			return json.toJson();
		}

		List<UserSecurity> sList = uSecurityService.listByUserId(sessionUser.getId());
		if(sList.size() == 0) {
			boolean flag1 = uSecurityService.add(sessionUser.getId(), question1, answer1);
			boolean flag2 = uSecurityService.add(sessionUser.getId(), question2, answer2);
			boolean flag3 = uSecurityService.add(sessionUser.getId(), question3, answer3);
			if(flag1 && flag2 && flag3) {
				uActionLogService.bindSecurity(sessionUser.getId(), ip, question1, question2, question3);
				json.set(0, "0-1");
			} else {
				json.set(1, "1-1");
			}
		} else {
			json.set(2, "2-1055");
		}
		return json.toJson();
	}
	
}