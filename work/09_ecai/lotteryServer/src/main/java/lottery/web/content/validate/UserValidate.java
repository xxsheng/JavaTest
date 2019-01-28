package lottery.web.content.validate;

import javautils.StringUtil;
import javautils.encrypt.DESUtil;
import javautils.ip.IpUtil;
import javautils.regex.RegexUtil;
import lottery.domains.content.api.ag.AGAPI;
import lottery.domains.content.api.ag.AGCode;
import lottery.domains.content.api.ag.AGValidation;
import lottery.domains.content.api.ag.AGValidationResult;
import lottery.domains.content.api.im.IMCode;
import lottery.domains.content.api.im.IMValidationResult;
import lottery.domains.content.biz.UserBlacklistService;
import lottery.domains.content.biz.UserGameAccountService;
import lottery.domains.content.biz.UserService;
import lottery.domains.content.biz.read.UserLoginLogReadService;
import lottery.domains.content.entity.SysPlatform;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserGameAccount;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.user.LoginValidateVO;
import lottery.domains.content.vo.user.UserLoginLogVO;
import lottery.domains.pool.DataFactory;
import lottery.web.WebJSON;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class UserValidate {
	private static final DESUtil DES_UTIL = DESUtil.getInstance();
	private static final String APP_TOKEN_DES_KEY = "nqKxo$zsaPURbL~uW/wW@dj&jO8U6tGh5";

	@Autowired
	private UserService uService;
	
	@Autowired
	private UserBlacklistService uBlacklistService;
	
	@Autowired
	private UserCardValidate uCardValidate;
	
	@Autowired
	private UserSecurityValidate uSecurityValidate;

	@Autowired
	private UserLoginLogReadService uLoginLogReadService;

	@Autowired
	private AGAPI agAPI;

	@Autowired
	private UserGameAccountService uGameAccountService;

	@Autowired
	private DataFactory dataFactory;

	/**
	 * 验证登录参数
	 */
	public boolean testLoginParams(WebJSON json, String username, String password, String code) {
		if(!StringUtil.isNotNull(username)) {
			json.set(2, "2-1000");
			return false;
		}
		if(!StringUtil.isNotNull(password)) {
			json.set(2, "2-1001");
			return false;
		}
		if(!StringUtil.isNotNull(code)) {
			json.set(2, "2-1002");
			return false;
		}
		return true;
	}
	
	/**
	 * 验证app登录
	 */
	public boolean testAppLoginParams(WebJSON json, String username, String password) {
		if(!StringUtil.isNotNull(username)) {
			json.set(2, "2-1000");
			return false;
		}
		if(!StringUtil.isNotNull(password)) {
			json.set(2, "2-1001");
			return false;
		}
		return true;
	}
	
	/**
	 * 验证密码信息
	 */
	public boolean testPassword(WebJSON json, String password) {
		if(!StringUtil.isNotNull(password)) {
			json.set(2, "2-1001");
			return false;
		}
		return true;
	}
	
	/**
	 * 验证用户名信息
	 */
	public boolean testUsername(WebJSON json, String username) {
		String patrn = "^[a-zA-Z]{1}([a-zA-Z0-9]){5,11}$";
		if(!RegexUtil.isMatcher(username, patrn)) {
			json.set(2, "2-2002");
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		String patrn = "^[a-zA-Z]{1}([a-zA-Z0-9]){5,11}$";
		if(!RegexUtil.isMatcher("1aasdf123", patrn)) {
			System.out.println("false");
		}
		else {
			System.out.println("true");

		}
	}
	
	/**
	 * 验证昵称
	 */
	public boolean testNickname(WebJSON json, String nickname) {
		int l = length(nickname);
		if(l < 4 || l > 12) {
			json.set(2, "2-2003");
			return false;
		}
		return true;
	}
	
	/**
	 * 验证用户是否存在
	 */
	public boolean testUserExist(WebJSON json, String username) {
		User bean = uService.getByUsername(username);
		if(bean != null) {
			json.set(2, "2-2004");
			return false;
		}
		return true;
	}

	public boolean allowEqualCode(int code) {
		if (code >= dataFactory.getCodeConfig().getNotCreateAccount()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 重写字符串长度计算方法
	 */
	public static int length(String s) {
		int l = 0;
		for (int i = 0; i < s.length(); i++) {
			l += s.charAt(i) > 255 ?2 : 1;
		}
		return l;
	}
	
	/**
	 * 验证用户
	 */
	public boolean testUser(WebJSON json, String username) {
		if(!testUsername(json, username)) return false;
		if(!testUserExist(json, username)) return false;
		return true;
	}
	
	/**
	 * 验证用户注册基本信息
	 */
	public boolean testRegistParams(WebJSON json, String username, String nickname, String password) {
		if(!testUsername(json, username)) return false;
		if(!testUserExist(json, username)) return false;
		if(!testNickname(json, nickname)) return false;
		if(!testPassword(json, password)) return false;
		return true;
	}
	
	/**
	 * 验证绑定资料
	 */
	public boolean testBindRequired(WebJSON json, Integer bankId, String bankBranch, String cardName, String cardId, String question, String answer, String withdrawPwd) {
		if(!uCardValidate.required(json, bankId, cardName, cardId)) return false;
		if(!uSecurityValidate.required(json, question, answer)) return false;
		if(!testPassword(json, withdrawPwd)) return false;
		return true;
	}

	/**
	 * 验证是否ip黑名单，永久的
	 * @param ip
	 * @return
	 */
	public boolean testIpBlackList(String ip){
//		String[] addressArr = IpUtil.find(ip);
//		if (addressArr != null) {
//			String address = Arrays.toString(IpUtil.find(ip));
//			if (address != null && address.indexOf("铜仁") > -1) {
//				return true;
//			}
//		}
		int count = uBlacklistService.getByIp(ip);
		return count > 0 ? true : false;
	}

	public LoginValidateVO testLoginValidate(String ip, String userAgent, User user) {
		// if (MobileChecker.checkIsMobile(userAgent)) {
		// 	return null;
		// }

		if (user.getLoginValidate() == 0) {
			return null;
		}
		UserLoginLogVO lastLogin = uLoginLogReadService.getLastLogin(user.getId(), 0);
		if (lastLogin == null) {
			return null;
		}

		String thisAddress = "[未知地址]";
		try {
			String[] infos = IpUtil.find(ip);
			thisAddress = Arrays.toString(infos);
		} catch (Exception e) {
		}
        
		if (lastLogin.getAddress().equals(thisAddress)) {
			return null;
		}

		return new LoginValidateVO(null, thisAddress, lastLogin.getAddress());
	}

	public AGValidationResult validateAG(String body, String domain, String ip) {
		// 转换参数
		AGValidation validation = agAPI.transValidationFromString(body);

		// 验证参数
		boolean verify = agAPI.verifyAGValidation(validation);
		if (!verify) {
			String xml = agAPI.transValidationToString(validation, AGCode.VALIDATE_PWD_ERROR, "用户名密码错误", domain, ip);
			return new AGValidationResult(false, xml, null);
		}

		UserGameAccount account = uGameAccountService.getByUsername(validation.getUserid(), 4, 1); // 4：AG
		if (account == null) {
			String xml = agAPI.transValidationToString(validation, AGCode.VALIDATE_PWD_ERROR, "用户名密码错误", domain, ip);
			return new AGValidationResult(false, xml, null);
		}

		// 验证密码
		String password = uGameAccountService.decryptPwd(account.getPassword());
		if (!password.equals(validation.getRealPassword())) {
			String xml = agAPI.transValidationToString(validation, AGCode.VALIDATE_PWD_ERROR, "用户名密码错误", domain, ip);
			return new AGValidationResult(false, xml, null);
		}

		// 验证用户及状态
		User user = uService.getById(account.getUserId());
		if (user == null) {
			String xml = agAPI.transValidationToString(validation, AGCode.VALIDATE_PWD_ERROR, "用户名密码错误", domain, ip);
			return new AGValidationResult(false, xml, user);
		}

		if (user.getAStatus() != 0) {
			String xml = agAPI.transValidationToString(validation, AGCode.VALIDATE_ILLEGAL, "用户状态非正常", domain, ip);
			return new AGValidationResult(false, xml, user);
		}

		// 验证平台状态
		SysPlatform sysPlatform = dataFactory.getSysPlatform(Global.BILL_ACCOUNT_AG);
		if (sysPlatform == null || sysPlatform.getStatus() != 0) {
			String xml = agAPI.transValidationToString(validation, AGCode.VALIDATE_CLOSED, "游戏未开启", domain, ip);
			return new AGValidationResult(false, xml, user);
		}

		String xml = agAPI.transValidationToString(validation, AGCode.VALIDATE_SUCCESS, "", domain, ip);
		return new AGValidationResult(true, xml, user);
	}

	public IMValidationResult validateIM(String userName, String password, String ip) {
		if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
			return new IMValidationResult(userName, ip, false, IMCode.ACCOUNT_ERROR, "用户名密码错误");
		}

		UserGameAccount account = uGameAccountService.getByUsername(userName, 12, 1); // 12：IM
		if (account == null) {
			return new IMValidationResult(userName, ip, false, IMCode.ACCOUNT_ERROR, "用户名密码错误");
		}

		// 验证密码
		String dbPassword = uGameAccountService.decryptPwd(account.getPassword());
		if (!password.equals(dbPassword)) {
			return new IMValidationResult(userName, ip, false, IMCode.ACCOUNT_ERROR, "用户名密码错误");
		}

		// 验证用户及状态
		User user = uService.getById(account.getUserId());
		if (user == null) {
			return new IMValidationResult(userName, ip, false, IMCode.ACCOUNT_ERROR, "用户名密码错误");
		}

		if (user.getAStatus() != 0) {
			return new IMValidationResult(userName, ip, false, IMCode.ACCOUNT_ERROR, "用户状态非正常");
		}

		// 验证平台状态
		SysPlatform sysPlatform = dataFactory.getSysPlatform(Global.BILL_ACCOUNT_IM);
		if (sysPlatform == null || sysPlatform.getStatus() != 0) {
			return new IMValidationResult(userName, ip, false, IMCode.MAINTENANCE, "游戏未开启");
		}

		return new IMValidationResult(userName, ip, true, IMCode.SUCCESS, "Success");
	}


}