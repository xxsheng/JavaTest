package admin.web.google;


import admin.domains.content.biz.AdminUserService;
import admin.domains.content.dao.AdminUserDao;
import admin.domains.content.entity.AdminUser;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import javautils.StringUtil;
import javautils.encrypt.PasswordUtil;
import javautils.http.HttpUtil;
import lottery.domains.pool.LotteryDataFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class GoogleController extends AbstractActionController{

	@Autowired
	private AdminUserDao adminUserDao;
	
	@Autowired
	private AdminUserService adminUserService;

	@Autowired
	private LotteryDataFactory dataFactory;
	
	/**
	 * 绑定方法
	 * 1.首先验证用户要登录后台
	 * 2.点击绑定谷歌身份验证器器
	 * 3.判断用户是否已经绑定过认证器，如果已经绑定并且通过验证(isValidate == 1)，则不允许重复绑定
	 * 4.未绑定的用户或者未通过验证的用户，生成二维码和秘钥，传递到前端让用户进行身份绑定
	 * 5.把绑定信息保存到后台数据库，并且状态为未通过验证(isValidate == 0)
	 * 6.绑定完成后，输入 谷歌校验码 进行身份的验证，通过验证后，更新数据库改变isValidte 为 1。
	 * 7.提示用户绑定成功
	 */
	
	/**
	 * 登录校验方法
	 * 如果已经绑定了google身份验证，并且验证成功的用户，在登录的时候，需要输入动态密码来进行登录验证
	 * @param response
	 */
	@RequestMapping(value = WUC.GOOGLE_AUTH_BIND, method = RequestMethod.POST)
	@ResponseBody
	public void GOOGLE_AUTH_BIND(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getGoogleBindUser(session);
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		if (uEntity == null) {
			json.set(1, "1-5");
			HttpUtil.write(response, json.toString(), HttpUtil.json);
			return;
		}

		if(StringUtils.isEmpty(uEntity.getSecretKey()) || uEntity.getIsValidate() == 0){
			GoogleAuthenticatorKey credentials = adminUserService.createCredentialsForUser(uEntity.getUsername());
			String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthURL("", uEntity.getUsername(), credentials);
			uEntity.setSecretKey(credentials.getKey());
			super.setGoogleBindUser(session, uEntity);
			json.accumulate("qr", otpAuthURL);
			json.accumulate("secret", credentials.getKey());
			json.set(0, "0-5");
		}else{
			json.set(2, "2-25");
		}

		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.GOOGLE_AUTH_AUTHROIZE, method = RequestMethod.POST)
	@ResponseBody
	public void GOOGLE_AUTH_AUTHROIZE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getGoogleBindUser(session);
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		if (uEntity == null) {
			json.set(1, "1-5");
			HttpUtil.write(response, json.toString(), HttpUtil.json);
			return;
		}

		int verificationCode = HttpUtil.getIntParameter(request, "vCode"); // 这个是用户输入的
		String loginPwd = request.getParameter("loginPwd");
		String token = getDisposableToken(session, request);

		boolean authorize = adminUserService.authoriseUser(uEntity.getUsername(), verificationCode);
		boolean isValidPwd = PasswordUtil.validatePassword(uEntity.getPassword(), token, loginPwd);
		if(authorize && isValidPwd){
			uEntity.setIsValidate(1);
			adminUserDao.update(uEntity);
			super.clearGoogleBindUser(session);
			json.set(0, "0-5");
		}

		if(!isValidPwd){
			json.set(2, "2-5");
		}else if(!authorize){
			json.set(2, "2-24");
		}

		HttpUtil.write(response, json.toString(), HttpUtil.json);

	}
	
	@RequestMapping(value = WUC.GOOGLE_AUTH_ISBIND, method = RequestMethod.POST)
	@ResponseBody
	public void GOOGLE_AUTH_ISBIND(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		if (dataFactory.getAdminGoogleConfig().isLoginStatus()) {
			String username = request.getParameter("username");
			if(StringUtil.isNotNull(username)){
				AdminUser user = adminUserDao.getByUsername(username);
				if(user != null && StringUtils.isNotEmpty(user.getSecretKey()) && user.getIsValidate() == 1){
					HttpUtil.write(response, Boolean.toString(true));
				}else{
					HttpUtil.write(response, Boolean.toString(false));
				}
			}
		}
		else {
			HttpUtil.write(response, Boolean.toString(false));
		}
	}
}