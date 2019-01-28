package lottery.web.content;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import javautils.StringUtil;
import javautils.date.DateUtil;
import javautils.date.Moment;
import javautils.encrypt.PasswordUtil;
import javautils.http.HttpUtil;
import javautils.image.ImageUtil;
import javautils.jdbc.PageList;
import javautils.mobile.MobileChecker;
import lottery.domains.content.biz.*;
import lottery.domains.content.biz.read.UserLoginLogReadService;
import lottery.domains.content.biz.read.UserReadService;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserInfo;
import lottery.domains.content.entity.UserSecurity;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.config.CodeConfig;
import lottery.domains.content.vo.config.SysLotteryConfigVO;
import lottery.domains.content.vo.user.*;
import lottery.domains.pool.DataFactory;
import lottery.domains.pool.payment.utils.RequestUtils;
import lottery.web.AppWUC;
import lottery.web.WSC;
import lottery.web.WUC;
import lottery.web.WebJSON;
import lottery.web.content.validate.UserCardValidate;
import lottery.web.content.validate.UserValidate;
import lottery.web.helper.AbstractActionController;
import lottery.web.helper.session.SessionUser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController extends AbstractActionController {
	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	/**
	 * Service
	 */
	@Autowired
	private UserCardService uCardService;

	@Autowired
	private UserSecurityService uSecurityService;

	@Autowired
	private UserLoginLogService uLoginLogService;

	@Autowired
	private UserLoginLogReadService uLoginLogReadService;

	@Autowired
	private UserActionLogService uActionLogService;

	@Autowired
	private UserService uService;

	@Autowired
	private UserReadService uReadService;

	@Autowired
	private UserInfoService uInfoService;

	/**
	 * Validate
	 */
	@Autowired
	private UserValidate uValidate;

	@Autowired
	private UserCardValidate uCardValidate;

	@Autowired
	private UserBillService userBillService;

	/**
	 * Util
	 */

	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;

	@Autowired
	private UserWithdrawLimitService uWithdrawLimitService;
	
	@RequestMapping(value = WUC.DEMO_LOGIN, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> APP_DEMO_LOGIN(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = getSessionUser(json, session, request);
		if (sessionUser != null) {
			json.set(2, "2-16"); // 您已登录另一账号，请先退出
			return json.toJson();
		}
		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}
		List<User> offlineUser  = null;
		offlineUser = uService.getOfflineDemoList(0);
		if(offlineUser.size() == 0){
			offlineUser =  uService.getOfflineDemoList(1);
		}
		int index=(int)(Math.random()*offlineUser.size());
		User uEntity = offlineUser.get(index);
		
		Moment thisTime = new Moment();
		String userAgent = request.getHeader("User-Agent");
		userAgent = HttpUtil.escapeInput(userAgent);
		
		//先增加ip黑名单验证，这是后台加的，属于永久冻结
		boolean blackList = uValidate.testIpBlackList(ip);
		if(blackList){
			return this.APP_DEMO_LOGIN(session, request);
		}



		if(uEntity.getAStatus() != 0) {
			json.set(2, "2-1"); // 您的账号已被系统冻结，请联系客服处理!
			return this.APP_DEMO_LOGIN(session, request);
		}
		
		double sysdemoLottery= dataFactory.getRegistConfig().getDemoLotteryMoney();
		if(sysdemoLottery>uEntity.getLotteryMoney()){
			uService.updateDemoUserLotteryMoney(uEntity.getId(), sysdemoLottery);
			uEntity.setLotteryMoney(sysdemoLottery);
		}
		
		// 登录成功
		String loginLine = RequestUtils.getReferer(request);
		uLoginLogService.add(uEntity.getId(), ip, userAgent, thisTime.toSimpleTime(),loginLine);
		setSessionUser(session, uEntity);
		UserBaseVO uBaseVO = new UserBaseVO(uEntity);
		json.data("uBean", uBaseVO);
		json.set(0, "0-1");

		return json.toJson();
	}

	@RequestMapping(value = WUC.APP_LOGIN, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> APP_LOGIN(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = getSessionUser(json, session, request);
		if (sessionUser != null) {
			json.set(2, "2-16"); // 您已登录另一账号，请先退出
			return json.toJson();
		}
		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}

		String username = HttpUtil.getStringParameterTrim(request, "username"); // 用户名是不允许有空格的
		String password = request.getParameter("password"); // 密码传输时的加密方式：MD5大写(MD5大写(MD5大写(MD5大写(密码明文))) + token)
		String code = HttpUtil.getStringParameterTrim(request, "checkcode");
		Moment thisTime = new Moment();
		String userAgent = request.getHeader("User-Agent");
		userAgent = HttpUtil.escapeInput(userAgent);

		//先增加ip黑名单验证，这是后台加的，属于永久冻结
		boolean blackList = uValidate.testIpBlackList(ip);
		if(blackList){
			json.set(2, "2-1073", ip);
			return json.toJson();
		}

		// 验证登录参数是否合法
		if(!uValidate.testLoginParams(json, username, password, code)) return json.toJson();

		String sessionCode = (String) session.getAttribute(WSC.LOGIN_VALIDATE_CODE);
		if(!code.equalsIgnoreCase(sessionCode)) {
			json.set(2, "2-1005"); // 验证码输入有误
			session.removeAttribute(WSC.LOGIN_VALIDATE_CODE);
			return json.toJson();
		}

		String disposableToken = getDisposableToken(session, request); // 获取一次性验证token
		if (StringUtils.isEmpty(disposableToken)) {
			json.set(2, "2-15"); // 您的请求已超时，请重新提交 一次性验证token没有了，说明session失效
			session.removeAttribute(WSC.LOGIN_VALIDATE_CODE);
			return json.toJson();
		}

		User uEntity = uService.getByUsername(username);
		if(uEntity == null) {
			json.set(2, "2-1074"); // 用户名或密码错误
			session.removeAttribute(WSC.LOGIN_VALIDATE_CODE);
			return json.toJson();
		}

		if(uEntity.getAStatus() != 0) {
			json.set(2, "2-1"); // 您的账号已被系统冻结，请联系客服处理!
			session.removeAttribute(WSC.LOGIN_VALIDATE_CODE);
			return json.toJson();
		}

		// 验证登录密码
		if(!PasswordUtil.validatePassword(uEntity.getPassword(), disposableToken, password)) {
			json.set(2, "2-1074"); // 用户名或密码错误
			session.removeAttribute(WSC.LOGIN_VALIDATE_CODE);
			return json.toJson();
		}

		// 验证异地登录
		LoginValidateVO validateVO = uValidate.testLoginValidate(ip, userAgent, uEntity);
		if (validateVO != null) {
			UserCardVO userCardVO = uCardService.getRandomByUserId(uEntity.getId());
			if (userCardVO != null) {
				setLoginValidate(session, uEntity, userCardVO.getId());
				validateVO.setCardId(userCardVO.getCardId());
				json.data("data", validateVO);
				json.set(2, "2-4026");
				return json.toJson();
			}
		}

		// 登录成功
		String loginLine = RequestUtils.getReferer(request);
		uLoginLogService.add(uEntity.getId(), ip, userAgent, thisTime.toSimpleTime(),loginLine);
		setSessionUser(session, uEntity);
		// UserBaseVO uBaseVO = new UserBaseVO(uEntity);
		// json.data("uBean", uBaseVO);
		json.set(0, "0-1");
		//清空验证码，防止重复提交
		session.removeAttribute(WSC.LOGIN_VALIDATE_CODE);

		return json.toJson();
	}

	@Autowired
	private ActivityFirstRechargeBillService activityFirstRechargeBillService;

	@RequestMapping(value = WUC.VALIDATE_LOGIN, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> VALIDATE_LOGIN(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		boolean setLoginValidate = isSetLoginValidate(session);
		if (!setLoginValidate) {
			json.set(2, "2-6");
			return json.toJson();
		}

		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}

		User uEntity = getLoginValidateUser(session);
		if (uEntity == null) {
			json.set(2, "2-6");
			return json.toJson();
		}

		String cardName = HttpUtil.getStringParameterTrim(request, "cardName");
		if (StringUtils.isEmpty(cardName)) {
			json.set(2, "2-6");
			return json.toJson();
		}

		int id = getLoginValidateCardId(session);
		boolean validate = uCardService.validateCard(json, id, uEntity.getId(), cardName);
		if (!validate) {
			return json.toJson();
		}

		Moment thisTime = new Moment();
		String userAgent = request.getHeader("User-Agent");
		String loginLine = HttpUtil.getHost(request);
		uService.updateLoginTime(uEntity.getId(), thisTime.toSimpleTime());
		uLoginLogService.add(uEntity.getId(), ip, userAgent, thisTime.toSimpleTime(),loginLine);
		// Set online
		setSessionUser(session, uEntity);
		removeLoginValidate(session);
		UserBaseVO uBaseVO = new UserBaseVO(uEntity);
		json.data("uBean", uBaseVO);
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_BASE_GET, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_BASE_GET(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);

		// // 限制访问频率,10秒最多20次
		// boolean exceedTime = super.validateAccessTimeForAPI(session, request, WUC.USER_BASE_GET, 10, 20, -1);
		// if (exceedTime == true) {
		// 	json.set(2, "2-17"); // 您的账户信息发生变更，请重新登录
		// 	return json.toJson();
		// }

		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		User uEntity = uReadService.getByIdFromRead(sessionUser.getId());
		if (uEntity == null) {
			super.logOut(session, request);
			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
			return json.toJson();
		}

		UserBaseVO bean = new UserBaseVO(uEntity);
		int coco = dataFactory.getCodeConfig().getSysCode();
		
		json.data("data", bean);
		json.data("coco", coco);
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_INFO_GET, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_INFO_GET(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);

		// // 限制访问频率,10秒最多20次
		// boolean exceedTime = super.validateAccessTimeForAPI(session, request, WUC.USER_INFO_GET, 10, 20, -1);
		// if (exceedTime == true) {
		// 	json.set(2, "2-17"); // 您的账户信息发生变更，请重新登录
		// 	return json.toJson();
		// }

		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		User uEntity = uService.getById(sessionUser.getId());
		if (uEntity == null) {
			super.logOut(session, request);
			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
			return json.toJson();
		}

		UserInfo iBean = uInfoService.get(uEntity.getId());
		UserInfoVO bean = new UserInfoVO(uEntity, iBean);
		json.data("data", bean);
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_LAST_LOGIN_GET, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_LAST_LOGIN_GET(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);

		// // 限制访问频率,10秒最多20次
		// boolean exceedTime = super.validateAccessTimeForAPI(session, request, WUC.USER_LAST_LOGIN_GET, 10, 20, -1);
		// if (exceedTime == true) {
		// 	json.set(2, "2-17"); // 您的账户信息发生变更，请重新登录
		// 	return json.toJson();
		// }

		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		UserLoginLogVO bean = uLoginLogReadService.getLastLogin(sessionUser.getId(), 1);
		json.data("data", bean);
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_LOGIN_LOG_GET, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_LOGIN_LOG_GET(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		// // 限制访问频率,10秒最多20次
		// boolean exceedTime = super.validateAccessTimeForAPI(session, request, WUC.USER_LOGIN_LOG_GET, 10, 20, -1);
		// if (exceedTime == true) {
		// 	json.set(2, "2-17"); // 您的账户信息发生变更，请重新登录
		// 	return json.toJson();
		// }

		Integer count = HttpUtil.getIntParameter(request, "count");
		if (count == null) {
			count = 3;
		}
		else if (count == 5) {
			count = 5;
		}
		else {
			count = 3;
		}

		List<UserLoginLogVO> logs = uLoginLogReadService.getLoginLog(sessionUser.getId(), count);
		json.data("data", logs);
		json.set(0, "0-1");
		return json.toJson();
	}
	@RequestMapping(value = WUC.USER_LOGIN_LOG_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_LOGIN_LOG_LIST(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}
		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");

		PageList pList =  uLoginLogReadService.getLoginLogList(sessionUser.getId(), start, limit);
		json.data("totalCount", pList.getCount());
		json.data("data", pList.getList());
		json.set(0, "0-1");
		return json.toJson();
	}
	@RequestMapping(value = WUC.USER_BIND_GET, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_BIND_GET(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);

		// // 限制访问频率,10秒最多20次
		// boolean exceedTime = super.validateAccessTimeForAPI(session, request, WUC.USER_BIND_GET, 10, 20, -1);
		// if (exceedTime == true) {
		// 	json.set(2, "2-17"); // 您的账户信息发生变更，请重新登录
		// 	return json.toJson();
		// }

		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		User uEntity = uService.getById(sessionUser.getId());
		if (uEntity == null) {
			super.logOut(session, request);
			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
			return json.toJson();
		}

		UserInfo uInfoBean = uInfoService.get(uEntity.getId());
		List<UserSecurity> uSecurityList = uSecurityService.listByUserId(uEntity.getId());
		List<UserCardVO> uCardList = uCardService.listByUserId(uEntity.getId());

		// 验证是否已经绑定
		boolean hasLoginPwd = StringUtil.isNotNull(uEntity.getPassword());
		boolean hasWithdrawPwd = StringUtil.isNotNull(uEntity.getWithdrawPassword());
		boolean hasWithdrawName = StringUtil.isNotNull(uEntity.getWithdrawName());
		boolean hasSecurity = uSecurityList != null ? uSecurityList.size() > 0 : false;
		boolean hasCard = uCardList != null ? uCardList.size() > 0 : false;
		boolean hasBirthday = uInfoBean != null ? StringUtil.isNotNull(uInfoBean.getBirthday()) : false;
		boolean hasEmail = uInfoBean != null ? StringUtil.isNotNull(uInfoBean.getEmail()) : false;
		boolean hasPostscript = uInfoBean != null ? StringUtil.isNotNull(uInfoBean.getPostscript()) : false;
		boolean isLoginValidate = uEntity.getLoginValidate() == 1;
		boolean hasBindGoogle = uEntity.getIsBindGoogle() == 1 && StringUtil.isNotNull(uEntity.getSecretKey());
		Map<String, Object> data = new HashMap<>();
		data.put("hasLoginPwd", hasLoginPwd);
		data.put("hasSecurity", hasSecurity);
		data.put("hasWithdrawName", hasWithdrawName);
		data.put("hasWithdrawPwd", hasWithdrawPwd);
		data.put("hasCard", hasCard);
		data.put("hasBirthday", hasBirthday);
		data.put("hasEmail", hasEmail);
		data.put("hasPostscript", hasPostscript);
		data.put("isLoginValidate", isLoginValidate);
		data.put("hasBindGoogle", hasBindGoogle);
		json.data("data", data);
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_BIND_LOAD, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_BIND_LOAD(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);

		// // 限制访问频率,10秒最多20次
		// boolean exceedTime = super.validateAccessTimeForAPI(session, request, WUC.USER_BIND_LOAD, 10, 20, -1);
		// if (exceedTime == true) {
		// 	json.set(2, "2-17"); // 您的账户信息发生变更，请重新登录
		// 	return json.toJson();
		// }

		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		User uEntity = uService.getById(sessionUser.getId());
		if (uEntity == null) {
			super.logOut(session, request);
			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
			return json.toJson();
		}

		UserBindVO uBindBean = new UserBindVO();

		UserInfo uInfoBean = uInfoService.get(sessionUser.getId());
		List<UserCardVO> uCardList = uCardService.listByUserId(sessionUser.getId());
		uBindBean.setWithdrawName(uEntity.getWithdrawName());
		if(uInfoBean != null) {
			uBindBean.setBirthday(uInfoBean.getBirthday());
		}
		if(uCardList != null && uCardList.size() > 0) {
			UserCardVO uCardBean = uCardList.get(0);
			uBindBean.setBankName(uCardBean.getBankName());
			uBindBean.setBankBranch(uCardBean.getBankBranch());
			uBindBean.setCardId(uCardBean.getCardId());
		}
		uBindBean.setWithdrawPwd("******");
		json.data("data", uBindBean);
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_BIND_MOD, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_BIND_MOD(HttpSession session, HttpServletRequest request) {
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

		User uEntity = uService.getById(sessionUser.getId());
		if (uEntity == null) {
			super.logOut(session, request);
			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
			return json.toJson();
		}

//		String withdrawPwd = request.getParameter("withdrawPwd");
//		String birthday = HttpUtil.getStringParameterTrim(request, "birthday");
//		birthday = HttpUtil.escapeInput(birthday);
		String withdrawName = HttpUtil.getStringParameterTrim(request, "withdrawName");
		withdrawName = HttpUtil.escapeInput(withdrawName);
//		Integer bankId = HttpUtil.getIntParameter(request, "bankId");
//		String bankBranch = HttpUtil.getStringParameterTrim(request, "bankBranch");
//		bankBranch = HttpUtil.escapeInput(bankBranch);
//		String cardId = HttpUtil.getStringParameterTrim(request, "cardId");
//		cardId = HttpUtil.escapeInput(cardId);

		if(uEntity.getBindStatus() == 1 && StringUtils.isNotEmpty(uEntity.getWithdrawName())) {
			json.set(2, "2-1008");
			return json.toJson();
		}

		// 验证取款人姓名
		boolean isPassWithdrawName = false;
		{
			if(StringUtil.isNotNull(withdrawName)) {
				//if(uValidate.testWithdrawName(json, withdrawName)) {
				isPassWithdrawName = true;
				//}
			} else {
				json.set(2, "2-1058");
			}
			if(!isPassWithdrawName) {
				return json.toJson();
			}
		}
		//验证姓名
		if(withdrawName.length() > 13){
			json.set(2, "2-4016");
			return json.toJson();
		}
		if(StringUtil.isNameChn(withdrawName) == false){
			json.set(2, "2-4015");
			return json.toJson();
		}

		// 验证生日
//		boolean isPassBirthday = false;
//		{
//			if(StringUtil.isNotNull(birthday)) {
//				isPassBirthday = true;
//			} else {
//				json.set(2, "2-1056");
//			}
//			if(!isPassBirthday) {
//				return json.toJson();
//			}
//		}
		// 验证卡片
//		boolean isPassCard = false;
//		{
//			String cardName = withdrawName;
//			if(uCardValidate.required(json, bankId, cardName, cardId)) {
//				if(uCardValidate.checkCardId(cardId)) {
//					UserCard userCard = uCardService.getByCardId(cardId);
//					if(userCard == null || StringUtils.isEmpty(userCard.getCardId())) {
//						boolean isPassBlackWhitelist = uCardValidate.testBlackWhiteList(json, uEntity, bankId.intValue(), cardName, cardId, ip);
//						if(isPassBlackWhitelist) {
//							isPassCard = true;
//						}
//					} else {
//						json.set(2, "2-1015");
//					}
//				} else {
//					json.set(2, "2-1014");
//				}
//			}
//			if(!isPassCard) {
//				return json.toJson();
//			}
//		}
		// 验证资金密码
//		boolean isPassWithdrawPwd = false;
//		{
//			if(uValidate.testPassword(json, withdrawPwd)) {
//				isPassWithdrawPwd = true;
//			}
//			// 是否与登录密码一样
//			if (CipherUtil.validatePassword(uEntity.getPassword(), withdrawPwd)) {
//				json.set(2, "2-1053");
//				isPassWithdrawPwd = false;
//			}
//			if(!isPassWithdrawPwd) {
//				return json.toJson();
//			}
//		}

		// 更新结果
		boolean result = true;
		try {
			UserInfo uInfoBean = uInfoService.get(uEntity.getId());
			if(uInfoBean == null) {
				uInfoBean = new UserInfo();
				uInfoBean.setUserId(uEntity.getId());
				uInfoBean.setBirthday(DateUtil.getYesterday());
				uInfoService.add(uInfoBean);
			} else {
				uInfoBean.setBirthday(DateUtil.getYesterday());
				uInfoService.update(uInfoBean);
			}
			uService.updateWithdrawName(uEntity.getId(), withdrawName);
//			String cardName = withdrawName;
//			uCardService.add(uEntity.getId(), bankId.intValue(), bankBranch, cardName, cardId, 1);
//			String md5Pwd = CipherUtil.generatePassword(withdrawPwd);
//			uService.updateWithdrawPassword(uEntity.getId(), md5Pwd);
			uService.updateBindStatus(uEntity.getId(), 1); // 更新为已绑定状态
			uActionLogService.bindWithdrawName(uEntity.getId(), ip, withdrawName);
			// 添加绑定活动
			//aBindBillService.add(uEntity, ip, cardName, bankId, cardId);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		if (result) {
			json.set(0, "0-1");
		} else {
			json.set(1, "1-1");
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_NICKNAME_MOD, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_NICKNAME_MOD(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);

		// // 限制访问频率,10秒最多20次，间隔300毫秒
		// boolean exceedTime = super.validateAccessTimeForAPI(session, request, WUC.USER_NICKNAME_MOD, 10, 20, 300);
		// if (exceedTime == true) {
		// 	json.set(2, "2-17"); // 您的账户信息发生变更，请重新登录
		// 	return json.toJson();
		// }

		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String nickname = HttpUtil.getStringParameterTrim(request, "nickname");
		nickname = HttpUtil.escapeInput(nickname);
		if(nickname.equals("试玩用户") ||sessionUser.getNickname().equals("试玩用户")){
			json.set(1, "1-1");
			return json.toJson();
		}
		if(uValidate.testNickname(json, nickname)) {
			boolean result = uService.updateNickname(sessionUser.getId(), nickname);
			if(result) {
				sessionUser.setNickname(nickname); // 重新设置到session中
				json.set(0, "0-1");
			} else {
				json.set(1, "1-1");
			}
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_LOGIN_PWD_MOD, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_LOGIN_PWD_MOD(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);

		// // 限制访问频率,10秒最多20次，间隔300毫秒
		// boolean exceedTime = super.validateAccessTimeForAPI(session, request, WUC.USER_LOGIN_PWD_MOD, 10, 20, 300);
		// if (exceedTime == true) {
		// 	json.set(2, "2-17"); // 您的账户信息发生变更，请重新登录
		// 	return json.toJson();
		// }

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

		User uEntity = uService.getById(sessionUser.getId());
		if (uEntity == null) {
			super.logOut(session, request);
			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
			return json.toJson();
		}

		String oldPassword = request.getParameter("oldPassword"); // 密码传输时的加密方式：MD5大写(MD5大写(MD5大写(MD5大写(密码明文))) + token)
		String newPassword = request.getParameter("newPassword"); // 密码传输时的加密方式：MD5大写(MD5大写(密码明文)
		if(!uValidate.testPassword(json, newPassword)) return json.toJson();

		String disposableToken = getDisposableToken(session, request); // 获取一次性验证token
		if (StringUtils.isEmpty(disposableToken)) {
			json.set(2, "2-15"); // 您的请求已超时，请重新提交 一次性验证token没有了，说明session失效
			return json.toJson();
		}

		// 验证登录密码
		if(!PasswordUtil.validatePassword(uEntity.getPassword(), disposableToken, oldPassword)) {
			json.set(2, "2-1004"); // 密码输入有误
			return json.toJson();
		}

		// 验证是否与旧密码相同
		if(PasswordUtil.validateSamePassword(uEntity.getPassword(), newPassword)) {
			json.set(2, "2-1054"); // 新密码不能与旧密码相同
			return json.toJson();
		}

		if (StringUtils.isNotEmpty(uEntity.getWithdrawPassword()) && uEntity.getBindStatus() == 1) {
			// 验证是否与资金密码相同
			if(PasswordUtil.validateSamePassword(uEntity.getWithdrawPassword(), newPassword)) {
				json.set(2, "2-1080"); // 新密码不能与资金密码相同
				return json.toJson();
			}
		}

		String usernamePwd = PasswordUtil.generatePasswordByPlainString(sessionUser.getUsername());
		boolean sameWithUsername = PasswordUtil.validateSamePassword(usernamePwd, newPassword);
		if (sameWithUsername) {
			json.set(2, "2-1086"); // 密码设置不允许和用户名相同！为了保障您的资金安全，请您将密码设置得更为复杂一些！
			return json.toJson();
		}

		String dbPassword = PasswordUtil.generatePasswordByMD5(newPassword);
		if (PasswordUtil.isSimplePassword(dbPassword)) {
			json.set(2, "2-1087");
			return json.toJson();
		}

		boolean result = uService.updateLoginPwd(uEntity.getId(), dbPassword);
		if(result) {
			logOut(session, request);
			uActionLogService.modLoginPwd(uEntity.getId(), ip);
			json.set(0, "0-1");
		} else {
			json.set(1, "1-1");
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_WITHDRAW_PWD_BIND, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_WITHDRAW_PWD_BIND(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);

		// // 限制访问频率,10秒最多20次，间隔300毫秒
		// boolean exceedTime = super.validateAccessTimeForAPI(session, request, WUC.USER_WITHDRAW_PWD_MOD, 10, 20, 300);
		// if (exceedTime == true) {
		// 	json.set(2, "2-17"); // 您的账户信息发生变更，请重新登录
		// 	return json.toJson();
		// }

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

		int sid = HttpUtil.getIntParameter(request, "sid");
		String answer = request.getParameter("answer"); // MD5大写(MD5大写(MD5大写(MD5大写(答案明文))) + token)
		String password = request.getParameter("password"); // 密码传输时的加密方式：MD5大写(MD5大写(取款密码明文)

		if(!uValidate.testPassword(json, password)) return json.toJson();
		if (StringUtils.isEmpty(answer)) {
			json.set(2, "2-1010");
			return json.toJson();
		}

		String disposableToken = getDisposableToken(session, request); // 获取并删除一次性token
		if (StringUtils.isEmpty(disposableToken)) {
			json.set(2, "2-15"); // 您的请求已超时，请重新提交 一次性验证token没有了，说明session失效
			return json.toJson();
		}

		User uEntity = uService.getById(sessionUser.getId());
		if (uEntity == null) {
			super.logOut(session, request);
			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
			return json.toJson();
		}

		// 未绑定资料不允许修改
		if(uEntity.getBindStatus() != 1) {
			json.set(2, "2-1061");
			return json.toJson();
		}

		if(!uSecurityService.validateSecurity(sid, uEntity.getId(), disposableToken, answer)) {
			json.set(2, "2-1010");
			return json.toJson();
		}

		// 输入的密码是否与登录密码相同
		if (PasswordUtil.validateSamePassword(uEntity.getPassword(), password)) {
			json.set(2, "2-1053");
			return json.toJson();
		}

		// 输入的密码是否与原资金密码相同
		if (StringUtils.isNotEmpty(uEntity.getWithdrawPassword())) {
			if (PasswordUtil.validateSamePassword(uEntity.getWithdrawPassword(), password)) {
				json.set(2, "2-1054");
				return json.toJson();
			}
		}

		String usernamePwd = PasswordUtil.generatePasswordByPlainString(sessionUser.getUsername());
		boolean sameWithUsername = PasswordUtil.validateSamePassword(usernamePwd, password);
		if (sameWithUsername) {
			json.set(2, "2-1086"); // 密码设置不允许和用户名相同！为了保障您的资金安全，请您将密码设置得更为复杂一些！
			return json.toJson();
		}

		String md5Pwd = PasswordUtil.generatePasswordByMD5(password);
		if (PasswordUtil.isSimplePassword(md5Pwd)) {
			json.set(2, "2-1087");
			return json.toJson();
		}

		boolean result = uService.updateWithdrawPassword(uEntity.getId(), md5Pwd);
		if(result) {
			uActionLogService.bindWithdrawPwd(uEntity.getId(), ip);
			json.set(0, "0-1");
		} else {
			json.set(1, "1-1");
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_WITHDRAW_PWD_MOD, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_WITHDRAW_PWD_MOD(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);

		// // 限制访问频率,10秒最多20次，间隔300毫秒
		// boolean exceedTime = super.validateAccessTimeForAPI(session, request, WUC.USER_WITHDRAW_PWD_MOD, 10, 20, 300);
		// if (exceedTime == true) {
		// 	json.set(2, "2-17"); // 您的账户信息发生变更，请重新登录
		// 	return json.toJson();
		// }

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

		String oldPassword = HttpUtil.getStringParameterTrim(request, "oldPassword"); // MD5大写(MD5大写(MD5大写(MD5大写(答案明文))) + token)
		String newPassword = HttpUtil.getStringParameterTrim(request, "newPassword"); // 密码传输时的加密方式：MD5大写(MD5大写(取款密码明文)

		if(!uValidate.testPassword(json, newPassword)) return json.toJson();
		if (StringUtils.isEmpty(oldPassword)) {
			json.set(2, "2-1094");
			return json.toJson();
		}

		String disposableToken = getDisposableToken(session, request); // 获取并删除一次性token
		if (StringUtils.isEmpty(disposableToken)) {
			json.set(2, "2-15"); // 您的请求已超时，请重新提交 一次性验证token没有了，说明session失效
			return json.toJson();
		}

		User uEntity = uService.getById(sessionUser.getId());
		if (uEntity == null) {
			super.logOut(session, request);
			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
			return json.toJson();
		}

		// 请先绑定您的账户姓名
		if(uEntity.getBindStatus() != 1 || StringUtils.isEmpty(uEntity.getWithdrawName())) {
			json.set(2, "2-1061");
			return json.toJson();
		}

		// 验证资金密码
		if(!PasswordUtil.validatePassword(uEntity.getWithdrawPassword(), disposableToken, oldPassword)) {
			json.set(2, "2-1094");
			return json.toJson();
		}

		// 输入的密码是否与登录密码相同
		if (PasswordUtil.validateSamePassword(uEntity.getPassword(), newPassword)) {
			json.set(2, "2-1053");
			return json.toJson();
		}

		// 输入的密码是否与原资金密码相同
		if (StringUtils.isNotEmpty(uEntity.getWithdrawPassword())) {
			if (PasswordUtil.validateSamePassword(uEntity.getWithdrawPassword(), newPassword)) {
				json.set(2, "2-1054");
				return json.toJson();
			}
		}

		String usernamePwd = PasswordUtil.generatePasswordByPlainString(sessionUser.getUsername());
		boolean sameWithUsername = PasswordUtil.validateSamePassword(usernamePwd, newPassword);
		if (sameWithUsername) {
			json.set(2, "2-1086"); // 密码设置不允许和用户名相同！为了保障您的资金安全，请您将密码设置得更为复杂一些！
			return json.toJson();
		}

		String md5Pwd = PasswordUtil.generatePasswordByMD5(newPassword);
		if (PasswordUtil.isSimplePassword(md5Pwd)) {
			json.set(2, "2-1087");
			return json.toJson();
		}

		boolean result = uService.updateWithdrawPassword(uEntity.getId(), md5Pwd);
		if(result) {
			logOut(session, request);
			uActionLogService.modWithdrawPwd(uEntity.getId(), ip);
			json.set(0, "0-1");
		} else {
			json.set(1, "1-1");
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_WITHDRAW_NAME_BIND, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_WITHDRAW_NAME_BIND(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);

		// // 限制访问频率,10秒最多20次，间隔300毫秒
		// boolean exceedTime = super.validateAccessTimeForAPI(session, request, WUC.USER_WITHDRAW_NAME_BIND, 10, 20, 300);
		// if (exceedTime == true) {
		// 	json.set(2, "2-17"); // 您的账户信息发生变更，请重新登录
		// 	return json.toJson();
		// }

		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		json.set(2, "2-8");
		return json.toJson();
		//
		// String ip = HttpUtil.getRealIp(json, request);
		// if (ip == null) {
		// 	return json.toJson();
		// }
		//
		// //先增加ip黑名单验证
		// boolean blackList = uValidate.testIpBlackList(ip);
		// if(blackList){
		// 	json.set(2, "2-1073", ip);
		// 	return json.toJson();
		// }
		//
		// User uEntity = uService.getById(sessionUser.getId());
		// if (uEntity == null) {
		// 	super.logOut(session, request);
		// 	json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
		// 	return json.toJson();
		// }
		//
		// String withdrawName = HttpUtil.getStringParameterTrim(request, "withdrawName");
		// withdrawName = HttpUtil.escapeInput(withdrawName);
		// if(uEntity.getBindStatus() == 1) {
		// 	boolean hasWithdrawName = StringUtil.isNotNull(uEntity.getWithdrawName());
		// 	if(!hasWithdrawName) {
		// 		if(StringUtil.isNotNull(withdrawName)) {
		// 			boolean result = uService.updateWithdrawName(uEntity.getId(), withdrawName);
		// 			if(result) {
		// 				uActionLogService.bindWithdrawName(uEntity.getId(), ip, withdrawName);
		// 				json.set(0, "0-1");
		// 			} else {
		// 				json.set(1, "1-1");
		// 			}
		// 		} else {
		// 			json.set(2, "2-1058");
		// 		}
		// 	} else {
		// 		json.set(2, "2-1059");
		// 	}
		// } else {
		// 	json.set(2, "2-1061");
		// }
		// return json.toJson();
	}

	@RequestMapping(value = WUC.USER_GOOGLE_BIND_GET, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_GOOGLE_BIND_GET(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);

		// // 限制访问频率,10秒最多20次
		// boolean exceedTime = super.validateAccessTimeForAPI(session, request, WUC.USER_GOOGLE_BIND_GET, 10, 20, -1);
		// if (exceedTime == true) {
		// 	json.set(2, "2-17"); // 您的账户信息发生变更，请重新登录
		// 	return json.toJson();
		// }

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

		User uEntity = uService.getById(sessionUser.getId());
		if (uEntity == null) {
			super.logOut(session, request);
			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
			return json.toJson();
		}

		// 已经绑定google了
		if(uEntity.getIsBindGoogle() == 1 && StringUtil.isNotNull(uEntity.getSecretKey())) {
			json.set(2, "2-4020");
			return json.toJson();
		}

		UserSecurityVO bean = uSecurityService.getRandomByUserId(uEntity.getId());
		if (bean == null) {
			json.data("data", null);
			json.set(0, "0-1");
		}
		else {
			Map<String, Object> data = new HashMap<>();
			GoogleAuthenticatorKey credentials = uService.createCredentialsForUser(uEntity.getId());
			String otpAuthTotpURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("", uEntity.getUsername(), credentials);
			String qr = ImageUtil.encodeQR(otpAuthTotpURL, 200, 200);

			data.put("sid", bean.getId());
			data.put("key", bean.getKey());
			data.put("secretKey", credentials.getKey());
			data.put("qr", qr);
			json.data("data", data);
			json.set(0, "0-1");
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_GOOGLE_BIND, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_GOOGLE_BIND(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);

		// // 限制访问频率,10秒最多20次，间隔300毫秒
		// boolean exceedTime = super.validateAccessTimeForAPI(session, request, WUC.USER_GOOGLE_BIND, 10, 20, 300);
		// if (exceedTime == true) {
		// 	json.set(2, "2-17"); // 您的账户信息发生变更，请重新登录
		// 	return json.toJson();
		// }

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

		int sid = HttpUtil.getIntParameter(request, "sid");
		String answer = request.getParameter("answer"); // MD5大写(MD5大写(MD5大写(MD5大写(答案明文))) + token)
		int verificationCode = HttpUtil.getIntParameter(request, "vCode");

		if (StringUtils.isEmpty(answer)) {
			json.set(2, "2-1010");
			return json.toJson();
		}

		String disposableToken = getDisposableToken(session, request); // 获取并删除一次性token
		if (StringUtils.isEmpty(disposableToken)) {
			json.set(2, "2-15"); // 您的请求已超时，请重新提交 一次性验证token没有了，说明session失效
			return json.toJson();
		}

		// 验证密保
		if(!uSecurityService.validateSecurity(sid, sessionUser.getId(), disposableToken, answer)) {
			json.set(2, "2-1010");
			return json.toJson();
		}

		boolean authorize = uService.authoriseUser(sessionUser.getId(), verificationCode);
		if (authorize) {
			uService.updateIsBindGoogle(sessionUser.getId(), 1);
			uActionLogService.bindGoogle(sessionUser.getId(), ip);
			json.set(0, "0-1");
		}
		else {
			// 谷歌验证码输入错误！
			json.set(2, "2-4021");
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_LIST_DIRECT_LOWER, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_LIST_DIRECT_LOWER(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		List<User> uList = uReadService.getUserDirectLowerFromRead(sessionUser.getId());
		List<UserVO> list = new ArrayList<>();
		for (User tmpBean : uList) {
			list.add(new UserVO(tmpBean.getId(), tmpBean.getUsername()));
		}
		json.data("list", list);
		json.set(0, "0-1");
		return json.toJson();
	}

	// @RequestMapping(value = WUC.USER_NAVIGATE_UPDATE, method = { RequestMethod.POST })
	// @ResponseBody
	// public Map<String, Object> USER_NAVIGATE_UPDATE(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	SessionUser sessionUser = super.getSessionUser(json, session, request);
	// 	if (sessionUser == null) {
	// 		return json.toJson();
	// 	}
	//
	// 	UserInfo entity = uInfoService.get(sessionUser.getId());
	// 	if(entity == null) {
	// 		entity = new UserInfo();
	// 		entity.setUserId(sessionUser.getId());
	// 		entity.setNavigate(1);
	// 		uInfoService.add(entity);
	// 	} else {
	// 		entity.setNavigate(1);
	// 		uInfoService.update(entity);
	// 	}
	// 	return json.toJson();
	// }

	// @RequestMapping(value = WUC.USER_AVATAR_MOD, method = { RequestMethod.POST })
	// @ResponseBody
	// public Map<String, Object> USER_AVATAR_MOD(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	User uEntity = super.getCurrUser(session, request);
	// 	if(uEntity != null) {
	// 		int avatar = HttpUtil.getIntParameter(request, "avatar");
	// 		UserInfo entity = uInfoDao.get(uEntity.getId());
	// 		boolean result = false;
	// 		if(entity == null) {
	// 			entity = new UserInfo();
	// 			entity.setUserId(uEntity.getId());
	// 			entity.setAvatar(avatar);
	// 			result = uInfoDao.add(entity);
	// 		} else {
	// 			entity.setAvatar(avatar);
	// 			result = uInfoDao.update(entity);
	// 		}
	// 		if(result) {
	// 			json.set(0, "0-1");
	// 		} else {
	// 			json.set(1, "1-1");
	// 		}
	// 	} else {
	// 		String message = (String) request.getAttribute("message");
	// 		message = StringUtil.isNotNull(message) ? message : "2-1006";
	// 		json.set(2, message);
	// 	}
	// 	return json.toJson();
	// }

	@RequestMapping(value = WUC.RANDOM_CARD_GET, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> RANDOM_CARD_GET(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		UserCardVO cardVO = uCardService.getRandomByUserId(sessionUser.getId());
		json.data("data", cardVO);
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.LOGIN_VALIDATE_MOD, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> LOGIN_VALIDATE_MOD(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}

		User uEntity = uService.getById(sessionUser.getId());
		if (uEntity == null) {
			super.logOut(session, request);
			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
			return json.toJson();
		}

		// 绑定账户姓名
		{
			if (StringUtils.isBlank(uEntity.getWithdrawName())) {
				json.set(2, "2-1012");//您还没有绑定取款人！
				return json.toJson();
			}
			if (StringUtils.isBlank(uEntity.getWithdrawPassword())) {
				json.set(2, "2-1085");//验证是否绑定了资金密码
				return json.toJson();
			}
		}

		// 验证密保
		{
			UserSecurityVO securityVO = uSecurityService.getRandomByUserId(uEntity.getId());
			if (securityVO == null) {
				json.set(2, "2-1033");
				return json.toJson();
			}
		}

		// 银行卡
		{
			// 检查是否有绑定银行卡
			UserCardVO cardVO = uCardService.getRandomByUserId(uEntity.getId());
			if (cardVO == null) {
				json.set(2, "2-1077");
				return json.toJson();
			}
		}

		int loginValidate = uEntity.getLoginValidate() == 0 ? 1 : 0;

		if (loginValidate == 0) { // 关闭
			int cid = HttpUtil.getIntParameter(request, "cid");
			String cardName = HttpUtil.getStringParameterTrim(request, "cardName");
			boolean validate = uCardService.validateCard(json, cid, uEntity.getId(), cardName);
			if (!validate) {
				return json.toJson();
			}
		}

		boolean result = uService.updateLoginValidate(uEntity.getId(), loginValidate);
		if(result) {
			uActionLogService.modLoginValidate(uEntity.getId(), ip, loginValidate);
			json.set(0, "0-1");
		} else {
			json.set(1, "1-1");
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_CHECK_EXIST, method = { RequestMethod.POST })
	@ResponseBody
	public String USER_CHECK_EXIST(HttpServletRequest request) {
		String username = HttpUtil.getStringParameterTrim(request, "username");
		//uDao.getByUsername(username) != null ? true : false;
		String message = "";
		if(uService.getByUsername(username) != null){
			message = "true";
		}else{
			message = "false";
		}
		return message;
	}

	@RequestMapping(value = WUC.GET_USER_OTHER, method = { RequestMethod.POST })
	@ResponseBody
	public HashMap<String, Object> GET_USER_OTHER(HttpServletRequest request) {
		HashMap<String, Object> result = new HashMap<>();
		String username = HttpUtil.getStringParameterTrim(request, "username");
		User user = uService.getByUsername(username);
		if(user == null){
			result.put("status",false);
		}else{
			result.put("status",true);
			result.put("userId",user.getId());
		}
		return result;
	}

	@RequestMapping(value = WUC.TRANSFER_SAVE, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> transferSave(HttpSession session, HttpServletRequest request){

		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}
		String disposableToken = getDisposableToken(session, request); // 获取并删除一次性token
		if (StringUtils.isEmpty(disposableToken)) {
			json.set(2, "2-15"); // 您的请求已超时，请重新提交 一次性验证token没有了，说明session失效
            json.setMessage("您的请求已超时，请重新提交");
			return json.toJson();
		}

		// 获取参数
		int userId = HttpUtil.getIntParameter(request, "userId");
		double amount = HttpUtil.getDoubleParameter(request, "amount");
		String withdrawPwd = request.getParameter("withdrawPwd"); // MD5大写(MD5大写(MD5大写(MD5大写(密码明文))) + token)

		User uEntity = uService.getById(sessionUser.getId());

		if(uEntity.getId()==userId){
		    json.setMessage("不能给自己转账！");
		    return json.toJson();
        }

		// 验证资金密码
		if(!PasswordUtil.validatePassword(uEntity.getWithdrawPassword(), disposableToken, withdrawPwd)) {
			json.set(2, "2-1009");
			return json.toJson();
		}

		if(uEntity.getTotalMoney() - amount<0){
            json.setMessage("您的余额不足");
            return json.toJson();
        }

        //更新主账户
        boolean flag = uService.updateTotalMoney(uEntity.getId(), -amount);
        json.setMessage("操作失败");
        if(flag){
            //更新转账用户
            User byId = uService.getById(userId);
            flag = uService.updateTotalMoney(userId, amount);
            if(flag){
				String time = new Moment().toSimpleTime();

				//如果是转账操作，减去相应的消费限制
				uWithdrawLimitService.add(uEntity.getId(), -amount, time, Global.PAYMENT_CHANNEL_TYPE_USER, Global.PAYMENT_CHANNEL_SUB_TYPE_TRANSFER_OUT, dataFactory.getWithdrawConfig().getTransferConsumptionPercent());

				String remarks="向"+byId.getUsername()+"账户转账"+amount+"元";
				userBillService.addUserTransInBill(uEntity, Global.BILL_TYPE_TRANS_ACCOUNT,amount,remarks);

				//消费限制
				uWithdrawLimitService.add(byId.getId(), amount, time, Global.PAYMENT_CHANNEL_TYPE_USER, Global.PAYMENT_CHANNEL_SUB_TYPE_TRANSFER_IN, dataFactory.getWithdrawConfig().getTransferConsumptionPercent());

				remarks=uEntity.getUsername()+"账户向您转账"+amount+"元";
				userBillService.addUserTransInBill(byId, Global.BILL_TYPE_TRANS_ACCOUNT,amount,remarks);

                json.setMessage("操作成功");
            }
        }
		return json.toJson();
	}

}