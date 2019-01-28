package lottery.web.content;

import javautils.StringUtil;
import javautils.date.Moment;
import javautils.encrypt.PasswordUtil;
import javautils.http.HttpUtil;
import lottery.domains.content.biz.UserActionLogService;
import lottery.domains.content.biz.UserRegistLinkService;
import lottery.domains.content.biz.UserService;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserRegistLink;
import lottery.domains.content.vo.user.UserCodeQuotaVO;
import lottery.domains.pool.DataFactory;
import lottery.web.WSC;
import lottery.web.WUC;
import lottery.web.WebJSON;
import lottery.web.content.utils.UserCodePointUtil;
import lottery.web.content.validate.UserProxyValidate;
import lottery.web.content.validate.UserValidate;
import lottery.web.helper.AbstractActionController;
import org.apache.commons.lang.StringUtils;
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
public class UserRegistController extends AbstractActionController {
	/**
	 * Service
	 */
	@Autowired
	private UserService uService;

	@Autowired
	private UserActionLogService uActionLogService;

	@Autowired
	private UserRegistLinkService uRegistLinkService;

	/**
	 * Validate
	 */
	@Autowired
	private UserValidate uValidate;

	@Autowired
	private UserProxyValidate uProxyValidate;

	/**
	 * Util
	 */
	@Autowired
	private UserCodePointUtil uCodePointUtil;

	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;

	@RequestMapping(value = WUC.USER_REGIST, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_REGIST(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		if (!dataFactory.getRegistConfig().isEnable()) {
			json.set(2, "2-2013"); // 平台暂未开放注册功能
			return json.toJson();
		}

		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}

		String username = HttpUtil.getStringParameterTrim(request, "username");
		String nickname = HttpUtil.getStringParameterTrim(request, "nickname");
		nickname = HttpUtil.escapeInput(nickname);
		String password = HttpUtil.getStringParameterTrim(request, "password");
		String link = HttpUtil.getStringParameterTrim(request, "link");
		String code = HttpUtil.getStringParameterTrim(request, "code");
		String sessionCode = (String) session.getAttribute(WSC.REGIST_VALIDATE_CODE);

		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(code)) {
			json.set(2, "2-12");
			return json.toJson();
		}
		if (StringUtils.isEmpty(nickname)) nickname = username;

		//先增加ip黑名单验证
		boolean blackList = uValidate.testIpBlackList(ip);
		if(blackList){
			json.set(2, "2-1073", ip);
			return json.toJson();
		}
		
		if(StringUtil.isNotNull(code) && code.equalsIgnoreCase(sessionCode)) {
			// 链接必须不能为空
			if(StringUtil.isNotNull(link)) {
				UserRegistLink linkBean = uRegistLinkService.get(link);
				// 验证链接有效性
				if(linkBean != null && linkBean.getStatus() == 0) {
					boolean isExpired = false;
					if(StringUtil.isNotNull(linkBean.getExpTime())) {
						// isExpired = thisTime.compareTo(linkBean.getExpTime()) > 0 ? false : true;
						Moment now = new Moment();
						Moment expTime = new Moment().fromTime(linkBean.getExpTime());
						isExpired = now.gt(expTime);
					}
					if(!isExpired) {
						User uEntity = uService.getById(linkBean.getUserId());
						double uLp = linkBean.getLocatePoint();
						double uNlp = uCodePointUtil.getNotLocatePoint(uLp);
						int uCode = uCodePointUtil.getUserCode(uLp);
						int type = linkBean.getType();

						// 不允许创建1958等级
//						if (uCode == 1958) {
//							json.set(2, "2-2022");
//							return json.toJson();
//						}

						if (uCode > dataFactory.getCodeConfig().getSysCode() || uCode < 1800) {
							json.set(2, "2-2022");
							return json.toJson();
						}

						password = PasswordUtil.generatePasswordByMD5(password);
						if(uValidate.testUser(json, username)) {
							if(uProxyValidate.testLowerPoint(json, uEntity, type, uLp)) {

								// // 配额验证
								// if(!uCodePointUtil.hasQuota(uEntity, uCode)) {
								// 	json.set(2, "2-2011");
								// 	return json.toJson();
								// }

								Integer userId = uService.addLowerUser(uEntity, username, nickname, password, type, uCode, uLp, uNlp);
								if(userId != null) {
									uActionLogService.registUser(userId, ip, username, uEntity.getUsername(), type, uLp);
									session.removeAttribute(WSC.REGIST_VALIDATE_CODE);
									json.set(0, "0-1");
								} else {
									json.set(1, "1-1");
								}
							}
						}
					} else {
						json.set(2, "2-2014");
					}
				} else {
					json.set(2, "2-2014");
				}
			} else {
				json.set(2, "2-2014");
			}
		} else {
			json.set(2, "2-1005");
		}
		return json.toJson();
		// WebJSON json = new WebJSON(dataFactory);
		// if (!dataFactory.getRegistConfig().isEnable()) {
		// 	json.set(2, "2-2013"); // 平台暂未开放注册功能
		// 	return json.toJson();
		// }
        //
		// String username = HttpUtil.getStringParameterTrim(request, "username");
		// String nickname = HttpUtil.getStringParameterTrim(request, "nickname");
		// nickname = HttpUtil.escapeInput(nickname);
		// String password = HttpUtil.getStringParameterTrim(request, "password");
		// String link = HttpUtil.getStringParameterTrim(request, "link");
		// String code = HttpUtil.getStringParameterTrim(request, "code");
		// String sessionCode = (String) session.getAttribute(WSC.REGIST_VALIDATE_CODE);
		// String ip = HttpUtil.getRealIp(request);
        //
		// if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(code)) {
		// 	json.set(2, "2-12");
		// 	return json.toJson();
		// }
		// if (StringUtils.isEmpty(nickname)) nickname = username;
        //
		// //先增加ip黑名单验证
		// boolean blackList = uValidate.testIpBlackList(ip);
		// if(blackList){
		// 	json.set(2, "2-1073", ip);
		// 	return json.toJson();
		// }
		//
		// if(StringUtil.isNotNull(code) && code.equalsIgnoreCase(sessionCode)) {
		// 	// 链接必须不能为空
		// 	if(StringUtil.isNotNull(link)) {
		// 		UserRegistLink linkBean = uRegistLinkService.get(link);
		// 		String thisTime = new Moment().toSimpleTime();
		// 		// 验证链接有效性
		// 		if(linkBean != null && linkBean.getStatus() == 0) {
		// 			boolean isExpect = false;
		// 			if(StringUtil.isNotNull(linkBean.getExpTime())) {
		// 				isExpect = thisTime.compareTo(linkBean.getExpTime()) > 0 ? false : true;
		// 			}
		// 			if(!isExpect) {
		// 				User uEntity = uService.getById(linkBean.getUserId());
		// 				double uLp = linkBean.getLocatePoint();
		// 				double uNlp = uCodePointUtil.getNotLocatePoint(uLp);
		// 				int uCode = uCodePointUtil.getUserCode(uLp);
		// 				int type = linkBean.getType();
        //
		// 				// 不允许创建1958等级
		// 				if (uCode == 1958) {
		// 					json.set(2, "2-2022");
		// 					return json.toJson();
		// 				}
        //
		// 				if (uCode > dataFactory.getCodeConfig().getSysCode()) {
		// 					json.set(2, "2-2022");
		// 					return json.toJson();
		// 				}
        //
		// 				if (uCode < 1800) {
		// 					json.set(2, "2-2022");
		// 					return json.toJson();
		// 				}
        //
		// 				password = PasswordUtil.generatePasswordByMD5(password);
		// 				if(uValidate.testUser(json, username)) {
		// 					if(uProxyValidate.testLowerPoint(json, uEntity, type, uLp)) {
		// 						List<UserCodeQuotaVO> surplusQuota = uCodePointUtil.listSurplusQuota(uEntity);
		// 						for (UserCodeQuotaVO tmpQuota : surplusQuota) {
		// 							if(uLp >= tmpQuota.getMinPoint() && uLp <= tmpQuota.getMaxPoint()) {
		// 								// 配额不足
		// 								if(tmpQuota.getSurplusCount() < 1) {
		// 									json.set(2, "2-2011");
		// 									return json.toJson();
		// 								}
		// 							}
		// 						}
		// 						Integer userId = uService.addLowerUser(uEntity, username, nickname, password, type, uCode, uLp, uNlp);
		// 						if(userId != null) {
		// 							uActionLogService.registUser(userId, ip, username, uEntity.getUsername(), type, uLp);
		// 							session.removeAttribute(WSC.REGIST_VALIDATE_CODE);
		// 							json.set(0, "0-1");
		// 						} else {
		// 							json.set(1, "1-1");
		// 						}
		// 					}
		// 				}
		// 			} else {
		// 				json.set(2, "2-2014");
		// 			}
		// 		} else {
		// 			json.set(2, "2-2014");
		// 		}
		// 	} else {
		// 		json.set(2, "2-2014");
		// 	}
		// } else {
		// 	json.set(2, "2-1005");
		// }
		// return json.toJson();
	}
	
}